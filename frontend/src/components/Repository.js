import React from "react";
import axios from "axios";
import * as PROP from "../properties";
import {Link} from "react-router-dom";
import {formatDate} from "../util";
import {Header} from "./Header";

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
    axios.get(PROP.serverUrl + "/api/repositories/" + repoId + "/files/update/detached")
  }

  deleteAnalysis(repoId, analysisId) {
    axios.delete(PROP.serverUrl + "/api/analyzes/" + analysisId).then(() => {
      this.downloadAnalyzes()
    });
  }

  render() {
    return (
      <div className="container">
        {Header(
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
                <table className="table table-hover">
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
              <Link to={"/repos/" + this.state.repoId + "/analyze"}
                    className="btn list-group-item list-group-item-action">
                Run new analysis
              </Link>
              <Link to={"/repos/" + this.state.repoId + "/files"} className="list-group-item list-group-item-action">Downloaded
                files</Link>
              <div onClick={() => Repository.startUpdateOfFiles(this.state.repoId)}
                   className="btn list-group-item list-group-item-action">Update files from git
              </div>
              <Link className="btn list-group-item list-group-item-action" to={"/repos/" + this.state.repoId + "/edit"}>Manage
                the repository</Link>
            </div>
          </div>
        </div>
      </div>);
  }
}