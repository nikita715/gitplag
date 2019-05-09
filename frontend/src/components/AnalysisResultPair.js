import React from "react";
import axios from "axios";
import * as PROP from "../properties";
import {times} from "../util";

export class AnalysisResultPair extends React.Component {
  state = {
    analysisId: 0,
    pairId: 0,
    files1: [],
    files2: [],
    leftName: "",
    rightName: ""
  };

  constructor(props, context) {
    super(props, context);
    let analysisId = props.match.params.analysisId;
    let pairId = props.match.params.pairId;
    this.state.analysisId = analysisId;
    this.state.pairId = pairId;
    axios.get(PROP.serverUrl + "/api/analyzes/" + analysisId + "/pairs/" + pairId).then((response) => {
      let files1 = response.data.files1.map((file) => ({fileName: file.name, lines: file.content}));
      let files2 = response.data.files2.map((file) => ({fileName: file.name, lines: file.content}));
      this.setState({
        files1: files1,
        files2: files2,
        leftName: response.data.pair.student1,
        rightName: response.data.pair.student2
      });
    });
  }

  render() {
    if (this.state.files1[0] !== undefined) {
      return (
        <div>
          <div className="compare-names">
            <div>{this.state.leftName}</div>
            <div>{this.state.rightName}</div>
          </div>
          <div className="compare-wrapper">
            {/*<Link to={"/analyzes/" + this.state.analysisId}>Back to analysis</Link>*/}
            <div className="compare-side-wrapper">
              <pre className="compare">
                {this.state.files1.map((it) => it.lines.map(it2 => <div>{it2 === "" ? " " : it2}</div>))}
              </pre>
              <pre className="compare compare__indexes">{times(this.state.files1[0].lines.length + 1).map((it) =>
                <div>{it}</div>)}
              </pre>
            </div>
            <div className="compare-side-wrapper">
              <pre className="compare compare__indexes">{times(this.state.files2[0].lines.length + 1).map((it) =>
                <div>{it}</div>)}
              </pre>
              <pre className="compare">
                {this.state.files2.map((it) => it.lines.map(it2 => <div>{it2 === "" ? " " : it2}</div>))}
              </pre>
            </div>
          </div>
        </div>
      );
    } else return null;
  }
}