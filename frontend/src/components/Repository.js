import React from "react";
import axios from "axios";
import * as PROP from "../properties";
import {Link} from "react-router-dom";
import {formatDate} from "../util";
import {Header} from "./Header";
import {RunAnalysis} from "./RunAnalysis";
import {EditRepo} from "./EditRepo";

export class Repository extends React.Component {
  state = {
    repoId: 0,
    analyzes: [],
    repoName: ""
  };

  constructor(props, context) {
    super(props, context);
    this.state.repoId = props.match.params.id;
    axios.get(PROP.serverUrl + "/api/repositories/" + this.state.repoId).then((response) => {
      this.setState({repoName: response.data.name});
    });
    this.downloadAnalyzes();
    this.downloadAnalyzes = this.downloadAnalyzes.bind(this);
    this.deleteAnalysis = this.deleteAnalysis.bind(this);
  }

  downloadAnalyzes() {
    axios.get(PROP.serverUrl + "/api/repositories/" + this.state.repoId + "/analyzes").then((response) => {
      let analyzes = [];
      response.data.map((analysis) => analyzes.push(<tr>
        <td><Link to={"/analyzes/" + analysis.id}>{analysis.id}</Link></td>
        <td>{analysis.branch}</td>
        <td>{formatDate(analysis.date)}</td>
        <td>{analysis.analyzer.toLowerCase()}</td>
        <td>
          <div className="btn badge badge-danger"
               onClick={() => this.deleteAnalysis(this.state.repoId, analysis.id)}>Delete
          </div>
        </td>
      </tr>));
      this.setState({analyzes});
    });
  }

  static startUpdateOfFiles(repoId) {
    axios.get(PROP.serverUrl + "/api/repositories/" + repoId + "/files/update/detached");
  }

  deleteAnalysis(repoId, analysisId) {
    axios.delete(PROP.serverUrl + "/api/analyzes/" + analysisId).then(() => {
      this.downloadAnalyzes();
    });
  }

  openReposList() {
    this.props.history.push("/repos");
  }

  render() {
    return (
      <div className="container">
        {new Header(
          <ol className="breadcrumb">
            <li className="breadcrumb-item"><Link to="/repos">Repositories</Link></li>
            <li className="breadcrumb-item"><Link onClick={() => this.downloadAnalyzes()}
                                                  to={"/repos/" + this.state.repoId}>{this.state.repoName}</Link></li>
          </ol>)}
        <div className="row">
          <div className="col container">
            <div className="col">
              <div className="row">
                <h5>Analyzes of repository {this.state.repoName}</h5>
              </div>
              <div className="row">
                <table className="table table-hover table-sm">
                  <thead className="thead-light">
                  <tr>
                    <th>Id</th>
                    <th>Branch</th>
                    <th>Date</th>
                    <th>Analyzer</th>
                    <th className="fit"/>
                  </tr>
                  </thead>
                  <tbody>{this.state.analyzes}</tbody>
                </table>
              </div>
            </div>
          </div>
          <div className="container col-sm-3">
            <div className="list-group">
              <button className="btn list-group-item list-group-item-action" type="button" data-toggle="modal"
                      data-target="#newAnalysisModalWindow">
                Run new analysis
              </button>
              <Link to={"/repos/" + this.state.repoId + "/files"} className="list-group-item list-group-item-action">Downloaded
                files</Link>
              <div onClick={() => Repository.startUpdateOfFiles(this.state.repoId)}
                   className="btn list-group-item list-group-item-action">Update files from git
              </div>
              <button className="btn list-group-item list-group-item-action" type="button" data-toggle="modal"
                      data-target="#editRepoModalWindow">
                Manage the repository
              </button>
            </div>
          </div>
        </div>
        <RunAnalysis id={this.state.repoId}/>
        <EditRepo id={this.state.repoId} openReposList={this.openReposList}/>
      </div>);
  }
}