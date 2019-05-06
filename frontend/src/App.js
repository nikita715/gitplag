import React from "react";
import "./App.css";
import axios from "axios";
import * as PROP from "./properties";
import {render} from "react-dom";

function App() {
  return (
    <div className="App">
      <Repositories/>
    </div>
  );
}

class Repositories extends React.Component {

  state = {
    repos: []
  };

  constructor(props, context) {
    super(props, context);
    axios.get(PROP.serverUrl + "/api/repositories").then((response) => {
      let data = [];
      response.data.map((repo) =>
        data.push(<li><span onClick={() => showRepo(repo.id)}>{repo.name}</span>
          <button onClick={() => changeRepo(repo.id)}>Edit</button>
        </li>)
      );
      this.setState({repos: data});
      window.history.pushState(null, "Repos", "/repos/");
    });
  }

  render() {
    return (
      <div>
        <ul className="Repo-List">
          <li>
            <button className="New-Repo-Button" onClick={showNewRepoForm}>Create new repo</button>
          </li>
          {this.state.repos}</ul>
      </div>
    );
  }

}

class RepositoryAnalyzes extends React.Component {
  state = {
    repoId: 0,
    analyzes: []
  };

  constructor(props, context) {
    super(props, context);
    let repoId = props.id;
    axios.get(PROP.serverUrl + "/api/repositories/" + repoId + "/analyzes").then((response) => {
      let analyzes = [];
      response.data.map((analysis) => analyzes.push(<li onClick={() => showAnalysis(analysis.id)}>{analysis.id}</li>));
      this.setState({analyzes, repoId});
      window.history.pushState(null, "Repo " + repoId, "/repos/" + this.state.repoId);
    });
  }

  render() {
    return (<div>
      <ul>
        <li>
          <button onClick={() => showRunAnalysisForm(this.state.repoId)}>Run analysis</button>
        </li>
        {this.state.analyzes}</ul>
      <BackButton back={showRepositories}/></div>);
  }
}

class AnalysisResult extends React.Component {

  state = {
    analysisId: 0,
    repoId: 0,
    results: [],
    resultLink: ""
  };

  constructor(props, context) {
    super(props, context);
    axios.get(PROP.serverUrl + "/api/analyzes/" + props.id).then((response) => {
      let data = [];
      response.data.analysisPairs.map((result) => data.push(<li>{result.id}</li>));
      this.setState({
        results: data, repoId: response.data.repository.id, analysisId: response.data.id,
        resultLink: response.data.resultLink
      });
    });
  }

  render() {
    return (<div>
      <button onClick={() => showGraph(this.state.analysisId)}>Graph</button>
      <span>Analyzer result link: {this.state.resultLink}</span>
      <ul>{this.state.results}</ul>
      <BackButton back={() => showRepo(this.state.repoId)}/></div>);
  }
}

class BackButton extends React.Component {

  back = {};

  constructor(props, context) {
    super(props, context);
    this.back = props.back;
  }

  render() {
    return (<button className="Back-Button" onClick={this.back}>Back</button>);
  }
}

function showGraph(analysisId) {
  render(<IFrame analysisId={analysisId}/>, document.getElementById("root"));
}

class IFrame extends React.Component {
  state = {
    analysisId: 0
  };

  constructor(props, context) {
    super(props, context);
    this.state.analysisId = props.analysisId;
  }

  render() {
    return (
      <div>
        <iframe title="graph"
                src={"http://83.243.70.130:8088/?graph_url=http://localhost/graph/" + this.state.analysisId}/>
        <BackButton back={() => showAnalysis(this.state.analysisId)}/>
      </div>
    );
  }
}

class RepoDto {
  id = 0;
  name = "";
  mossParameters = "";
  jplagParameters = "";
  analysisMode = "";
  language = "";
  git = "";
  analyzer = "";
  filePatterns = [];

  constructor(state) {
    this.id = state.id;
    this.name = state.name;
    this.mossParameters = state.mossParameters;
    this.jplagParameters = state.jplagParameters;
    this.analysisMode = state.analysisMode;
    this.language = state.language;
    this.git = state.git;
    this.analyzer = state.analyzer;
    if (state.filePatterns.length === 0) {
      this.filePatterns = [];
    } else {
      this.filePatterns = state.filePatterns.split("\n");
    }
  }
}

class NewRepo extends React.Component {

  state = {
    id: 0,
    name: "",
    mossParameters: "",
    jplagParameters: "",
    analysisMode: "",
    language: "",
    git: "",
    analyzer: "",
    filePatterns: ""
  };

  constructor(props, context) {
    super(props, context);
    if (props.id) {
      axios.get(PROP.serverUrl + "/api/repositories/" + props.id).then((response) => {
        let data = response.data;
        let name = data.name;
        let mossParameters = data.mossParameters;
        let jplagParameters = data.jplagParameters;
        let analysisMode = data.analysisMode;
        let language = data.language;
        let git = data.gitService;
        let analyzer = data.analyzer;
        let filePatterns = data.filePatterns.join("\n");
        this.setState({
          id: props.id,
          name,
          analyzer,
          filePatterns,
          git,
          language,
          analysisMode,
          jplagParameters,
          mossParameters
        });
      });
    }
    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleChange = this.handleChange.bind(this);
  }

  handleChange(event) {
    const target = event.target;
    if (target.type === "radio" && target.checked) {
      this.setState({
        [target.name]: target.value
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
    let dto = new RepoDto(this.state);
    axios.put((PROP.serverUrl + "/api/repositories/" + this.state.id), dto);
    showRepositories();
  }

  render() {
    return (
      <div>
        <form onSubmit={this.handleSubmit} className="new-repo-form">
          <span>Git</span>
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
          <span>Language</span>
          <div>
            <select name="language" value={this.state.language} onChange={this.handleChange}>
              <option value="JAVA">Java</option>
              <option value="C">C</option>
              <option value="CPP">C++</option>
              <option value="PYTHON">Python</option>
              <option value="HASKELL">Haskell</option>
              <option value="PASCAL">Pascal</option>
            </select>
          </div>
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
          <label htmlFor="moss-parameters">Moss parameters</label>
          <div><input type="text" autoComplete="off" id="moss-parameters" name="mossParameters"
                      value={this.state.mossParameters} onChange={this.handleChange}/></div>
          <label htmlFor="jplag-parameters">JPlag parameters</label>
          <div><input type="text" autoComplete="off" id="jplag-parameters" name="jplagParameters"
                      value={this.state.jplagParameters} onChange={this.handleChange}/></div>
          <label htmlFor="file-patterns">File patterns</label>
          <div><textarea name="filePatterns" id="file-patterns" value={this.state.filePatterns}
                         onChange={this.handleChange}/></div>
          <div>
            <button form="none" onClick={this.handleSubmit}>Submit</button>
          </div>
        </form>
        <BackButton back={showRepositories}/>
      </div>
    );
  }
}

class RunAnalysis extends React.Component {

  state = {
    repoId: 0,
    analyzer: "",
    language: "",
    analysisMode: "",
    jplagParameters: "",
    mossParameters: "",
    parameters: ""
  };

  constructor(props, context) {
    super(props, context);
    let repoId = props.repoId;
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
    } else {
      const value = target.value;
      const name = target.name;

      this.setState({
        [name]: value
      });
    }
  }

  handleSubmit() {
    axios.post((PROP.serverUrl + "/api/repositories/" + this.state.repoId + "/analyzeWithNoResponse"), this.state);
    showRepo(this.state.repoId);
  }

  render() {
    return (<div>
      <label htmlFor="branch-name">Branch name</label>
      <div><input id="branch-name" name="branch" onChange={this.handleChange}/></div>
      <span>Language</span>
      <div>
        <select name="language" value={this.state.language} onChange={this.handleChange}>
          <option value="JAVA">Java</option>
          <option value="C">C</option>
          <option value="CPP">C++</option>
          <option value="PYTHON">Python</option>
          <option value="HASKELL">Haskell</option>
          <option value="PASCAL">Pascal</option>
        </select>
      </div>
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
      <label htmlFor="response-url">Response url</label>
      <div><input id="response-url" name="responseUrl" onChange={this.handleChange}/>
      </div>
      <div>
        <button type="submit" onClick={this.handleSubmit}>Submit</button>
      </div>
      <BackButton back={() => showRepo(this.state.repoId)}/>
    </div>);
  }
}

function showRepo(id) {
  render(<RepositoryAnalyzes id={id}/>, document.getElementById("root"));
}

function changeRepo(id) {
  render(<NewRepo id={id}/>, document.getElementById("root"));
}

function showRepositories() {
  render(<Repositories/>, document.getElementById("root"));
}

function showAnalysis(id) {
  render(<AnalysisResult id={id}/>, document.getElementById("root"));
}

function showNewRepoForm() {
  render(<NewRepo/>, document.getElementById("root"));
}

function showRunAnalysisForm(id) {
  render(<RunAnalysis repoId={id}/>, document.getElementById("root"));
}

export default App;
