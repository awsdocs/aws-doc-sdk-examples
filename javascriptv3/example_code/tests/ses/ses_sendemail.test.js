import { run } from "../../ses/src/ses_sendemail";

describe("ses_sendemail", () => {
  test("attempting to send an unverified email returns an error", async () => {
    const result = await run();
    expect(result.Error.Message).toContain("Email address is not verified.");
  });
});
