const express = require("express");
const cors = require("cors");
const app = express();
const port = 8081;
app.use(express.json());
app.use(cors());

const fs = require("fs");
const dirPath = "./api";
const files = fs.readdirSync("./src/fake-backend/api");

for (const file of files) {
  require(dirPath + "/" + file)(app);
}

app.listen(port, () => {
  console.log(`Fake backend app listening on port ${port}`);
});
