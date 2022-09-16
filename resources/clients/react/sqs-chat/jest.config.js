const merge = require("merge");
const cloudscapePreset = require("@cloudscape-design/jest-preset");

module.exports = merge.recursive(cloudscapePreset, {
  setupFilesAfterEnv: ["<rootDir>/src/setupTests.js"],
  testEnvironment: "jsdom",
  transform: {
    "\\.[jt]sx?$": "babel-jest",
  },
});
