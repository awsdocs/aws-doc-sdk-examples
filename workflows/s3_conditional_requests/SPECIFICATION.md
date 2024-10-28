# Amazon S3 Conditional Requests Feature Scenario - Technical specification

This document contains the technical specifications for _Amazon S3 Conditional Requests Feature Scenario_,
a feature scenario that showcases AWS services and SDKs. It is primarily intended for the AWS code
examples team to use while developing this example in additional languages.

This document explains the following:

- Architecture and features of the example scenario.
- Metadata information for the scenario.
- Sample reference output.

For an introduction, see the [README.md](README.md).

---

### Table of contents

- [Resources and User Input](#resources-and-user-input)
- [Errors](#errors)
- [Metadata](#metadata)

## Resources and User Input

- Amazon Simple Storage Service (Amazon S3) Buckets (created in the scenario).
  - One bucket as the source bucket.
  - One bucket as the target bucket.
Bucket names will begin with a prefix provided by the user.

Example:
```
todo

```
- Amazon S3 objects created in the scenario:
  - One test file in the source bucket.
    
Example:

```
todo
```

- Conditional Requests
  - In order to cover all the example topics in the S3 guide section, the scenario covers write, copy, or read operations with a precondition using the SDK.
  - The user can choose the type of precondition to add.
  - This section should provide a menu of options to the user, so they can observe the results of their conditional request.

Example
```
Explore S3 conditional requests by selecting one of the following choices:
        1. List all files in buckets.
        2. Perform a conditional read.
        3. Perform a conditional copy.
        4. Perform a conditional write.
        5. Finish the scenario and clean up resources.
        

--------------------------------------------------------------------------------

todo
--------------------------------------------------------------------------------
```
- Cleanup
  - The scenario should get the full list of objects, and remove all objects before deleting the buckets.
    - The user should be notified if the delete operation cannot occur.

Example:

```
todo
```

---

## Errors
The PreconditionFailed exceptions are part of the flow of this scenario. After a success or failure,
the user can print the contents of the buckets to see the result.

| action       | Error                 | Handling                                   |
|--------------|-----------------------|--------------------------------------------|
| `GetObject`  | PreconditionFailed    | Notify the user and do not print contents. |
| `GetObject`  | ObjectNotModified 304 | Notify the user and do not print contents. |
| `CopyObject` | PreconditionFailed    | Notify the user of the failure.            |
| `CopyObject` | ObjectNotModified 304 | Notify the user of the failure.            |
| `PutObject`  | PreconditionFailed    | Notify the user of the failure.            |


---

## Metadata
For languages which already have an entry for the action, add a description for the snippet describing the conditional request options.

| action / scenario                  | metadata file    | metadata key                    |
|------------------------------------|------------------|---------------------------------|
| `GetObject`                        | s3_metadata.yaml | s3_GetObject                    |
| `CopyObject`                       | s3_metadata.yaml | s3_CopyObject                   |
| `PutObject`                        | s3_metadata.yaml | s3_PutObject                    |
| `S3 Conditional Requests Scenario` | s3_metadata.yaml | s3_Scenario_ConditionalRequests |

