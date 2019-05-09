import React from "react";
import "./App.css";
import axios from "axios";
import * as PROP from "./properties";
import {BrowserRouter, Link, Redirect, Route} from 'react-router-dom';
import moment from "moment";

function App() {
  return (
    <BrowserRouter>
      <Route exact path="/" component={Repositories}/>
      <Route exact path="/repos" component={Repositories}/>
      <Route exact path="/repos/:id/analyzes" component={RepositoryAnalyzes}/>
      <Route exact path="/repos/:id/analyze" component={RunAnalysis}/>
      <Route exact path="/repos/:id/edit" component={NewRepo}/>
      <Route exact path="/repos/new" component={NewRepo}/>
      <Route exact path="/analyzes/:analysisId/graph" component={IFrameGraph}/>
      <Route exact path="/analyzes/:id" component={AnalysisResult}/>
      <Route exact path="/analyzes/:analysisId/pairs/:pairId" component={AnalysisResultPair}/>
    </BrowserRouter>
  );
}

class Repositories extends React.Component {

  state = {
    repos: []
  };

  constructor(props, context) {
    super(props, context);
    window.history.pushState(null, "Repos", "/repos/");
    axios.get(PROP.serverUrl + "/api/repositories").then((response) => {
      let data = [];
      response.data.map((repo) =>
        data.push(<tr>
          <td><Link to={"/repos/" + repo.id + "/analyzes"}>{repo.id}</Link></td>
          <td>{repo.name}</td>
          <td>{repo.gitService.toLowerCase()}</td>
          <td><Link to={"/repos/" + repo.id + "/edit"}>Edit</Link>
          </td>
        </tr>)
      );
      this.setState({repos: data});
    });
  }

  render() {
    return (
      <div className="Repo-List">
        <h3>Git repositories</h3>
        <Link to="/repos/new">Add repository</Link><br/>
        <table>
          <tr>
            <th>Id</th>
            <th>Name</th>
            <th>Git</th>
          </tr>
          {this.state.repos}</table>
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
    this.state.repoId = props.match.params.id;
  }

  componentDidMount() {
    axios.get(PROP.serverUrl + "/api/repositories/" + this.state.repoId + "/analyzes").then((response) => {
      let analyzes = [];
      response.data.map((analysis) => analyzes.push(<tr>
        <td><Link to={"/analyzes/" + analysis.id}>{analysis.id}</Link></td>
        <td>{analysis.branch}</td>
        <td>{formatDate(analysis.executionDate)}</td>
      </tr>));
      this.setState({analyzes});
    });
  }

  render() {
    return (
      <div className="Repo-List">
        <Link to={"/repos"}>Back to repositories</Link>
        <h3>Analyzes of repo #{this.state.repoId}</h3>
        <Link to={"/repos/" + this.state.repoId + "/analyze"}>Run new analysis</Link>
        <table>
          <tr>
            <th>Id</th>
            <th>Branch</th>
            <th>Date</th>
          </tr>
          {this.state.analyzes}</table>
      </div>);
  }
}

function formatDate(dateString) {
  return moment(dateString).calendar();
}

class AnalysisResult extends React.Component {

  state = {
    analysisId: 0,
    repoId: 0,
    results: [],
    resultLink: "",
    sortedByName: ""
  };

  constructor(props, context) {
    super(props, context);
    let id = props.match.params.id;
    axios.get(PROP.serverUrl + "/api/analyzes/" + id).then((response) => {
      let data = [];
      response.data.analysisPairs.map((result) => data.push({
        id: result.id,
        student1: result.student1,
        student2: result.student2,
        percentage: result.percentage
      }));
      this.setState({
        results: data, repoId: response.data.repository.id, analysisId: response.data.id,
        resultLink: response.data.resultLink
      });
    });
    this.handleChange = this.handleChange.bind(this)
  }

  handleChange(event) {
    this.setState({
      [event.target.name]: event.target.value.toLowerCase()
    });
    console.log(this.state.sortedByName);
  }

  render() {
    return (<div className="Repo-List">
      <Link to={"/repos/" + this.state.repoId + "/analyzes"}>Back to analyzes</Link>
      <h3>Analysis result #{this.state.analysisId}</h3>
      <a href={this.state.resultLink}>Source analysis</a><br/>
      <Link to={"/analyzes/" + this.state.analysisId + "/graph"}>Graph</Link><br/>
      <label>Find by student name: </label><input name="sortedByName" onChange={this.handleChange}/>
      <table>
        <tr>
          <th>Id</th>
          <th>Student 1</th>
          <th>Student 2</th>
          <th>Percentage</th>
        </tr>
        {this.state.results.filter(it => it.student1.toLowerCase().includes(this.state.sortedByName) || it.student2.toLowerCase().includes(this.state.sortedByName)).map(
          (result) =>
            <tr>
              <td>
                <Link to={"/analyzes/" + this.state.analysisId + "/pairs/" + result.id}>{result.id}</Link>
              </td>
              <td>{result.student1}</td>
              <td>{result.student2}</td>
              <td>{result.percentage}</td>
            </tr>)
        }
      </table>
    </div>);
  }
}

class BackButton extends React.Component {

  back = {};

  constructor(props, context) {
    super(props, context);
    this.back = props.back;
  }

  render() {
    return (<button className="Back-Button"><Link to={this.back}>Back</Link></button>);
  }
}

class IFrameGraph extends React.Component {
  state = {
    analysisId: 0
  };

  constructor(props, context) {
    super(props, context);
    this.state.analysisId = props.match.params.analysisId;
  }

  render() {
    return (
      <div onClick={this.handleClick}>
        <iframe title="graph" onClick={this.handleClick}
                src={"http://83.243.70.130:8088/?graph_url=" + PROP.serverUrl + "/api/analyzes/" + this.state.analysisId + "/graph"}/>
        <BackButton back={"/analyzes/" + this.state.analysisId}/>
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
    language: "JAVA",
    git: "",
    analyzer: "",
    filePatterns: ""
  };

  constructor(props, context) {
    super(props, context);
    let id = props.match.params.id;
    if (id) {
      axios.get(PROP.serverUrl + "/api/repositories/" + id).then((response) => {
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
          id,
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
    let request = (this.state.id === 0) ?
      axios.post((PROP.serverUrl + "/api/repositories"), dto) :
      axios.put((PROP.serverUrl + "/api/repositories/" + this.state.id), dto);
    request.then(() => Redirect("/repos"))
  }

  render() {
    return (
      <div>
        <form onSubmit={this.handleSubmit} className="new-repo-form">
          <Link to={"/repos"}>Back to repositories</Link><br/>
          <h3>New repository</h3>
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
          <label htmlFor="file-patterns">File patterns (split by lines)</label>
          <div><textarea name="filePatterns" id="file-patterns" value={this.state.filePatterns}
                         onChange={this.handleChange}/></div>
          <div>
            <button form="none" onClick={this.handleSubmit}>Submit</button>
          </div>
        </form>
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
      () => Redirect("/repos/" + this.state.repoId)
    );
  }

  render() {
    return (<div>
      <form className="new-repo-form">
        <Link to={"/repos/" + this.state.repoId + "/analyzes"}>Back to analyzes</Link>
        <h3>New analysis</h3>
        <label htmlFor="branch-name">Branch name</label>
        <div><input type="text" id="branch-name" name="branch" onChange={this.handleChange}/></div>
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
        <div><input type="text" id="response-url" name="responseUrl" onChange={this.handleChange}/>
        </div>
        <label htmlFor="moss-parameters">Update files before the analysis</label>
        <input type="checkbox" id="updateFiles" name="updateFiles"
               value={this.state.updateFiles} onChange={this.handleChange}/>
        <div>
          <button type="submit" onClick={this.handleSubmit}>Submit</button>
        </div>
      </form>
    </div>);
  }
}

class AnalysisResultPair extends React.Component {
  state = {
    analysisId: 0,
    pairId: 0,
    files1: [],
    files2: [],
    leftName: "",
    rightName: ""
  };

  constructor(props, context) {
    super(props, context);
    let analysisId = props.match.params.analysisId;
    let pairId = props.match.params.pairId;
    this.state.analysisId = analysisId;
    this.state.pairId = pairId;
    axios.get(PROP.serverUrl + "/api/analyzes/" + analysisId + "/pairs/" + pairId).then((response) => {
      let files1 = response.data.files1.map((file) => ({fileName: file.name, lines: file.content}));
      let files2 = response.data.files2.map((file) => ({fileName: file.name, lines: file.content}));
      this.setState({
        files1: files1,
        files2: files2,
        leftName: response.data.pair.student1,
        rightName: response.data.pair.student2
      });
    });
  }

  render() {
    return (
      <div className="compare-wrapper">
        {/*<Link to={"/analyzes/" + this.state.analysisId}>Back to analysis</Link>*/}
        <pre className="compare">
          {this.state.files1.map((it) => it.lines.map(it2 => <div>{it2 === "" ? " " : it2}</div>))}
        </pre>
        <pre className="compare">
          {this.state.files2.map((it) => it.lines.map(it2 => <div>{it2 === "" ? " " : it2}</div>))}
        </pre>
      </div>
    );
  }
}

export default App;
