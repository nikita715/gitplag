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
      response.data.map((repo) => {
        let id = repo.id;
        data.push(<li><a onClick={(e) => showRepo(id)}>{repo.name}</a>
          <button onClick={(e) => changeRepo(id)}>Edit</button>
        </li>);
      });
      this.setState({repos: data});
      window.history.pushState(null, "Repos", "/repos/");
    });
  }

  render() {
    return (
      <div>
        <button className="New-Repo-Button" onClick={showNewRepoForm}/>
        <ul className="Repo-List">{this.state.repos}</ul>
      </div>
    );
  }

}

export default App;

function showRepo(id) {
  render(<Repository id={id}/>, document.getElementById("root"));
}

function changeRepo(id) {
  render(<NewRepo id={id}/>, document.getElementById("root"));
}

function showRepositories() {
  render(<Repositories/>, document.getElementById("root"));
}

function showAnalysis(id) {
  render(<Analysis id={id}/>, document.getElementById("root"));
}

function showNewRepoForm() {
  render(<NewRepo/>, document.getElementById("root"));
}

class Repository extends React.Component {
  state = {
    repoId: 0,
    analyzes: []
  };

  constructor(props, context) {
    super(props, context);
    let repoId = props.id;
    axios.get(PROP.serverUrl + "/api/repositories/" + repoId).then((response) => {
      let data = [];
      response.data.map((analysis) => data.push(<li onClick={(e) => showAnalysis(analysis.id)}>{analysis.id}</li>));
      this.setState({analyzes: data, repoId: repoId});
      window.history.pushState(null, "Repo " + repoId, "/repos/" + this.state.repoId);
    });
  }

  render() {
    return (<div>
      <ul>{this.state.analyzes}</ul>
      <BackButton back={showRepositories}/></div>);
  }
}

class Analysis extends React.Component {

  state = {
    analysisId: 0,
    repoId: 0,
    results: []
  };

  constructor(props, context) {
    super(props, context);
    axios.get(PROP.serverUrl + "/api/analyzes/" + props.id).then((response) => {
      let data = [];
      response.data.analysisPairs.map((result) => data.push(<li>{result.id}</li>));
      this.setState({results: data, repoId: response.data.repository.id, analysisId: response.data.id});
    });
  }

  render() {
    return (<div>
      <button onClick={(e) => showGraph(this.state.analysisId)}>Graph</button>
      <ul>{this.state.results}</ul>
      <BackButton back={(e) => showRepo(this.state.repoId)}/></div>);
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
  render(<IFrame iframe={"http://83.243.70.130:8088/?graph_url="}
                 analysisId={analysisId}/>, document.getElementById("root"));
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
        <iframe src={"http://83.243.70.130:8088/?graph_url=http://localhost/graph/" + this.state.analysisId}/>
        <BackButton back={(e) => showAnalysis(this.state.analysisId)}/>
      </div>
    );
  }
}

class NewRepo extends React.Component {

  state = {
    id: 0,
    name: "",
    mossParameters: "",
    jplagParameters: "",
    mode: "",
    language: "",
    git: "",
    analyzer: "",
    filePatterns: ""
  };

  constructor(props, context) {
    super(props, context);
    if (props.id) {
      axios.get(PROP.serverUrl + "/api/repo/" + props.id).then((response) => {
        let data = response.data;
        let name = data.name;
        let mossParameters = data.mossParameters;
        let jplagParameters = data.jplagParameters;
        let mode = data.analysisMode;
        let language = data.language;
        let git = data.gitService;
        let analyzer = data.analyzer;
        let filePatterns = data.filePatterns.concat();
        this.setState({
          id: props.id, name: name, analyzer: analyzer, filePatterns: filePatterns,
          git: git, language: language, mode: mode, jplagParameters: jplagParameters, mossParameters: mossParameters
        });
      });
    }
  }

  handleSubmit(event) {
    alert('A name was submitted: ' + this.state.value);
  }

  render() {
    return (
      <div>
        <form action={this.handleSubmit} className="new-repo-form">
          <span>Git</span>
          <div className="git-select">
            <input type="radio" id="git1" name="git" value="github" checked={this.state.git === "GITHUB"}/>
            <label htmlFor="git1">Github</label>

            <input type="radio" id="git2" name="git" value="gitlab" checked={this.state.git === "GITLAB"}/>
            <label htmlFor="git2">Gitlab</label>

            <input type="radio" id="git3" name="git" value="bitbucket" checked={this.state.git === "BITBUCKET"}/>
            <label htmlFor="git3">Bitbucket</label>
          </div>
          <label htmlFor="repo-name">Repo name</label>
          <div><input type="text" autoComplete="off" id="repo-name" name="repo-name" value={this.state.name}/></div>
          <span>Language</span>
          <div>
            <select name="language" value={this.state.language}>
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
            <input type="radio" id="analyzer1" name="analyzer" value="moss" checked={this.state.analyzer === "MOSS"}/>
            <label htmlFor="git1">Moss</label>

            <input type="radio" id="analyzer2" name="analyzer" value="jplag" checked={this.state.analyzer === "JPLAG"}/>
            <label htmlFor="git2">JPlag</label>
          </div>
          <span>Analysis mode</span>
          <div className="mode-select">
            <input type="radio" id="mode1" name="mode" value="link" checked={this.state.mode === "LINK"}/>
            <label htmlFor="mode1">Link</label>

            <input type="radio" id="mode2" name="mode" value="pairs" checked={this.state.mode === "PAIRS"}/>
            <label htmlFor="mode2">Pairs</label>

            <input type="radio" id="mode3" name="mode" value="full" checked={this.state.mode === "FULL"}/>
            <label htmlFor="mode3">Full</label>
          </div>
          <label htmlFor="moss-parameters">Moss parameters</label>
          <div><input type="text" autoComplete="off" id="moss-parameters" name="moss-parameters"
                      value={this.state.mossParameters}/></div>
          <label htmlFor="jplag-parameters">JPlag parameters</label>
          <div><input type="text" autoComplete="off" id="jplag-parameters" name="jplag-parameters"
                      value={this.state.jplagParameters}/></div>
          <label htmlFor="file-patterns">File patterns</label>
          <div><textarea name="file-patterns" id="file-patterns" value={this.state.filePatterns}/></div>
          <div>
            <button onClick={this.handleSubmit}>Submit</button>
          </div>
        </form>
        <BackButton back={showRepositories}/>
      </div>
    );
  }
}