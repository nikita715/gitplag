import React from "react";
import {Link} from "react-router-dom";
import * as PROP from "../properties";
import axios from "axios";
import {formatDate} from "../util";

export class RepositoryFiles extends React.Component {

  state = {
    repoId: 0,
    data: null
  };

  constructor(props, context) {
    super(props, context);
    this.state.repoId = props.match.params.id;
    axios.get(PROP.serverUrl + "/api/repositories/" + this.state.repoId + "/files").then((response) => {
      let bases = response.data.bases.map((base) => RepositoryFiles.baseElement(base));
      let solutions = response.data.solutions.map((solution) => RepositoryFiles.solutionElement(solution));
      this.setState({
        data: <div className="col-md-10">

          <div className="list-group">
            <a href="#base_files" className="" data-toggle="collapse">Base files <i
              className="fa fa-caret-down"/></a>
            <ul className="collapse" id="base_files">
              {bases}
            </ul>
            <a href="#solution_files" className="" data-toggle="collapse">Solution files <i
              className="fa fa-caret-down"/></a>
            <ul className="collapse" id="solution_files">
              {solutions}
            </ul>
          </div>

        </div>
      });
    });
  }

  static baseElement(base) {
    return (<li>
      <a href={"#base_branch_" + base.branch} className=""
         data-toggle="collapse">{base.branch + ", updated at " + formatDate(base.updated) + " "}<i
        className="fa fa-caret-down"/></a>
      <ul className="collapse" id={"base_branch_" + base.branch}>
        {base.files.map((file) => RepositoryFiles.fileElement(file))}
      </ul>
    </li>);
  }

  static solutionElement(solution) {
    return (<li>
      <a href={"#branch_" + solution.sourceBranch} className=""
         data-toggle="collapse">{solution.sourceBranch + " "}<i className="fa fa-caret-down"/></a>
      <ul className="collapse" id={"branch_" + solution.sourceBranch}>
        {solution.students.map((student) => RepositoryFiles.solutionUserElement(solution, student))}
      </ul>
    </li>);
  }

  static solutionUserElement(solution, student) {
    return (<li>
      <a href={"#branch_" + solution.sourceBranch + "_user_" + student.student} className=""
         data-toggle="collapse">{student.student + ", updated at " + formatDate(student.updated) + " "}<i
        className="fa fa-caret-down"/></a>
      <ul className="collapse" id={"branch_" + solution.sourceBranch + "_user_" + student.student}>
        {student.files.map((file) => RepositoryFiles.fileElement(file))}
      </ul>
    </li>);
  }

  static fileElement(file) {
    return <li className="">{file.name}</li>;
  }

  render() {
    console.log(this.state.data);
    return (<div className="container">
      <Link to={"/repos/" + this.state.repoId}>Back to analyzes</Link>
      {this.state.data}
    </div>);
  }
}