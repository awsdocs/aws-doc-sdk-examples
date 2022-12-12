/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe } from "@jest/globals";
import { createString } from "../src/s3_multipartupload.js";

describe('createString', () => {
  it('should create a string of the correct size', () => {
    expect(createString(10).length).toEqual(10);
  });
})