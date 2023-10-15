/**
 * @typedef EmployeeBE
 * @property {string} id
 * @property {string} firstName
 * @property {string} lastName
 * @property {string} email
 * @property {'EMPLOYEE'|'HR'|'TEAMLEAD'} role
 * @property {string} contractStartDate
 * @property {TeamBE} teamDetails
 * @property {number} totalVacationDays
 */

/**
 * @typedef RequestBE
 * @property {string} id
 * @property {string} crtUsr
 * @property {string} crtTms
 * @property {string} mdfUsr
 * @property {string} mdfTms
 * @property {string} startDate
 * @property {string} endDate
 * @property {'PENDING'|'APPROVED'|'REJECTED'} status
 * @property {'HOLIDAYS' | 'MEDICAL' | 'SPECIAL'} type
 * @property {string} description
 * @property {string} rejectReason
 * @property {string} noOfDays
 * @property {string} v
 * @property {EmployeeDetailsBE} employeeDetails
 */

/**
 * @typedef EmployeeDetailsBE
 * @property {string} firstName
 * @property {string} lastName
 * @property {string} id
 */

/**
 * @typedef TeamBE
 * @property {string} id
 * @property {string} name
 */

/**
 * @typedef FreeDayBE
 * @property {string} name
 * @property {string} startDate
 * @property {string} endDate
 */

/**
 * @typedef HolidayBE
 * @property {string} employeeId
 * @property {string} startDate
 * @property {string} endDate
 */

/**
 * @typedef LegallyDaysOffBE
 * @property {string} date
 * @property {string} description
 */

/**
 * @typedef DataBE
 * @property {Array<TeamBE>} teams
 * @property {Array<EmployeeBE>} employees
 * @property {Array<RequestBE>} requests
 */

/**
 * @typedef AddHolidayBE
 * @property {Array<string>} employeeIds
 * @property {number} noDays
 * @property {'INCREASE'|'DECREASE'} type
 * @property {string} description
 */

/**
 * @typedef LegalDayOff
 * @property {Date} date
 * @property {string} description
 */
