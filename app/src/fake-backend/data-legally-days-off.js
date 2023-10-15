
/**
 * @type {LegallyDaysOffBE}
 */
const LEGALLY_DAYS_OFF = [
  {
    date: new Date(2022, 01, 01).toISOString().split("T")[0],
    description: "Anul nou"
  },
  {
    date: new Date(2022, 08, 15).toISOString().split("T")[0],
    description: "Adormirea Maicii Domnului"
  },
  {
    date: new Date(2022, 06, 01).toISOString().split("T")[0],
    description: "Ziua copilului"
  },
  {
    date: new Date(2022, 08, 10).toISOString().split("T")[0],
    description: "Sfanta Roxana"
  },
  {
    date: new Date(2022, 09, 11).toISOString().split("T")[0],
    description: "Ziua Roxanei"
  },
];

module.exports = LEGALLY_DAYS_OFF;
