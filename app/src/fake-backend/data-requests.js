/**
 * @type {RequestBE}
 */
const REQUESTS = [
  {
    id: "2",
    crtUsr: "1f3e806c-c313-4069-b41e-8660b212fcc3",
    crtTms: "2022-08-02T21:00:00Z",
    mdfUsr: "1f3e806c-c313-4069-b41e-8660b212fcc3",
    mdfTms: "2022-08-02T21:00:00Z",
    startDate: "2023-01-10",
    endDate: "2023-01-15",
    status: "PENDING",
    type: "MEDICAL",
    description: "description",
    rejectReason: "",
    noOfDays: "6",
    v: "0",
    employeeDetails: {
      firstName: "Maria",
      lastName: "Popescu",
      id: "1f3e806c-c313-4069-b41e-8660b212fcc3",
    },
  },
  {
    id: "1",
    crtUsr: "435fec23-7277-4cf2-bcd4-f58689b8279d",
    crtTms: "2022-08-02T21:00:00Z",
    mdfUsr: "435fec23-7277-4cf2-bcd4-f58689b8279d",
    mdfTms: "2022-08-22T06:45:33.637655Z",
    startDate: "2022-10-18",
    endDate: "2022-10-20",
    status: "APPROVED",
    type: "VACATION",
    description: "description",
    rejectReason: "",
    noOfDays: "3",
    v: "1",
    employeeDetails: {
      firstName: "Madalin",
      lastName: "Clipici",
      id: "435fec23-7277-4cf2-bcd4-f58689b8279d",
    },
  },
  {
    id: "4",
    crtUsr: "b8df475c-c9e8-4bbf-8df4-a9a0b1b3e878",
    crtTms: "2022-06-03T21:00:00Z",
    mdfUsr: "b8df475c-c9e8-4bbf-8df4-a9a0b1b3e878",
    mdfTms: "2022-08-08T21:00:00Z",
    startDate: "2022-07-17",
    endDate: "2022-07-24",
    status: "REJECTED",
    type: "VACATION",
    description: "description",
    rejectReason: "rejectReason",
    noOfDays: "6",
    v: "0",
    employeeDetails: {
      firstName: "Paul",
      lastName: "Maga",
      id: "b8df475c-c9e8-4bbf-8df4-a9a0b1b3e878",
    },
  },
  {
    id: "3",
    crtUsr: "ca1984ca-bb8b-4044-bc87-91c64d361e41",
    crtTms: "2022-08-03T21:00:00Z",
    mdfUsr: "ca1984ca-bb8b-4044-bc87-91c64d361e41",
    mdfTms: "2022-08-22T06:46:07.288132Z",
    startDate: "2022-10-10",
    endDate: "2022-10-20",
    status: "REJECTED",
    type: "VACATION",
    description: "description",
    rejectReason: "reject reason",
    noOfDays: "11",
    v: "1",
    employeeDetails: {
      firstName: "Claudia",
      lastName: "Anton",
      id: "ca1984ca-bb8b-4044-bc87-91c64d361e41",
    },
  },
  {
    id: "8",
    crtUsr: "1f3e806c-c313-4069-b41e-8660b212fcc3",
    crtTms: "2022-08-02T21:00:00Z",
    mdfUsr: "1f3e806c-c313-4069-b41e-8660b212fcc3",
    mdfTms: "2022-08-02T21:00:00Z",
    startDate: "2023-01-10",
    endDate: "2023-01-15",
    status: "APPROVED",
    type: "MEDICAL",
    description: "description",
    rejectReason: "",
    noOfDays: "6",
    v: "0",
    employeeDetails: {
      firstName: "Maria",
      lastName: "Popescu",
      id: "1",
    },
  },
  {
    id: "9",
    crtUsr: "ca1984ca-bb8b-4044-bc87-91c64d361e41",
    crtTms: "2022-08-03T21:00:00Z",
    mdfUsr: "ca1984ca-bb8b-4044-bc87-91c64d361e41",
    mdfTms: "2022-08-22T06:46:07.288132Z",
    startDate: "2022-10-10",
    endDate: "2022-10-20",
    status: "PENDING",
    type: "VACATION",
    description: "description",
    rejectReason: "reject reason",
    noOfDays: "11",
    v: "1",
    employeeDetails: {
      firstName: "Claudia",
      lastName: "Anton",
      id: "1",
    },
  },
];

module.exports = REQUESTS;
