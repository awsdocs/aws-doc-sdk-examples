/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, it, expect, vi } from "vitest";
import * as fs from "fs";

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

class StreamMock {
  constructor(dataArray) {
    this.dataArray = dataArray;
  }

  transformToByteArray() {
    return this.dataArray;
  }
}

const { getImageFrame } = await import("../actions/get-image-frame.js");

describe("get-image-set-metadata", () => {
  it("should log the response", async () => {
    const logSpy = vi.spyOn(console, "log");
    const datastoreId = "12345678901234567890123456789012";
    const imageSetId = "12345678901234567890123456789012";
    const imageFrameID = "12345678901234567890123456789012";
    const metadataFileName = "med_image_frame_test.jph";

    const response = {
      metadata: {
        httpStatusCode: 200,
        requestId: "5219b274-30ff-4986-8cab-48753de3a599",
        extendedRequestId: undefined,
        cfId: undefined,
        attempts: 1,
        totalRetryDelay: 0,
      },
      contentType: "text/plain",
      imageFrameBlob: new StreamMock(new Uint8Array(256)),
    };

    send.mockResolvedValueOnce(response);

    await getImageFrame(
      metadataFileName,
      datastoreId,
      imageSetId,
      imageFrameID
    );

    expect(logSpy).toHaveBeenCalledWith(response);
    expect(fs.existsSync(metadataFileName)).toBeTruthy();
    fs.unlinkSync(metadataFileName);
  });
});
