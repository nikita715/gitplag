import React from "react";
import ReactDOM from "react-dom";
import "./index.css";
import App from "./App";
import * as serviceWorker from "./serviceWorker";
import {Redirect} from "react-router-dom";
import {positions, Provider as AlertProvider} from "react-alert";
import Alert from "react-bs-notifier/es/alert";

const options = {
  timeout: 8000,
  position: positions.TOP_RIGHT
};

const AlertTemplate = ({style, options, message, close}) => (
  <div className="alert-wrapper"><Alert className="shadow"
                                        onDismiss={(e) => e.currentTarget.parentNode.remove()}>{message}</Alert></div>
);

Redirect("/repos");
ReactDOM.render(<AlertProvider
  template={AlertTemplate} {...options}><App/></AlertProvider>, document.getElementById("root"));

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
