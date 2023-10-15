module.exports = function (app) {
  const DATA = require("../data");

  app.get("/core/api/v1/requests", (req, res) => {
    const startDate = req.query["startDate"];
    const endDate = req.query["endDate"];
    const searchText = req.query["search"];
    const status = req.query["status"];
    const requests = DATA.requests.filter((request) => {
      if (
        startDate &&
        new Date(request.startDate).getTime() < new Date(startDate).getTime()
      ) {
        return false;
      }
      if (endDate && new Date(request.endDate) > new Date(endDate)) {
        return false;
      }
      if (searchText) {
        const employee = DATA.employees.find(
          (empl) => empl.id === request.employeeId
        );
        if (
          !employee ||
          !(
            employee.firstName.startsWith(searchText) ||
            employee.lastName.startsWith(searchText)
          )
        ) {
          return false;
        }
      }
      if (status && request.status !== status) {
        return false;
      }
      return true;
    });
    res
      .status(200)
      .contentType("application/json")
      .send(JSON.stringify({ items: requests }));
  });

  app.get("/core/public/api/requests/approved", (req, res) => {
    const startDate = req.query["startDate"];
    const endDate = req.query["endDate"];
    const teamId = req.query["teamId"];

    const requests = DATA.requests.filter((request) => {
      if (startDate && new Date(request.startDate) < new Date(startDate)) {
        return false;
      }
      if (endDate && new Date(request.endDate) > new Date(endDate)) {
        return false;
      }
      if (request.status !== "APPROVED") {
        return false;
      }
      if (teamId) {
        const employee = DATA.employees.find(
          (empl) => empl.id === request.employeeId
        );
        if (employee.teamId !== teamId) {
          return false;
        }
      }
      return true;
    });
    res
      .status(200)
      .contentType("application/json")
      .send(JSON.stringify(requests));
  });

  app.patch(
    "/core/api/v1/employees/:userId/requests/:requestId",
    (req, res) => {
      const reqBody = req.body;
      const requestId = req.params["requestId"];
      const requestStatus = reqBody["reviewType"];
      const rejectionReason = reqBody["rejectionReason"];

      if (
        !reqBody ||
        !requestStatus ||
        (requestStatus !== "APPROVED" && requestStatus !== "REJECTED")
      ) {
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
      } else {
        const request = DATA.requests.find((r) => r.id === requestId);
        if (request) {
          request.status = requestStatus;
          if (rejectionReason) {
            request.rejectionReason = rejectionReason;
          }
          res.status(200).contentType("application/json").send(request);
        } else {
          res
            .status(400)
            .contentType("application/json")
            .send({
              errors: [
                {
                  errorCode: "E0001C400",
                  devMessage: "The request does not exist!",
                },
              ],
            });
        }
      }
    }
  );

  app.get("/core/public/api/requests/:requestId", (req, res) => {
    const requestId = req.params["requestId"];

    const requestIndex = DATA.requests.findIndex(
      (request) => request.requestId === requestId
    );

    if (requestIndex === -1) {
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

    res
      .status(200)
      .contentType("application/json")
      .send(JSON.stringify(DATA.requests[requestIndex]));
  });
};
