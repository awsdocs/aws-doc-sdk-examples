// const { testEnvironment } = require("../jest.config");

// Get service clients module and commands using ES6 syntax.
import 'regenerator-runtime/runtime'
import { run, params } from "../src/cw_deletealarms";
import { cwClient } from "../src/libs/cwClient.js";

jest.mock("../src/libs/cwClient.js");
jest.mock("@aws-sdk/client-cloudwatch");

describe("@aws-sdk/client-cloudwatch mock", () => {
  it("should run async equal", async (done) => {
    expect(1).toBe(1);
    done();
  });
  
  it("should be equal", () => {
    expect(1).toBe(1);
  });
  
  it("should successfully mock CloudWatch client", async (done) => {
    cwClient.send.mockResolvedValue({ isMock: true });
    const response = await run(params);
    expect(response.isMock).toEqual(true);
    done();
  });
});

