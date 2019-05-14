import React from "react";
import {Link} from "react-router-dom";

export class WebhookBanner extends React.Component {

  render() {

    return <div className="no-connection">
      Add a webhook to the repo to automatically upload new files

      URL: <strong>{"{server_url}"}/webhook</strong>

      Events: <strong>push, pull request</strong>
      <Link to="/repos">Back to repositories</Link>
    </div>;

  }
}