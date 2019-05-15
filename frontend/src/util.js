import moment from "moment";

export function formatDate(dateString) {
  return moment(dateString).calendar()
}

export function formatDateSimple(dateString) {
  return dateString.replace("T", " ")
}

export function times(n) {
  var accum = Array(Math.max(0, n));
  for (var i = 1; i < n; i++) accum[i] = i;
  return accum;
}

export function capitalize(string) {
  return string.charAt(0).toUpperCase() + string.slice(1);
}