/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, it, expect, vi } from "vitest";

const send = vi.fn();

vi.doMock("@aws-sdk/client-medical-imaging", async () => {
  const actual = await vi.importActual("@aws-sdk/client-medical-imaging");
  return {
    ...actual,
    MedicalImagingClient: class {
      send = send;
    },
  };
});

const { untagResource } = await import("../actions/untag-resource.js");

describe("untag-resource", () => {
  it("should log the response", async () => {
    const logSpy = vi.spyOn(console, "log");
    const resourceArn =
      "arn:aws:medical-imaging:us-east-1:123456789012:datastore/xxxxxxxxxxxxxxxx/imageset/xxxxxxxxxxxxxxx";
    const keys = ["Deployment"];

    const response = {
      $metadata: {
        httpStatusCode: 204,
        requestId: "8a6de9a3-ec8e-47ef-8643-473518b19d45",
        extendedRequestId: undefined,
        cfId: undefined,
        attempts: 1,
        totalRetryDelay: 0,
      },
    };

    send.mockResolvedValueOnce(response);

    await untagResource(resourceArn, keys);

    expect(logSpy).toHaveBeenCalledWith(response);
  });
});
