// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { describe, it, expect } from "vitest";
import { snsClient } from "../libs/snsClient.js";
import { setSmsType } from "../actions/set-sms-attribute-type.js";
import { GetSMSAttributesCommand } from "@aws-sdk/client-sns";

describe("getSmsType", () => {
  it("should return a DefaultSMSType of Transactional", async () => {
    await setSmsType();

    const getSmsType = new GetSMSAttributesCommand({
      attributes: ["DefaultSMSType"],
    });

    const response = await snsClient.send(getSmsType);

    expect(response.attributes.DefaultSMSType).toBe("Transactional");
  });
});
