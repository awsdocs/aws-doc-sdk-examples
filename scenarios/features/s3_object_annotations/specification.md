# Amazon S3 Object Annotations Specification

This document contains the specification for the *Amazon S3 Object Annotations Feature Scenario*, a feature scenario that showcases the new S3 Object Annotations capability using AWS SDKs. It is primarily intended for the AWS code examples team to use while developing this example in additional languages.

Amazon S3 now supports object annotations — named payloads of 1 byte to 1 MiB that you can attach to S3 objects. Each object can have up to 1,000 annotations in flexible formats like JSON, XML, or plain text. Annotations can be added, retrieved, listed, updated, and deleted independently of the object itself, making it easy to enrich stored data with evolving context such as ML inference results, content classifications, processing status, or business metadata — all without re-uploading or modifying the original object.

This scenario demonstrates the full lifecycle of S3 object annotations: creating a bucket, uploading an object, attaching multiple annotations, retrieving and listing them, updating an annotation, conditionally deleting annotations, and cleaning up resources.

### Resources

- An Amazon S3 general purpose bucket (created and deleted during the scenario).
- One or more test objects uploaded to the bucket during the scenario.
- No additional AWS resources (IAM roles, CloudFormation stacks, etc.) are required.

### Relevant documentation

- [What is Amazon S3?](https://docs.aws.amazon.com/AmazonS3/latest/userguide/Welcome.html)
- [PutObjectAnnotation API Reference](https://docs.aws.amazon.com/AmazonS3/latest/API/API_PutObjectAnnotation.html)
- [GetObjectAnnotation API Reference](https://docs.aws.amazon.com/AmazonS3/latest/API/API_GetObjectAnnotation.html)
- [ListObjectAnnotations API Reference](https://docs.aws.amazon.com/AmazonS3/latest/API/API_ListObjectAnnotations.html)
- [DeleteObjectAnnotation API Reference](https://docs.aws.amazon.com/AmazonS3/latest/API/API_DeleteObjectAnnotation.html)
- [Amazon S3 Annotations Blog Post](https://aws.amazon.com/blogs/storage/analyze-amazon-s3-annotations-at-scale-with-materialized-views/)

### API Actions Used

- [CreateBucket](https://docs.aws.amazon.com/AmazonS3/latest/API/API_CreateBucket.html) — Creates a new S3 bucket to host test objects and annotations.
- [PutObject](https://docs.aws.amazon.com/AmazonS3/latest/API/API_PutObject.html) — Uploads a test object to the bucket.
- [PutObjectAnnotation](https://docs.aws.amazon.com/AmazonS3/latest/API/API_PutObjectAnnotation.html) — Attaches a named annotation payload (1 byte to 1 MiB) to an S3 object. Required parameters: `Bucket`, `Key`, `AnnotationName`, `AnnotationPayload`. Annotation names must be 1-512 bytes and may contain only Unicode letters, digits, underscores (`_`), periods (`.`), and hyphens (`-`); they cannot start with `aws` or `s3`, and cannot contain `/`. This scenario uses a period as a namespace delimiter (e.g. `ml.sentiment-analysis`) for prefix filtering. Optional parameters include `VersionId`, `ObjectIfMatch` (conditional write based on object ETag), and `ChecksumAlgorithm`. Returns the annotation ETag, object version ID, and checksum values.
- [GetObjectAnnotation](https://docs.aws.amazon.com/AmazonS3/latest/API/API_GetObjectAnnotation.html) — Retrieves a specific annotation by name from an S3 object. Required parameters: `Bucket`, `Key`, `AnnotationName`. Optional parameters include `VersionId` and `ChecksumMode` (set to `ENABLED` to validate checksums). Returns the annotation payload, ETag, content length, last modified date, and checksum values.
- [ListObjectAnnotations](https://docs.aws.amazon.com/AmazonS3/latest/API/API_ListObjectAnnotations.html) — Lists annotations attached to an S3 object. Supports pagination and prefix filtering. Required parameters: `Bucket`, `Key`. Optional parameters include `AnnotationPrefix`, `MaxAnnotationResults`, `ContinuationToken`, and `VersionId`. Returns a list of `AnnotationEntry` items (each with `AnnotationName`, `LastModified`, `ETag`, `Size`), along with pagination fields (`IsTruncated`, `NextContinuationToken`).
- [DeleteObjectAnnotation](https://docs.aws.amazon.com/AmazonS3/latest/API/API_DeleteObjectAnnotation.html) — Permanently deletes a specific annotation from an S3 object. Required parameters: `Bucket`, `Key`, `AnnotationName`. Optional parameters include `VersionId` and `ObjectIfMatch` (conditional delete using the object's ETag to prevent race conditions). Returns the object version ID. Note: deletion is permanent; annotations are not independently versioned.
- [DeleteObject](https://docs.aws.amazon.com/AmazonS3/latest/API/API_DeleteObject.html) — Removes the test object from the bucket during cleanup.
- [DeleteBucket](https://docs.aws.amazon.com/AmazonS3/latest/API/API_DeleteBucket.html) — Deletes the S3 bucket during cleanup.

## Scenario

This scenario walks through the complete lifecycle of S3 object annotations, demonstrating how to attach, retrieve, list, update, and delete custom metadata on S3 objects without modifying the objects themselves.

### Setup

1. **Create an S3 bucket.**
   - Prompt the user for a bucket name prefix. Generate a unique bucket name (e.g., `annotations-demo-<random-suffix>`).
   - Call `CreateBucket` to create a general purpose bucket.
   - Display the bucket name.

2. **Upload a test object.**
   - Call `PutObject` to upload a small test file (e.g., a text file with sample content) to the bucket.
   - Display the object key and ETag.

Example output:
```
--------------------------------------------------------------------------------
Welcome to the Amazon S3 Object Annotations demo!

S3 Object Annotations let you attach up to 1,000 named payloads (each up to 1 MiB)
to any S3 object. Annotations can store rich metadata like JSON, XML, or plain text
without modifying the original object.
--------------------------------------------------------------------------------
Creating bucket 'annotations-demo-a1b2c3d4'...
Bucket created successfully.

Uploading test object 'sample-data.txt'...
Object uploaded. ETag: "d41d8cd98f00b204e9800998ecf8427e"
--------------------------------------------------------------------------------
```

### Attach annotations to the object

3. **Put a first annotation (plain text).**
   - Call `PutObjectAnnotation` with:
     - `AnnotationName`: `processing-status`
     - `AnnotationPayload`: `{"status": "pending", "submitted": "2026-09-16T10:00:00Z"}`
   - Display the annotation name and returned ETag.

4. **Put a second annotation (JSON).**
   - Call `PutObjectAnnotation` with:
     - `AnnotationName`: `ml.sentiment-analysis`
     - `AnnotationPayload`: `{"sentiment": "positive", "confidence": 0.95, "model": "v2.1"}`
   - Display the annotation name and returned ETag.

5. **Put a third annotation (classification label).**
   - Call `PutObjectAnnotation` with:
     - `AnnotationName`: `ml.content-classification`
     - `AnnotationPayload`: `{"category": "technical-documentation", "language": "en", "topics": ["cloud", "storage"]}`
   - Display the annotation name and returned ETag.

Example output:
```
--------------------------------------------------------------------------------
Attaching annotations to 'sample-data.txt'...

  Added annotation 'processing-status' (ETag: "abc123...")
  Added annotation 'ml.sentiment-analysis' (ETag: "def456...")
  Added annotation 'ml.content-classification' (ETag: "ghi789...")

3 annotations attached successfully.
--------------------------------------------------------------------------------
```

### Retrieve and list annotations

6. **Get a specific annotation by name.**
   - Call `GetObjectAnnotation` with `AnnotationName`: `ml.sentiment-analysis`.
   - Display the annotation payload, size, ETag, and last modified date.

7. **List all annotations on the object.**
   - Call `ListObjectAnnotations` with no prefix filter.
   - Display each annotation entry: name, size, ETag, and last modified date.

8. **List annotations with a prefix filter.**
   - Call `ListObjectAnnotations` with `AnnotationPrefix`: `ml.`.
   - Display only the annotations matching the prefix, demonstrating how prefix filtering narrows results.

Example output:
```
--------------------------------------------------------------------------------
Retrieving annotation 'ml.sentiment-analysis'...

  Payload: {"sentiment": "positive", "confidence": 0.95, "model": "v2.1"}
  Size: 62 bytes
  ETag: "def456..."
  Last Modified: 2026-09-16T10:01:00Z

Listing all annotations on 'sample-data.txt'...
  Found 3 annotation(s):
    1. "ml.content-classification" (87 bytes)
    2. "ml.sentiment-analysis" (62 bytes)
    3. "processing-status" (58 bytes)

Listing annotations with prefix 'ml.'...
  Found 2 annotation(s):
    1. "ml.content-classification" (87 bytes)
    2. "ml.sentiment-analysis" (62 bytes)
--------------------------------------------------------------------------------
```

### Update an existing annotation

9. **Update an annotation by overwriting it.**
   - Call `PutObjectAnnotation` with the same `AnnotationName` (`processing-status`) but a new payload:
     `{"status": "completed", "submitted": "2026-09-16T10:00:00Z", "completed": "2026-09-16T10:05:00Z"}`
   - Display a message indicating the annotation was updated, along with the new ETag.

10. **Verify the update.**
    - Call `GetObjectAnnotation` with `AnnotationName`: `processing-status`.
    - Display the updated payload to confirm the change.

Example output:
```
--------------------------------------------------------------------------------
Updating annotation 'processing-status' with new content...
  Annotation updated. New ETag: "jkl012..."

Verifying the update...
  Payload: {"status": "completed", "submitted": "2026-09-16T10:00:00Z", "completed": "2026-09-16T10:05:00Z"}
  Update confirmed.
--------------------------------------------------------------------------------
```

### Delete annotations

11. **Delete a single annotation.**
    - Call `DeleteObjectAnnotation` with `AnnotationName`: `processing-status`.
    - Display a message confirming deletion.

12. **Attempt to retrieve the deleted annotation.**
    - Call `GetObjectAnnotation` with `AnnotationName`: `processing-status`.
    - Catch the `NoSuchAnnotation` error and display a message confirming the annotation no longer exists.

13. **List remaining annotations.**
    - Call `ListObjectAnnotations` to confirm only two annotations remain.
    - Display the remaining annotation names.

14. **Delete the remaining annotations.**
    - Call `DeleteObjectAnnotation` for each remaining annotation (`ml.sentiment-analysis`, `ml.content-classification`).
    - Display deletion confirmations.

15. **Verify all annotations are removed.**
    - Call `ListObjectAnnotations` to confirm the list is empty.

Example output:
```
--------------------------------------------------------------------------------
Deleting annotation 'processing-status'...
  Annotation deleted successfully.

Attempting to retrieve deleted annotation 'processing-status'...
  Expected error: NoSuchAnnotation - The annotation does not exist. Deletion confirmed!

Listing remaining annotations...
  Found 2 annotation(s):
    1. "ml.content-classification"
    2. "ml.sentiment-analysis"

Deleting remaining annotations...
  Deleted 'ml.sentiment-analysis'.
  Deleted 'ml.content-classification'.

Verifying all annotations removed...
  0 annotations remaining. All annotations cleaned up.
--------------------------------------------------------------------------------
```

### Cleanup

16. **Delete the test object.**
    - Call `DeleteObject` to remove the test object from the bucket.

17. **Delete the bucket.**
    - Call `DeleteBucket` to remove the bucket.
    - Display a cleanup complete message.

Example output:
```
--------------------------------------------------------------------------------
Cleaning up resources...
  Deleted object 'sample-data.txt'.
  Deleted bucket 'annotations-demo-a1b2c3d4'.
Cleanup complete!
--------------------------------------------------------------------------------
```

### Outcome

After running this scenario, the user will understand how to:
- Attach named annotations (up to 1,000 per object, each up to 1 MiB) to S3 objects.
- Retrieve specific annotations by name.
- List all annotations on an object, with optional prefix filtering for organized naming schemes (e.g., `ml.`, `audit.`).
- Update annotations by overwriting them with `PutObjectAnnotation`.
- Permanently delete annotations, and confirm deletion by handling the `NoSuchAnnotation` error.
- Use annotations to store evolving metadata (ML results, processing status, classifications) without modifying the original object.

## Errors

SDK code examples include basic exception handling for each action used. The table below describes the single most relevant exception to handle for each action in this scenario.

| Action | Error | Handling |
|-|-|-|
| `CreateBucket` | `BucketAlreadyOwnedByYou` | Notify the user the bucket already exists and prompt for a different name. |
| `PutObject` | `NoSuchBucket` | Notify the user the bucket does not exist; verify bucket creation succeeded. |
| `PutObjectAnnotation` | `InvalidAnnotationName` | Notify the user the annotation name is invalid; display naming rules (1-512 bytes, see S3 User Guide). |
| `GetObjectAnnotation` | `NoSuchAnnotation` | Notify the user the specified annotation does not exist on this object. |
| `ListObjectAnnotations` | `NoSuchKey` | Notify the user the specified object does not exist in the bucket. |
| `DeleteObjectAnnotation` | `NoSuchAnnotation` | Notify the user the annotation has already been deleted or does not exist. |
| `DeleteObject` | `NoSuchKey` | Notify the user the object does not exist; it may have already been deleted. |
| `DeleteBucket` | `NoSuchBucket` | Notify the user the bucket does not exist; it may have already been deleted. |

## Metadata

| action / scenario | metadata file | metadata key |
|-|-|-|
| `ListObjectAnnotations` | s3_metadata.yaml | s3_ListObjectAnnotations |
| `CreateBucket` | s3_metadata.yaml | s3_CreateBucket |
| `PutObject` | s3_metadata.yaml | s3_PutObject |
| `PutObjectAnnotation` | s3_metadata.yaml | s3_PutObjectAnnotation |
| `GetObjectAnnotation` | s3_metadata.yaml | s3_GetObjectAnnotation |
| `DeleteObjectAnnotation` | s3_metadata.yaml | s3_DeleteObjectAnnotation |
| `DeleteObject` | s3_metadata.yaml | s3_DeleteObject |
| `DeleteBucket` | s3_metadata.yaml | s3_DeleteBucket |
| `S3 Object Annotations Scenario` | s3_metadata.yaml | s3_Scenario_ObjectAnnotations |
