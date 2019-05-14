import React from "react";
import axios from "axios";
import * as PROP from "../properties";
import {Link} from "react-router-dom";

export class EditRepo extends React.Component {

  state = {
    id: 0,
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
  }

  deleteRepository(repoId) {
    axios.delete(PROP.serverUrl + "/api/repositories/" + repoId).then(() => {
      this.componentDidMount()
    });
  }

  componentDidMount() {
    let id = this.state.id;
    if (id) {
      axios.get(PROP.serverUrl + "/api/repositories/" + id).then((response) => {
        let data = response.data;
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
          analyzer,
          filePatterns,
          git,
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
    let request = (this.state.id === undefined) ?
      axios.post((PROP.serverUrl + "/api/repositories"), dto) :
      axios.put((PROP.serverUrl + "/api/repositories/" + this.state.id), dto);
    request.then(() => this.props.history.push("/webhook"))
  }

  selectLanguageMoss() {
    return <div><span>Language</span>
      <select className="select-language" name="language" value={this.state.language} onChange={this.handleChange}>
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
    return <div><span>Language</span>
      <select className="select-language" name="language" value={this.state.language} onChange={this.handleChange}>
        <option value="C">C</option>
        <option value="CPP">C++</option>
        <option value="JAVA">Java</option>
        <option value="SCHEME">Scheme</option>
        <option value="PYTHON">Python</option>
        <option value="ASCII">Text</option>
      </select></div>;
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
          <Link to={"/repos"}>Back to repositories</Link><br/>
          <h3>New repository</h3>
          <span>Git</span>
          {this.state.id !== undefined ? "" : (<div>
            <div className="git-select">
              <input type="radio" id="git1" name="git" value="GITHUB" checked={this.state.git === "GITHUB"}
                     onChange={this.handleChange}/>
              <label htmlFor="git1">Github</label>

              <input type="radio" id="git2" name="git" value="GITLAB" checked={this.state.git === "GITLAB"}
                     onChange={this.handleChange}/>
              <label htmlFor="git2">Gitlab</label>

              <input type="radio" id="git3" name="git" value="BITBUCKET" checked={this.state.git === "BITBUCKET"}
                     onChange={this.handleChange}/>
              <label htmlFor="git3">Bitbucket</label>
            </div>
            <label htmlFor="repo-name">Repo name</label>
            <div><input type="text" autoComplete="off" id="repo-name" name="name" value={this.state.name}
                        onChange={this.handleChange}/></div>
          </div>)}
          <span>Analyzer</span>
          <div className="analyzer-select">
            <input type="radio" id="analyzer1" name="analyzer" value="MOSS" checked={this.state.analyzer === "MOSS"}
                   onChange={this.handleChange}/>
            <label htmlFor="analyzer1">Moss</label>

            <input type="radio" id="analyzer2" name="analyzer" value="JPLAG" checked={this.state.analyzer === "JPLAG"}
                   onChange={this.handleChange}/>
            <label htmlFor="analyzer2">JPlag</label>
          </div>
          {this.selectLanguages()}
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
          <label htmlFor="moss-parameters">Moss parameters</label>
          <div><input type="text" autoComplete="off" id="moss-parameters" name="mossParameters"
                      value={this.state.mossParameters} onChange={this.handleChange}/></div>
          <label htmlFor="jplag-parameters">JPlag parameters</label>
          <div><input type="text" autoComplete="off" id="jplag-parameters" name="jplagParameters"
                      value={this.state.jplagParameters} onChange={this.handleChange}/></div>
          <label htmlFor="file-patterns">File patterns (split by lines)</label>
          <div><textarea name="filePatterns" id="file-patterns" value={this.state.filePatterns}
                         onChange={this.handleChange}/></div>
          <label htmlFor="autoCloningEnabled">Enable auto-upload by webhook</label>
          <input type="checkbox" id="autoCloningEnabled" name="autoCloningEnabled"
                 checked={this.state.autoCloningEnabled} onChange={this.handleChange}/>
          <div>
            <button form="none" onClick={this.handleSubmit}>Submit</button>
          </div>
        </form>
      </div>
    );
  }
}
