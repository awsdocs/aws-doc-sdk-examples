// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/**
 * These variables are created by the CDK deployment.
 */

export const LABELS_TABLE_NAME =
  process.env.LABELS_TABLE_NAME || "labels-table-name";
export const STORAGE_BUCKET_NAME =
  process.env.STORAGE_BUCKET_NAME || "storage-bucket-name";
export const WORKING_BUCKET_NAME =
  process.env.WORKING_BUCKET_NAME || "working-bucket-name";
export const NOTIFICATION_TOPIC =
  process.env.NOTIFICATION_TOPIC || "notification-topic";
