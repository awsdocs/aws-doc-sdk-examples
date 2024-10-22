// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import type { RequestHandler } from "express";
import type { Sendable } from "./sendable.js";

export type Handler = {
  withClient: ({
    rdsDataClient,
    sesClient,
  }: {
    rdsDataClient?: Sendable;
    sesClient?: Sendable;
  }) => (...params: Parameters<RequestHandler>) => Promise<void>;
};
