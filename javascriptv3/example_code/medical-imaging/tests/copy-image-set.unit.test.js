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

const { copyImageSet } = await import("../actions/copy-image-set.js");

describe("copy-image-set", () => {
  const response = {
    $metadata: {
      httpStatusCode: 200,
      requestId: "d9b219ce-cc48-4a44-a5b2-c5c3068f1ee8",
      extendedRequestId: undefined,
      cfId: undefined,
      attempts: 1,
      totalRetryDelay: 0,
    },
    datastoreId: "xxxxxxxxxxxxxx",
    destinationImageSetProperties: {
      createdAt: "2023-09-27T19:46:21.824Z",
      imageSetArn:
        "arn:aws:medical-imaging:us-east-1:xxxxxxxxxxx:datastore/xxxxxxxxxxxxx/imageset/xxxxxxxxxxxxxxxxxxx",
      imageSetId: "xxxxxxxxxxxxxxx",
      imageSetState: "LOCKED",
      imageSetWorkflowStatus: "COPYING",
      latestVersionId: "1",
      updatedAt: "2023-09-27T19:46:21.824Z",
    },
    sourceImageSetProperties: {
      createdAt: "2023-09-22T14:49:26.427Z",
      imageSetArn:
        "arn:aws:medical-imaging:us-east-1:xxxxxxxxxxx:datastore/xxxxxxxxxxxxx/imageset/xxxxxxxxxxxxxxxx",
      imageSetId: "xxxxxxxxxxxxxxxx",
      imageSetState: "LOCKED",
      imageSetWorkflowStatus: "COPYING_WITH_READ_ONLY_ACCESS",
      latestVersionId: "4",
      updatedAt: "2023-09-27T19:46:21.824Z",
    },
  };

  it("should log the response without destination", async () => {
    const logSpy = vi.spyOn(console, "log");
    const datastoreId = "12345678901234567890123456789012";
    const imageSetID = "12345678901234567890123456789012";
    const sourceVersionId = "1";

    send.mockResolvedValueOnce(response);

    await copyImageSet(datastoreId, imageSetID, sourceVersionId);

    expect(logSpy).toHaveBeenCalledWith(response);
  });

  it("should log the response with destination", async () => {
    const logSpy = vi.spyOn(console, "log");
    const datastoreId = "12345678901234567890123456789012";
    const imageSetID = "12345678901234567890123456789012";
    const sourceVersionId = "1";
    const destinationImageSetID = "12345678901234567890123456789012";
    const destinationVersionId = "1";

    send.mockResolvedValueOnce(response);

    await copyImageSet(
      datastoreId,
      imageSetID,
      sourceVersionId,
      destinationImageSetID,
      destinationVersionId
    );

    expect(logSpy).toHaveBeenCalledWith(response);
  });
});
