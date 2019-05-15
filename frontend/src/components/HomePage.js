import React from "react";
import {Link} from "react-router-dom";

export class HomePage extends React.Component {

  constructor(props, context) {
    super(props, context);
  }

  render() {
    return <div className="jumbotron">
      <h1 className="display-4">Gitplag</h1>
      <p className="lead">Plagiarism analyser for git educational repositories</p>
      <hr className="my-4"/>
      <p>See our <a href="https://github.com/nikita715/gitplag/wiki">wiki</a> for information about the system.</p>
      <Link to="/repos/new" className="btn btn-primary btn-lg mr-2" href="#" role="button">Add new repository</Link>
      <Link to="/repos" className="btn btn-outline-secondary btn-lg" href="#" role="button">List of repositories</Link>
    </div>;
  }
}