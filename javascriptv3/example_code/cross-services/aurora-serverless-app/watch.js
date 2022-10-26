import { spawn } from "node:child_process";

const format = (data) => data.toString().trim();

const makeLogger = (prefix) => (data) => {
  if (data) {
    console.log(`${prefix} ${data}`);
  }
};

const loadApp = (app) => {
  if (app) {
    app.kill();
  }

  const newApp = spawn("node", ["./build/cross-services/aurora-serverless-app/src/index.js"]);
  newApp.stdout.on("data", (data) => appLogger(format(data)));
  newApp.stderr.on("data", (data) => appLogger(format(data)));
  return newApp;
};

const tscLogger = makeLogger("[TSC]");
const appLogger = makeLogger("[APP]");

let app;

const typescriptWatch = spawn("tsc", ["-w"]);

typescriptWatch.stdout.on("data", (data) => {
  const formatted = format(data);

  tscLogger(formatted);

  if (formatted.includes("Watching for file changes.")) {
    app = loadApp(app);
  }
});

typescriptWatch.stderr.on("data", (data) => {
  tscLogger(format(data));
});
