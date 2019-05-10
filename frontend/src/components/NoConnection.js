import React from "react";
import * as PROP from "../properties";
import {Link} from "react-router-dom";

export class NoConnection extends React.Component {

  render() {

    return <div className="no-connection">
      No connection to the server. {PROP.serverUrl} is unavailable.
      <br/>
      <Link to="/repos">Reload</Link>
    </div>;

  }
}