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

const { listTagsForResource } = await import(
  "../actions/list-tags-for-resource.js"
);

describe("list-tags-for-resource", () => {
  it("should log the response", async () => {
    const logSpy = vi.spyOn(console, "log");
    const resourceArn =
      "arn:aws:medical-imaging:us-east-1:123456789012:datastore/xxxxxxxxxxxxxxxx/imageset/xxxxxxxxxxxxxxx";

    const response = {
      $metadata: {
        httpStatusCode: 200,
        requestId: "008fc6d3-abec-4870-a155-20fa3631e645",
        extendedRequestId: undefined,
        cfId: undefined,
        attempts: 1,
        totalRetryDelay: 0,
      },
      tags: { Deployment: "Development" },
    };

    send.mockResolvedValueOnce(response);

    await listTagsForResource(resourceArn);

    expect(logSpy).toHaveBeenCalledWith(response);
  });
});
