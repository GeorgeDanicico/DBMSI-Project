module.exports = function (app) {
  const DATA = require("../data");

  app.get("/core/public/api/holidays", (req, res) => {
    const employeeId = req.query["employeeId"];
    const startDate = req.query["startDate"];
    const endDate = req.query["endDate"];

    const holidays = DATA.holidays.filter((holiday) => {
      if (employeeId && employeeId !== holiday.employeeId) {
        return false;
      }
      if (startDate && new Date(holiday.startDate) < new Date(startDate)) {
        return false;
      }
      if (endDate && new Date(holiday.endDate) > new Date(endDate)) {
        return false;
      }
      return true;
    });
    res
      .status(200)
      .contentType("application/json")
      .send(JSON.stringify(holidays));
  });

  app.get("/core/public/api/requests/own", (req, res) => {
    const startDate = req.query["startDate"];
    const endDate = req.query["endDate"];

    const requests = DATA.requests.filter((request) => {
      if (
        startDate &&
        new Date(request.startDate).getTime() < new Date(startDate).getTime()
      ) {
        return false;
      }

      if (
        endDate &&
        new Date(request.endDate).getTime() > new Date(endDate).getTime()
      ) {
        return false;
      }

      if (request.employeeId != 6) {
        return false;
      }
      return true;
    });

    res
      .status(200)
      .contentType("application/json")
      .send(JSON.stringify(requests));
  });

  app.put("/core/public/api/requests/:requestId", (req, res) => {
    const requestId = req.params["requestId"];
    const reqBody = req.body;

    const requestIndex = DATA.requests.findIndex(
      (request) => request.requestId === requestId
    );

    if (
      !reqBody.employeeId ||
      !reqBody.startDate ||
      !reqBody.endDate ||
      !reqBody.createDate ||
      !reqBody.editDate ||
      !reqBody.status ||
      !reqBody.type ||
      requestIndex === -1
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
    }

    (newRequest = {
      requestId: requestId,
      employeeId: reqBody.employeeId,
      startDate: reqBody.startDate,
      endDate: reqBody.endDate,
      createDate: reqBody.createDate,
      editDate: reqBody.editDate,
      status: reqBody.status,
      mentions: reqBody.mentions,
      refusalReason: reqBody.refusalReason,
      type: reqBody.type,
    }),
      (DATA.requests[requestIndex] = newRequest);

    res.status(200).contentType("application/json").send(newRequest);
  });

  app.post("/core/public/api/requests", (req, res) => {
    const reqBody = req.body;

    if (
      !reqBody.startDate ||
      !reqBody.endDate ||
      !reqBody.createDate ||
      !reqBody.editDate ||
      !reqBody.status ||
      !reqBody.employeeId ||
      !reqBody.type
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
    }

    reqBody.requestId = `${DATA.requests.length + 1}`;
    const newRequestIndex = DATA.requests.push(reqBody) - 1;
    res
      .status(200)
      .contentType("application/json")
      .send(DATA.requests[newRequestIndex]);
  });

  app.get("/core/public/api/freeDays", (req, res) => {
    const startDate = req.query["startDate"];
    const endDate = req.query["endDate"];

    const requests = DATA.freeDays.filter((request) => {
      if (startDate && new Date(request.startDate) < new Date(startDate)) {
        return false;
      }
      if (endDate && new Date(request.endDate) > new Date(endDate)) {
        return false;
      }
      return true;
    });
    res
      .status(200)
      .contentType("application/json")
      .send(JSON.stringify(requests));
  });

  app.delete(
    "/core/api/v1/employees/:employeeId/requests/:requestId",
    (req, res) => {
      const requestId = req.params["requestId"];
      if (requestId) {
        requests = DATA.requests;
        const request = DATA.requests.find((r) => r.requestId === requestId);
        const index = DATA.requests.indexOf(request);

        if (
          request &&
          (request.status === "APPROVED" || request.status === "PENDING")
        ) {
          requests.splice(index, 1);
          res.status(200).contentType("application/json").send();
        } else {
          res
            .status(400)
            .contentType("application/json")
            .send({
              errors: [
                {
                  errorCode: "E0001C400",
                  devMessage: "Something is not valid in the request!",
                },
              ],
            });
        }
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
  );
};
