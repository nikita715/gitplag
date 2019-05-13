import React from "react";
import {Link} from "react-router-dom";

export class WebhookBanner extends React.Component {

  render() {

    return <div className="no-connection">
      Add a webhook to the repo to automatically upload new files
      <br/>
      URL: <strong>{"{server_url}"}/webhook</strong>
      <br/>
      Events: <strong>push, pull request</strong><br/>
      <Link to="/repos">Back to repositories</Link>
    </div>;

  }
}