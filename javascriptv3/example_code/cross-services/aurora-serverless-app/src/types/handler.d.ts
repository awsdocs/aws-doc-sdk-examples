// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { RequestHandler } from "express";

declare type Handler = {
  withClient: ({
    rdsDataClient,
    sesClient,
  }: {
    rdsDataClient?: Sendable;
    sesClient?: Sendable;
  }) => (...params: Parameters<RequestHandler>) => Promise<void>;
};
