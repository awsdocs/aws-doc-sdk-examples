// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
declare interface Sendable {
  send: <R = any>(command: any) => Promise<R>
}