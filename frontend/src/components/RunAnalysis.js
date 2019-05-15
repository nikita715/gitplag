import React from "react";
import axios from "axios";
import * as PROP from "../properties";
import {Link} from "react-router-dom";

export class RunAnalysis extends React.Component {

  state = {
    repoId: 0,
    analyzer: "",
    language: "",
    analysisMode: "",
    jplagParameters: "",
    mossParameters: "",
    parameters: "",
    updateFiles: false
  };

  constructor(props, context) {
    super(props, context);
    let repoId = props.match.params.id;
    axios.get(PROP.serverUrl + "/api/repositories/" + repoId).then((response) => {
      let data = response.data;
      let mossParameters = data.mossParameters;
      let jplagParameters = data.jplagParameters;
      let analysisMode = data.analysisMode;
      let language = data.language;
      let analyzer = data.analyzer;
      this.setState({
        repoId,
        analyzer,
        language,
        analysisMode,
        jplagParameters,
        mossParameters,
        parameters: analyzer === "MOSS" ? mossParameters : jplagParameters
      });
    });
    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleChange = this.handleChange.bind(this);
    this.handlePlatformChange = this.handlePlatformChange.bind(this);
  }

  handleChange(event) {
    const target = event.target;
    if (target.name === "parameters") {
      if (this.state.analyzer === "MOSS") {
        this.setState({
          parameters: target.value,
          mossParameters: target.value
        });
      } else {
        this.setState({
          parameters: target.value,
          jplagParameters: target.value
        });
      }
    } else if (target.type === "radio" && target.checked) {
      this.setState({
        [target.name]: target.value
      });
    } else if (target.type === "checkbox") {
      this.setState({
        [target.name]: target.checked
      });
    } else {
      const value = target.value;
      const name = target.name;

      this.setState({
        [name]: value
      });
    }
  }

  handleSubmit() {
    axios.post((PROP.serverUrl + "/api/repositories/" + this.state.repoId + "/analyzeWithNoResponse"), this.state).then(
      () => this.props.history.push("/repos/" + this.state.repoId)
    );
  }

  handlePlatformChange(event) {
    let input = event.currentTarget.querySelector("input");
    if (input.name === "analyzer") {
      let pars = input.value === "MOSS" ? this.state.mossParameters : this.state.jplagParameters;
      this.setState({parameters: pars, analyzer: input.value});
    }
    this.setState({
      [input.name]: input.value
    });
  }

  render() {
    return (<div>
      <form className="new-repo-form">
        <div className="form-group">
          <Link to={"/repos/" + this.state.repoId}>Back to analyzes</Link>
        </div>
        <div className="form-group">
          <h3>New analysis</h3>
        </div>
        <div className="form-group">
          <label htmlFor="branch">Branch name</label>
          <div><input type="text" id="branch" name="branch" onChange={this.handleChange} className="form-control"
                      autoComplete="off"/></div>
        </div>
        <div className="form-group">
          <legend className="col-form-label">Analyzer</legend>
          <div className="btn-group btn-group-toggle" data-toggle="buttons">
            <label className={"btn btn-light " + (this.state.analyzer === "MOSS" ? "active" : "")} htmlFor="analyzer1"
                   onClick={this.handlePlatformChange}>
              <input type="radio" id="analyzer1" name="analyzer" value="MOSS"
                     checked={this.state.analyzer === "MOSS"}/>Moss</label>
            <label className={"btn btn-light " + (this.state.analyzer === "JPLAG" ? "active" : "")} htmlFor="analyzer2"
                   onClick={this.handlePlatformChange}>
              <input type="radio" id="analyzer2" name="analyzer" value="JPLAG"
                     checked={this.state.analyzer === "JPLAG"}/>JPlag</label>
          </div>
        </div>
        <div className="form-group">
          <legend className="col-form-label">Analysis mode</legend>
          <div className="btn-group btn-group-toggle" data-toggle="buttons" onChange={this.handleChange}>
            <label className={"btn btn-light " + (this.state.analysisMode === "LINK" ? "active" : "")} htmlFor="mode1"
                   onClick={this.handlePlatformChange} defaultChecked={true}>
              <input type="radio" id="mode1" name="analysisMode" value="LINK"
                     checked={this.state.analysisMode === "LINK"}/>Link</label>
            <label className={"btn btn-light " + (this.state.analysisMode === "PAIRS" ? "active" : "")} htmlFor="mode2"
                   onClick={this.handlePlatformChange}>
              <input type="radio" id="mode2" name="analysisMode"
                     value="PAIRS"
                     checked={this.state.analysisMode === "PAIRS"}/>Pairs</label>
            <label className={"btn btn-light " + (this.state.analysisMode === "FULL" ? "active" : "")} htmlFor="mode3"
                   onClick={this.handlePlatformChange}>
              <input type="radio" id="mode3" name="analysisMode"
                     value="FULL"
                     checked={this.state.analysisMode === "FULL"}/>Full</label>
          </div>
        </div>
        <div className="form-group">
          <label htmlFor="parameters">Parameters</label>
          <div><input type="text" id="parameters" name="parameters" value={this.state.parameters}
                      onChange={this.handleChange} className="form-control" autoComplete="off"/></div>
        </div>
        <div className="form-group">
          <div className="custom-control custom-switch">
            <input type="checkbox" className="custom-control-input" id="updateFiles" name="updateFiles"
                   onChange={this.handleChange} checked={this.state.updateFiles}/>
            <label className="custom-control-label" htmlFor="updateFiles">Update files before the analysis</label>
          </div>
        </div>
        <div>
          <button form="none" type="submit" className="btn btn-primary" onClick={this.handleSubmit}>Submit</button>
        </div>
      </form>
    </div>);
  }
}