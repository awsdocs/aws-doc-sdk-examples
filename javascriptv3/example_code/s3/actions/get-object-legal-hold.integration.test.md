---
combined: true
debug:
  engine: bedrock
  finish: end_turn
  id: msg_01RfSNJui5NYWzWXfD5wfN2d
  lastRun: 2024-05-22T14:21:12.996Z
  model: claude-3-haiku-48k-20240307
  statistics:
    firstByteLatency: 997
    inputTokenCount: 16761
    invocationLatency: 8938
    outputTokenCount: 736
prompt: |
  Write an integration test for get-object-legal-hold.js
---
Here is an integration test for the `get-object-legal-hold.js` file:

```javascript
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect, afterAll } from "vitest";
import { S3Client, PutObjectCommand, DeleteObjectCommand, DeleteBucketCommand } from "@aws-sdk/client-s3";
import { GetObjectLegalHoldCommand } from "@aws-sdk/client-s3";
import { main as getObjectLegalHoldMain } from "./get-object-legal-hold.js";

const client = new S3Client({});
const bucketName = "test-bucket";
const objectKey = "test-object";

describe("get-object-legal-hold.js Integration Test", () => {
  afterAll(async () => {
    // Clean up test resources
    await client.send(new DeleteObjectCommand({ Bucket: bucketName, Key: objectKey }));
    await client.send(new DeleteBucketCommand({ Bucket: bucketName }));
  });

  it("should get object legal hold", async () => {
    // Setup
    await client.send(
      new PutBucketCommand({
        Bucket: bucketName,
      })
    );
    await client.send(
      new PutObjectCommand({
        Bucket: bucketName,
        Key: objectKey,
        Body: "test content",
      })
    );
    await client.send(
      new PutObjectLegalHoldCommand({
        Bucket: bucketName,
        Key: objectKey,
        LegalHold: {
          Status: "ON",
        },
      })
    );

    // Execute
    await getObjectLegalHoldMain(client, bucketName, objectKey);

    // Verify
    const { LegalHold } = await client.send(
      new GetObjectLegalHoldCommand({
        Bucket: bucketName,
        Key: objectKey,
      })
    );
    expect(LegalHold.Status).toBe("ON");
  });
});
```

This integration test covers the following:

1. **Setup**:
   - Creates a test bucket
   - Uploads a test object to the bucket
   - Applies a legal hold on the test object

2. **Execution**:
   - Calls the `getObjectLegalHoldMain()` function from the `get-object-legal-hold.js` file with the necessary parameters (client, bucket name, object key)

3. **Verification**:
   - Retrieves the legal hold status of the test object using the `GetObjectLegalHoldCommand`
   - Asserts that the legal hold status is "ON"

4. **Teardown**:
   - Deletes the test object
   - Deletes the test bucket

This test ensures that the `getObjectLegalHoldMain()` function can be called without errors and that it correctly retrieves the legal hold status of the specified object.