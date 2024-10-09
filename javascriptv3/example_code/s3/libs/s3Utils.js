// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import {
  CreateBucketCommand,
  PutBucketPolicyCommand,
  ListObjectsCommand,
  DeleteBucketCommand,
  DeleteObjectsCommand,
  ListObjectVersionsCommand,
  GetObjectLegalHoldCommand,
  PutObjectLegalHoldCommand,
  GetObjectRetentionCommand,
  DeleteObjectCommand,
  waitUntilObjectNotExists,
  waitUntilBucketExists,
  waitUntilBucketNotExists,
} from "@aws-sdk/client-s3";
import { client as s3Client } from "../client.js";

export async function createBucket(bucketName) {
  const createBucketCommand = new CreateBucketCommand({
    Bucket: bucketName,
  });

  await s3Client.send(createBucketCommand);
  await waitUntilBucketExists({ client: s3Client }, { Bucket: bucketName });
}

export async function deleteBucket(bucketName) {
  const deleteBucketCommand = new DeleteBucketCommand({
    Bucket: bucketName,
  });

  await s3Client.send(deleteBucketCommand);
  await waitUntilBucketNotExists({ client: s3Client }, { Bucket: bucketName });
}

export async function emptyBucket(bucketName) {
  const listObjectsResult = await s3Client.send(
    new ListObjectsCommand({ Bucket: bucketName }),
  );
  const objects = listObjectsResult.Contents;
  const objectIdentifiers = objects.map((o) => ({ Key: o.Key }));
  await s3Client.send(
    new DeleteObjectsCommand({
      Bucket: bucketName,
      Delete: { Objects: objectIdentifiers },
    }),
  );
  for (const objId of objectIdentifiers) {
    await waitUntilObjectNotExists(
      { client: s3Client },
      { Bucket: bucketName, Key: objId.Key },
    );
  }
}

export function putBucketPolicyAllowPuts(bucketName, sid) {
  const putBucketPolicyCommand = new PutBucketPolicyCommand({
    Bucket: bucketName,
    Policy: JSON.stringify({
      Version: "2012-10-17",
      Statement: [
        {
          Sid: sid,
          Effect: "Allow",
          Principal: {
            Service: "ses.amazonaws.com",
          },
          Action: "s3:PutObject",
          Resource: `arn:aws:s3:::${bucketName}/*`,
        },
      ],
    }),
  });

  return s3Client.send(putBucketPolicyCommand);
}

export async function legallyEmptyAndDeleteBuckets(bucketNames) {
  for (const bucketName of bucketNames) {
    const objectsResponse = await s3Client.send(
      new ListObjectVersionsCommand({ Bucket: bucketName }),
    );

    for (const version of objectsResponse.Versions || []) {
      const { Key, VersionId } = version;

      try {
        const legalHold = await s3Client.send(
          new GetObjectLegalHoldCommand({
            Bucket: bucketName,
            Key,
            VersionId,
          }),
        );
        if (legalHold.LegalHold?.Status === "ON") {
          await s3Client.send(
            new PutObjectLegalHoldCommand({
              Bucket: bucketName,
              Key,
              VersionId,
              LegalHold: {
                Status: "OFF",
              },
            }),
          );
        }
      } catch (err) {
        console.log(
          `Unable to fetch legal hold for ${Key} in ${bucketName}: '${err.message}'`,
        );
      }

      try {
        const retention = await s3Client.send(
          new GetObjectRetentionCommand({
            Bucket: bucketName,
            Key,
            VersionId,
          }),
        );
        if (retention.Retention?.Mode === "GOVERNANCE") {
          await s3Client.send(
            new DeleteObjectCommand({
              Bucket: bucketName,
              Key,
              VersionId,
              BypassGovernanceRetention: true,
            }),
          );
        }
      } catch (err) {
        console.log(
          `Unable to fetch object lock retention for ${Key} in ${bucketName}: '${err.message}'`,
        );
      }

      await s3Client.send(
        new DeleteObjectCommand({ Bucket: bucketName, Key, VersionId }),
      );
    }

    await s3Client.send(new DeleteBucketCommand({ Bucket: bucketName }));
    console.log(`Delete for ${bucketName} complete.`);
  }
}
