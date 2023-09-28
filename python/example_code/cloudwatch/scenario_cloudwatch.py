from prompt_toolkit import prompt
from prompt_toolkit.completion import WordCompleter
from cloudwatch_actions import CloudWatchActions
import os
import time
import base64

def main():
    # List at least 5 available unique namespaces from CloudWatch.
    # Have the user select a namespace. (ListMetrics)
    # There is no action for just listing namespaces, but you can list some known namespaces,
    # or alternatively you can list the metrics and then get distinct namespaces from that list.

    namespaces = CloudWatchActions.list_unique_namespaces()

    if not namespaces:
        print("No namespaces found.")
        return

    print("Available namespaces:")
    for i, ns in enumerate(namespaces, 1):
        print(f"{i}. {ns}")

    completer = WordCompleter(namespaces, ignore_case=True)
    selected_namespace = prompt(
        "Select a namespace by typing its name: ", completer=completer
    )

    if selected_namespace in namespaces:
        print(f"You selected: {selected_namespace}")
    else:
        print("Invalid selection.")

    # List available metrics within the selected Namespace. H
    # ave the user select a metric. Example: Billing, or Usage metrics for one of the CloudWatch APIs.
    # You will want to test several namespaces because they may act differently. (ListMetrics)
    # Some metrics may have the same name, so when listing metrics, you should also list their dimensions.
    # See “Usage” namespace as an example.
    # Use pagination if available.
    # You may limit to the 10 most recent metrics, in case the namespace has many metrics to show.

    namespace = prompt("Enter the namespace for which you want to list metrics: ")

    metrics = CloudWatchActions.list_metrics_for_namespace(namespace)

    if not metrics:
        print("No metrics found for this namespace.")
        return

    metric_options = []
    for metric in metrics:
        dimensions = ", ".join(
            [f"{dim['Name']}={dim['Value']}" for dim in metric["Dimensions"]]
        )
        display_str = f"{metric['MetricName']} [{dimensions}]"
        metric_options.append(display_str)

    print("\nAvailable metrics:")
    for i, option in enumerate(metric_options, 1):
        print(f"{i}. {option}")

    completer = WordCompleter(metric_options, ignore_case=True)
    selected_metric = prompt(
        "\nSelect a metric by typing its name (and dimensions if necessary): ",
        completer=completer,
    )

    if selected_metric in metric_options:
        print(f"\nYou selected: {selected_metric}")
    else:
        print("Invalid selection.")

    # Get statistics for the selected metric over the last day. (GetMetricStatistics)
    # Different metrics support different statistics. Present the user with a list of stats to choose from,
    # such as SampleCount, Average, Sum, Minimum, and Maximum.
    # Present the stats with timestamps and values, sorted by time.

    namespace = prompt("Enter the namespace of the metric: ")
    metric_name = prompt("Enter the metric name: ")
    dimensions_input = prompt("Enter dimensions (format: Name=Value,Name2=Value2): ")

    dimensions = [
        {"Name": pair.split("=")[0], "Value": pair.split("=")[1]}
        for pair in dimensions_input.split(",")
    ]

    statistic_types = ["SampleCount", "Average", "Sum", "Minimum", "Maximum"]
    completer = WordCompleter(statistic_types, ignore_case=True)
    selected_statistic = prompt("Select a statistic type: ", completer=completer)

    if selected_statistic not in statistic_types:
        print("Invalid statistic type.")
        return

    statistics = CloudWatchActions.get_metric_statistics(
        namespace, metric_name, dimensions, selected_statistic
    )

    if not statistics:
        print("No statistics found for this metric over the last day.")
        return

    print("\nTimestamp\t\tValue")
    print("--------------------------")
    for stat in statistics:
        print(f"{stat['Timestamp']}\t{stat[selected_statistic]}")

    #    Let the user get Estimated Billing statistics for their account for the last week. (GetMetricStatistics)
    #    See .NET example for an example query with Period and Dimension definitions.
    #    Present the estimated billing by day, in chronological order.

    currency = prompt("Enter the currency (e.g., USD): ")

    statistic_types = ["SampleCount", "Average", "Sum", "Minimum", "Maximum"]
    completer = WordCompleter(statistic_types, ignore_case=True)
    selected_statistic = prompt("Select a statistic type: ", completer=completer)

    if selected_statistic not in statistic_types:
        print("Invalid statistic type.")
        return

    statistics = CloudWatchActions.get_billing_statistics(currency, selected_statistic)

    if not statistics:
        print("No estimated billing statistics found for the last week.")
        return

    print("\nDate\t\tEstimated Charges")
    print("--------------------------")
    for stat in statistics:
        print(f"{stat['Timestamp'].strftime('%Y-%m-%d')}\t{stat[selected_statistic]}")

    # Create a new dashboard and add 2 metrics to it (Example: SDK Usage and Estimated Billing) (PutDashboard)
    # You can also create this dashboard in the AWS Console, and use “View Source” to get a JSON definition of a dashboard.
    # Ensure your cleanup operation gives the user the option to cleanup this dashboard.
    # Best practice according to the docs is to include a text widget indicating this dashboard was created in code.

    dashboard_name = prompt("Enter a name for the dashboard: ")

    print("Creating dashboard...")
    result = CloudWatchActions.create_dashboard(dashboard_name)

    if result:
        print(f"Dashboard {dashboard_name} created successfully!")

        option_completer = WordCompleter(["Yes", "No"], ignore_case=True)
        cleanup_option = prompt(
            "Would you like to clean up (delete) this dashboard? (Yes/No): ",
            completer=option_completer,
        )

        if cleanup_option.lower() == "yes":
            CloudWatchActions.delete_dashboard(dashboard_name)
            print(f"Dashboard {dashboard_name} deleted successfully!")
    else:
        print(f"Failed to create the dashboard {dashboard_name}.")

    # List dashboards. (ListDashboards)
    # Use pagination if available.
    dashboards = CloudWatchActions.list_dashboards()

    if not dashboards:
        print("No dashboards found.")
        return

    print("Available dashboards:")
    for i, dashboard in enumerate(dashboards, 1):
        print(f"{i}. {dashboard}")

    # Create a new custom metric by adding data for it. (PutMetricData)
    # Example: create a metric and add 10 random values less than 100 to the metric. Adding data creates the metric.
    # There is no “cleanup” for a metric, it just expires after a period of having no new data.
    namespace = prompt("Enter a custom namespace for the metric: ")
    metric_name = prompt("Enter a name for the custom metric: ")

    print("Publishing data to custom metric...")
    success = CloudWatchActions.put_metric_data(namespace, metric_name)

    if success:
        print(
            f"Data added to metric {metric_name} in namespace {namespace} successfully!"
        )
    else:
        print(
            f"Failed to add data to the metric {metric_name} in namespace {namespace}."
        )

    # Add the custom metric to the dashboard. (PutDashboard)
    # Use the PutDashboard to update the dashboard.
    # All data will be overwritten, so you should also include your original metric definitions.
    dashboard_name = prompt("Enter the name of the dashboard to update: ")
    namespace = prompt("Enter the custom metric namespace: ")
    metric_name = prompt("Enter the custom metric name: ")

    print("Fetching current dashboard...")
    dashboard_body = CloudWatchActions.get_dashboard(dashboard_name)

    print("Adding custom metric to dashboard...")
    success = CloudWatchActions.add_metric_to_dashboard(
        dashboard_name, namespace, metric_name, dashboard_body
    )

    if success:
        print(
            f"Custom metric {metric_name} added to dashboard {dashboard_name} successfully!"
        )
    else:
        print(f"Failed to add custom metric to the dashboard {dashboard_name}.")

    # Describe current alarms. (DescribeAlarms)
    # Use pagination if available.
    # You may limit the list to the most recent 10 updated alarms, in case the account has many alarms to show.
    # Display the current state and thresholds of each alarm.
    alarms = CloudWatchActions.describe_alarms()

    if not alarms:
        print("No alarms found.")
        return

    print("Most Recent 10 Updated Alarms:")
    print("-------------------------------")
    for alarm in alarms:
        print(f"Name: {alarm['AlarmName']}")
        print(f"State: {alarm['StateValue']}")
        print(f"Threshold: {alarm['Threshold']}")
        print("-------------------------------")

    # Get metric data for the custom metric. (GetMetricData)
    # See .NET for an example query. You should see some data returned for the custom metric. Data pushed in the last minute may not show up, so you may need to delay this call until data exists.
    namespace = input("Enter the custom metric namespace: ")
    metric_name = input("Enter the custom metric name: ")

    data_points = CloudWatchActions.get_metric_data(namespace, metric_name)

    if not data_points:
        print(f"No data found for metric {metric_name} in namespace {namespace}.")
        return

    print(f"Data for metric {metric_name} in namespace {namespace}:")
    for point in data_points:
        print(f"Timestamp: {point['Timestamp']}, Value: {point['Value']}")

    # Push data into the custom metric to trigger the alarm. (PutMetricData)
    # You will need several (I used 3) consecutive over-threshold data points to trigger the alarm.
    namespace = input("Enter the custom metric namespace: ")
    metric_name = input("Enter the custom metric name: ")

    print(
        "Pushing consecutive over-threshold data points to potentially trigger an alarm..."
    )
    success = CloudWatchActions.put_consecutive_metric_data(namespace, metric_name)

    if success:
        print(
            f"Consecutive over-threshold data points pushed to metric {metric_name} in namespace {namespace}!"
        )
    else:
        print(f"Failed to push data to metric {metric_name} in namespace {namespace}.")

    # Check the alarm state using the action DescribeAlarmsForMetric until the new alarm is in an Alarm state. (DescribeAlarmsForMetric)
    namespace = input("Enter the custom metric namespace: ")
    metric_name = input("Enter the custom metric name: ")
    interval = int(input("Enter the time interval (in seconds) for periodic checks: "))

    print("Checking alarm state periodically...")
    while True:
        alarm_state = CloudWatchActions.describe_alarms_for_metric(namespace, metric_name)
        if alarm_state == "ALARM":
            print(
                f"Alarm for metric {metric_name} in namespace {namespace} is now in ALARM state!"
            )
            break
        else:
            print(
                f"Current state for metric {metric_name}: {alarm_state}. Retrying in {interval} seconds..."
            )
            time.sleep(interval)

    # Get alarm history for the new alarm. (DescribeAlarmHistory)
    # Use pagination if available.
    # Display the state and timestamp for the history items.
    alarm_name = input("Enter the name of the alarm to fetch history: ")

    history_items = CloudWatchActions.describe_alarm_history(alarm_name)

    if not history_items:
        print(f"No history found for alarm {alarm_name}.")
        return

    print(f"History for alarm {alarm_name}:")
    for item in history_items:
        print(f"State: {item['HistoryItemType']}, Timestamp: {item['Timestamp']}")

    # Add an anomaly detector for the custom metric. (PutAnomalyDetector)
    # Ensure your cleanup operation gives the user the option to cleanup this anomaly detector.
    # You may use the Console to explore some appropriate anomaly settings, or use the .NET for an example.
    namespace = input("Enter the custom metric namespace: ")
    metric_name = input("Enter the custom metric name: ")

    print("Creating anomaly detector for custom metric...")
    success = CloudWatchActions.put_anomaly_detector(namespace, metric_name)

    if success:
        print(
            f"Anomaly detector created for metric {metric_name} in namespace {namespace}!"
        )

        option_completer = WordCompleter(["Yes", "No"], ignore_case=True)
        cleanup_option = prompt(
            "Would you like to clean up (delete) this anomaly detector? (Yes/No): ",
            completer=option_completer,
        )

        if cleanup_option.lower() == "yes":
            CloudWatchActions.delete_anomaly_detector(namespace, metric_name)
            print(
                f"Anomaly detector for metric {metric_name} in namespace {namespace} deleted successfully!"
            )
    else:
        print(
            f"Failed to create an anomaly detector for metric {metric_name} in namespace {namespace}."
        )

    # Describe current anomaly detectors. (DescribeAnomalyDetectors)
    # Use pagination if available.
    detectors = CloudWatchActions.describe_anomaly_detectors()

    if not detectors:
        print("No anomaly detectors found.")
        return

    print("Available anomaly detectors:")
    for i, detector in enumerate(detectors, 1):
        print(
            f"{i}. Namespace: {detector['Namespace']}, Metric: {detector['MetricName']}"
        )

    # Get a metric image for the custom metric. (GetMetricWidgetImage)
    # The data returned will be a Base64 encoded image, you can display or save as a file and open.
    # You may use the console to set up graphic parameters in a dashboard, and/or see the .NET MVP for an example.
    namespace = input("Enter the custom metric namespace: ")
    metric_name = input("Enter the custom metric name: ")

    image_data = CloudWatchActions.get_metric_widget_image(namespace, metric_name)

    if not image_data:
        print(
            f"Failed to retrieve image for metric {metric_name} in namespace {namespace}."
        )
        return

    file_name = f"{metric_name}_widget_image.png"
    with open(file_name, "wb") as f:
        f.write(base64.b64decode(image_data))

    print(f"Image saved as {file_name}.")

    # Open the saved image using the default image viewer
    os.system(f"open {file_name}")

    # Cleanup
    # Delete the anomaly detector. (DeleteAnomalyDetector)
    # Delete the alarm. (DeleteAlarms)
    # Delete the new dashboard. (DeleteDashboards)
    namespace = input("Enter the custom metric namespace: ")
    metric_name = input("Enter the custom metric name: ")
    alarm_name = input("Enter the alarm name: ")
    dashboard_name = input("Enter the dashboard name: ")

    print("Cleaning up resources...")

    if CloudWatchActions.delete_anomaly_detector(namespace, metric_name):
        print(
            f"Anomaly detector for metric {metric_name} in namespace {namespace} deleted successfully!"
        )

    if CloudWatchActions.delete_alarm(alarm_name):
        print(f"Alarm {alarm_name} deleted successfully!")

    if CloudWatchActions.delete_dashboard(dashboard_name):
        print(f"Dashboard {dashboard_name} deleted successfully!")


if __name__ == "__main__":
    main()
