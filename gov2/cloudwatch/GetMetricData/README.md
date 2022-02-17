### GetMetricData/GetMetricDatav2.go

This example displays the metric data points for the provided input in the given time-frame.

`go run CreateCustomMetricv2.go -mN METRIC-NAME -n NAMESPACE -dn DIMENSION-NAME -dv DIMENSION-VALUE -id ID -dM DIFFINMINUTES -s STAT -p PERIOD`

- _NAMESPACE_ is the namespace for the metric.
- _METRIC-NAME_ is the name of the metric.
- _DIMENSION-NAME_ is the name of the dimension.
- _DIMENSION-VALUE_ is the value of the dimension.
- _ID_ is a short name used to tie the object to the results in the response
- _DIFFINMINUTES_ is the difference in minutes for which the metrics are requested
- _STAT_ is the Statistic to return i.e. SUM, COUNT, AVERAGE etc
- _PERIOD_ is the granularity, in seconds, of the returned data points

The unit test accepts similar values in _config.json_