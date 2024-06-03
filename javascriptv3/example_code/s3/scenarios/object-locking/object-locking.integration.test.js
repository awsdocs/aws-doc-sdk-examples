// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { describe, it, expect, afterAll } from "vitest";
import {
  S3Client,
  ListBucketsCommand,
  GetBucketVersioningCommand,
  GetObjectLockConfigurationCommand,
  ListObjectsCommand,
  GetObjectLegalHoldCommand,
  GetObjectRetentionCommand,
} from "@aws-sdk/client-s3";
import {
  createBucketsAction,
  updateRetentionAction,
  populateBucketsAction,
  updateLockPolicyAction,
  setLegalHoldFileEnabledAction,
  setRetentionPeriodFileEnabledAction,
  setLegalHoldFileRetentionAction,
  setRetentionPeriodFileRetentionAction,
} from "./setup.steps.js";
import * as Scenarios from "@aws-doc-sdk-examples/lib/scenario/index.js";
import { legallyEmptyAndDeleteBuckets } from "../../libs/s3Utils.js";

const bucketPrefix = "js-object-locking";
const client = new S3Client({});

describe("S3 Object Locking Integration Tests", () => {
  const state = {
    noLockBucketName: `${bucketPrefix}-no-lock`,
    lockEnabledBucketName: `${bucketPrefix}-lock-enabled`,
    retentionBucketName: `${bucketPrefix}-retention-after-creation`,
  };

  afterAll(async () => {
    // Clean up resources
    const buckets = [
      state.noLockBucketName,
      state.lockEnabledBucketName,
      state.retentionBucketName,
    ];

    await legallyEmptyAndDeleteBuckets(buckets);
  });

  it("should create buckets with correct configurations", async () => {
    const action = createBucketsAction(Scenarios, client);
    await action.handle(state);

    const bucketList = await client.send(new ListBucketsCommand({}));
    expect(bucketList.Buckets?.map((bucket) => bucket.Name)).toContain(
      state.noLockBucketName,
    );
    expect(bucketList.Buckets?.map((bucket) => bucket.Name)).toContain(
      state.lockEnabledBucketName,
    );
    expect(bucketList.Buckets?.map((bucket) => bucket.Name)).toContain(
      state.retentionBucketName,
    );
  });

  it("should enable versioning and set retention period on retention bucket", async () => {
    const action = updateRetentionAction(Scenarios, client);
    await action.handle(state);

    const versioningConfig = await client.send(
      new GetBucketVersioningCommand({ Bucket: state.retentionBucketName }),
    );
    expect(versioningConfig.Status).toEqual("Enabled");

    const lockConfig = await client.send(
      new GetObjectLockConfigurationCommand({
        Bucket: state.retentionBucketName,
      }),
    );
    expect(lockConfig.ObjectLockConfiguration?.ObjectLockEnabled).toEqual(
      "Enabled",
    );
    expect(
      lockConfig.ObjectLockConfiguration?.Rule?.DefaultRetention?.Mode,
    ).toEqual("GOVERNANCE");
    expect(
      lockConfig.ObjectLockConfiguration?.Rule?.DefaultRetention?.Years,
    ).toEqual(1);
  });

  it("should upload files to buckets", async () => {
    const action = populateBucketsAction(Scenarios, client);
    await action.handle(state);

    const noLockObjects = await client.send(
      new ListObjectsCommand({ Bucket: state.noLockBucketName }),
    );
    expect(noLockObjects.Contents?.map((obj) => obj.Key)).toContain(
      "file0.txt",
    );
    expect(noLockObjects.Contents?.map((obj) => obj.Key)).toContain(
      "file1.txt",
    );

    const lockEnabledObjects = await client.send(
      new ListObjectsCommand({ Bucket: state.lockEnabledBucketName }),
    );
    expect(lockEnabledObjects.Contents?.map((obj) => obj.Key)).toContain(
      "file0.txt",
    );
    expect(lockEnabledObjects.Contents?.map((obj) => obj.Key)).toContain(
      "file1.txt",
    );

    const retentionObjects = await client.send(
      new ListObjectsCommand({ Bucket: state.retentionBucketName }),
    );
    expect(retentionObjects.Contents?.map((obj) => obj.Key)).toContain(
      "file0.txt",
    );
    expect(retentionObjects.Contents?.map((obj) => obj.Key)).toContain(
      "file1.txt",
    );
  });

  it("should add object lock policy to lock-enabled bucket", async () => {
    const action = updateLockPolicyAction(Scenarios, client);
    await action.handle(state);

    const lockConfig = await client.send(
      new GetObjectLockConfigurationCommand({
        Bucket: state.lockEnabledBucketName,
      }),
    );
    expect(lockConfig.ObjectLockConfiguration?.ObjectLockEnabled).toEqual(
      "Enabled",
    );
  });

  it("should set legal hold on enabled file", async () => {
    const action = setLegalHoldFileEnabledAction(Scenarios, client);
    state.confirmSetLegalHoldFileEnabled = true;
    await action.handle(state);

    const legalHold = await client.send(
      new GetObjectLegalHoldCommand({
        Bucket: state.lockEnabledBucketName,
        Key: "file0.txt",
      }),
    );
    expect(legalHold.LegalHold?.Status).toEqual("ON");
  });

  it("should set retention period on enabled file", async () => {
    const action = setRetentionPeriodFileEnabledAction(Scenarios, client);
    state.confirmSetRetentionPeriodFileEnabled = true;
    await action.handle(state);

    const retention = await client.send(
      new GetObjectRetentionCommand({
        Bucket: state.lockEnabledBucketName,
        Key: "file1.txt",
      }),
    );
    expect(retention.Retention?.Mode).toEqual("GOVERNANCE");
    expect(retention.Retention?.RetainUntilDate).toBeDefined();
  });

  it("should set legal hold on retention file", async () => {
    const action = setLegalHoldFileRetentionAction(Scenarios, client);
    state.confirmSetLegalHoldFileRetention = true;
    await action.handle(state);

    const legalHold = await client.send(
      new GetObjectLegalHoldCommand({
        Bucket: state.retentionBucketName,
        Key: "file0.txt",
      }),
    );
    expect(legalHold.LegalHold?.Status).toEqual("ON");
  });

  it("should set retention period on retention file", async () => {
    const action = setRetentionPeriodFileRetentionAction(Scenarios, client);
    state.confirmSetRetentionPeriodFileRetention = true;
    await action.handle(state);

    const retention = await client.send(
      new GetObjectRetentionCommand({
        Bucket: state.retentionBucketName,
        Key: "file1.txt",
      }),
    );
    expect(retention.Retention?.Mode).toEqual("GOVERNANCE");
    expect(retention.Retention?.RetainUntilDate).toBeDefined();
  });
});
