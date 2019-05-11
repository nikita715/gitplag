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
  }

  handleChange(event) {
    const target = event.target;
    if (target.name === "analyzer") {
      let pars = target.value === "MOSS" ? this.state.mossParameters : this.state.jplagParameters;
      this.setState({parameters: pars, analyzer: target.value});
    } else if (target.name === "parameters") {
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
      () => this.props.history.push("/repos/" + this.state.repoId + "/analyzes")
    );
  }

  render() {
    return (<div>
      <form className="new-repo-form">
        <Link to={"/repos/" + this.state.repoId + "/analyzes"}>Back to analyzes</Link>
        <h3>New analysis</h3>
        <label htmlFor="branch-name">Branch name</label>
        <div><input type="text" id="branch-name" name="branch" onChange={this.handleChange}/></div>
        <span>Analyzer</span>
        <div className="analyzer-select">
          <input type="radio" id="analyzer1" name="analyzer" value="MOSS" checked={this.state.analyzer === "MOSS"}
                 onChange={this.handleChange}/>
          <label htmlFor="analyzer1">Moss</label>

          <input type="radio" id="analyzer2" name="analyzer" value="JPLAG" checked={this.state.analyzer === "JPLAG"}
                 onChange={this.handleChange}/>
          <label htmlFor="analyzer2">JPlag</label>
        </div>
        <span>Analysis mode</span>
        <div className="mode-select">
          <input type="radio" id="mode1" name="analysisMode" value="LINK" checked={this.state.analysisMode === "LINK"}
                 onChange={this.handleChange}/>
          <label htmlFor="mode1">Link</label>

          <input type="radio" id="mode2" name="analysisMode" value="PAIRS"
                 checked={this.state.analysisMode === "PAIRS"} onChange={this.handleChange}/>
          <label htmlFor="mode2">Pairs</label>

          <input type="radio" id="mode3" name="analysisMode" value="FULL" checked={this.state.analysisMode === "FULL"}
                 onChange={this.handleChange}/>
          <label htmlFor="mode3">Full</label>
        </div>
        <label htmlFor="moss-parameters">Parameters</label>
        <div><input type="text" autoComplete="off" id="parameters" name="parameters"
                    value={this.state.parameters} onChange={this.handleChange}/></div>
        <label htmlFor="moss-parameters">Update files before the analysis</label>
        <input type="checkbox" id="updateFiles" name="updateFiles"
               value={this.state.updateFiles} onChange={this.handleChange}/>
        <div>
          <button form="none" onClick={this.handleSubmit}>Submit</button>
        </div>
      </form>
    </div>);
  }
}