module.exports = function (app) {
  const DATA = require("../data");

  app.get("/core/api/v1/employees", (req, res) => {
    const teamId = req.query["teamId"];
    if (!teamId || teamId === "ALL") {
      employees = DATA.employees;
    } else {
      employees = DATA.employees.filter(
        (employee) => employee.teamDetails.id === teamId
      );
    }
    res
      .status(200)
      .contentType("application/json")
      .send(JSON.stringify({ items: employees }));
  });

  app.post("/core/api/v1/employees", (req, res) => {
    const reqBody = req.body;
    if (
      !reqBody.firstName ||
      !reqBody.lastName ||
      !reqBody.username ||
      !reqBody.teamId ||
      !reqBody.email ||
      !reqBody.noDaysOff ||
      !reqBody.role ||
      !reqBody.contractStartDate
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
    const newEmpIndex =
      DATA.employees.push({
        firstName: reqBody.firstName,
        lastName: reqBody.lastName,
        username: reqBody.username,
        email: reqBody.email,
        role: reqBody.role,
        totalVacationDays: reqBody.noDaysOff,
        teamDetails: { id: reqBody.teamId },
        contractStartDate: reqBody.contractStartDate,
        id: `${DATA.employees.length}`,
      }) - 1;
    console.error(DATA.employees);
    res
      .status(200)
      .contentType("application/json")
      .send({ items: DATA.employees[newEmpIndex] });
  });

  app.patch("/core/api/v1/employees/:id", (req, res) => {
    const reqBody = req.body;
    const id = req.params["id"];
    const teamId = reqBody["teamId"];
    if (
      !reqBody.firstName ||
      !reqBody.lastName ||
      !reqBody.teamId ||
      !reqBody.email ||
      !reqBody.role ||
      !reqBody.v
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
    const team = DATA.teams.find((team) => team.id === teamId);
    const employee = DATA.employees.find((empl) => empl.id === id);
    newEmployee = {
      id: id,
      firstName: reqBody.firstName,
      lastName: reqBody.lastName,
      username: employee.username,
      teamDetails: { id: team.id, name: team.name },
      email: reqBody.email,
      totalVacationDays: employee.totalVacationDays,
      role: reqBody.role,
      contractStartDate: employee.contractStartDate,
      v: reqBody.v,
    };
    employees = DATA.employees;
    const index = employees.indexOf(employee);
    employees[index] = newEmployee;
    res.status(200).contentType("application/json").send({ items: employees });
  });

  app.patch("/core/api/v1/employees/:id/inactivate", (req, res) => {
    const id = req.params["id"];
    employees = DATA.employees;
    const employee = DATA.employees.find((empl) => empl.id === id);
    const index = DATA.employees.indexOf(employee);

    if (employee) {
      employees.splice(index, 1);
      res
        .status(200)
        .contentType("application/json")
        .send({ items: employees });
    } else {
      res
        .status(400)
        .contentType("application/json")
        .send({
          errors: [
            {
              errorCode: "E0001C400",
              devMessage: "The employee does not exist!",
            },
          ],
        });
    }
  });

  app.get("/core/public/api/employees/:id", (req, res) => {
    const id = req.params["id"];
    const employee = DATA.employees.find((r) => r.id === id);
    if (employee) {
      res.status(200).contentType("application/json").send(employee);
    }
  });

  app.post("/core/api/v1/employees/daysOff", (req, res) => {
    const reqBody = req.body;
    const employeeIds = reqBody["employeeIds"];
    const noDays = reqBody["noDays"];

    if (
      !reqBody.employeeIds ||
      !reqBody.noDays ||
      !reqBody.type ||
      !reqBody.description
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

    for (let i = 0; i < employeeIds.length; i++) {
      const employee = DATA.employees.find(
        (empl) => empl.id === employeeIds[i]
      );
      const index = DATA.employees.indexOf(employee);
      if (reqBody.type === "INCREASE") {
        const totalVacationDays = +employee.totalVacationDays + +noDays;
        employee.totalVacationDays = totalVacationDays;
        DATA.employees[index] = employee;
      } else {
        const totalVacationDays = +employee.totalVacationDays - noDays;
        employee.totalVacationDays = totalVacationDays;
        DATA.employees[index] = employee;
      }
    }
    res
      .status(200)
      .contentType("application/json")
      .send({ items: DATA.employees });
  });
};
