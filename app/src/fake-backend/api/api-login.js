module.exports = function (app) {
  const DATA = require("../data");

  app.post("/core/api/v1/login", (req, res) => {
    const reqBody = req.body;
    const employee = DATA.employees.find(
      (employee) => employee.username === reqBody.username
    );

    if (!reqBody.username || !reqBody.password || !employee) {
      res
        .status(400)
        .contentType("application/json")
        .send({
          errors: [
            {
              errorCode: "E0001C400",
              devMessage: "Something is not valid in the request",
            },
          ],
        });
    }

    const responseData = {
      accessToken: "AT",
      refreshToken: "RT",
      expiresIn: "3600",
      userDetails: {
        employeeId: employee.id,
        username: employee.username,
        team: employee.teamId,
        role: employee.role,
      },
    };

    res.status(200).contentType("application/json").send(responseData);
  });
};
