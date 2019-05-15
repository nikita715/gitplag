import React from "react";
import {Link} from "react-router-dom";

export function Header(ol) {
  return (
    <div className="row">
      <div className="col col-md-auto"><h1 className="app-name"><Link to="">Gitplag</Link></h1></div>
      <div className="col">
        <nav aria-label="breadcrumb">
          {ol}
        </nav>
      </div>
    </div>);
}