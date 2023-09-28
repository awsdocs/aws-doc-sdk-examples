import boto3
import json
from datetime import datetime, timedelta
import random


# snippet-start:[python.example_code.cloudwatch.CloudWatchActions]
class CloudWatchActions:
    def __init__(self):
        self.client = boto3.client("cloudwatch")
# snippet-end:[python.example_code.cloudwatch.CloudWatchActions]

    # snippet-start:[python.example_code.cloudwatch.handle_cloudwatch_exceptions]
    def handle_cloudwatch_exceptions(self, func):
        """A decorator to handle CloudWatchException."""

        def wrapper(*args, **kwargs):
            try:
                return func(*args, **kwargs)
            except self.client.exceptions.CloudWatchException as e:
                print(f"An error occurred: {e}")
                return []

        return wrapper
    # snippet-start:[python.example_code.cloudwatch.handle_cloudwatch_exceptions]

    # snippet-start:[python.example_code.cloudwatch.list_unique_namespaces]
    @handle_cloudwatch_exceptions
    def list_unique_namespaces(self):
        """
        Lists all unique CloudWatch metric namespaces.

        Uses pagination to handle potentially large results from AWS.

        Returns:
            list: A list of unique namespaces. Returns an empty list if an error occurs.
        """

        # Initialize paginator for the 'list_metrics' operation
        paginator = self.client.get_paginator("list_metrics")

        # Use set comprehension to collect unique namespaces
        unique_namespaces = {
            metric["Namespace"]
            for page in paginator.paginate()
            for metric in page.get("Metrics", [])
        }

        return list(unique_namespaces)
    # snippet-end:[python.example_code.cloudwatch.list_unique_namespaces]

    # snippet-start:[python.example_code.cloudwatch.list_metrics_for_namespace]
    @handle_cloudwatch_exceptions
    def list_metrics_for_namespace(self, namespace):
        """
        Lists the 10 most recent metrics for the specified CloudWatch metric namespace.

        Uses pagination to handle potentially large results from AWS. Only the most
        recent 10 metrics are returned.

        Args:
            namespace (str): The CloudWatch metric namespace to filter by.

        Returns:
            list: A list of up to 10 metrics. Returns an empty list if an error occurs.
        """

        # Initialize paginator for the 'list_metrics' operation
        paginator = self.client.get_paginator("list_metrics")

        # Use list comprehension to collect metrics for the given namespace
        metrics = [
            metric
            for page in paginator.paginate(Namespace=namespace)
            for metric in page.get("Metrics", [])
        ]

        # Return the 10 most recent metrics
        return metrics[:10]
    # snippet-end:[python.example_code.cloudwatch.list_metrics_for_namespace]

    # snippet-start:[python.example_code.cloudwatch.get_metric_statistics]
    @handle_cloudwatch_exceptions
    def get_metric_statistics(self, namespace, metric_name, dimensions, statistic):
        """
        Fetches and sorts metric statistics based on a given metric's namespace and name.

        The function retrieves data for a 24-hour period, segmented into 1-hour intervals.

        Args:
            namespace (str): The CloudWatch metric namespace.
            metric_name (str): The name of the metric.
            dimensions (list): A list of dimensions for the metric.
            statistic (str): The metric statistic (e.g., 'Average', 'Sum').

        Returns:
            list: A list of sorted datapoints based on timestamp.
                Returns an empty list if an error occurs or no data is available.
        """

        # Determine the time range for fetching metric data (last 24 hours)
        end_time = datetime.utcnow()
        start_time = end_time - timedelta(days=1)

        # Request metric statistics from CloudWatch
        response = self.client.get_metric_statistics(
            Namespace=namespace,
            MetricName=metric_name,
            Dimensions=dimensions,
            StartTime=start_time,
            EndTime=end_time,
            Period=3600,  # 1-hour periods
            Statistics=[statistic],
        )

        # Sort the datapoints based on timestamp
        sorted_data = sorted(response.get("Datapoints", []), key=lambda x: x["Timestamp"])

        return sorted_data
    # snippet-end:[python.example_code.cloudwatch.get_metric_statistics]

    # snippet-start:[python.example_code.cloudwatch.create_dashboard]
    @handle_cloudwatch_exceptions
    def create_dashboard(self, dashboard_name):
        """
        Creates a CloudWatch dashboard programmatically.

        Args:
        - dashboard_name (str): The name for the new CloudWatch dashboard.

        Returns:
        - bool: True if the dashboard was successfully created, False otherwise.
        """
        # This is a basic dashboard structure. In a real-world scenario, customization might be required.
        dashboard_body = {
            "widgets": [
                {
                    "type": "text",
                    "x": 0,
                    "y": 0,
                    "width": 24,
                    "height": 4,
                    "properties": {
                        "markdown": "This dashboard was created programmatically using the AWS SDK."
                    },
                },
                {
                    "type": "metric",
                    "x": 0,
                    "y": 4,
                    "width": 12,
                    "height": 6,
                    "properties": {
                        "metrics": [
                            [
                                "AWS/Usage",
                                "MetricName",
                                "ServiceName",
                                "SDK",
                                "Resource",
                                "Method",
                            ]
                        ],
                        "view": "timeSeries",
                        "title": "SDK Usage",
                    },
                },
                {
                    "type": "metric",
                    "x": 12,
                    "y": 4,
                    "width": 12,
                    "height": 6,
                    "properties": {
                        "metrics": [["AWS/Billing", "EstimatedCharges", "Currency", "USD"]],
                        "view": "timeSeries",
                        "title": "Estimated Billing",
                    },
                },
            ]
        }

        response = self.client.put_dashboard(
            DashboardName=dashboard_name, DashboardBody=str(dashboard_body)
        )
        return response["ResponseMetadata"]["HTTPStatusCode"] == 200
    # snippet-end:[python.example_code.cloudwatch.create_dashboard]


    # snippet-start:[python.example_code.cloudwatch.delete_dashboard]
    @handle_cloudwatch_exceptions
    def delete_dashboard(self, dashboard_name):
        """
        Deletes a specified CloudWatch dashboard.

        Args:
        - dashboard_name (str): The name of the CloudWatch dashboard to delete.

        Returns:
        - bool: True if the dashboard was successfully deleted, False otherwise.
        """

        response = self.client.delete_dashboards(DashboardNames=[dashboard_name])
        return response["ResponseMetadata"]["HTTPStatusCode"] == 200
    # snippet-end:[python.example_code.cloudwatch.delete_dashboard]

    # snippet-start:[python.example_code.cloudwatch.list_dashboards]
    @handle_cloudwatch_exceptions
    def list_dashboards(self):
        """
        Lists the names of all CloudWatch dashboards.

        Returns:
        - list: A list of dashboard names.
        """

        paginator = self.client.get_paginator("list_dashboards")
        all_dashboards = [
            dashboard
            for page in paginator.paginate()
            for dashboard in page.get("DashboardEntries", [])
        ]

        return [dashboard["DashboardName"] for dashboard in all_dashboards]
    # snippet-end:[python.example_code.cloudwatch.list_dashboards]

    # snippet-start:[python.example_code.cloudwatch.put_metric_data]
    @handle_cloudwatch_exceptions
    def put_metric_data(self, namespace, metric_name):
        """
        Puts custom metric data into CloudWatch.

        Args:
        - namespace (str): The namespace for the metric data.
        - metric_name (str): The name of the metric.

        Returns:
        - bool: True if the metric data was successfully put, False otherwise.
        """
        
        # Generate a list of metric data points
        metric_data = [{
            'MetricName': metric_name,
            'Value': random.uniform(0, 100),
            'Unit': 'None'
        } for _ in range(10)]
        
        response = self.client.put_metric_data(
            Namespace=namespace,
            MetricData=metric_data
        )
        
        return response['ResponseMetadata']['HTTPStatusCode'] == 200
    # snippet-end:[python.example_code.cloudwatch.put_metric_data]

    # snippet-start:[python.example_code.cloudwatch.get_dashboard]
    @handle_cloudwatch_exceptions
    def get_dashboard(self, dashboard_name):
        """
        Retrieves the specified CloudWatch dashboard.

        Args:
        - dashboard_name (str): The name of the CloudWatch dashboard to retrieve.

        Returns:
        - dict: A dictionary representing the dashboard's configuration. Returns None on failure.
        """
        
        response = self.client.get_dashboard(DashboardName=dashboard_name)
        dashboard_body = response.get("DashboardBody", "{}")
        
        return json.loads(dashboard_body)
    # snippet-end:[python.example_code.cloudwatch.get_dashboard]

    # snippet-start:[python.example_code.cloudwatch.describe_alarms]
    @handle_cloudwatch_exceptions
    def describe_alarms(self):
        paginator = self.client.get_paginator("describe_alarms")
        all_alarms = []

        for page in paginator.paginate():
            all_alarms.extend(page.get("MetricAlarms", []))

        # Sort alarms by last modified date and limit to 10 most recent
        sorted_alarms = sorted(
            all_alarms,
            key=lambda x: x["AlarmConfigurationUpdatedTimestamp"],
            reverse=True,
        )
        return sorted_alarms[:10]
    # snippet-end:[python.example_code.cloudwatch.describe_alarms]

    # snippet-start:[python.example_code.cloudwatch.get_metric_data]
    @handle_cloudwatch_exceptions
    def get_metric_data(self, namespace, metric_name):
        start_time = datetime.utcnow() - timedelta(days=1)
        end_time = datetime.utcnow()

        metric_query = {
            "Id": "customMetricQuery",
            "MetricStat": {
                "Metric": {"Namespace": namespace, "MetricName": metric_name},
                "Period": 300,  # 5-minute periods
                "Stat": "Average",
            },
            "ReturnData": True,
        }

        response = self.client.get_metric_data(
            MetricDataQueries=[metric_query], StartTime=start_time, EndTime=end_time
        )

        if response["MetricDataResults"] and response["MetricDataResults"][0]["Values"]:
            values = response["MetricDataResults"][0]["Values"]
            timestamps = response["MetricDataResults"][0]["Timestamps"]
            return [{"Timestamp": t, "Value": v} for t, v in zip(timestamps, values)]
        else:
            return []
    # snippet-end:[python.example_code.cloudwatch.get_metric_data]

    # snippet-start:[python.example_code.cloudwatch.put_consecutive_metric_data]
    @handle_cloudwatch_exceptions
    def put_consecutive_metric_data(self, namespace, metric_name):
        # Assuming a common threshold value, e.g., 1000.
        # In real-world scenarios, this value might need to be more thoughtfully set based on the metric and alarm configuration.
        threshold_exceeding_value = 1000

        metric_data = [
            {"MetricName": metric_name, "Value": threshold_exceeding_value, "Unit": "Count"}
            for _ in range(3)
        ]  # 3 consecutive data points

        response = self.client.put_metric_data(Namespace=namespace, MetricData=metric_data)
        return response["ResponseMetadata"]["HTTPStatusCode"] == 200
    # snippet-end:[python.example_code.cloudwatch.put_consecutive_metric_data]

    # snippet-start:[python.example_code.cloudwatch.describe_alarms_for_metric]
    @handle_cloudwatch_exceptions
    def describe_alarms_for_metric(self, namespace, metric_name):
        response = self.client.describe_alarms_for_metric(
            Namespace=namespace, MetricName=metric_name
        )
        if response["MetricAlarms"]:
            # Assuming there is only one alarm for the metric.
            # If there can be multiple alarms, you might need to check the state for each alarm.
            return response["MetricAlarms"][0]["StateValue"]
        else:
            return "No alarms found for the metric."
    # snippet-end:[python.example_code.cloudwatch.describe_alarms_for_metric]

    # snippet-start:[python.example_code.cloudwatch.describe_alarm_history]
    @handle_cloudwatch_exceptions
    def describe_alarm_history(self, alarm_name):
        paginator = self.client.get_paginator("describe_alarm_history")
        all_history_items = []
        
        for page in paginator.paginate(AlarmName=alarm_name):
            all_history_items.extend(page.get("AlarmHistoryItems", []))

        return all_history_items
    # snippet-end:[python.example_code.cloudwatch.describe_alarm_history]

    # snippet-start:[python.example_code.cloudwatch.put_anomaly_detector]
    @handle_cloudwatch_exceptions
    def put_anomaly_detector(self, namespace, metric_name):
        response = self.client.put_anomaly_detector(
            Namespace=namespace, MetricName=metric_name
        )
        return response["ResponseMetadata"]["HTTPStatusCode"] == 200
    # snippet-end:[python.example_code.cloudwatch.put_anomaly_detector]

    # snippet-start:[python.example_code.cloudwatch.delete_anomaly_detector]
    @handle_cloudwatch_exceptions
    def delete_anomaly_detector(self, namespace, metric_name):
        response = self.client.delete_anomaly_detector(
            Namespace=namespace, MetricName=metric_name
        )
        return response["ResponseMetadata"]["HTTPStatusCode"] == 200
    # snippet-end:[python.example_code.cloudwatch.delete_anomaly_detector]

    # snippet-start:[python.example_code.cloudwatch.describe_anomaly_detectors]
    @handle_cloudwatch_exceptions
    def describe_anomaly_detectors(self):
        paginator = self.client.get_paginator("describe_anomaly_detectors")
        all_detectors = []

        for page in paginator.paginate():
            all_detectors.extend(page.get("AnomalyDetectors", []))

        return all_detectors
    # snippet-end:[python.example_code.cloudwatch.describe_anomaly_detectors]

    # snippet-start:[python.example_code.cloudwatch.get_metric_widget_image]
    @handle_cloudwatch_exceptions
    def get_metric_widget_image(self, namespace, metric_name):
        widget_definition = {
            "width": 600,
            "height": 400,
            "view": "timeSeries",
            "stacked": False,
            "metrics": [[namespace, metric_name]],
            "period": 300,
        }

        response = self.client.get_metric_widget_image(
            MetricWidget=json.dumps(widget_definition), OutputFormat="png"
        )
        return response["MetricWidgetImage"]
    # snippet-end:[python.example_code.cloudwatch.get_metric_widget_image]

    # snippet-start:[python.example_code.cloudwatch.delete_anomaly_detector]
    @handle_cloudwatch_exceptions
    def delete_anomaly_detector(self, namespace, metric_name):
        response = self.client.delete_anomaly_detector(
            Namespace=namespace, MetricName=metric_name
        )
        return response["ResponseMetadata"]["HTTPStatusCode"] == 200
    # snippet-end:[python.example_code.cloudwatch.delete_anomaly_detector]

    # snippet-start:[python.example_code.cloudwatch.delete_alarm]
    @handle_cloudwatch_exceptions
    def delete_alarm(self, alarm_name):
        response = self.client.delete_alarms(AlarmNames=[alarm_name])
        return response["ResponseMetadata"]["HTTPStatusCode"] == 200
    # snippet-end:[python.example_code.cloudwatch.delete_alarm]

    # snippet-start:[python.example_code.cloudwatch.delete_dashboard]
    @handle_cloudwatch_exceptions
    def delete_dashboard(self, dashboard_name):
        response = self.client.delete_dashboards(DashboardNames=[dashboard_name])
        return response["ResponseMetadata"]["HTTPStatusCode"] == 200

