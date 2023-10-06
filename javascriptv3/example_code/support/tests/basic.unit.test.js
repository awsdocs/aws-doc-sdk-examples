/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, it, expect, vi, beforeEach } from "vitest";

const send = vi.fn();
const prompt = vi.fn();

vi.doMock("@aws-sdk/client-support", async () => {
  const actual = await vi.importActual("@aws-sdk/client-support");
  return {
    ...actual,
    SupportClient: class {
      send = send;
    },
  };
});

vi.doMock("inquirer", () => {
  return Promise.resolve({
    default: { prompt },
  });
});

const {
  verifyAccount,
  getService,
  getCategory,
  getSeverityLevel,
  createCase,
  getTodaysOpenCases,
  createAttachmentSet,
  linkAttachmentSetToCase,
  getCommunications,
  getFirstAttachment,
  getAttachment,
  resolveCase,
  findCase,
  getTodaysResolvedCases,
} = await import("../scenarios/basic.js");

describe("Basic", () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  describe("verifyAccount", () => {
    it("should not throw an error if the client call was successful", async () => {
      expect.assertions(1);
      send.mockResolvedValueOnce();
      await expect(verifyAccount()).resolves.toBeUndefined();
    });

    it("should throw an error if the client call failed", async () => {
      expect.assertions(1);
      send.mockRejectedValueOnce(new Error("Failed"));
      await expect(verifyAccount()).rejects.toThrow("Failed");
    });

    it("should throw a user friendly error if the SubscriptionRequiredException error is thrown", async () => {
      expect.assertions(1);
      const err = new Error();
      err.name = "SubscriptionRequiredException";
      send.mockRejectedValueOnce(err);
      await expect(verifyAccount()).rejects.toThrow(
        "You must be subscribed to the AWS Support plan to use this feature.",
      );
    });
  });

  describe("getService", () => {
    it("should return the selected service", async () => {
      expect.assertions(1);
      const service = {
        name: "Amazon Elastic Compute Cloud",
        code: "AmazonEC2",
      };
      send.mockResolvedValueOnce({ services: [] });
      prompt.mockResolvedValueOnce({ selectedService: service });

      await expect(getService()).resolves.toEqual(service);
    });
  });

  describe("getCategory", () => {
    it("should return the selected category", async () => {
      expect.assertions(1);
      const category = { name: "General Support", code: "GeneralSupport" };
      prompt.mockResolvedValueOnce({ selectedCategory: category });

      await expect(getCategory({ categories: [] })).resolves.toEqual(category);
    });
  });

  describe("getSeverityLevel", () => {
    it("should return the selected severity level", async () => {
      expect.assertions(1);
      const severityLevel = { name: "low", code: "low" };
      send.mockResolvedValueOnce({ severityLevels: [] });
      prompt.mockResolvedValueOnce({ selectedSeverityLevel: severityLevel });

      await expect(getSeverityLevel()).resolves.toEqual(severityLevel);
    });
  });

  describe("createCase", () => {
    it("should return the caseId", async () => {
      expect.assertions(1);
      send.mockResolvedValueOnce({ caseId: "caseId" });

      await expect(
        createCase({
          selectedCategory: "",
          selectedService: "",
          selectedSeverityLevel: "",
        }),
      ).resolves.toEqual("caseId");
    });
  });

  describe("getTodaysOpenCases", () => {
    it("should return a list of cases if any are found", async () => {
      expect.assertions(1);
      send.mockResolvedValueOnce({ cases: [{ caseId: "caseId" }] });

      await expect(getTodaysOpenCases()).resolves.toEqual([
        { caseId: "caseId" },
      ]);
    });

    it("should throw an error if no cases are found", async () => {
      expect.assertions(1);
      send.mockResolvedValueOnce({ cases: [] });

      await expect(getTodaysOpenCases()).rejects.toThrow(
        "Unexpected number of cases. Expected more than 0 open cases.",
      );
    });
  });

  describe("createAttachmentSet", () => {
    it("should return the attachmentSetId", async () => {
      expect.assertions(1);
      send.mockResolvedValueOnce({ attachmentSetId: "attachmentSetId" });

      await expect(createAttachmentSet()).resolves.toEqual("attachmentSetId");
    });
  });

  describe("linkAttachmentSetToCase", () => {
    it("should call the send method on the client", async () => {
      expect.assertions(1);
      send.mockResolvedValueOnce();

      await linkAttachmentSetToCase("caseId", "attachmentSetId");

      expect(send).toHaveBeenCalled();
    });
  });

  describe("getCommunications", () => {
    it("should return communications", async () => {
      expect.assertions(1);
      send.mockResolvedValueOnce({ communications: [{ body: "body" }] });

      await expect(getCommunications("caseId")).resolves.toEqual([
        { body: "body" },
      ]);
    });
  });

  describe("getFirstAttachment", () => {
    it("should return the first attachmentId if any are found", () => {
      const firstAttachment = {
        attachmentId: "attachment1",
        fileName: "file1.txt",
      };
      const communications = [
        { attachmentSet: [] },
        { attachmentSet: [] },
        {
          attachmentSet: [
            firstAttachment,
            { attachmentId: "attachment2", fileName: "file2.txt" },
          ],
        },
      ];
      expect(getFirstAttachment(communications)).toEqual("attachment1");
    });

    it("should return undefined if no attachments are found", () => {
      const communications = [{ attachmentSet: [] }, { attachmentSet: [] }];
      expect(getFirstAttachment(communications)).toBeUndefined();
    });
  });

  describe("getAttachment", () => {
    it("should return the attachment", async () => {
      expect.assertions(1);
      send.mockResolvedValueOnce({ attachment: "attachment" });

      await expect(getAttachment("attachmentId")).resolves.toEqual(
        "attachment",
      );
    });
  });

  describe("resolveCase", () => {
    it("should resolve the case if the user confirms", async () => {
      prompt.mockResolvedValueOnce({ shouldResolve: true });
      send.mockResolvedValueOnce();
      await expect(resolveCase("case1")).resolves.toEqual(true);
      expect(send).toHaveBeenCalled();
    });

    it("should not resolve the case if the user does not confirm", async () => {
      prompt.mockResolvedValueOnce({ shouldResolve: false });
      await expect(resolveCase("case1")).resolves.toEqual(false);
      expect(send).not.toHaveBeenCalled();
    });
  });

  describe("findCase", () => {
    it("should return the correct case based on the caseId", async () => {
      const case1 = { caseId: "case1", subject: "Test case 1" };
      const case2 = { caseId: "case2", subject: "Test case 2" };

      expect(
        await findCase({ caseId: "case1", cases: [case1, case2] }),
      ).toEqual(case1);
    });

    it("should continue pagination when the case is not found", () => {
      const case1 = { caseId: "case1", subject: "One" };
      const case2 = { caseId: "case2", subject: "One" };
      send.mockResolvedValueOnce({ cases: [case2] });
      expect(
        findCase({ caseId: "case2", cases: [case1], nextToken: "abc" }),
      ).resolves.toEqual(case2);
    });

    it("should throw an error when the case is not found after pagination", () => {
      const case1 = { caseId: "case1", subject: "One" };
      send.mockResolvedValueOnce({ cases: [] });
      expect(
        findCase({ caseId: "special-case", cases: [case1], nextToken: "abc" }),
      ).rejects.toThrow("special-case not found");
    });
  });

  describe("getTodaysResolvedCases", () => {
    it("should return today's resolved cases", async () => {
      const case1 = {
        caseId: "1",
        subject: "one",
        status: "resolved",
      };
      const case2 = { caseId: "2", subject: "two", status: "open" };
      send.mockResolvedValueOnce({ cases: [case1, case2], nextToken: null });
      await expect(getTodaysResolvedCases("1")).resolves.toEqual([case1]);
    });
  });
});
