### EncryptDatav2.go

This example encrypts some text using an AWS Key Management Service (AWS KMS) customer master key (CMK).

`go run EncryptDatav2.go -k KEYID -t TEXT`

- _KEYID_ is the ID for the AWS KMS key to use for encrypting the text.
- _TEXT_ is the text to encrypt.

The unit test accepts similar values in _config.json_.
