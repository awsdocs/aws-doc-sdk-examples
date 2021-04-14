### GetObjectAclv2.go

This example retrieves the access control list (ACL) for an Amazon S3 bucket object.

`go run GetObjectAclv2.go -b BUCKET -o OBJECT`

- _BUCKET_ is the name of the bucket containing the item.
- _OBJECT_ is the name of the object for which the ACL is retrieved.

The unit test accepts similar values in _config.json_.
