### MonitorInstancesv2.go

This example enables or disables monitoring for an Amazon EC2 instance.

`go run MonitorInstancesv2.go -m MODE -i INSTANCE-ID`

- _MODE_ is either "OFF" to disable monitoring or "ON" to enable monitoring.
- _INSTANCE-ID_ is the ID of the instance.

The unit test accepts similar values in _config.json_.
