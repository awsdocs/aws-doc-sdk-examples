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

const { getImageSet } = await import("../actions/get-image-set.js");

describe("get-image-set", () => {
  it("should log the response running without version ID", async () => {
    const logSpy = vi.spyOn(console, "log");
    const datastoreId = "12345678901234567890123456789012";
    const imageSetId = "12345678901234567890123456789012";

    const response = {
      createdAt: "2023-09-22T14:49:26.427Z",
      datastoreId: datastoreId,
      imageSetArn:
        "arn:aws:medical-imaging:us-east-1:xxxxxxxxxx:datastore/xxxxxxxxxxxxxxxxxxxx/imageset/xxxxxxxxxxxxxxxxxxxx",
      imageSetId: imageSetId,
      imageSetState: "ACTIVE",
      imageSetWorkflowStatus: "CREATED",
      updatedAt: "2023-09-22T14:49:26.427Z",
      versionId: "1",
    };

    send.mockResolvedValueOnce(response);

    await getImageSet(datastoreId, imageSetId);

    expect(logSpy).toHaveBeenCalledWith(response);
  });

  it("should log the response running with version ID", async () => {
    const logSpy = vi.spyOn(console, "log");
    const datastoreId = "12345678901234567890123456789012";
    const imageSetId = "12345678901234567890123456789012";
    const versionId = "1";

    const response = {
      createdAt: "2023-09-22T14:49:26.427Z",
      datastoreId: datastoreId,
      imageSetArn:
        "arn:aws:medical-imaging:us-east-1:xxxxxxxxxx:datastore/xxxxxxxxxxxxxxxxxxxx/imageset/xxxxxxxxxxxxxxxxxxxx",
      imageSetId: imageSetId,
      imageSetState: "ACTIVE",
      imageSetWorkflowStatus: "CREATED",
      updatedAt: "2023-09-22T14:49:26.427Z",
      versionId: versionId,
    };

    send.mockResolvedValueOnce(response);

    await getImageSet(datastoreId, imageSetId, versionId);

    expect(logSpy).toHaveBeenCalledWith(response);
  });
});
