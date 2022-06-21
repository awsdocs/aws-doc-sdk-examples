# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon CloudWatch unit tests.
"""

from test_tools.example_stubber import ExampleStubber


class CloudWatchStubber(ExampleStubber):
    """
    A class that implements stub functions used by Amazon CloudWatch unit tests.

    The stubbed functions expect certain parameters to be passed to them as
    part of the tests, and raise errors if the parameters are not as expected.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 CloudWatch client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_list_metrics(
            self, namespace, name=None, metrics=None, recent=None, dimensions=None, error_code=None):
        expected_params = {'Namespace': namespace}
        if name is not None:
            expected_params['MetricName'] = name
        if recent is not None:
            expected_params['RecentlyActive'] = 'PT3H'
        if dimensions is not None:
            expected_params['Dimensions'] = dimensions
        response = {'Metrics': [{
            'Namespace': metric.namespace, 'MetricName': metric.name
        } for metric in metrics]}
        self._stub_bifurcator(
            'list_metrics', expected_params, response, error_code=error_code)

    def stub_put_metric_data(self, namespace, name, value, unit, error_code=None):
        expected_params = {
            'Namespace': namespace,
            'MetricData': [{'MetricName': name, 'Value': value, 'Unit': unit}]}
        response = {}
        self._stub_bifurcator(
            'put_metric_data', expected_params, response, error_code=error_code)

    def stub_put_metric_data_set(
            self, namespace, name, timestamp, unit, data_set, error_code=None):
        expected_params = {
            'Namespace': namespace,
            'MetricData': [{
                'MetricName': name, 'Timestamp': timestamp, 'Unit': unit,
                'Values': data_set['values'], 'Counts': data_set['counts']}]
        }
        response = {}
        self._stub_bifurcator(
            'put_metric_data', expected_params, response, error_code=error_code)

    def stub_get_metric_statistics(
            self, namespace, name, start, end, period, stat_type, stats, dimensions=None,
            error_code=None):
        expected_params = {
            'Namespace': namespace, 'MetricName': name, 'StartTime': start,
            'EndTime': end, 'Period': period, 'Statistics': [stat_type]}
        if dimensions is not None:
            expected_params['Dimensions'] = dimensions
        response = {
            'Label': name,
            'Datapoints': [{stat_type: stat} for stat in stats]
        }
        self._stub_bifurcator(
            'get_metric_statistics', expected_params, response, error_code=error_code)

    def stub_put_metric_alarm(
            self, metric_namespace, metric_name, alarm_name, stat_type, period,
            eval_periods, threshold, comparison_op, error_code=None):
        expected_params = {
            'Namespace': metric_namespace,
            'MetricName': metric_name,
            'AlarmName': alarm_name,
            'Statistic': stat_type,
            'Period': period,
            'EvaluationPeriods': eval_periods,
            'Threshold': threshold,
            'ComparisonOperator': comparison_op
        }
        response = {}
        self._stub_bifurcator(
            'put_metric_alarm', expected_params, response, error_code=error_code)

    def stub_describe_alarms_for_metric(self, namespace, name, alarms, error_code=None):
        expected_params = {
            'Namespace': namespace,
            'MetricName': name}
        response = {
            'MetricAlarms': [{
                'AlarmName': alarm.name,
                'AlarmArn': alarm.alarm_arn,
                'Namespace': namespace,
                'MetricName': name
            } for alarm in alarms]
        }
        self._stub_bifurcator(
            'describe_alarms_for_metric', expected_params, response, error_code=error_code)

    def stub_enable_alarm_actions(self, alarm_name, error_code=None):
        expected_params = {'AlarmNames': [alarm_name]}
        response = {}
        self._stub_bifurcator(
            'enable_alarm_actions', expected_params, response, error_code=error_code)

    def stub_disable_alarm_actions(self, alarm_name, error_code=None):
        expected_params = {'AlarmNames': [alarm_name]}
        response = {}
        self._stub_bifurcator(
            'disable_alarm_actions', expected_params, response, error_code=error_code)

    def stub_delete_alarms(self, alarms, error_code=None):
        expected_params = {'AlarmNames': [a.name for a in alarms]}
        response = {}
        self._stub_bifurcator(
            'delete_alarms', expected_params, response, error_code=error_code)
