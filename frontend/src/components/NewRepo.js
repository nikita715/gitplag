import React from "react";
import axios from "axios";
import * as PROP from "../properties";
import {Link} from "react-router-dom";
import {RepoDto} from "./RepoDto";

export class NewRepo extends React.Component {

  state = {
    id: 0,
    name: "",
    analysisMode: "FULL",
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
    this.handlePlatformChange = this.handlePlatformChange.bind(this);
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
    axios.post((PROP.serverUrl + "/api/repositories"), dto).then((response) => {
      if (response.data.length !== 0) {
        this.props.history.push("/webhook/" + this.state.git.toLowerCase())
      }
    });
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
    } else if (this.state.analyzer === "JPLAG" || this.state.analyzer === "COMBINED") {
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
            <Link to={"/repos"}>Back to repositories</Link>
          </div>
          <div className="form-group">
            <h4>New repository</h4>
          </div>
          <div className="form-group">
            <legend className="col-form-label">Git</legend>
            <div className="btn-group btn-group-toggle" data-toggle="buttons">
              <label className="btn btn-light" htmlFor="git1" onClick={this.handlePlatformChange}>
                <input type="radio" id="git1" name="git" value="GITHUB"
                       checked={this.state.git === "GITHUB"}/>Github</label>
              <label className="btn btn-light" htmlFor="git2" onClick={this.handlePlatformChange}>
                <input type="radio" id="git2" name="git" value="GITLAB"
                       checked={this.state.git === "GITLAB"}/>Gitlab</label>
              <label className="btn btn-light" htmlFor="git3" onClick={this.handlePlatformChange}>
                <input type="radio" id="git3" name="git" value="BITBUCKET" checked={this.state.git === "BITBUCKET"}/>Bitbucket</label>
            </div>
          </div>
          <div className="form-group">
            <label htmlFor="repo-name">Repo name</label>
            <div><input className="form-control" type="text" autoComplete="off" id="repo-name" name="name"
                        value={this.state.name}
                        onChange={this.handleChange} placeholder="E.g. myUser/myRepo"/></div>
          </div>
          <div className="form-group">
            <legend className="col-form-label">Default analyzer</legend>
            <div className="btn-group btn-group-toggle" data-toggle="buttons">
              <label className="btn btn-light" htmlFor="analyzer1" onClick={this.handlePlatformChange}>
                <input type="radio" id="analyzer1" name="analyzer" value="MOSS"
                       checked={this.state.analyzer === "MOSS"}/>Moss</label>
              <label className="btn btn-light" htmlFor="analyzer2" onClick={this.handlePlatformChange}>
                <input type="radio" id="analyzer2" name="analyzer" value="JPLAG"
                       checked={this.state.analyzer === "JPLAG"}/>JPlag</label>
              <label className="btn btn-light" htmlFor="analyzer3" onClick={this.handlePlatformChange}>
                <input type="radio" id="analyzer3" name="analyzer" value="COMBINED"
                       checked={this.state.analyzer === "COMBINED"}/>Combined</label>
            </div>
          </div>
          {this.selectLanguages()}
          <div className="form-group">
            <label htmlFor="filePatterns">File patterns</label>
            <textarea className="form-control" id="filePatterns" name="filePatterns" value={this.state.filePatterns}
                      onChange={this.handleChange} rows="3"/>
            <small id="emailHelp" className="form-text text-muted">Split regexps by new lines. Leave empty to download all files.</small>
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
