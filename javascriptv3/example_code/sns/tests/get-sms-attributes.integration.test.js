// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { describe, it, expect } from "vitest";
import { getSmsAttributes } from "../actions/get-sms-attributes.js";
import { SetSMSAttributesCommand } from "@aws-sdk/client-sns";
import { snsClient } from "../libs/snsClient.js";

describe("getSmsAttributes", () => {
  it("should return a DefaultSMSType of Transactional", async () => {
    const setAttributes = new SetSMSAttributesCommand({
      attributes: {
        DefaultSMSType: "Transactional",
      },
    });
    await snsClient.send(setAttributes);

    const response = await getSmsAttributes();

    expect(response.attributes?.DefaultSMSType).toBe("Transactional");
  });
});
