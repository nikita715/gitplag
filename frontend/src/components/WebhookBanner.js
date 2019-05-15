import React from "react";
import {Link} from "react-router-dom";

export class WebhookBanner extends React.Component {

  render() {

    return <div className="jumbotron vertical-center">
      <div className="jumbotron my-auto mx-auto">
        <div className="col align-content-center align-items-center">
          <div className="row">
            <span>Add a webhook to the repo to automatically upload new files.</span></div>
          <div className="row">
            <span>URL: <strong>{"{server_url}"}/webhook</strong></span></div>
          <div className="row">
            <span>Events: <strong>push, pull request</strong></span></div>
        </div>
        <div className="align-content-center text-center">
          <Link className="text-center align-content-center" to="/repos">Back to repositories</Link>
        </div>
      </div>
    </div>;

  }
}