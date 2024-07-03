# Amazon S3 Object Integrity Workflow - Technical specification

This document contains the technical specifications for _Amazon S3 Integrity Lock Workflow_,
a workflow scenario that showcases AWS services and SDKs. It is primarily intended for the AWS code
examples team to use while developing this example in additional languages.

This document explains the following:

- Architecture and features of the example workflow.
- Metadata information for the scenario.
- Sample reference output.

For an introduction, see the [README.md](README.md).

---

### Table of contents

- [Resources and User Input](#resources-and-user-input)
- [Metadata](#metadata)

## Resources and User Input

- Amazon Simple Storage Service (Amazon S3) Buckets and objects (created in the scenario)
  - One bucket to upload files to.

A file that is at least 10 MB is needed to demonstrate multi-part uploads.
This file is create by writing the workspace source file repeated to a new
file in the working directory.
Example:
```
This workflow demonstrates how Amazon S3 uses checksum values to verify the integrity of data
uploaded to Amazon S3 buckets
The AWS SDK for C++ automatically handles checksums.
By default it calculates a checksum that is uploaded with an object.
The default checksum algorithm for PutObject and MultiPart upload is an MD5 hash.
The default checksum algorithm for TransferManager uploads is a CRC32 checksum.
You can override the default behavior, requiring one of the following checksums,
MD5, CRC32, CRC32C, SHA-1 or SHA-256.
You can also set the checksum hash value, instead of letting the SDK calculate the value.
For more information, see https://docs.aws.amazon.com/AmazonS3/latest/userguide/checking-object-integrity.html.
This workflow will locally compute checksums for files uploaded to an Amazon S3 bucket,
even when the SDK also computes the checksum.
This is done to provide demonstration code for how the checksums are calculated.
A bucket named 'integrity-workflow-92e7e370-b096-4b7f-bf24-7d3496a19772' will be created for the object uploads.
Created bucket integrity-workflow-92e7e370-b096-4b7f-bf24-7d3496a19772 in the specified AWS Region.


```
The user chooses the hash method. They also choose whether to let the SDK calculate hashes
or to use a hash calculated by this app.
Example:

```
Choose from one of the following checksum algorithms.
  1 - Default
  2 - MD5
  3 - CRC32
  4 - CRC32C
  5 - SHA1
  6 - SHA256
Enter an index: 3
Let the SDK calculate the checksum for you? (y/n) n
```

The workflow demonstrates object integrity for PutObject. The hash
is always calculated in the app, providing the user with example code
demonstrating hash calculation. Hashes are downloaded from the server
and compared to locally calculated hashes.
Example
```
The workflow will now upload a file using PutObject.
Object integrity will be verified using the CRC32 algorithm.
A checksum computed by this workflow will be used for object integrity verification,
except for the TransferManager upload.
Press Enter to continue...

***************************************************************************************

Object successfully uploaded.
The upload was successful.
If the checksums had not matched, the upload would have failed.
The checksums calculated by the server have been retrieved using the GetObjectAttributes.
The locally calculated checksums have been verified against the retrieved checksums.
For PutObject upload retrieved hash is AP39dw==
For PutObject upload, locally and remotely calculated hashes all match!
```
The workflow repeats this process with the TransferManager, uploading the 
large file using the multi-part upload APIs. In the case of the TransferManager,
SDK calculated APIs are always used, because of difficulties using locally calculated
hashes.
Example:

```
Now the workflow will demonstrate object integrity for TransferManager multi-part uploads.
The AWS C++ SDK has a TransferManager class which simplifies multipart uploads.
The following code lets the TransferManager handle much of the checksum configuration.
An object with the key 'tr_CRC32_large_test_file.cpp will be uploaded by the TransferManager using a 5 MB buffer.
For TransferManager uploads, this demo always lets the SDK calculate the hash value.
Press Enter to continue...

***************************************************************************************

Uploading the file...
For TransferManager upload retrieved hash is 7YCXxg==
6 part hash(es) were also retrieved.
  Part hash rCTaNA==
  Part hash yOLe+Q==
  Part hash I60aeg==
  Part hash boj9Ew==
  Part hash 0tmIfQ==
  Part hash 6/tRKA==
For TransferManager upload, locally and remotely calculated hashes all match!
```

The workflow demonstrates hashing using the multi-part upload APIs. In this case,
locally calculated hashes are used if the user selected that option.

```
Now we will provide an in-depth demonstration of multi-part uploading by calling the multi-part upload APIs directly.
These are the same APIs used by the TransferManager when uploading large files.
In the following code, the checksums are also calculated locally and then compared.
For multi-part uploads, a checksum is uploaded with each part. The final checksum is a concatenation of
the checksums for each part.
This is explained in the user guide, https://docs.aws.amazon.com/AmazonS3/latest/userguide/checking-object-integrity.html," in the section "Using part-level checksums for multipart uploads".
Starting multipart upload of with hash method CRC32 uploading to with object key
'mp_CRC32_large_test_file.cpp',
Press Enter to continue...

Uploading part 1.
Uploading part 2.
Uploading part 3.
Uploading part 4.
Uploading part 5.
Uploading part 6.
Multipart upload completed.
Finished multipart upload of with hash method CRC32
Now we will retrieve the checksums from the server.
For MultiPart upload retrieved hash is 7YCXxg==
6 part hash(es) were also retrieved.
  Part hash rCTaNA==
  Part hash yOLe+Q==
  Part hash I60aeg==
  Part hash boj9Ew==
  Part hash 0tmIfQ==
  Part hash 6/tRKA==
For MultiPart upload, locally and remotely calculated hashes all match!
```

The user is given the option to delete the resources created by this workflow.

## Metadata

| action / scenario              | metadata file    | metadata key                |
|--------------------------------|------------------|-----------------------------|
| `AbortMultipartUpload`         | s3_metadata.yaml | s3_AbortMultipartUpload     |
| `CreateMultipartUpload`        | s3_metadata.yaml | s3_CreateMultipartUpload    |
| `DeleteObject`                 | s3_metadata.yaml | s3_DeleteObject             |
| `GetObjectAttributes`          | s3_metadata.yaml | s3_GetObjectAttributes      |
| `PutObject`                    | s3_metadata.yaml | s3_PutObject                |
| `UploadPart`                   | s3_metadata.yaml | s3_UploadPart               |
| `CompleteMultipartUpload`      | s3_metadata.yaml | s3_CompleteMultipartUpload  |
| `S3 Object Integrity Scenario` | s3_metadata.yaml | s3_Scenario_ObjectIntegrity |

