# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[cloudwatch.Ruby.UpdateAlarm]
# snippet-start:[cloudwatch.Ruby.Initialize]
require "aws-sdk-cloudwatch"

class CloudWatchAlarmManager
    # Initializes the AlarmMetricsManager with a CloudWatch client.
    #
    # @param cloudwatch_client [Aws::CloudWatch::Client] An initialized CloudWatch client.
    def initialize(cloudwatch_client)
      @cloudwatch_client = cloudwatch_client
    end
    # snippet-end:[cloudwatch.Ruby.Initialize]

    # snippet-start:[cloudwatch.Ruby.DescribeAlarms]
    # Displays information about available metric alarms in Amazon CloudWatch.
    def describe_metric_alarms
      response = @cloudwatch_client.describe_alarms

      if response.metric_alarms.count.positive?
        response.metric_alarms.each do |alarm|
          puts "-" * 16
          puts "Name:           " + alarm.alarm_name
          puts "State value:    " + alarm.state_value
          puts "State reason:   " + alarm.state_reason
          puts "Metric:         " + alarm.metric_name
          puts "Namespace:      " + alarm.namespace
          puts "Statistic:      " + alarm.statistic
          puts "Period:         " + alarm.period.to_s
          puts "Unit:           " + alarm.unit.to_s
          puts "Eval. periods:  " + alarm.evaluation_periods.to_s
          puts "Threshold:      " + alarm.threshold.to_s
          puts "Comp. operator: " + alarm.comparison_operator

          if alarm.key?(:ok_actions) && alarm.ok_actions.count.positive?
            puts "OK actions:"
            alarm.ok_actions.each do |a|
              puts "  " + a
            end
          end

          if alarm.key?(:alarm_actions) && alarm.alarm_actions.count.positive?
            puts "Alarm actions:"
            alarm.alarm_actions.each do |a|
              puts "  " + a
            end
          end

          if alarm.key?(:insufficient_data_actions) &&
            alarm.insufficient_data_actions.count.positive?
            puts "Insufficient data actions:"
            alarm.insufficient_data_actions.each do |a|
              puts "  " + a
            end
          end

          puts "Dimensions:"
          if alarm.key?(:dimensions) && alarm.dimensions.count.positive?
            alarm.dimensions.each do |d|
              puts "  Name: " + d.name + ", Value: " + d.value
            end
          else
            puts "  None for this alarm."
          end
        end
      else
        puts "No alarms found."
      end
    rescue StandardError => e
      puts "Error getting information about alarms: #{e.message}"
    end
    # snippet-end:[cloudwatch.Ruby.DescribeAlarms]


    # snippet-start:[cloudwatch.Ruby.CreateOrUpdateAlarm]
    def manage_alarm(action:, alarm_name:, **opts)
        case action
        when :list
          describe_metric_alarms
        when :create_or_update
          @cloudwatch_client.put_metric_alarm(
              alarm_name: alarm_name,
              alarm_description: alarm_description,
              metric_name: metric_name,
              alarm_actions: alarm_actions,
              namespace: namespace,
              statistic: statistic,
              dimensions: dimensions,
              period: period,
              unit: unit,
              evaluation_periods: evaluation_periods,
              threshold: threshold,
              comparison_operator: comparison_operator
        )
        when :delete
          @cloudwatch_client.delete_alarms(alarm_names: [alarm_name])
        when :disable
          @cloudwatch_client.disable_alarm_actions(alarm_names: [alarm_name])
        when :enable
          @cloudwatch_client.enable_alarm_actions(alarm_names: [alarm_name])
        else
          raise ArgumentError, "Unknown action: #{action}"
        end
    rescue StandardError => e
        puts "Error during #{action} action: #{e.message}"
        false
    end
end
# snippet-end:[cloudwatch.Ruby.UpdateAlarm]