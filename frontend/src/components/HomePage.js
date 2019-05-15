import React from "react";
import {Link} from "react-router-dom";

export class HomePage extends React.Component {

  render() {

    return <div className="jumbotron">
      <h1 className="display-4">Gitplag</h1>
      <p className="lead">Plagiarism analyser for git educational repositories</p>
      <hr className="my-4"/>
      <p>Create a record of your git repository to start using the system</p>
      <Link to="/repos" className="btn btn-primary btn-lg" href="#" role="button">List of repositories</Link>
    </div>;

  }
}