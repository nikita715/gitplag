import React from "react";

export function Header(ol) {
  return (
    <div className="row">
      <div className="col col-md-auto"><h1>Gitplag</h1></div>
      <div className="col">
        <nav aria-label="breadcrumb">
          {ol}
        </nav>
      </div>
    </div>);
}