### CopyObjectv2.go

This example copies an Amazon S3 object from one bucket to another.

`go run CopyObjectv2.go -s SOURCE -d DESTINATION -o OBJECT`

- _SOURCE_ is the name of the bucket containing the item to copy.
- _DESTINATION_ is the name of the bucket to which the item is copied.
- _OBJECT_ is the name of the object to copy.

The unit test accepts similar values in _config.json_.
