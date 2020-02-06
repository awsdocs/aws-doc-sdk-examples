# Amazon S3 Example in Go #

This Go code example enables deleting all of the Amazon S3 buckets you own that begin with a given prefix.

For example, if you call `go run s3_delete_buckets.go -p dummy-`,
it first removes all objects in the S3 buckets with names starting with *dummy-*,
then deletes all of those S3 buckets.

## Testing ##

Running `go test`:

1. Creates three S3 buckets,
   with a name starting with *dummy-*,
   followed by a GUID, and then the numbers 0-2.
2. Lists all of the buckets with a name starting with *dummy-*
3. Removes all objects in the S3 buckets with names starting with *dummy-*
4. Deletes all S3 buckets with names starting with *dummy-*
5. Lists all of the buckets with a name starting with *dummy-*

Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0