import React from "react";
import axios from "axios";
import * as PROP from "../properties";
import {Link} from "react-router-dom";
import {formatDate} from "../util";

export class RepositoryAnalyzes extends React.Component {
  state = {
    repoId: 0,
    analyzes: [],
    repoName: ""
  };

  constructor(props, context) {
    super(props, context);
    this.state.repoId = props.match.params.id;
    this.deleteAnalysis = this.deleteAnalysis.bind(this);
  }

  deleteAnalysis(repoId, analysisId) {
    axios.delete(PROP.serverUrl + "/api/analyzes/" + analysisId).then(() => {
      this.componentDidMount()
    });
  }

  componentDidMount() {
    axios.get(PROP.serverUrl + "/api/repositories/" + this.state.repoId + "/analyzes").then((response) => {
      let analyzes = [];
      response.data.map((analysis) => analyzes.push(<tr>
        <td><Link to={"/analyzes/" + analysis.id}>{analysis.id}</Link></td>
        <td>{analysis.branch}</td>
        <td>{formatDate(analysis.date)}</td>
        <td><a onClick={(e) => this.deleteAnalysis(this.state.repoId, analysis.id)}>Delete</a></td>
      </tr>));
      this.setState({analyzes, repoName: response.data[0] ? response.data[0].repoName : ""});
    });
  }

  static startUpdateOfFiles(repoId) {
    axios.get(PROP.serverUrl + "/api/repositories/" + repoId + "/updateFilesAsync")
  }

  render() {
    return (
      <div className="Repo-List">
        <Link to={"/repos"}>Back to repositories</Link>
        <h3>Analyzes of repo {this.state.repoName}</h3>
        <Link to={"/repos/" + this.state.repoId + "/analyze"}>Run new analysis</Link><br/>
        <Link to={"/repos/" + this.state.repoId + "/files"}>Downloaded files</Link><br/>
        <a onClick={() => RepositoryAnalyzes.startUpdateOfFiles(this.state.repoId)}>Update files from git</a>
        <table>
          <tr>
            <th>Id</th>
            <th>Branch</th>
            <th>Date</th>
          </tr>
          {this.state.analyzes}</table>
      </div>);
  }
}