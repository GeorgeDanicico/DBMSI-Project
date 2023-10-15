/**
 * @type DataBE
 */
const DATA = {
  employees: [...require("./data-employees")],
  teams: [...require("./data-teams")],
  requests: [...require("./data-requests")],
  freeDays: [...require("./data-freeDays")],
  holidays: [...require("./data-holidays")],
  legalDaysOff: [...require("./data-legally-days-off")],
};

module.exports = DATA;
