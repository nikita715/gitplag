import React from "react";
import "./App.css";
import {BrowserRouter, Route} from 'react-router-dom';
import {Repositories} from "./components/Repositories";
import {RepositoryAnalyzes} from "./components/RepositoryAnalyzes";
import {RunAnalysis} from "./components/RunAnalysis";
import {IFrameGraph} from "./components/IFrameGraph";
import {AnalysisResult} from "./components/AnalysisResult";
import {AnalysisResultPair} from "./components/AnalysisResultPair";
import {NewRepo} from "./components/NewRepo";

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

export default App;
