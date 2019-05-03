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
      response.data.map(repo => {
        let id = repo.id;
        data.push(<li onClick={e => showRepo(id)}>{repo.name}</li>);
      });
      this.setState({repos: data});
    });
  }

  render() {
    return (
      <div className="Repo-Name">
        <ul>{this.state.repos}</ul>
      </div>
    );
  }

}

export default App;

function showRepo(id) {
  render(<Repository id={id}/>, document.getElementById("root"));
}

function showRepositories() {
  render(<Repositories/>, document.getElementById("root"));
}

function showAnalysis(id) {
  render(<Analysis id={id}/>, document.getElementById("root"));
}

class Repository extends React.Component {
  state = {
    analyzes: []
  };

  constructor(props, context) {
    super(props, context);
    axios.get(PROP.serverUrl + "/api/repositories/" + props.id).then((response) => {
      let data = [];
      response.data.map(analysis => data.push(<li onClick={(e) => showAnalysis(analysis.id)}>{analysis.id}</li>));
      this.setState({analyzes: data});
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
      response.data.analysisPairs.map(result => data.push(<li>{result.id}</li>));
      this.setState({results: data, repoId: response.data.repository.id, analysisId: response.data.id});
    });
  }

  render() {
    return (<div>
      <button onClick={e => showGraph(this.state.analysisId)}>Graph</button>
      <ul>{this.state.results}</ul>
      <BackButton back={e => showRepo(this.state.repoId)}/></div>);
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
    this.state.analysisId = props.analysisId
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