import React from "react";
import axios from "axios";
import * as PROP from "../properties";
import {Link} from "react-router-dom";
import {formatDate} from "../util";

export class AnalysisResult extends React.Component {

  state = {
    analysisId: 0,
    repoId: 0,
    results: [],
    resultLink: "",
    sortedByName: "",
    analyzer: "",
    date: "",
    branch: ""
  };

  constructor(props, context) {
    super(props, context);
    let id = props.match.params.id;
    axios.get(PROP.serverUrl + "/api/analyzes/" + id).then((response) => {
      let data = [];
      response.data.analysisPairs.map((result) => data.push({
        id: result.id,
        student1: result.student1,
        student2: result.student2,
        percentage: result.percentage
      }));
      this.setState({
        results: data, repoId: response.data.repo, analysisId: response.data.id,
        resultLink: response.data.resultLink, analyzer: response.data.analyzer,
        date: response.data.date, branch: response.data.branch
      });
    });
    this.handleChange = this.handleChange.bind(this);
  }

  handleChange(event) {
    this.setState({
      [event.target.name]: event.target.value.toLowerCase()
    });
  }

  render() {
    return (<div className="Repo-List">
      <Link to={"/repos/" + this.state.repoId + "/analyzes"}>Back to analyzes</Link>
      <h3>Analysis result #{this.state.analysisId}, executed {formatDate(this.state.date).toLowerCase()}</h3>
      <span>Analyzed by {this.state.analyzer.toLowerCase()}, branch {this.state.branch}</span><br/>
      <a href={(this.state.analyzer === "JPLAG" ? PROP.serverUrl : "") + this.state.resultLink}>Source analysis</a><br/>
      <Link to={"/analyzes/" + this.state.analysisId + "/graph"}>Graph</Link><br/>
      <label>Find by student name: </label><input name="sortedByName" onChange={this.handleChange}/>
      <table>
        <tr>
          <th>Id</th>
          <th>Student 1</th>
          <th>Student 2</th>
          <th>Percentage</th>
        </tr>
        {this.state.results.filter((it) =>
          it.student1.toLowerCase().includes(this.state.sortedByName)
          || it.student2.toLowerCase().includes(this.state.sortedByName)).map((result) =>
          <tr>
            <td>
              <Link to={"/analyzes/" + this.state.analysisId + "/pairs/" + result.id}>{result.id}</Link>
            </td>
            <td>{result.student1}</td>
            <td>{result.student2}</td>
            <td>{result.percentage}</td>
          </tr>)
        }
      </table>
    </div>);
  }
}