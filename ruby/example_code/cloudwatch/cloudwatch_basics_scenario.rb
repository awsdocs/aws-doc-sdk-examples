# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# The following code example shows how to get started with Amazon CloudWatch using the
# AWS SDK for Ruby, framed around the OpenTelemetry (OTel) metrics experience that
# CloudWatch now supports natively.
#
# CloudWatch ingests OpenTelemetry metrics natively, and this scenario walks through what
# you do with them: turning on enrichment so CloudWatch can correlate incoming OTLP
# metrics with the resources that produced them, alarming on those metrics with a PromQL
# query, and finding out which individual series drove the alarm.
#
# A PromQL alarm works differently from a classic metric alarm. Rather than watching one
# metric and counting breaching periods, it evaluates a query that can match many series
# at once, and tracks each matching series separately as a contributor.
#
# Note that sending OTLP metrics to CloudWatch is not an AWS SDK operation. Metrics
# arrive over the OTLP protocol through the CloudWatch agent, an OpenTelemetry Collector,
# or an ADOT SDK. Everything this scenario does is configuration and querying around that
# ingestion path.
#
# This scenario performs the following tasks:
#
# 1. List metrics and namespaces from Amazon CloudWatch.
# 2. Start OpenTelemetry enrichment for the account.
# 3. Explain how OTLP metrics reach CloudWatch.
# 4. Create an alarm that evaluates a PromQL query.
# 5. Inspect the contributors to the PromQL alarm.
# 6. Get metric statistics and chart the metric on a dashboard.
# 7. Mute the alarm for a maintenance window.
# 8. Clean up the Amazon CloudWatch resources.

require 'aws-sdk-cloudwatch'
require 'json'

# snippet-start:[cloudwatch.Ruby.basicsScenario]
DASHES = ('-' * 80).freeze
DEFAULT_QUERY = 'avg by (host) (system_cpu_utilization) > 80'.freeze

# Valid evaluation intervals are 10, 20, 30, or any multiple of 60 up to 3600 seconds.
EVALUATION_INTERVAL = 60
PENDING_PERIOD = 300
RECOVERY_PERIOD = 120

# Lists the metrics and namespaces already present in the account, to orient the reader
# before any configuration happens.
#
# @param cloudwatch_client [Aws::CloudWatch::Client] An initialized CloudWatch client.
# @return [Hash] A hash of namespace to the metrics found in it, most populated first.
def metrics_by_namespace(cloudwatch_client)
  by_namespace = {}
  metric_count = 0

  cloudwatch_client.list_metrics.each_page do |page|
    page.metrics.each do |metric|
      (by_namespace[metric.namespace] ||= []) << metric
      metric_count += 1
    end
    # This account may have a very large number of metrics, so stop once we have enough
    # to give the reader a sense of what is there.
    break if metric_count >= 500
  end

  puts "\tFound #{metric_count} metrics across #{by_namespace.size} namespaces:"
  by_namespace
    .sort_by { |_namespace, metrics| -metrics.size }
    .first(10)
    .each { |namespace, metrics| puts "\t  #{namespace} (#{metrics.size} metrics)" }

  if by_namespace.empty?
    puts "\tNo metrics found in this account. The statistics and dashboard steps later"
    puts "\ton need an existing metric, so they will be skipped."
  end

  by_namespace
rescue StandardError => e
  puts "Error listing metrics: #{e.message}"
  {}
end

# Turns on OTel enrichment, but only if it is not already running. Enrichment is an
# account-wide setting, so this scenario only turns it off again if it was the thing that
# turned it on.
#
# @param cloudwatch_client [Aws::CloudWatch::Client] An initialized CloudWatch client.
# @return [Boolean] true if this run started enrichment; otherwise, false.
def enrichment_started_by_example?(cloudwatch_client)
  # The Ruby SDK renders the OTel prefix as +o_tel+, so the methods are
  # +get_o_tel_enrichment+ and +start_o_tel_enrichment+.
  status = cloudwatch_client.get_o_tel_enrichment.status
  puts "\tEnrichment status: #{status}"

  if status == 'Running'
    puts
    puts "\tEnrichment was already running, so we will leave it alone. The cleanup step"
    puts "\twill not stop it, because other workloads in this account may depend on it."
    return false
  end

  cloudwatch_client.start_o_tel_enrichment
  puts "\tEnrichment status: #{cloudwatch_client.get_o_tel_enrichment.status}"
  puts
  puts "\tNote: this run started enrichment, so the cleanup step will stop it again."
  true
rescue StandardError => e
  puts "Error starting OTel enrichment: #{e.message}"
  false
end

# Creates an alarm whose evaluation is a PromQL query.
#
# @param cloudwatch_client [Aws::CloudWatch::Client] An initialized CloudWatch client.
# @param alarm_name [String] The name of the alarm to create.
# @param query [String] The PromQL query to evaluate.
# @return [Boolean] true if the alarm was created; otherwise, false.
def promql_alarm_created?(cloudwatch_client, alarm_name, query)
  # The comparison belongs in the query itself. A PromQL alarm has no separate threshold,
  # comparison operator, statistic, period, or evaluation periods. Note that the Ruby SDK
  # spells the criteria member +prom_ql_criteria+.
  cloudwatch_client.put_metric_alarm(
    alarm_name: alarm_name,
    alarm_description: 'A PromQL alarm created by the AWS SDK for Ruby Basics scenario.',
    evaluation_criteria: {
      prom_ql_criteria: {
        query: query,
        pending_period: PENDING_PERIOD,
        recovery_period: RECOVERY_PERIOD
      }
    },
    evaluation_interval: EVALUATION_INTERVAL
  )
  true
rescue StandardError => e
  puts "Error creating PromQL alarm: #{e.message}"
  false
end

# Prints the contributors to a PromQL alarm. Each contributor is one series the query
# matched, identified by its label set.
#
# @param cloudwatch_client [Aws::CloudWatch::Client] An initialized CloudWatch client.
# @param alarm_name [String] The name of the PromQL alarm.
# @return [void]
def report_alarm_contributors(cloudwatch_client, alarm_name)
  contributors = []
  next_token = nil

  loop do
    response = cloudwatch_client.describe_alarm_contributors(
      alarm_name: alarm_name,
      next_token: next_token
    )
    contributors.concat(response.alarm_contributors)
    next_token = response.next_token
    # A page can come back empty while still carrying a token, so keep going until the
    # token itself is gone rather than stopping at the first empty page.
    break if next_token.nil? || next_token.empty?
  end

  if contributors.empty?
    puts "\tNo contributors yet. The query matched no series, which usually means no"
    puts "\tOTel metrics with these labels have arrived. Once your collector is sending"
    puts "\tdata, each matching series appears here with its labels and why it breached."
    return
  end

  puts "\tFound #{contributors.size} contributors:"
  contributors.each do |contributor|
    labels = contributor.contributor_attributes.sort.map { |k, v| "#{k}=#{v}" }.join(', ')
    puts "\t  #{contributor.contributor_id}: #{labels}"
    puts "\t    reason: #{contributor.state_reason}"
  end
rescue StandardError => e
  puts "Error describing alarm contributors: #{e.message}"
end

# Builds a single-widget dashboard body that charts the given metric.
#
# @param metric [Aws::CloudWatch::Types::Metric] The metric to chart.
# @param region [String] The region the metric is in. A metric widget must name its
#   region, because a dashboard can chart metrics from several.
# @return [String] The dashboard body, as JSON.
def dashboard_body(metric, region)
  metric_spec = [metric.namespace, metric.metric_name]
  metric.dimensions.each { |dimension| metric_spec.push(dimension.name, dimension.value) }

  {
    widgets: [
      {
        type: 'text',
        x: 0, y: 0, width: 24, height: 2,
        properties: {
          markdown: 'This dashboard was created programmatically by an AWS SDK code example.'
        }
      },
      {
        type: 'metric',
        x: 0, y: 2, width: 12, height: 6,
        properties: {
          metrics: [metric_spec],
          view: 'timeSeries',
          stat: 'Average',
          period: 300,
          region: region,
          title: metric.metric_name
        }
      }
    ]
  }.to_json
end

# Gets statistics for an existing metric and charts it on a dashboard, so the reader can
# see what the alarm is evaluating.
#
# @param cloudwatch_client [Aws::CloudWatch::Client] An initialized CloudWatch client.
# @param dashboard_name [String] The name of the dashboard to create.
# @param by_namespace [Hash] The namespaces and metrics discovered in step 1.
# @return [Boolean] true if a dashboard was created; otherwise, false.
def chart_metric_on_dashboard(cloudwatch_client, dashboard_name, by_namespace)
  if by_namespace.empty?
    puts "\tSkipping statistics and dashboard because no metrics exist yet."
    return false
  end

  metric = by_namespace.max_by { |_namespace, metrics| metrics.size }.last.first

  stats = cloudwatch_client.get_metric_statistics(
    namespace: metric.namespace,
    metric_name: metric.metric_name,
    dimensions: metric.dimensions,
    start_time: Time.now - (60 * 60 * 24),
    end_time: Time.now,
    period: 3600,
    statistics: %w[Average Maximum]
  )
  puts "\tStatistics for #{metric.namespace} #{metric.metric_name} over the last day:"
  puts "\t  Datapoints: #{stats.datapoints.size}"
  stats.datapoints.first(3).each do |datapoint|
    puts "\t  #{datapoint.timestamp} average #{datapoint.average}, maximum #{datapoint.maximum}"
  end

  response = cloudwatch_client.put_dashboard(
    dashboard_name: dashboard_name,
    dashboard_body: dashboard_body(metric, cloudwatch_client.config.region)
  )
  response.dashboard_validation_messages.each do |message|
    puts "\tDashboard validation message: #{message.message}"
  end
  puts "\tCreated dashboard #{dashboard_name}."

  stored = cloudwatch_client.get_dashboard(dashboard_name: dashboard_name)
  puts "\tRead the dashboard back, #{stored.dashboard_body.length} characters of widget JSON."
  true
rescue StandardError => e
  puts "Error getting statistics or creating the dashboard: #{e.message}"
  false
end

# Creates a mute rule so the alarm's actions are suppressed during a maintenance window,
# then reads it back and finds it in the account's rules.
#
# @param cloudwatch_client [Aws::CloudWatch::Client] An initialized CloudWatch client.
# @param mute_rule_name [String] The name of the mute rule to create.
# @param alarm_name [String] The name of the alarm to mute.
# @return [void]
def mute_alarm_for_maintenance(cloudwatch_client, mute_rule_name, alarm_name)
  # The expression is a five-field cron expression,
  # cron(Minutes Hours Day-of-month Month Day-of-week). Note that this is five fields, not
  # the six that Amazon EventBridge uses. For a one-time window, use at(yyyy-MM-ddThh:mm),
  # with no seconds. The duration is an ISO 8601 duration from PT1M to P15D, so PT2H
  # rather than 2h.
  expression = 'cron(0 2 * * SUN)'
  duration = 'PT2H'
  timezone = 'America/Los_Angeles'

  cloudwatch_client.put_alarm_mute_rule(
    name: mute_rule_name,
    description: 'A mute rule created by the AWS SDK for Ruby Basics scenario.',
    rule: {
      schedule: {
        expression: expression,
        duration: duration,
        timezone: timezone
      }
    },
    # Target up to 100 alarms. If mute_targets is omitted, the rule applies to every alarm
    # in the account.
    mute_targets: { alarm_names: [alarm_name] }
  )

  puts "\tCreated mute rule #{mute_rule_name}:"
  puts "\t  schedule: #{expression} for #{duration}"
  puts "\t  timezone: #{timezone}"
  puts "\t  targets:  #{alarm_name}"
  puts
  puts "\tNote the two formats here. The expression is a five-field cron expression, five"
  puts "\trather than the six Amazon EventBridge uses. The duration is an ISO 8601"
  puts "\tduration, so 'PT2H' and not '2h'."
  puts
  puts "\tAlso note that mute_targets is set explicitly. If you leave it out, the rule"
  puts "\tapplies to every alarm in the account."

  rule = cloudwatch_client.get_alarm_mute_rule(alarm_mute_rule_name: mute_rule_name)
  puts "\tRead the rule back: status #{rule.status}, mute type #{rule.mute_type}."

  summaries = cloudwatch_client.list_alarm_mute_rules(alarm_name: alarm_name)
                               .alarm_mute_rule_summaries
  puts "\tFound #{summaries.size} mute rules targeting this alarm."
  # Mute rule summaries carry no name field, only an ARN, so match on the ARN suffix.
  match = summaries.find do |summary|
    summary.alarm_mute_rule_arn.end_with?("/#{mute_rule_name}", ":#{mute_rule_name}")
  end
  puts "\t  matched by ARN: #{match.alarm_mute_rule_arn} (#{match.status})" if match
rescue StandardError => e
  puts "Error muting the alarm: #{e.message}"
end

# Deletes the resources the scenario created. Each deletion is attempted independently so
# that one failure does not leave the remaining resources behind.
#
# @param cloudwatch_client [Aws::CloudWatch::Client] An initialized CloudWatch client.
# @param names [Hash] The alarm, dashboard, and mute rule names to delete.
# @param started_here [Boolean] Whether this run turned OTel enrichment on.
# @return [void]
def clean_up(cloudwatch_client, names, started_here)
  begin
    cloudwatch_client.delete_alarm_mute_rule(alarm_mute_rule_name: names[:mute_rule])
    puts "\tDeleted mute rule #{names[:mute_rule]}."
  rescue StandardError => e
    puts "\tCould not delete the mute rule: #{e.message}"
  end

  begin
    cloudwatch_client.delete_alarms(alarm_names: [names[:alarm]])
    puts "\tDeleted alarm #{names[:alarm]}."
  rescue StandardError => e
    puts "\tCould not delete the alarm: #{e.message}"
  end

  if names[:dashboard]
    begin
      cloudwatch_client.delete_dashboards(dashboard_names: [names[:dashboard]])
      puts "\tDeleted dashboard #{names[:dashboard]}."
    rescue StandardError => e
      puts "\tCould not delete the dashboard: #{e.message}"
    end
  end

  unless started_here
    puts "\tLeft OTel enrichment running, because it was already on before this run."
    return
  end

  begin
    cloudwatch_client.stop_o_tel_enrichment
    puts "\tStopped OTel enrichment, because this run started it."
  rescue StandardError => e
    puts "\tCould not stop OTel enrichment: #{e.message}"
  end
end

# Prints the scenario's introduction.
#
# @return [void]
def print_intro
  puts DASHES
  puts 'Welcome to the Amazon CloudWatch Basics scenario.'
  puts
  puts 'CloudWatch now ingests OpenTelemetry metrics natively. This scenario walks through'
  puts 'that experience: it turns on OTel enrichment so CloudWatch can correlate incoming'
  puts 'OTLP metrics with the resources that produced them, alarms on those metrics with a'
  puts 'PromQL query, and shows you which individual series drove the alarm.'
  puts
  puts 'A PromQL alarm works differently from a classic metric alarm. Rather than watching'
  puts 'one metric and counting breaching periods, it evaluates a query that can match many'
  puts 'series at once, and tracks each one separately as a contributor.'
  puts DASHES
end

# Explains that OTLP metric ingestion is not an AWS SDK operation. This step makes no
# service call; naming the gap explicitly is the point.
#
# @return [void]
def explain_otlp_ingestion
  puts '3. Send OTLP metrics to CloudWatch'
  puts
  puts 'This step is not an AWS SDK operation, and that\'s worth being explicit about.'
  puts 'Metrics reach CloudWatch over the OTLP protocol, through the CloudWatch agent, an'
  puts 'OpenTelemetry Collector, or an ADOT SDK. There is no PutOTelMetrics API to call.'
  puts
  puts 'Point your collector at the CloudWatch metrics endpoint, which follows the pattern'
  puts "\thttps://monitoring.<region>.amazonaws.com/v1/metrics"
  puts
  puts 'The endpoint is HTTP/1.1 only and does not support gRPC, so use an otlphttp'
  puts 'exporter rather than otlp. The metrics endpoint signs as "monitoring".'
  puts DASHES
end

# Prompts for a PromQL query and creates an alarm that evaluates it.
#
# @param cloudwatch_client [Aws::CloudWatch::Client] An initialized CloudWatch client.
# @param alarm_name [String] The name of the alarm to create.
# @return [void]
def create_promql_alarm_step(cloudwatch_client, alarm_name)
  puts '4. Create a PromQL alarm'
  puts
  puts 'Now we alarm on those metrics. The comparison goes inside the query itself: a'
  puts 'PromQL alarm has no separate threshold, comparison operator, statistic, or period.'
  puts
  print "Enter a PromQL query, or press ENTER for [#{DEFAULT_QUERY}]: "
  input = $stdin.gets
  query = input.nil? || input.strip.empty? ? DEFAULT_QUERY : input.strip

  if promql_alarm_created?(cloudwatch_client, alarm_name, query)
    puts "\tCreated alarm #{alarm_name}:"
    puts "\t  query:              #{query}"
    puts "\t  evaluationInterval: #{EVALUATION_INTERVAL} seconds"
    puts "\t  pendingPeriod:      #{PENDING_PERIOD} seconds"
    puts "\t  recoveryPeriod:     #{RECOVERY_PERIOD} seconds"
    puts
    puts "\tA PromQL alarm starts in the OK state rather than INSUFFICIENT_DATA, which is"
    puts "\tanother way it differs from a classic alarm."
  end
  puts DASHES
end

# Runs the eight steps of the scenario in order.
def run_me
  region = 'us-east-1'
  cloudwatch_client = Aws::CloudWatch::Client.new(region: region)

  # Suffix the resource names so repeated runs do not collide.
  suffix = rand(1000..9999)
  alarm_name = "doc-example-promql-alarm-#{suffix}"
  dashboard_name = "doc-example-dashboard-#{suffix}"
  mute_rule_name = "doc-example-mute-rule-#{suffix}"

  print_intro

  puts '1. List metrics and namespaces'
  puts
  puts 'Before configuring anything, let\'s see what CloudWatch is already collecting in'
  puts 'this account by calling ListMetrics.'
  puts
  by_namespace = metrics_by_namespace(cloudwatch_client)
  puts DASHES

  puts '2. Start OpenTelemetry enrichment'
  puts
  puts 'Enrichment is what lets CloudWatch attach AWS resource context to the OTLP metrics'
  puts 'you send it. Without it, your metrics arrive as opaque series with no connection to'
  puts 'the resources that emitted them.'
  puts
  puts 'We check the current state first, and only start enrichment if it isn\'t already on.'
  puts
  started_here = enrichment_started_by_example?(cloudwatch_client)
  puts DASHES

  explain_otlp_ingestion
  create_promql_alarm_step(cloudwatch_client, alarm_name)

  puts '5. Inspect the alarm\'s contributors'
  puts
  puts 'Each contributor is one series the query matched, identified by its label set. This'
  puts 'is how you find out which host is unhealthy rather than only that something is.'
  puts 'Classic alarms have no equivalent.'
  puts
  report_alarm_contributors(cloudwatch_client, alarm_name)
  puts DASHES

  puts '6. Get statistics and chart the metric on a dashboard'
  puts
  puts 'Statistics and dashboards are how you see what the alarm is evaluating.'
  puts
  dashboard_created = chart_metric_on_dashboard(cloudwatch_client, dashboard_name, by_namespace)
  puts DASHES

  mute_alarm_step(cloudwatch_client, mute_rule_name, alarm_name)

  clean_up_step(
    cloudwatch_client,
    { alarm: alarm_name, dashboard: dashboard_created ? dashboard_name : nil,
      mute_rule: mute_rule_name },
    started_here
  )

  puts 'This concludes the Amazon CloudWatch Basics scenario.'
end

# Explains what a mute rule does, then creates one for the scenario's alarm.
#
# @param cloudwatch_client [Aws::CloudWatch::Client] An initialized CloudWatch client.
# @param mute_rule_name [String] The name of the mute rule to create.
# @param alarm_name [String] The name of the alarm to mute.
# @return [void]
def mute_alarm_step(cloudwatch_client, mute_rule_name, alarm_name)
  puts '7. Mute the alarm for a maintenance window'
  puts
  puts 'While a mute rule is active the targeted alarms keep evaluating and keep changing'
  puts 'state, but their actions do not fire. This is the supported way to suppress'
  puts 'notifications during planned maintenance, instead of disabling alarm actions and'
  puts 'hoping someone remembers to turn them back on.'
  puts
  mute_alarm_for_maintenance(cloudwatch_client, mute_rule_name, alarm_name)
  puts DASHES
end

# Asks whether to delete the resources the scenario created, and deletes them if so.
#
# @param cloudwatch_client [Aws::CloudWatch::Client] An initialized CloudWatch client.
# @param names [Hash] The alarm, dashboard, and mute rule names to delete.
# @param started_here [Boolean] Whether this run turned OTel enrichment on.
# @return [void]
def clean_up_step(cloudwatch_client, names, started_here)
  puts '8. Clean up'
  print 'Delete the resources this scenario created? (y/n) '
  answer = $stdin.gets
  if answer.nil? || answer.strip.downcase != 'y'
    puts "\tSkipping cleanup. Note that the alarm, dashboard, and mute rule are still in"
    puts "\tyour account, and enrichment may still be running."
  else
    clean_up(cloudwatch_client, names, started_here)
  end
  puts DASHES
end
# snippet-end:[cloudwatch.Ruby.basicsScenario]

run_me if $PROGRAM_NAME == __FILE__
