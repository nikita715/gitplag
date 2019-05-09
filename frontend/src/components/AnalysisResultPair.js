import React from "react";
import axios from "axios";
import * as PROP from "../properties";
import {times} from "../util";
import {Link} from "react-router-dom";

export class AnalysisResultPair extends React.Component {
  state = {
    analysisId: 0,
    pairId: 0,
    files1: [],
    files2: [],
    leftName: "",
    rightName: "",
    leftMatches: [],
    rightMatches: [],
    percentage: 0
  };
  matchIndex = 0;
  redClass = false;

  constructor(props, context) {
    super(props, context);
    let analysisId = props.match.params.analysisId;
    let pairId = props.match.params.pairId;
    this.state.analysisId = analysisId;
    this.state.pairId = pairId;
    axios.get(PROP.serverUrl + "/api/analyzes/" + analysisId + "/pairs/" + pairId).then((response) => {
      let files1 = response.data.files1.map((file) => ({fileName: file.name, lines: file.content}));
      let files2 = response.data.files2.map((file) => ({fileName: file.name, lines: file.content}));

      let leftMatches = [];
      let rightMatches = [];
      response.data.pair.lines.map((match) => {
        leftMatches.push(match.from1);
        leftMatches.push(match.to1);
        rightMatches.push(match.from2);
        rightMatches.push(match.to2);
      });

      this.setState({
        files1: files1,
        files2: files2,
        leftName: response.data.pair.student1,
        rightName: response.data.pair.student2,
        leftMatches: leftMatches,
        rightMatches: rightMatches,
        percentage: response.data.pair.percentage
      });
    });
    this.getLineClass = this.getLineClass.bind(this);
    this.getHrefToLine = this.getHrefToLine.bind(this);
  }

  getLineClass(matches, lineIndex) {
    if (matches[this.matchIndex] === undefined) this.matchIndex = 0;
    let end = false;
    if (matches[this.matchIndex] === lineIndex) {
      this.redClass = !this.redClass;
      this.matchIndex += 1;
      end = true;
    }
    return this.redClass || end ? "red-line" : "";
  }

  getHrefToLine(side, match2) {
    return this.redClass ? "#" + side + match2[this.matchIndex - 1] : null;
  }

  render() {
    let file1 = this.state.files1[0];
    let file2 = this.state.files2[0];
    let matches1 = this.state.leftMatches;
    let matches2 = this.state.rightMatches;
    if (this.state.files1[0] !== undefined) {
      return (
        <div>
          <div className="compare-exit"><Link to={"/analyzes/" + this.state.analysisId}>Back</Link></div>
          <div className="compare-names">
            <div>{this.state.leftName}</div>
            <div><b>{this.state.percentage + "%"}</b></div>
            <div>{this.state.rightName}</div>
          </div>
          <div className="compare-wrapper">
            <div className="compare-side-wrapper">
              {times(file1.lines.length + 1).map((index) =>
                <div id={"left" + index} className="compare-line-wrapper">
                  <pre className="compare">
                    <a href={this.getHrefToLine("right", matches2)}><div className={this.getLineClass(matches1, index)}>
                      {checkLine(file1.lines[index - 1])}
                    </div></a>
                  </pre>
                  <pre className="compare compare__indexes"><div>{index}</div></pre>
                </div>
              )}
            </div>
            <div className="compare-side-wrapper">
              {times(file2.lines.length + 1).map((index) =>
                <div id={"right" + index} className="compare-line-wrapper">
                  <pre className="compare compare__indexes"><div>{index}</div></pre>
                  <pre className="compare">
                    <a href={this.getHrefToLine("left", matches1)}><div className={this.getLineClass(matches2, index)}>
                      {checkLine(file2.lines[index - 1])}
                    </div></a>
                  </pre>
                </div>
              )}
            </div>
          </div>
        </div>
      );
    } else return null;
  }
}

function checkLine(line) {
  return line === "" ? " " : line;
}