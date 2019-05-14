import React from "react";
import axios from "axios";
import * as PROP from "../properties";
import {Link} from "react-router-dom";
import {formatDate} from "../util";
import {Header} from "./Header";

export class AnalysisResult extends React.Component {

  state = {
    analysisId: 0,
    repoId: 0,
    repoName: "",
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
        results: data, repoId: response.data.repo, repoName: response.data.repoName, analysisId: response.data.id,
        resultLink: AnalysisResult.createResultLink(response.data.resultLink, response.data.analyzer),
        analyzer: response.data.analyzer, date: response.data.date, branch: response.data.branch
      });
    });
    this.handleChange = this.handleChange.bind(this);
  }

  static createResultLink(link, analyzer) {
    if (analyzer === "JPLAG") {
      return PROP.serverUrl + link;
    } else {
      return link;
    }
  }

  handleChange(event) {
    this.setState({
      [event.target.name]: event.target.value.toLowerCase()
    });
  }

  render() {
    return (<div className="container">
        {Header(
          <ol className="breadcrumb">
            <li className="breadcrumb-item"><Link to="/repos">Repositories</Link></li>
            <li className="breadcrumb-item"><Link to={"/repos/" + this.state.repoId}>{this.state.repoName}</Link></li>
            <li className="breadcrumb-item active" aria-current="page">Analysis #{this.state.analysisId}</li>
          </ol>)}
        <div className="row">
          <div className="col container">
            <div className="col">
              <div className="row mb-2">
                <div className="col">
                  <div className="row">
                    <h5>Analysis result #{this.state.analysisId} of repository {this.state.repoName}</h5>
                  </div>
                  <div className="row">
                    <div className="badge badge-info mr-2">{this.state.analyzer}</div>
                    <div className="badge badge-info mr-2">Branch {this.state.branch}</div>
                    <div className="badge badge-info mr-2">{formatDate(this.state.date).toLowerCase()}</div>
                    <a className="badge badge-primary mr-2" role="button" href={this.state.resultLink}>Source</a>
                    <Link to={"/analyzes/" + this.state.analysisId + "/graph"} className="badge badge-primary mr-2"
                          role="button" aria-pressed="true">Graph</Link>
                  </div>
                </div>
              </div>
              <div className="row">
                <table className="table table-hover">
                  <thead className="thead-light">
                  <tr>
                    <th>Id</th>
                    <th>Student 1</th>
                    <th>Student 2</th>
                    <th>Percentage</th>
                  </tr>
                  </thead>
                  <tbody>
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
                  </tbody>
                </table>
              </div>
            </div>
          </div>
          <div className="container col-sm-3">
            <div className="list-group input-group">
              <input name="sortedByName" onChange={this.handleChange} autoComplete="off"
                     className="list-group-item list-group-item-action text-input" placeholder="Type a name"/>
            </div>
          </div>
        </div>
      </div>
    );
  }
}