# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require "aws-sdk-cloudwatch"

# Manages metric data and alarm metrics in Amazon CloudWatch.
class AlarmMetricsManager
  # Initializes the AlarmMetricsManager with a CloudWatch client.
  #
  # @param cloudwatch_client [Aws::CloudWatch::Client] An initialized CloudWatch client.
  def initialize(cloudwatch_client)
    @cloudwatch_client = cloudwatch_client
  end

  # snippet-start:[cloudwatch.Ruby.addDataPoint]
  # Adds a datapoint to a metric in Amazon CloudWatch.
  #
  # @param metric_namespace [String] The namespace of the metric to add the
  #   datapoint to.
  # @param metric_name [String] The name of the metric to add the datapoint to.
  # @param dimension_name [String] The name of the dimension to add the
  #   datapoint to.
  # @param dimension_value [String] The value of the dimension to add the
  #   datapoint to.
  # @param metric_value [Float] The value of the datapoint.
  # @param metric_unit [String] The unit of measurement for the datapoint.
  # @return [Boolean] True if the datapoint was added successfully, otherwise false.
  def datapoint_added_to_metric?(
    metric_namespace,
    metric_name,
    dimension_name,
    dimension_value,
    metric_value,
    metric_unit
  )
    @cloudwatch_client.put_metric_data(
      namespace: metric_namespace,
      metric_data: [
        {
          metric_name: metric_name,
          dimensions: [
            {
              name: dimension_name,
              value: dimension_value
            }
          ],
          value: metric_value,
          unit: metric_unit
        }
      ]
    )
    puts "Added data about '#{metric_name}' to namespace '#{metric_namespace}'."
    true
  rescue StandardError => e
    puts "Error adding data about '#{metric_name}' to namespace '#{metric_namespace}': #{e.message}"
    false
  end
  # snippet-end:[cloudwatch.Ruby.addDataPoint]

  # snippet-start:[cloudwatch.Ruby.listMetrics]
  # Lists available metrics for a metric namespace in Amazon CloudWatch.
  #
  # @param metric_namespace [String] The namespace of the metric.
  # @return [Array] The list of available metrics.
  def list_metrics_for_namespace(metric_namespace)
    response = @cloudwatch_client.list_metrics(namespace: metric_namespace)

    if response.metrics.count.positive?
      response.metrics.each do |metric|
        puts "  Metric name: #{metric.metric_name}"
        if metric.dimensions.count.positive?
          puts "    Dimensions:"
          metric.dimensions.each do |dimension|
            puts "      Name: #{dimension.name}, Value: #{dimension.value}"
          end
        else
          puts "No dimensions found."
        end
      end
    else
      puts "No metrics found for namespace '#{metric_namespace}'. " \
        "Note that it could take up to 15 minutes for recently-added metrics " \
        "to become available."
    end
  end
  # snippet-end:[cloudwatch.Ruby.listMetrics]
end
