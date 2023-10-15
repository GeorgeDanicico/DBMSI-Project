module.exports = function (app) {
    const DATA = require("../data");

    app.get("/api/v1/requests/monthly", (req, res) => {
      const startDate = req.query["startDate"];
      const endDate = req.query["endDate"];
      const teamId = req.query["teamId"];
    
      if (!startDate || !endDate) {
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
    
      const requests = DATA.requests.filter((request) => {
        if (
          new Date(request.startDate) < new Date(startDate) &&
          new Date(request.endDate) > new Date(endDate)
        ) {
          return false;
        }
        if (request.status !== "APPROVED") {
          return false;
        }
        return true;
      });
      if (teamId) {
        employees = DATA.employees.filter((empl) => empl.teamDetails.id === teamId);
      } else {
        employees = DATA.employees;
      }
      requestsByTeamAndMonth = {
        items: employees.map((empl) => {
          return {
            firstName: empl.firstName,
            lastName: empl.lastName,
            noOfTakenDays: 10,
            noOfTakenMedicalDays: 10,
            leaveRequests: requests.filter(
              (request) => request.employeeDetails.id === empl.id
            ),
          };
        }),
      };
      res
        .status(200)
        .contentType("application/json")
        .send(JSON.stringify(requestsByTeamAndMonth));
    });

    app.get("/core/api/v1/misc/legally-days-off", (req, res) => {
      const years = req.query["years"];
      const periods = req.query["periods"];
      response = {
        items: DATA.legallyDaysOff.filter((legallyDayOff) => {
          if (years) {
            if (
              legallyDayOff.date.split("-")[0] >= years.split(",")[0] &&
              legallyDayOff.date.split("-")[0] < years.split(",")[1]
            ) {
              return true;
            }
            return false;
          }
          if (periods && !years) {
            if (
              legallyDayOff.date.substring(0, 7) >= periods.split(",")[0] &&
              legallyDayOff.date.substring(0, 7) < periods.split(",")[1]
            ) {
              return true;
            }
            return false;
          }
          return true;
        }),
      };
    
      res
        .status(200)
        .contentType("application/json")
        .send(JSON.stringify(response));
    });

  }




