import React from "react";
import {Link} from "react-router-dom";
import * as PROP from "../properties";
import axios from "axios";
import {Header} from "./Header";

export class RepositoryFiles extends React.Component {

  state = {
    repoId: 0,
    bases: [],
    solutions: [],
    repoName: "",
    sortedByName: ""
  };

  constructor(props, context) {
    super(props, context);
    this.state.repoId = props.match.params.id;
    axios.get(PROP.serverUrl + "/api/repositories/" + this.state.repoId).then((response) => {
      this.setState({repoName: response.data.name});
    });
    axios.get(PROP.serverUrl + "/api/repositories/" + this.state.repoId + "/files").then((response) => {
      let bases = response.data.bases.flatMap((base) => RepositoryFiles.baseElement(base));
      let solutions = response.data.solutions.flatMap((solution) => RepositoryFiles.solutionElement(solution));
      this.setState({
        bases, solutions
      });
    });
    this.handleChange = this.handleChange.bind(this);
  }

  static baseElement(base) {
    return base.files.map((file) => RepositoryFiles.fileElementBase(base, file));
  }

  static solutionElement(solution) {
    return solution.students.flatMap((student) => RepositoryFiles.solutionUserElement(solution, student));
  }

  static solutionUserElement(solution, student) {
    return student.files.map((file) => RepositoryFiles.fileElementSolution(solution, student, file));
  }

  static fileElementBase(base, file) {
    console.log(base);
    console.log(file);
    return {branch: base.branch, updated: base.updated.toString().replace("T", " "), name: file.name};
  }

  static fileElementSolution(solution, student, file) {
    return {
      branch: solution.sourceBranch,
      student: student.student,
      updated: student.updated.replace("T", " "),
      name: file.name
    };
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
          <li className="breadcrumb-item">Files</li>
        </ol>)}
      <div className="row">
        <div className="col container">
          <div className="col">
            <div className="row mb-2">
              <div className="col">
                <div className="row">
                  <h5>Downloaded files of repository {this.state.repoName}</h5>
                </div>
              </div>
            </div>
            <div className="row">
              <h6>Base files</h6>
              <table className="table table-hover">
                <thead className="thead-light">
                <tr>
                  <th>Name</th>
                  <th>Branch</th>
                  <th>Updated</th>
                </tr>
                </thead>
                <tbody>
                {this.state.bases.filter(
                  (it) => it.name.toLowerCase().includes(this.state.sortedByName)
                    || it.branch.toLowerCase().includes(this.state.sortedByName)
                    || it.updated.toLowerCase().includes(this.state.sortedByName)
                ).map((base) => <tr>
                  <td>{base.name}</td>
                  <td>{base.branch}</td>
                  <td>{base.updated}</td>
                </tr>)}
                </tbody>
              </table>
            </div>
            <div className="row">
              <h6>Solution files</h6>
              <table className="table table-hover">
                <thead className="thead-light">
                <tr>
                  <th>Student</th>
                  <th>Name</th>
                  <th>Branch</th>
                  <th>Updated</th>
                </tr>
                </thead>
                <tbody>
                {this.state.solutions.filter(
                  (it) => it.name.toLowerCase().includes(this.state.sortedByName)
                    || it.branch.toLowerCase().includes(this.state.sortedByName)
                    || it.updated.toLowerCase().includes(this.state.sortedByName)
                    || it.student.toLowerCase().includes(this.state.sortedByName)
                ).map((base) => <tr>
                  <td>{base.student}</td>
                  <td>{base.name}</td>
                  <td>{base.branch}</td>
                  <td>{base.updated}</td>
                </tr>)}
                </tbody>
              </table>
            </div>
          </div>
        </div>
        <div className="container col-sm-3">
          <div className="list-group input-group">
            <input name="sortedByName" onChange={this.handleChange} autoComplete="off"
                   className="list-group-item list-group-item-action text-input" placeholder="Full text search"/>
          </div>
        </div>
      </div>
    </div>);
  }
}