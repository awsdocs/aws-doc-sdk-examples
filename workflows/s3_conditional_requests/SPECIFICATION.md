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
  - One test file in the source bucket.
Bucket names will begin with a prefix provided by the user.

Example:
```
----------------------------------------------------------------------------------------
Welcome to the Amazon S3 conditional requests example.
----------------------------------------------------------------------------------------
This example demonstrates the use of conditional requests for S3 operations.
You can use conditional requests to add preconditions to S3 read requests to return or copy
an object based on its Entity tag (ETag), or last modified date. 
You can use a conditional write requests to prevent overwrites by ensuring 
there is no existing object with the same key. 

This example will allow you to perform conditional reads
and writes that will succeed or fail based on your selected options.

Sample buckets and a sample object will be created as part of the example.
        
Enter a bucket name prefix: test555
Created source bucket: test555-source-279 and destination bucket: test555-dest-279
Uploading file test-upload-file.txt to bucket test555-source-279

```

- Conditional Requests
  - In order to cover all the example topics in the S3 guide section, the scenario covers write, copy, or read operations with a precondition using the SDK.
  - The user can choose the type of precondition to add.
  - This section should provide a menu of options to the user, so they can observe the results of their conditional request.
  - Known exceptions due to the conditional operations are expected and should not end the scenario.

- Menu options
  - Print list of bucket items
    - Iterate through buckets and objects, printing each one with their corresponding ETag.
  - Conditional Read
    - Provide the following options, and print the results of the operation.
      - If-Match: using the object's ETag. This condition should succeed. 
      - If-None-Match: using the object's ETag. This condition should fail. 
      - If-Modified-Since: using yesterday's date. This condition should succeed. 
      - If-Unmodified-Since: using yesterday's date. This condition should fail.
  - Conditional Copy
    - Request a new key for the copied item. Copy it to the destination bucket. Print the results.
    - Provide the following options, and print the results of the operation.
      - If-Match: using the object's ETag. This condition should succeed.
      - If-None-Match: using the object's ETag. This condition should fail.
      - If-Modified-Since: using yesterday's date. This condition should succeed.
      - If-Unmodified-Since: using yesterday's date. This condition should fail.
  - Conditional Write
    - Request a key for the new object.
    - Attempt the write with an If-None-Match condition.
    - If it is a duplicate, the operation will fail. Print the results.

Example
```
----------------------------------------------------------------------------------------
Choose an action to explore some example conditional requests.
1. Print list of bucket items.
2. Perform a conditional read.
3. Perform a conditional copy.
4. Perform a conditional write.
5. Clean up and exit.
Which action would you like to take? 
        
--------------------------------------------------------------------------------

Which action would you like to take? 1
Listing the objects and buckets.
	 Items in bucket test555-source-279
		 object: test-upload-file.txt ETag "3e3d5f53cec929a350af061a39a3a19d"
	 Items in bucket test555-dest-279
		No objects found.
--------------------------------------------------------------------------------
----------------------------------------------------------------------------------------
Choose an action to explore some example conditional requests.
1. Print list of bucket items.
2. Perform a conditional read.
3. Perform a conditional copy.
4. Perform a conditional write.
5. Clean up and exit.
Which action would you like to take? 2
Perform a conditional read.
1. If-Match: using the object's ETag. This condition should succeed.
2. If-None-Match: using the object's ETag. This condition should fail.
3. If-Modified-Since: using yesterday's date. This condition should succeed.
4. If-Unmodified-Since: using yesterday's date. This condition should fail.
Enter the condition type : 1
	Conditional read successful. Here are the first 20 bytes of the object:

	b'This is a test file '
----------------------------------------------------------------------------------------
Choose an action to explore some example conditional requests.
1. Print list of bucket items.
2. Perform a conditional read.
3. Perform a conditional copy.
4. Perform a conditional write.
5. Clean up and exit.
Which action would you like to take? 2
Perform a conditional read.
1. If-Match: using the object's ETag. This condition should succeed.
2. If-None-Match: using the object's ETag. This condition should fail.
3. If-Modified-Since: using yesterday's date. This condition should succeed.
4. If-Unmodified-Since: using yesterday's date. This condition should fail.
Enter the condition type : 2
	Conditional read failed: Object not modified
----------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------
Choose an action to explore some example conditional requests.
1. Print list of bucket items.
2. Perform a conditional read.
3. Perform a conditional copy.
4. Perform a conditional write.
5. Clean up and exit.
Which action would you like to take? 3
Perform a conditional copy.
1. If-Match: using the object's ETag. This condition should succeed.
2. If-None-Match: using the object's ETag. This condition should fail.
3. If-Modified-Since: using yesterday's date. This condition should succeed.
4. If-Unmodified-Since: using yesterday's date. This condition should fail.
Enter the condition type : 1
Enter an object key: test44
	Conditional copy successful for key test44 in bucket test555-dest-279.
----------------------------------------------------------------------------------------
Choose an action to explore some example conditional requests.
1. Print list of bucket items.
2. Perform a conditional read.
3. Perform a conditional copy.
4. Perform a conditional write.
5. Clean up and exit.
Which action would you like to take? 3
Perform a conditional copy.
1. If-Match: using the object's ETag. This condition should succeed.
2. If-None-Match: using the object's ETag. This condition should fail.
3. If-Modified-Since: using yesterday's date. This condition should succeed.
4. If-Unmodified-Since: using yesterday's date. This condition should fail.
Enter the condition type : 2
Enter an object key: test44
	Conditional copy failed: Precondition failed
----------------------------------------------------------------------------------------
Choose an action to explore some example conditional requests.
1. Print list of bucket items.
2. Perform a conditional read.
3. Perform a conditional copy.
4. Perform a conditional write.
5. Clean up and exit.
Which action would you like to take? 4
Perform a conditional write using IfNoneMatch condition on the object key.
If the key is a duplicate, the write will fail.
Enter an object key: test44
	Conditional write successful for key test44 in bucket test555-source-279.
----------------------------------------------------------------------------------------
Choose an action to explore some example conditional requests.
1. Print list of bucket items.
2. Perform a conditional read.
3. Perform a conditional copy.
4. Perform a conditional write.
5. Clean up and exit.
Which action would you like to take? 4
Perform a conditional write using IfNoneMatch condition on the object key.
If the key is a duplicate, the write will fail.
Enter an object key: test44
	Conditional write failed: Precondition failed
----------------------------------------------------------------------------------------

```
- Cleanup
  - The scenario should get the full list of objects, and remove all objects before deleting the buckets.
    - The user should be notified if the delete operation cannot occur.
  - If any previous operation should fail unexpectedly, perform the cleanup operation.

Example:

```
Choose an action to explore some example conditional requests.
1. Print list of bucket items.
2. Perform a conditional read.
3. Perform a conditional copy.
4. Perform a conditional write.
5. Clean up and exit.
Which action would you like to take? 5
Cleaned up bucket: test555-source-279.
Cleaned up bucket: test555-dest-279.
----------------------------------------------------------------------------------------
Thanks for watching.
----------------------------------------------------------------------------------------

Process finished with exit code 0

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

