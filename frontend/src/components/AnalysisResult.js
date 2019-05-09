import React from "react";
import axios from "axios";
import * as PROP from "../properties";
import {Link} from "react-router-dom";

export class AnalysisResult extends React.Component {

  state = {
    analysisId: 0,
    repoId: 0,
    results: [],
    resultLink: "",
    sortedByName: ""
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
        resultLink: response.data.resultLink
      });
    });
    this.handleChange = this.handleChange.bind(this)
  }

  handleChange(event) {
    this.setState({
      [event.target.name]: event.target.value.toLowerCase()
    });
    console.log(this.state.sortedByName);
  }

  render() {
    return (<div className="Repo-List">
      <Link to={"/repos/" + this.state.repoId + "/analyzes"}>Back to analyzes</Link>
      <h3>Analysis result #{this.state.analysisId}</h3>
      <a href={this.state.resultLink}>Source analysis</a><br/>
      <Link to={"/analyzes/" + this.state.analysisId + "/graph"}>Graph</Link><br/>
      <label>Find by student name: </label><input name="sortedByName" onChange={this.handleChange}/>
      <table>
        <tr>
          <th>Id</th>
          <th>Student 1</th>
          <th>Student 2</th>
          <th>Percentage</th>
        </tr>
        {this.state.results.filter(it => it.student1.toLowerCase().includes(this.state.sortedByName) || it.student2.toLowerCase().includes(this.state.sortedByName)).map(
          (result) =>
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