import React from "react";
import * as PROP from "../properties";
import {Link} from "react-router-dom";

export class IFrameGraph extends React.Component {
  state = {
    analysisId: 0
  };

  constructor(props, context) {
    super(props, context);
    this.state.analysisId = props.match.params.analysisId;
  }

  render() {
    return (
      <div onClick={this.handleClick}>
        <iframe title="graph" onClick={this.handleClick}
                src={PROP.graphUrl + "/?graph_url=" + PROP.serverUrl + "/api/analyzes/" + this.state.analysisId + "/graph"}/>
        <Link className="badge badge-primary iframe-back-button" role="button"
              to={"/analyzes/" + this.state.analysisId}>Back</Link>
      </div>
    );
  }
}
