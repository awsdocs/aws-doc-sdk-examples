### CreateEnableMetricAlarmv2.go

This example enables the specified Amazon CloudWatch alarm.

`go run CreateEnableMetricAlarmv2.go -n INSTANCE-NAME -i INSTANCE-ID -a ALARM-NAME`

- _INSTANCE-NAME_ is the name of the Amazon Elastic Compute Cloud (Amazon EC2) instance for which the alarm is enabled.
- _INSTANCE-ID_ is the ID of the Amazon EC2 instance for which the alarm is enabled.
- _ALARM-NAME_ is the name of the alarm.

The unit test accepts similar values in _config.json_.
