### CreateInstancev2.go

This example creates a T2-Micro instance from the Amazon EC2 image ami-e7527ed7 and attaches a tag to the instance.

`go run CreateInstancev2.go -n TAG-NAME -v TAG-VALUE`

- _TAG-NAME_ is the name of the tag to attach to the instance.
- _TAG-VALUE_ is the value of the tag to attach to the instance.

The unit test accepts similar values in _config.json_.
