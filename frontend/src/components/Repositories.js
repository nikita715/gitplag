import React from "react";
import axios from "axios";
import {Link} from "react-router-dom";
import * as PROP from "../properties";

export class Repositories extends React.Component {

  state = {
    ready: false,
    repos: []
  };

  constructor(props, context) {
    super(props, context);
    window.history.pushState(null, "Repos", "/repos/");
    axios.get(PROP.serverUrl + "/api/repositories").then((response) => {
      let data = [];
      response.data.map((repo) =>
        data.push(<tr>
          <td><Link to={"/repos/" + repo.id + "/analyzes"}>{repo.id}</Link></td>
          <td>{repo.name}</td>
          <td>{repo.gitService.toLowerCase()}</td>
          <td><Link to={"/repos/" + repo.id + "/edit"}>Edit</Link>
          </td>
        </tr>)
      );
      this.setState({repos: data, ready: true});
    }).catch(() => this.props.history.push("/error"));
  }

  render() {
    return (
      <div className="Repo-List">
        <h3>Git repositories</h3>
        <Link to="/repos/new">Add a repository</Link><br/>
        <table>
          <tr>
            <th>Id</th>
            <th>Name</th>
            <th>Git</th>
          </tr>
          {this.state.repos}</table>
      </div>
    );
  }

}