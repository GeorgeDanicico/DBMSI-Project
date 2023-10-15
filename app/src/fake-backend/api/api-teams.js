module.exports = function (app) {
  const DATA = require("../data");

  app.get("/core/api/v1/teams", (req, res) => {
    const teamId = req.query["teamId"];

    if (teamId) {
      teams = DATA.teams.filter((team) => team.id === teamId);
    } else {
      teams = DATA.teams;
    }

    res
      .status(200)
      .contentType("application/json")
      .send(JSON.stringify({ items: teams }));
  });
};
