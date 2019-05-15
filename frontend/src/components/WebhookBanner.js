import React from "react";
import {Link} from "react-router-dom";

export class WebhookBanner extends React.Component {

  render() {

    return <div className="jumbotron vertical-center">
      <div className="jumbotron my-auto mx-auto">
        <div className="align-content-center text-center mb-2">
          <h6>Add a webhook to the repo to automatically upload new files.</h6>
        </div>
        <table className="table table-bordered table-hover">
          <tbody>
          <tr className="">
            <td>URL</td>
            <td><strong>{"{server_url}"}/webhook</strong></td>
          </tr>
          <tr className="">
            <td>Events</td>
            <td><strong>push, pull request</strong></td>
          </tr>
          </tbody>
        </table>
        <div className="align-content-center text-center">
          <Link className="btn btn-primary mr-2 text-center align-content-center" role="button" to="/repos">Back to
            repositories</Link>
        </div>
      </div>
    </div>;

  }
}