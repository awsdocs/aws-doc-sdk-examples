### ReEncryptDatav2.go

This example reencrypts some text using an AWS Key Management Service (AWS KMS) key (KMS key).

`go run ReEncryptDatav2.go -k KeyID -d DATA`

- _KeyID_ is the ID of the AWS KMS key to use for reencrypting the data.
- _DATA_ is the data to reencrypt, as a string.

The unit test accepts similar values in _config.json_.
