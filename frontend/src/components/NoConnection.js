import React from "react";
import * as PROP from "../properties";

export class NoConnection extends React.Component {

  constructor(props, context) {
    super(props, context);
  }

  render() {

    return <div className="no-connection">No connection to the server. {PROP.serverUrl} is unavailable.</div>

  }
}