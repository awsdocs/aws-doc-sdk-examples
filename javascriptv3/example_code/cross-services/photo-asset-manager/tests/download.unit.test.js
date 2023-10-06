import { DynamoDBDocumentClient } from "@aws-sdk/lib-dynamodb";
import { describe, beforeEach, afterEach, it, expect, vi } from "vitest";
import { getImageKeysForLabels } from "../src/functions/download.js";

const LABELS_TABLE_NAME = "labels-table-name";

describe("getImageKeysForLabels", () => {
  /**
   * @type {{ send: import('vitest').Mock }}
   */
  let mockDocClient;

  beforeEach(() => {
    mockDocClient = {
      send: vi.fn(),
    };
    DynamoDBDocumentClient.from = vi.fn(() => mockDocClient);
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  it("should return an empty array if no labels are provided", async () => {
    mockDocClient.send.mockResolvedValueOnce({
      Responses: { [LABELS_TABLE_NAME]: [] },
    });
    const result = await getImageKeysForLabels([]);
    expect(result).toEqual([]);
  });

  it("should return an array of unique image keys for the provided labels", async () => {
    const mockResponses = {
      [LABELS_TABLE_NAME]: [
        { Images: ["image1", "image2"] },
        { Images: ["image2", "image3"] },
      ],
    };
    mockDocClient.send.mockResolvedValueOnce({ Responses: mockResponses });

    const result = await getImageKeysForLabels(["label1", "label2", "label3"]);
    expect(result).toEqual(["image1", "image2", "image3"]);
  });

  it("should handle empty responses from DynamoDB", async () => {
    const mockResponses = {
      [LABELS_TABLE_NAME]: [],
    };
    mockDocClient.send.mockResolvedValueOnce({ Responses: mockResponses });

    const result = await getImageKeysForLabels(["label1", "label2", "label3"]);
    expect(result).toEqual([]);
  });

  it("should handle errors from DynamoDB", async () => {
    const mockError = new Error("DynamoDB error");
    mockDocClient.send.mockRejectedValueOnce(mockError);

    await expect(
      getImageKeysForLabels(["label1", "label2", "label3"]),
    ).rejects.toThrow(mockError);
  });
});
