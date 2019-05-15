import React from "react";
import * as PROP from "../properties";
import {Link} from "react-router-dom";

export class NoConnection extends React.Component {

  render() {

    return <div className="jumbotron vertical-center">
      <div className="jumbotron my-auto mx-auto">
        <div className="align-content-center">
          <h6 className="text-center align-content-center">No connection to the server. <span
            className="font-weight-bold">{PROP.serverUrl}</span> is unavailable.</h6>
        </div>
        <div className="align-content-center text-center">
          <Link className="btn btn-primary mr-2 text-center align-content-center" role="button"
                to="/repos">Reload</Link>
        </div>
      </div>
    </div>;

  }
}