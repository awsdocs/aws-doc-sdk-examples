# Amazon S3 Object Annotations - Python SDK Example

## Overview

Amazon S3 Object Annotations are named payloads of 1 byte to 1 MiB that you attach to S3 objects without modifying the objects themselves. With annotations, you can attach up to 1,000 named annotations per object in flexible formats like JSON, XML, and YAML. Common use cases include storing machine learning inference results, AI-generated embeddings, content moderation labels, document classification output, data lineage and audit trails, compliance records such as PII flags or retention policies, and ETL pipeline status.

This example demonstrates how to use the AWS SDK for Python (Boto3) to manage S3 object annotations through their complete lifecycle.

## Scenario Steps

1. **Create an S3 bucket** and upload a test object.
2. **Add annotations** — attach classification, compliance PII scan, and compliance retention annotations with structured JSON payloads.
3. **Retrieve annotations** — get a specific annotation by name and list all annotations on the object.
4. **Filter by prefix** — use `AnnotationPrefix` to list only the `compliance.*` annotations.
5. **Update an annotation** — overwrite the classification annotation with updated data and verify the ETag changed.
6. **Delete annotations** — remove an annotation and verify it no longer appears in the listing.
7. **Cleanup** — delete all remaining annotations, the test object, and the bucket.

## Prerequisites

- Python 3.9 or later
- AWS SDK for Python (Boto3) version 1.43.31 or later
- An AWS account with permissions for S3 operations including:
  - `s3:CreateBucket`, `s3:DeleteBucket`
  - `s3:PutObject`, `s3:DeleteObject`
  - `s3:PutObjectAnnotation`, `s3:GetObjectAnnotation`
  - `s3:ListObjectAnnotations`, `s3:DeleteObjectAnnotation`

## Setup

Install dependencies:

```bash
pip install -r requirements.txt
```

## Running the Examples

### Hello S3

Verify S3 connectivity by listing your buckets:

```bash
python s3_hello.py
```

### Object Annotations Scenario

Run the interactive scenario:

```bash
python scenario_object_annotations.py
```

### Integration Tests

Run the integration tests (requires live AWS credentials):

```bash
python -m pytest test_object_annotations.py -v -m integ
```

## File Structure

```
├── s3_hello.py                      # Hello S3 example (ListBuckets)
├── s3_wrapper.py                    # Wrapper class for S3 annotation operations
├── scenario_object_annotations.py   # Interactive scenario
├── test_object_annotations.py       # Integration tests
├── s3_metadata.yaml                 # Metadata for doc generation
├── requirements.txt                 # Python dependencies
└── README.md                        # This file
```

## Additional Resources

- [Annotating your objects](https://docs.aws.amazon.com/AmazonS3/latest/userguide/annotations-overview.html)
- [PutObjectAnnotation API Reference](https://docs.aws.amazon.com/AmazonS3/latest/API/API_PutObjectAnnotation.html)
- [GetObjectAnnotation API Reference](https://docs.aws.amazon.com/AmazonS3/latest/API/API_GetObjectAnnotation.html)
- [ListObjectAnnotations API Reference](https://docs.aws.amazon.com/AmazonS3/latest/API/API_ListObjectAnnotations.html)
- [DeleteObjectAnnotation API Reference](https://docs.aws.amazon.com/AmazonS3/latest/API/API_DeleteObjectAnnotation.html)
- [Analyze Amazon S3 annotations at scale with materialized views](https://aws.amazon.com/blogs/storage/analyze-amazon-s3-annotations-at-scale-with-materialized-views/)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
