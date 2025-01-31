// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, vi, expect } from "vitest";
import { CloudWatchQuery } from "../scenarios/large-query/cloud-watch-query.js";
import {
  DescribeQueriesCommand,
  GetQueryResultsCommand,
  StartQueryCommand,
} from "@aws-sdk/client-cloudwatch-logs";

/**
 * @type {import('@aws-sdk/client-cloudwatch-logs').GetQueryResultsCommandOutput['results']}
 */
const results = [
  [
    { field: "a", value: "1" },
    { field: "b", value: "2" },
  ],
  [
    { field: "c", value: "3" },
    { field: "d", value: "4" },
  ],
];

const happyPathStartQueryMock = vi
  .fn()
  .mockResolvedValueOnce({
    queryId: String(Math.random()),
  })
  .mockResolvedValueOnce({
    queryId: String(Math.random()),
  })
  .mockRejectedValueOnce(new Error("Query's end date and time"));

const happyPathGetQueryResultsMock = vi.fn(() => {
  const rand = Math.random() * 10;

  if (rand < 3) {
    return Promise.resolve({ status: "Scheduled" });
  }

  if (rand >= 3 && rand < 6) {
    return Promise.resolve({ status: "Running" });
  }

  if (rand >= 6) {
    return Promise.resolve({ status: "Complete", results });
  }
});

const happyPathDescribeQueriesMock = vi
  .fn()
  .mockResolvedValueOnce({ queries: [{ status: "Running" }] })
  .mockResolvedValueOnce({ queries: [{ status: "Running" }] })
  .mockResolvedValueOnce({ queries: [{ status: "Running" }] })
  .mockResolvedValue({ queries: Array(30).fill({ status: "Running" }) });

const happyPathClient = {
  send: (command) => {
    if (command instanceof StartQueryCommand) {
      return happyPathStartQueryMock();
    }

    if (command instanceof GetQueryResultsCommand) {
      return happyPathGetQueryResultsMock();
    }

    if (command instanceof DescribeQueriesCommand) {
      return happyPathDescribeQueriesMock();
    }
  },
};

describe("CloudWatchQuery", () => {
  describe("with valid params", () => {
    const endDate = new Date();
    const cloudWatchQuery = new CloudWatchQuery(happyPathClient, {
      logGroupNames: ["some/log/group"],
      dateRange: [
        new Date(
          endDate.getFullYear(),
          endDate.getMonth() - 1,
          endDate.getDate(),
          endDate.getHours(),
        ),
        endDate,
      ],
    });

    it("should output", async () => {
      const results = await cloudWatchQuery.run();
      expect(results).toEqual([
        [
          { field: "a", value: "1" },
          { field: "b", value: "2" },
        ],
        [
          { field: "c", value: "3" },
          { field: "d", value: "4" },
        ],
      ]);
    });
  });
});
