import React from "react";
import axios from "axios";
import {Link} from "react-router-dom";
import * as PROP from "../properties";
import {Header} from "./Header";

export class Repositories extends React.Component {

  state = {
    ready: false,
    repos: []
  };

  componentDidMount() {
    this.fetchRepos();
    this.fetchRepos = this.fetchRepos.bind(this);
  }

  fetchRepos() {
    axios.get(PROP.serverUrl + "/api/repositories").then((response) => {
      let data = [];
      response.data.map((repo) =>
        data.push(<tr>
          <td><Link to={"/repos/" + repo.id}>{repo.name}</Link></td>
          <td>{repo.gitService.toLowerCase()}</td>
          <td/>
        </tr>)
      );
      this.setState({repos: data, ready: true});
    }).catch(() => this.props.history.push("/error"));
  }

  render() {
    return (
      <div className="container">
        {Header(
          <ol className="breadcrumb">
            <li className="breadcrumb-item active" aria-current="page"><Link onClick={() => this.fetchRepos()}
                                                                             to="/repos">Repositories</Link></li>
          </ol>
        )}
        <div className="row">
          <div className="col container">
            <div className="col">
              <div className="row">
                <h5>Repositories</h5>
              </div>
              <div className="row">
                <table className="table table-hover table-sm">
                  <thead className="thead-light">
                  <tr>
                    <th>Name</th>
                    <th>Git</th>
                    <th className="fit"></th>
                  </tr>
                  </thead>
                  <tbody>{this.state.repos}</tbody>
                </table>
              </div>
            </div>
          </div>
          <div className="container col-sm-3">
            <div className="list-group">
              <Link to={"/repos/new"}
                    className="btn list-group-item list-group-item-action">
                Add new repository
              </Link>
            </div>
          </div>
        </div>
      </div>
    );
  }
}