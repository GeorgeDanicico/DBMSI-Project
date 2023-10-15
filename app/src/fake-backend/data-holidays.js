/**
 * @type {HolidayBE}
 */
 const HOLIDAYS  = [
    {
        employeeId:'1',
        startDate: new Date(2022, 08, 01).toISOString().split('T')[0],
        endDate: new Date(2022, 08, 06).toISOString().split('T')[0],
    },
    {
        employeeId:'1',
        startDate: new Date(2022, 08, 15).toISOString().split('T')[0],
        endDate: new Date(2022, 08, 31).toISOString().split('T')[0],
    },
    {
        employeeId:'2',
        startDate: new Date(2022, 09, 01).toISOString().split('T')[0],
        endDate: new Date(2022, 09, 04).toISOString().split('T')[0],
    },
    {
        employeeId:'3',
        startDate: new Date(2022, 08, 13).toISOString().split('T')[0],
        endDate: new Date(2022, 08, 15).toISOString().split('T')[0],
    },
    {
        employeeId:'3',
        startDate: new Date(2022, 09, 11).toISOString().split('T')[0],
        endDate: new Date(2022, 09, 20).toISOString().split('T')[0],
    },
    {
        employeeId:'4',
        startDate: new Date(2022, 08, 11).toISOString().split('T')[0],
        endDate: new Date(2022, 08, 20).toISOString().split('T')[0],
    },
    {
        employeeId:'5',
        startDate: new Date(2022, 07, 11).toISOString().split('T')[0],
        endDate: new Date(2022, 07, 20).toISOString().split('T')[0],
    },
];

module.exports = HOLIDAYS;