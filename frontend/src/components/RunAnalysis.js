import React from "react";
import axios from "axios";
import * as PROP from "../properties";

export class RunAnalysis extends React.Component {

  state = {
    repoId: 0,
    analyzer: "",
    language: "",
    maxResultSize: null,
    minResultPercentage: 0,
    additionalRepositories: [],
    analysisMode: "FULL",
    updateFiles: false
  };

  constructor(props, context) {
    super(props, context);
    let repoId = props.id;
    axios.get(PROP.serverUrl + "/api/repositories/" + repoId).then((response) => {
      let data = response.data;
      let analysisMode = data.analysisMode;
      let language = data.language;
      let analyzer = data.analyzer;
      this.setState({
        repoId,
        analyzer,
        language,
        analysisMode
      });
    });
    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleChange = this.handleChange.bind(this);
    this.handlePlatformChange = this.handlePlatformChange.bind(this);
  }

  handleChange(event) {
    const target = event.target;
    if (target.type === "radio" && target.checked) {
      this.setState({
        [target.name]: target.value
      });
    } else if (target.type === "checkbox") {
      this.setState({
        [target.name]: target.checked
      });
    } else if (target.name === "additionalRepositories") {
      const name = target.name;
      this.setState({
        [name]: target.value.replace(/[^0-9,]+/g, "").split(",")
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
    axios.post((PROP.serverUrl + "/api/repositories/" + this.state.repoId + "/analyze/detached"), this.state)
      .then((response) => {
        if (response.data === true) {
          document.getElementById("newAnalysisModalWindow").click();
        }
      });
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
    return (
      <div className="modal fade" id="newAnalysisModalWindow" tabindex="-1" role="dialog"
           aria-labelledby="exampleModalLongTitle"
           aria-hidden="true">
        <div className="modal-dialog" role="document">
          <div className="modal-content">
            <div className="modal-header">
              <h3 className="modal-title">New analysis</h3>
            </div>
            <div className="modal-body">
              <form className="new-repo-form">
                <div className="form-group">
                  <label htmlFor="branch">Branch name</label>
                  <div><input type="text" id="branch" name="branch" onChange={this.handleChange}
                              className="form-control"
                              autoComplete="off"/></div>
                </div>
                <div className="form-group">
                  <legend className="col-form-label">Analyzer</legend>
                  <div className="btn-group btn-group-toggle" data-toggle="buttons">
                    <label className={"btn btn-light " + (this.state.analyzer === "MOSS" ? "active" : "")}
                           htmlFor="analyzer1"
                           onClick={this.handlePlatformChange}>
                      <input type="radio" id="analyzer1" name="analyzer" value="MOSS"
                             checked={this.state.analyzer === "MOSS"}/>Moss</label>
                    <label className={"btn btn-light " + (this.state.analyzer === "JPLAG" ? "active" : "")}
                           htmlFor="analyzer2"
                           onClick={this.handlePlatformChange}>
                      <input type="radio" id="analyzer2" name="analyzer" value="JPLAG"
                             checked={this.state.analyzer === "JPLAG"}/>JPlag</label>
                    <label className={"btn btn-light " + (this.state.analyzer === "COMBINED" ? "active" : "")}
                           htmlFor="analyzer3"
                           onClick={this.handlePlatformChange}>
                      <input type="radio" id="analyzer3" name="analyzer" value="COMBINED"
                             checked={this.state.analyzer === "COMBINED"}/>Combined</label>
                  </div>
                </div>
                <div className="form-group row">
                  <div className="col">
                    <label htmlFor="example-number-input" className="">Count of results</label>
                    <div className="">
                      <input className="form-control" type="number" onChange={this.handleChange} placeholder="All"
                             name="maxResultSize" value={this.state.maxResultSize}/>
                    </div>
                  </div>
                  <div className="col">
                    <label htmlFor="example-number-input" className="">Minimal percentage</label>
                    <div className="">
                      <input className="form-control" type="number" onChange={this.handleChange}
                             name="minResultPercentage" value={this.state.minResultPercentage}/>
                    </div>
                  </div>
                </div>
                <div className="form-group row">
                  <div className="col">
                    <label htmlFor="example-number-input" className="">Additional repositories</label>
                    <div className="">
                      <input className="form-control" type="text" onChange={this.handleChange} placeholder="Comma-separated ids"
                             name="additionalRepositories" value={this.state.additionalRepositories}/>
                    </div>
                  </div>
                </div>
                <div className="form-group">
                  <div className="custom-control custom-switch">
                    <input type="checkbox" className="custom-control-input" id="updateFiles" name="updateFiles"
                           onChange={this.handleChange} checked={this.state.updateFiles}/>
                    <label className="custom-control-label" htmlFor="updateFiles">Update files before the
                      analysis</label>
                  </div>
                </div>
                <div className="form-group mb-4">
                  <button form="none" type="submit" className="btn btn-primary"
                          onClick={this.handleSubmit}>Submit
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    );
  }
}