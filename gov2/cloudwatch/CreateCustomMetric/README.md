### CreateCustomMetricv2.go

This example creates a new Amazon CloudWatch metric in a namespace.

`go run CreateCustomMetricv2.go -n NAMESPACE -m METRIC-NAME -s SECONDS -dn DIMENSION-NAME -dv DIMENSION-VALUE`

- _NAMESPACE_ is the namespace for the metric.
- _METRIC-NAME_ is the name of the metric.
- _SECONDS_ is the number of seconds for the metric.
- _DIMENSION-NAME_ is the name of the dimension.
- _DIMENSION-VALUE_ is the value of the dimension.

The unit test accepts similar values in _config.json_.
