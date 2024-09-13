// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { describe, beforeAll, afterAll, it } from "vitest";

import {
  RULE_SET_NAME,
  S3_BUCKET_NAME,
  run,
} from "../src/ses_createreceiptrule";
import {
  createReceiptRuleSet,
  deleteReceiptRuleSet,
} from "../src/libs/sesUtils";
import {
  createBucket,
  deleteBucket,
  emptyBucket,
  putBucketPolicyAllowPuts,
} from "../../s3/libs/s3Utils.js";

describe("ses_createreceiptrule", () => {
  beforeAll(async () => {
    try {
      await createBucket(S3_BUCKET_NAME);
      await putBucketPolicyAllowPuts(S3_BUCKET_NAME, "AllowSESPuts");
      await createReceiptRuleSet(RULE_SET_NAME);
    } catch (e) {
      console.error(e);
    }
  });

  afterAll(async () => {
    try {
      await emptyBucket(S3_BUCKET_NAME);
      await deleteBucket(S3_BUCKET_NAME);
      await deleteReceiptRuleSet(RULE_SET_NAME);
    } catch (e) {
      console.error(e);
    }
  });

  it("should create a receipt rule", async () => {
    await run();
  });
});
