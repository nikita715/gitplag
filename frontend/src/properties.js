require("dotenv").config();
export const serverUrl = process.env.REACT_APP_BACKEND_URL ? process.env.REACT_APP_BACKEND_URL
  : "http://" + window.location.hostname + ":8090";