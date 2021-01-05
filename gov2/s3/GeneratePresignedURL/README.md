### GeneratePresignedURLv2.go

This example retrieves a presigned URL for an Amazon S3 bucket object.

`go run GeneratePresignedURLv2.go -b BUCKET -k KEY`

- _BUCKET_ is the name of the bucket.
- _KEY_ is the name of the object (key).

The unit test accepts a similar value in _config.json_.