import React from "react";
import axios from "axios";
import * as PROP from "../properties";
import {Link} from "react-router-dom";
import {RepoDto} from "./RepoDto";
import ToggleButtonGroup from "react-bootstrap/ToggleButtonGroup";
import ToggleButton from "react-bootstrap/ToggleButton";

export class NewRepo extends React.Component {

  state = {
    id: 0,
    name: "",
    mossParameters: "",
    jplagParameters: "",
    analysisMode: "",
    language: "JAVA",
    git: "",
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
  }

  handleChange(event) {
    console.log(event);
    const target = event.target;
    const name = target.name;
    const value = target.type === "checkbox" ? target.checked : target.value;
    this.setState({
      [name]: value
    });
  }

  handleSubmit() {
    let dto = new RepoDto(this.state);
    axios.post((PROP.serverUrl + "/api/repositories"), dto).then(() => this.props.history.push("/webhook"))
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

  render() {
    return (
      <div>
        <form onSubmit={this.handleSubmit} className="new-repo-form">
          <Link to={"/repos"}>Back to repositories</Link>
          <h4>New repository</h4>
          <div className="form-group">
            <legend className="col-form-label">Git</legend>
            <ToggleButtonGroup
              type="radio"
              name="git"
              value={this.state.git}>
              <ToggleButton name="git" value="GITHUB" onClick={this.handleChange}>Github</ToggleButton>
              <ToggleButton name="git" value="GITLAB" onClick={this.handleChange}>Gitlab</ToggleButton>
              <ToggleButton name="git" value="BITBUCKET" onClick={this.handleChange}>Bitbucket</ToggleButton>
            </ToggleButtonGroup>
          </div>
          <div className="form-group">
            <label htmlFor="repo-name">Repo name</label>
            <div><input className="form-control" type="text" autoComplete="off" id="repo-name" name="name"
                        value={this.state.name}
                        onChange={this.handleChange} placeholder="E.g. myUser/myRepo"/></div>
          </div>
          <div className="form-group">
            <legend className="col-form-label">Default analyser</legend>
            <ToggleButtonGroup
              type="radio"
              name="analyzer"
              value={this.state.analyzer}>
              <ToggleButton name="analyzer" value="MOSS" onClick={this.handleChange}>Moss</ToggleButton>
              <ToggleButton name="analyzer" value="JPLAG" onClick={this.handleChange}>JPlag</ToggleButton>
            </ToggleButtonGroup>
          </div>
          {this.selectLanguages()}
          <div className="form-group">
            <legend className="col-form-label">Default analysis mode</legend>
            <ToggleButtonGroup
              type="radio"
              name="analysisMode"
              value={this.state.analysisMode}>
              <ToggleButton name="analysisMode" value="LINK" onClick={this.handleChange}>Link</ToggleButton>
              <ToggleButton name="analysisMode" value="PAIRS" onClick={this.handleChange}>Pairs</ToggleButton>
              <ToggleButton name="analysisMode" value="FULL" onClick={this.handleChange}>Full</ToggleButton>
            </ToggleButtonGroup>
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
            <button form="none" type="submit" className="btn btn-primary" onClick={this.handleSubmit}>Create</button>
          </div>
        </form>
      </div>
    );
  }
}
