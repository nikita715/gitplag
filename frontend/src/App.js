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
import {NoConnection} from "./components/NoConnection";
import {useAlert} from "react-alert";
import * as PROP from "./properties";
import SockJsClient from "react-stomp";

const App = () => {
  const alert = useAlert();
  return (
    <div>
      <BrowserRouter>
        <Route exact path="/" component={Repositories}/>
        <Route exact path="/repos" component={Repositories}/>
        <Route exact path="/repos/:id/analyzes" component={RepositoryAnalyzes}/>
        <Route exact path="/repos/:id/analyze" component={RunAnalysis}/>
        <Route exact path="/repos/:id/edit" component={NewRepo}/>
        <Route exact path="/repos/new" component={NewRepo}/>
        <Route exact path="/analyzes/:analysisId/graph" component={IFrameGraph}/>
        <Route exact path="/analyzes/:id" component={AnalysisResult}/>
        <Route exact path="/error" component={NoConnection}/>
        <Route exact path="/analyzes/:analysisId/pairs/:pairId" component={AnalysisResultPair}/>
      </BrowserRouter>
      <SockJsClient
        url={PROP.serverUrl + "/ws"}
        topics={["/queue/notify"]}
        onMessage={(message) => alert.show(message)}/>
    </div>
  );
};

export default App;
