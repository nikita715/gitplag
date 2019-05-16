import React from "react";
import axios from "axios";
import * as PROP from "../properties";
import {Link} from "react-router-dom";
import {RepoDto} from "./RepoDto";

export class EditRepo extends React.Component {

  state = {
    id: 0,
    name: "",
    git: "",
    mossParameters: "",
    jplagParameters: "",
    analysisMode: "",
    language: "JAVA",
    analyzer: "",
    filePatterns: ".+\\.java",
    autoCloningEnabled: true
  };

  constructor(props, context) {
    super(props, context);
    this.state.id = this.props.match.params.id;
    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleChange = this.handleChange.bind(this);
    this.selectLanguages = this.selectLanguages.bind(this);
    this.selectLanguageMoss = this.selectLanguageMoss.bind(this);
    this.selectLanguageJPlag = this.selectLanguageJPlag.bind(this);
    this.deleteRepository = this.deleteRepository.bind(this);
    this.handlePlatformChange = this.handlePlatformChange.bind(this);
  }

  deleteRepository(repoId) {
    axios.delete(PROP.serverUrl + "/api/repositories/" + repoId).then(() => {
      this.props.history.push("/repos")
    });
  }

  componentDidMount() {
    let id = this.state.id;
    if (id) {
      axios.get(PROP.serverUrl + "/api/repositories/" + id).then((response) => {
        let data = response.data;
        let git = data.gitService;
        let name = data.name;
        let mossParameters = data.mossParameters;
        let jplagParameters = data.jplagParameters;
        let analysisMode = data.analysisMode;
        let language = data.language;
        let analyzer = data.analyzer;
        let autoCloningEnabled = data.autoCloningEnabled;
        let filePatterns = data.filePatterns.join("\n");
        this.setState({
          id,
          name,
          git,
          analyzer,
          filePatterns,
          language,
          analysisMode,
          jplagParameters,
          mossParameters,
          autoCloningEnabled
        });
      });
    }
  }

  handleChange(event) {
    const target = event.target;
    const name = target.name;
    const value = target.type === "checkbox" ? target.checked : target.value;
    this.setState({
      [name]: value
    });
  }

  handleSubmit() {
    let dto = new RepoDto(this.state);
    axios.put((PROP.serverUrl + "/api/repositories/" + this.state.id), dto).then(() => this.props.history.push("/repos/" + this.state.id))
  }

  selectLanguageMoss() {
    return <div className="form-group">
      <legend className="col-form-label">Language</legend>
      <select className="form-control" name="language" value={this.state.language} onChange={this.handleChange}>
        <option value="C">C</option>
        <option value="CPP">C++</option>
        <option value="JAVA">Java</option>
        <option value="ML">Ml</option>
        <option value="PASCAL">Pascal</option>
        <option value="ADA">Ada</option>
        <option value="LISP">Lisp</option>
        <option value="SCHEME">Scheme</option>
        <option value="HASKELL">Haskell</option>
        <option value="FORTRAN">Fortran</option>
        <option value="ASCII">Ascii</option>
        <option value="VHDL">Vhdl</option>
        <option value="PERL">Perl</option>
        <option value="MATLAB">Matlab</option>
        <option value="PYTHON">Python</option>
        <option value="MIPS_ASSEMBLY">Mips</option>
        <option value="PROLOG">Prolog</option>
        <option value="SPICE">Spice</option>
        <option value="VISUAL_BASIC">Visual Basic</option>
        <option value="CSHARP">C#</option>
        <option value="MODULA2">Modula2</option>
        <option value="A8086_ASSEMBLY">A8086</option>
        <option value="JAVASCRIPT">JavaScript</option>
        <option value="PLSQL">Pl/SQL</option>
        <option value="VERILOG">Verilog</option>
        <option value="TCL">Tcl</option>
      </select></div>;
  }

  selectLanguageJPlag() {
    return <div className="form-group">
      <legend className="col-form-label">Language</legend>
      <select className="form-control" name="language" value={this.state.language} onChange={this.handleChange}>
        <option value="C">C</option>
        <option value="CPP">C++</option>
        <option value="JAVA">Java</option>
        <option value="SCHEME">Scheme</option>
        <option value="PYTHON">Python</option>
        <option value="ASCII">Text</option>
      </select>
    </div>;
  }

  selectLanguages() {
    if (this.state.analyzer === "MOSS") {
      return this.selectLanguageMoss();
    } else if (this.state.analyzer === "JPLAG") {
      return this.selectLanguageJPlag();
    } else {
      return "";
    }
  }

  handlePlatformChange(event) {
    var input = event.currentTarget.querySelector("input");
    this.setState({
      [input.name]: input.value
    });
  }

  render() {
    return (
      <div>
        <form onSubmit={this.handleSubmit} className="new-repo-form">
          <div className="form-group">
            <div className="row">
              <div className="col">
                <Link to={"/repos/" + this.state.id}>Back to analyzes</Link>
              </div>
              <div className="col">
                <div onClick={() => this.deleteRepository(this.state.id)}
                     className="btn btn-danger mb-0">Delete this repository
                </div>
              </div>
            </div>
          </div>
          <div className="form-group">
            <h3 className="text-break">Edit repository {this.state.name}</h3>
          </div>
          <div className="form-group">
            <legend className="col-form-label">Default analyzer</legend>
            <div className="btn-group btn-group-toggle" data-toggle="buttons">
              <label className={"btn btn-light " + (this.state.analyzer === "MOSS" ? "active" : "")} htmlFor="analyzer1"
                     onClick={this.handlePlatformChange}>
                <input type="radio" id="analyzer1" name="analyzer" value="MOSS"
                       checked={this.state.analyzer === "MOSS"}/>Moss</label>
              <label className={"btn btn-light " + (this.state.analyzer === "JPLAG" ? "active" : "")}
                     htmlFor="analyzer2" onClick={this.handlePlatformChange}>
                <input type="radio" id="analyzer2" name="analyzer" value="JPLAG"
                       checked={this.state.analyzer === "JPLAG"}/>JPlag</label>
            </div>
          </div>
          {this.selectLanguages()}
          <div className="form-group">
            <legend className="col-form-label">Default analysis mode</legend>
            <div className="btn-group btn-group-toggle" data-toggle="buttons" onChange={this.handleChange}>
              <label className={"btn btn-light " + (this.state.analysisMode === "LINK" ? "active" : "")} htmlFor="mode1"
                     onClick={this.handlePlatformChange} defaultChecked={true}>
                <input type="radio" id="mode1" name="analysisMode" value="LINK"
                       checked={this.state.analysisMode === "LINK"}/>Link</label>
              <label className={"btn btn-light " + (this.state.analysisMode === "PAIRS" ? "active" : "")}
                     htmlFor="mode2" onClick={this.handlePlatformChange}>
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
            <label htmlFor="moss-parameters">Default Moss parameters</label>
            <div><input className="form-control" type="text" autoComplete="off" id="moss-parameters"
                        name="mossParameters"
                        value={this.state.mossParameters} onChange={this.handleChange}/></div>
            <small id="emailHelp" className="form-text text-muted">See <a
              href="http://moss.stanford.edu/general/scripts/mossnet">moss docs</a></small>
          </div>
          <div className="form-group">
            <label htmlFor="jplag-parameters">Default JPlag parameters</label>
            <div><input className="form-control" type="text" autoComplete="off" id="jplag-parameters"
                        name="jplagParameters"
                        value={this.state.jplagParameters} onChange={this.handleChange}/></div>
            <small id="emailHelp" className="form-text text-muted">See <a
              href="https://github.com/jplag/jplag/blob/master/README.md">jplag docs</a></small>
          </div>
          <div className="form-group">
            <label htmlFor="filePatterns">File patterns</label>
            <textarea className="form-control" id="filePatterns" name="filePatterns" value={this.state.filePatterns}
                      onChange={this.handleChange} rows="3"/>
            <small id="emailHelp" className="form-text text-muted">Split regexps by new lines</small>
          </div>
          <div className="form-group">
            <div className="custom-control custom-switch">
              <input type="checkbox" className="custom-control-input" id="autoCloningEnabled" name="autoCloningEnabled"
                     onChange={this.handleChange} checked={this.state.autoCloningEnabled}/>
              <label className="custom-control-label" htmlFor="autoCloningEnabled">Enable auto-upload by webhook</label>
            </div>
          </div>
          <div>
            <button form="none" type="submit" className="btn btn-primary" onClick={this.handleSubmit}>Save</button>
          </div>
        </form>
      </div>
    );
  }
}
