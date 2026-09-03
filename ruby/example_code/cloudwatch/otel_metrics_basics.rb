# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# The following code examples show how to use the OpenTelemetry features of Amazon
# CloudWatch: turning on OTel enrichment so that CloudWatch vended metrics are queryable
# with PromQL, alarming on a PromQL query, inspecting the individual series
# (contributors) that put a PromQL alarm into ALARM, and muting alarm actions on a
# schedule.
#
# Note that OTLP metric ingestion is not an AWS SDK operation. To send OpenTelemetry
# metrics to CloudWatch, point an OpenTelemetry collector or the AWS Distro for
# OpenTelemetry (ADOT) SDK at the CloudWatch OTLP metrics endpoint,
# https://monitoring.<region>.amazonaws.com/v1/metrics. The operations below cover
# everything you do after those metrics land in CloudWatch.

require 'aws-sdk-cloudwatch'

# snippet-start:[cloudwatch.Ruby.startOTelEnrichment]
# Turns on OTel enrichment for the account. Once enrichment is running, CloudWatch vended
# metrics that carry a resource identifier dimension, such as the Amazon EC2
# CPUUtilization metric with its InstanceId dimension, are decorated with resource ARN
# and resource tag labels and become queryable with PromQL.
#
# Resource tags on telemetry must already be enabled for the account before you call
# this operation.
#
# Note that the Ruby SDK renders the OTel prefix as +o_tel+, so the method is
# +start_o_tel_enrichment+ rather than +start_otel_enrichment+.
#
# @param cloudwatch_client [Aws::CloudWatch::Client] An initialized CloudWatch client.
# @return [Boolean] true if enrichment was started; otherwise, false.
def otel_enrichment_started?(cloudwatch_client)
  cloudwatch_client.start_o_tel_enrichment
  true
rescue StandardError => e
  puts "Error starting OTel enrichment: #{e.message}"
  false
end
# snippet-end:[cloudwatch.Ruby.startOTelEnrichment]

# snippet-start:[cloudwatch.Ruby.getOTelEnrichment]
# Gets the current OTel enrichment status for the account.
#
# @param cloudwatch_client [Aws::CloudWatch::Client] An initialized CloudWatch client.
# @return [String, nil] 'Running' or 'Stopped', or nil if the status could not be read.
def otel_enrichment_status(cloudwatch_client)
  cloudwatch_client.get_o_tel_enrichment.status
rescue StandardError => e
  puts "Error getting OTel enrichment status: #{e.message}"
  nil
end
# snippet-end:[cloudwatch.Ruby.getOTelEnrichment]

# snippet-start:[cloudwatch.Ruby.stopOTelEnrichment]
# Turns off OTel enrichment for the account. Existing PromQL alarms are not deleted, but
# vended metrics stop being enriched with resource ARN and tag labels, so queries that
# select on those labels stop matching.
#
# @param cloudwatch_client [Aws::CloudWatch::Client] An initialized CloudWatch client.
# @return [Boolean] true if enrichment was stopped; otherwise, false.
def otel_enrichment_stopped?(cloudwatch_client)
  cloudwatch_client.stop_o_tel_enrichment
  true
rescue StandardError => e
  puts "Error stopping OTel enrichment: #{e.message}"
  false
end
# snippet-end:[cloudwatch.Ruby.stopOTelEnrichment]

# snippet-start:[cloudwatch.Ruby.putPromQLMetricAlarm]
# Creates or updates an alarm that evaluates a PromQL query.
#
# A PromQL alarm differs from a classic metric alarm in a few ways. The query can match
# many series at once, and each matching series is tracked separately as a contributor.
# Instead of counting breaching periods, you specify durations: a contributor moves to
# ALARM after it breaches continuously for the pending period, and back to OK after it
# stops breaching for the recovery period. A PromQL alarm starts in the OK state rather
# than INSUFFICIENT_DATA.
#
# The +evaluation_criteria+ union is mutually exclusive with the classic +metric_name+
# and +metrics+ parameters. When you use it you must also set +evaluation_interval+, and
# you must not set +period+, +statistic+, +threshold+, +comparison_operator+,
# +evaluation_periods+, +datapoints_to_alarm+, or +treat_missing_data+.
#
# @param cloudwatch_client [Aws::CloudWatch::Client] An initialized CloudWatch client.
# @param alarm_name [String] The name of the alarm, unique within the Region.
# @param criteria [Hash] The PromQL criteria, mirroring the +prom_ql_criteria+ shape:
#   * +:query+ [String] The PromQL query to evaluate, such as
#     'avg(cpu_utilization_percent) > 80'. The comparison belongs in the query itself;
#     there is no separate threshold parameter.
#   * +:pending_period+ [Integer] How long, in seconds, a contributor must breach
#     continuously before it moves to ALARM.
#   * +:recovery_period+ [Integer] How long, in seconds, a contributor must stop
#     breaching before it moves back to OK.
# @param evaluation_interval [Integer] How often, in seconds, to run the query. Valid
#   values are 10, 20, 30, and any multiple of 60, up to 3600.
# @param alarm_description [String] A description of the alarm.
# @return [Boolean] true if the alarm was created or updated; otherwise, false.
def promql_alarm_created_or_updated?(
  cloudwatch_client,
  alarm_name,
  criteria,
  evaluation_interval,
  alarm_description
)
  cloudwatch_client.put_metric_alarm(
    alarm_name: alarm_name,
    alarm_description: alarm_description,
    evaluation_criteria: {
      prom_ql_criteria: {
        query: criteria[:query],
        pending_period: criteria[:pending_period],
        recovery_period: criteria[:recovery_period]
      }
    },
    evaluation_interval: evaluation_interval
  )
  true
rescue StandardError => e
  puts "Error creating PromQL alarm: #{e.message}"
  false
end
# snippet-end:[cloudwatch.Ruby.putPromQLMetricAlarm]

# snippet-start:[cloudwatch.Ruby.describeAlarmContributors]
# Gets the contributors for a PromQL alarm. Each contributor is one series that the
# alarm's query matched, identified by its label set. This is how you find out which
# hosts, services, or pods are breaching, rather than only that something is.
#
# @param cloudwatch_client [Aws::CloudWatch::Client] An initialized CloudWatch client.
# @param alarm_name [String] The name of the PromQL alarm.
# @return [Array] The contributors, as Aws::CloudWatch::Types::AlarmContributor.
def alarm_contributors(cloudwatch_client, alarm_name)
  contributors = []
  next_token = nil

  loop do
    response = cloudwatch_client.describe_alarm_contributors(
      alarm_name: alarm_name,
      next_token: next_token
    )
    contributors.concat(response.alarm_contributors)
    next_token = response.next_token
    break if next_token.nil? || next_token.empty?
  end

  contributors
rescue StandardError => e
  puts "Error getting alarm contributors: #{e.message}"
  []
end
# snippet-end:[cloudwatch.Ruby.describeAlarmContributors]

# snippet-start:[cloudwatch.Ruby.putAlarmMuteRule]
# Creates or updates an alarm mute rule. While a mute rule is active the targeted alarms
# keep evaluating and keep transitioning between states, but their configured actions do
# not fire. This is the supported way to suppress notifications during a known
# maintenance window, instead of disabling alarm actions and relying on someone to turn
# them back on.
#
# @param cloudwatch_client [Aws::CloudWatch::Client] An initialized CloudWatch client.
# @param name [String] The name of the mute rule.
# @param schedule [Hash] The mute window, mirroring the +rule.schedule+ shape:
#   * +:expression+ [String] When the rule activates. Use a cron expression for a
#     recurring window, such as 'cron(0 2 ? * SUN *)', or an at expression for a one-time
#     window, such as 'at(2026-09-05T02:00:00)'.
#   * +:duration+ [String] How long the mute window lasts once it activates, such as
#     '2h' or '30m'.
#   * +:timezone+ [String] The time zone the expression is evaluated in, such as
#     'America/Los_Angeles'.
# @param alarm_names [Array] The names of up to 100 alarms to mute. If empty, the rule
#   applies to all alarms in the account.
# @param description [String] A description of the mute rule.
# @return [Boolean] true if the mute rule was created or updated; otherwise, false.
def alarm_mute_rule_created_or_updated?(
  cloudwatch_client,
  name,
  schedule,
  alarm_names,
  description
)
  params = {
    name: name,
    description: description,
    rule: {
      schedule: {
        expression: schedule[:expression],
        duration: schedule[:duration],
        timezone: schedule[:timezone]
      }
    }
  }
  params[:mute_targets] = { alarm_names: alarm_names } unless alarm_names.empty?

  cloudwatch_client.put_alarm_mute_rule(params)
  true
rescue StandardError => e
  puts "Error putting alarm mute rule: #{e.message}"
  false
end
# snippet-end:[cloudwatch.Ruby.putAlarmMuteRule]

# snippet-start:[cloudwatch.Ruby.getAlarmMuteRule]
# Gets the full configuration of an alarm mute rule, including its schedule, the alarms
# it targets, and whether it is currently SCHEDULED, ACTIVE, or EXPIRED.
#
# @param cloudwatch_client [Aws::CloudWatch::Client] An initialized CloudWatch client.
# @param name [String] The name of the mute rule.
# @return [Aws::CloudWatch::Types::GetAlarmMuteRuleOutput, nil] The mute rule, or nil on
#   error.
def alarm_mute_rule(cloudwatch_client, name)
  cloudwatch_client.get_alarm_mute_rule(alarm_mute_rule_name: name)
rescue StandardError => e
  puts "Error getting alarm mute rule: #{e.message}"
  nil
end
# snippet-end:[cloudwatch.Ruby.getAlarmMuteRule]

# snippet-start:[cloudwatch.Ruby.listAlarmMuteRules]
# Lists the alarm mute rules in the account, optionally filtered to the rules that
# target one alarm.
#
# @param cloudwatch_client [Aws::CloudWatch::Client] An initialized CloudWatch client.
# @param alarm_name [String, nil] When given, only rules that target this alarm are
#   returned.
# @return [Array] The mute rule summaries, as
#   Aws::CloudWatch::Types::AlarmMuteRuleSummary.
def alarm_mute_rules(cloudwatch_client, alarm_name = nil)
  summaries = []
  next_token = nil

  loop do
    response = cloudwatch_client.list_alarm_mute_rules(
      alarm_name: alarm_name,
      next_token: next_token
    )
    summaries.concat(response.alarm_mute_rule_summaries)
    next_token = response.next_token
    break if next_token.nil? || next_token.empty?
  end

  summaries
rescue StandardError => e
  puts "Error listing alarm mute rules: #{e.message}"
  []
end
# snippet-end:[cloudwatch.Ruby.listAlarmMuteRules]

# snippet-start:[cloudwatch.Ruby.deleteAlarmMuteRule]
# Deletes an alarm mute rule. The alarms it targeted resume firing their actions.
#
# @param cloudwatch_client [Aws::CloudWatch::Client] An initialized CloudWatch client.
# @param name [String] The name of the mute rule.
# @return [Boolean] true if the mute rule was deleted; otherwise, false.
def alarm_mute_rule_deleted?(cloudwatch_client, name)
  cloudwatch_client.delete_alarm_mute_rule(alarm_mute_rule_name: name)
  true
rescue StandardError => e
  puts "Error deleting alarm mute rule: #{e.message}"
  false
end
# snippet-end:[cloudwatch.Ruby.deleteAlarmMuteRule]

# snippet-start:[cloudwatch.Ruby.otelMetricsScenario]
# Turns on OpenTelemetry enrichment if the account doesn't already have it on. Enrichment
# is an account-wide setting, so an example should only turn it off again if it was the
# one that turned it on.
#
# @param cloudwatch_client [Aws::CloudWatch::Client] An initialized CloudWatch client.
# @return [Boolean] true if this call started enrichment; otherwise, false.
def enrichment_started_by_example?(cloudwatch_client)
  puts 'Checking whether OTel enrichment is on for this account.'
  status = otel_enrichment_status(cloudwatch_client)
  if status == 'Stopped'
    puts 'Enrichment is stopped. Starting it so vended metrics accept PromQL.'
    return otel_enrichment_started?(cloudwatch_client)
  end

  puts "Enrichment status is '#{status}'. Leaving it alone."
  false
end

# Prints the contributors to a PromQL alarm, one line per matched series.
#
# @param cloudwatch_client [Aws::CloudWatch::Client] An initialized CloudWatch client.
# @param alarm_name [String] The name of the PromQL alarm.
def report_alarm_contributors(cloudwatch_client, alarm_name)
  puts "\nContributors for '#{alarm_name}':"
  contributors = alarm_contributors(cloudwatch_client, alarm_name)
  if contributors.empty?
    puts '  None yet. The query matched no series, which usually means no OTel metrics ' \
         'with these labels have arrived.'
    return
  end

  contributors.each do |contributor|
    labels = contributor.contributor_attributes.sort.map { |k, v| "#{k}=#{v}" }.join(', ')
    puts "  #{contributor.contributor_id}: #{labels}"
    puts "    reason: #{contributor.state_reason}"
  end
end

# Mutes an alarm for a recurring weekly maintenance window.
#
# @param cloudwatch_client [Aws::CloudWatch::Client] An initialized CloudWatch client.
# @param mute_rule_name [String] The name of the mute rule to create.
# @param alarm_name [String] The name of the alarm to mute.
def mute_alarm_for_maintenance(cloudwatch_client, mute_rule_name, alarm_name)
  puts "\nMuting '#{alarm_name}' for a weekly two-hour maintenance window."
  schedule = {
    expression: 'cron(0 2 ? * SUN *)',
    duration: '2h',
    timezone: 'America/Los_Angeles'
  }
  return unless alarm_mute_rule_created_or_updated?(
    cloudwatch_client,
    mute_rule_name,
    schedule,
    [alarm_name],
    'Suppress checkout CPU pages during Sunday patching.'
  )

  rule = alarm_mute_rule(cloudwatch_client, mute_rule_name)
  puts "Mute rule status is #{rule.status}." unless rule.nil?
  puts 'While the window is active the alarm keeps evaluating and still changes ' \
       'state; only its actions are suppressed.'
end

# Removes the mute rule and the alarm, and stops enrichment if this example started it.
#
# @param cloudwatch_client [Aws::CloudWatch::Client] An initialized CloudWatch client.
# @param alarm_name [String] The name of the alarm to delete.
# @param mute_rule_name [String] The name of the mute rule to delete.
# @param started_here [Boolean] Whether this example started OTel enrichment.
def clean_up(cloudwatch_client, alarm_name, mute_rule_name, started_here)
  puts "\nCleaning up."
  alarm_mute_rule_deleted?(cloudwatch_client, mute_rule_name)
  cloudwatch_client.delete_alarms(alarm_names: [alarm_name])
  return unless started_here

  puts 'Stopping OTel enrichment, since this example started it.'
  otel_enrichment_stopped?(cloudwatch_client)
end

# Walks through the OpenTelemetry metrics workflow in CloudWatch: turn on enrichment,
# alarm on a PromQL query, inspect the contributors that matched, mute the alarm for a
# maintenance window, then clean up.
#
# This scenario assumes OpenTelemetry metrics are already flowing into the account,
# either from an OpenTelemetry collector, the CloudWatch agent, or the ADOT SDK.
def run_me
  alarm_name = 'doc-example-promql-high-cpu'
  mute_rule_name = 'doc-example-maintenance-window'
  query = 'avg by (host_name) (cpu_utilization_percent{service_name="checkout"}) > 80'
  # Replace us-east-1 with the AWS Region you're using for Amazon CloudWatch.
  region = 'us-east-1'

  cloudwatch_client = Aws::CloudWatch::Client.new(region: region)
  started_here = enrichment_started_by_example?(cloudwatch_client)

  puts "\nCreating a PromQL alarm on: #{query}"
  criteria = { query: query, pending_period: 300, recovery_period: 120 }
  unless promql_alarm_created_or_updated?(
    cloudwatch_client,
    alarm_name,
    criteria,
    30,
    'Average CPU over 80% per host for the checkout service.'
  )
    puts "Could not create alarm '#{alarm_name}'. Stopping."
    return
  end
  puts 'The alarm evaluates every 30 seconds. A host moves to ALARM after breaching ' \
       'for 300 seconds straight, and back to OK after 120 seconds clean.'

  report_alarm_contributors(cloudwatch_client, alarm_name)
  mute_alarm_for_maintenance(cloudwatch_client, mute_rule_name, alarm_name)

  puts "\nMute rules targeting '#{alarm_name}':"
  alarm_mute_rules(cloudwatch_client, alarm_name).each do |summary|
    puts "  #{summary.alarm_mute_rule_arn} (#{summary.status})"
  end

  clean_up(cloudwatch_client, alarm_name, mute_rule_name, started_here)
end
# snippet-end:[cloudwatch.Ruby.otelMetricsScenario]

run_me if $PROGRAM_NAME == __FILE__
