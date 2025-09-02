import https from "https";
import fs from "fs";
import express from "express";

const app = express();

https.createServer({
  key: fs.readFileSync("server.key"),
  cert: fs.readFileSync("server.cert")
}, app).listen(3000, () => {
  console.log("Server running on https://localhost:3000");
});
