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

const { getImageSetMetadata } = await import(
  "../actions/get-image-set-metadata.js"
);

describe("get-image-set-metadata", () => {
  it("should log the response without version", async () => {
    const logSpy = vi.spyOn(console, "log");
    const datastoreId = "12345678901234567890123456789012";
    const imageSetId = "12345678901234567890123456789012";
    const metadataFileName = "med_image_test.gzip";

    const response = {
      metadata: {
        httpStatusCode: 200,
        requestId: "5219b274-30ff-4986-8cab-48753de3a599",
        extendedRequestId: undefined,
        cfId: undefined,
        attempts: 1,
        totalRetryDelay: 0,
      },
      contentType: "application/json",
      contentEncoding: "gzip",
      imageSetMetadataBlob: new StreamMock(new Uint8Array(256)),
    };

    send.mockResolvedValueOnce(response);

    await getImageSetMetadata(metadataFileName, datastoreId, imageSetId);

    expect(logSpy).toHaveBeenCalledWith(response);
    expect(fs.existsSync(metadataFileName)).toBeTruthy();
    fs.unlinkSync(metadataFileName);
  });

  it("should log the response with version", async () => {
    const logSpy = vi.spyOn(console, "log");
    const datastoreId = "12345678901234567890123456789012";
    const imageSetId = "12345678901234567890123456789012";
    const metadataFileName = "med_image_test.gzip";
    const versionID = "1";

    const response = {
      metadata: {
        httpStatusCode: 200,
        requestId: "5219b274-30ff-4986-8cab-48753de3a599",
        extendedRequestId: undefined,
        cfId: undefined,
        attempts: 1,
        totalRetryDelay: 0,
      },
      contentType: "application/json",
      contentEncoding: "gzip",
      imageSetMetadataBlob: new StreamMock(new Uint8Array(256)),
    };

    send.mockResolvedValueOnce(response);

    await getImageSetMetadata(
      metadataFileName,
      datastoreId,
      imageSetId,
      versionID
    );

    expect(logSpy).toHaveBeenCalledWith(response);
    expect(fs.existsSync(metadataFileName)).toBeTruthy();
    fs.unlinkSync(metadataFileName);
  });
});
