// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
/* eslint-disable -- This file existed pre-eslint configuration. Fix the next time the file is touched. */

declare interface Sendable {
  send: <R = any>(command: any) => Promise<R>;
}
