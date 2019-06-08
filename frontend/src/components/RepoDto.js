export class RepoDto {
  id = 0;
  name = "";
  mossParameters = "";
  jplagParameters = "";
  analysisMode = "";
  language = "";
  git = "";
  analyzer = "";
  autoCloningEnabled = false;
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
    this.autoCloningEnabled = state.autoCloningEnabled;
    if (state.filePatterns.length === 0) {
      this.filePatterns = [];
    } else {
      this.filePatterns = state.filePatterns.split("\n");
    }
  }
}