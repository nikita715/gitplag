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
                src={"http://83.243.70.130:8088/?graph_url=" + PROP.serverUrl + "/api/analyzes/" + this.state.analysisId + "/graph"}/>
        <BackButton back={"/analyzes/" + this.state.analysisId}/>
      </div>
    );
  }
}

class BackButton extends React.Component {

  back = {};

  constructor(props, context) {
    super(props, context);
    this.back = props.back;
  }

  render() {
    return (<button className="Back-Button"><Link to={this.back}>Close</Link></button>);
  }
}