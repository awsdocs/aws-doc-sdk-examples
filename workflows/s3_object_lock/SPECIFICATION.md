# Amazon S3 Object Lock Workflow - Technical specification

This document contains the technical specifications for _Amazon S3 Object Lock Workflow_,
a workflow scenario that showcases AWS services and SDKs. It is primarily intended for the AWS code
examples team to use while developing this example in additional languages.

This document explains the following:

- Architecture and features of the example workflow.
- Metadata information for the scenario.
- Sample reference output.

For an introduction, see the [README.md](README.md).

---

### Table of contents

- [Architecture](#architecture)
- [User input](#user-input)
- [Common resources](#common-resources)
- [Building the queries](#building-the-queries)
- [Output](#output)
- [Metadata](#metadata)

## Architecture

- Amazon Simple Storage Service (Amazon S3) Buckets and objects (created in the scenario)
  - One bucket with no locking features enabled.
  - One bucket with object lock enabled on creation.
  - One bucket with object lock enabled after creation.
  - One bucket with a default retention period.

---

## User input

The example should allow the user to examine the bucket and object lock settings, as well as attempt to delete or overwrite files in any of the example buckets.

---

## Output

The user should be guided through the 3 main parts of the example:
1. Setting up buckets and objects.
2. Explore the behavior of locked objects.
3. Cleaning up the objects and buckets.

Example:

```

```

---

## Metadata

| action / scenario            | metadata file    | metadata key                      |
|------------------------------|------------------| --------------------------------- |
| `GetObjectLegalHold`         | s3_metadata.yaml | s3_GetObjectLegalHoldConfiguration   |
| `GetObjectLockConfiguration` | s3_metadata.yaml | s3_GetObjectLockConfiguration   |
| `GetObjectRetention`         | s3_metadata.yaml | s3_GetObjectRetention   |
| `PutObjectLegalHold`         | s3_metadata.yaml | s3_PutObjectLegalHold   |
| `PutObjectLockConfiguration` | s3_metadata.yaml | s3_PutObjectLockConfiguration   |
| `PutObjectRetention`         | s3_metadata.yaml | s3_PutObjectRetention   |
| `S3 Object Lock Scenario`    | s3_metadata.yaml | s3_Scenario_ObjectLock   |
