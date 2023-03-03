// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Microsoft.Extensions.Logging;
using System.Net;
using System.Text.Json;
using Amazon.EventBridge;
using Amazon.EventBridge.Model;

namespace EventBridgeActions;

public class EventBridgeWrapper
{
    private readonly IAmazonEventBridge _amazonEventBridge;
    private readonly ILogger<EventBridgeWrapper> _logger;

    /// <summary>
    /// Constructor for the EventBridge wrapper.
    /// </summary>
    /// <param name="amazonEventBridge">The injected EventBridge client.</param>
    /// <param name="logger">The injected logger for the wrapper.</param>
    public EventBridgeWrapper(IAmazonEventBridge amazonEventBridge, ILogger<EventBridgeWrapper> logger)

    {
        _logger = logger;
        _amazonEventBridge = amazonEventBridge;
    }

    /// <summary>
    /// List event buses available.
    /// </summary>
    /// <returns>The list of event buses.</returns>
    public async Task<List<EventBus>> ListAllEventBuses()
    {
        var results = new List<EventBus>();
        var request = new ListEventBusesRequest();
        ListEventBusesResponse response;
        do
        {
            response = await _amazonEventBridge.ListEventBusesAsync(request);
            results.AddRange(response.EventBuses);
            request.NextToken = response.NextToken;

        } while (response.NextToken is not null);

        return results;
    }

    /// <summary>
    /// Create a new event bus.
    /// </summary>
    /// <returns>The ARN of the new event bus.</returns>
    public async Task<string> CreateEventBus(string name, string EventSource)
    {
        var response = await _amazonEventBridge.CreateEventBusAsync(
            new CreateEventBusRequest()
            {
                Name = name,
                EventSourceName = EventSource
            });

        return response.EventBusArn;
    }

    /// <summary>
    /// Create a new event bus.
    /// </summary>
    /// <returns>A description of the new event bus.</returns>
    public async Task<DescribeEventBusResponse> DescribeEventBus(string name)
    {
        var response = await _amazonEventBridge.DescribeEventBusAsync(
            new DescribeEventBusRequest()
            {
                Name = name
            });

        return response;
    }

    /// <summary>
    /// Add a rule with a particular pattern and name to an event bus.
    /// </summary>
    /// <param name="description">The description of the rule.</param>
    /// <param name="eventPattern">The pattern for the event to match.</param>
    /// <param name="name">The name for the rule.</param>
    /// <param name="eventBusName">The optional name for the event bus. If empty, uses the default event bus.</param>
    /// <returns>The Arn of the rule.</returns>
    public async Task<string> AddRuleToEventBus(string description, string eventPattern, string name, string eventBusName = "default")
    {
        var response = await _amazonEventBridge.PutRuleAsync(
            new PutRuleRequest()
            {
                Description = description,
                EventPattern = eventPattern,
                Name = name,
                ScheduleExpression = "cron(0 12 * * ? *)",
                EventBusName = eventBusName
            });

        return response.RuleArn;
    }

    /// <summary>
    /// List rules on an event bus.
    /// </summary>
    /// <returns>The list of rules.</returns>
    public async Task<RuleState> GetRuleStateByRuleName(string ruleName, string eventBusName)
    {
        var ruleResponse = await _amazonEventBridge.DescribeRuleAsync(
            new DescribeRuleRequest()
            {
                Name = ruleName,
                EventBusName = eventBusName
            });
        return ruleResponse.State;
    }

    /// <summary>
    /// Enable a particular rule on an eventBus
    /// </summary>
    /// <returns>True if successful.</returns>
    public async Task<bool> EnableRuleByName(string ruleName)
    {
        var ruleResponse = await _amazonEventBridge.EnableRuleAsync(
            new EnableRuleRequest()
            {
                Name = ruleName
            });
        return ruleResponse.HttpStatusCode == HttpStatusCode.OK;
    }

    /// <summary>
    /// Disable a particular rule on an eventBus
    /// </summary>
    /// <returns>True if successful.</returns>
    public async Task<bool> DisableRuleByName(string ruleName)
    {
        var ruleResponse = await _amazonEventBridge.DisableRuleAsync(
            new DisableRuleRequest()
            {
                Name = ruleName,
                //EventBusName = eventBusName
            });
        return ruleResponse.HttpStatusCode == HttpStatusCode.OK;
    }

    /// <summary>
    /// List rules on an event bus.
    /// </summary>
    /// <returns>The list of rules.</returns>
    public async Task<List<Rule>> ListRulesForEventBus(string eventBusArn)
    {
        var results = new List<Rule>();
        var request = new ListRulesRequest()
        {
            EventBusName = eventBusArn
        };
        ListRulesResponse response;
        do
        {
            response = await _amazonEventBridge.ListRulesAsync(request);
            results.AddRange(response.Rules);
            request.NextToken = response.NextToken;

        } while (response.NextToken is not null);

        return results;
    }

    /// <summary>
    /// Create an archive for all S3 events.
    /// Events will only fire for buckets with EventBridge Events enabled.
    /// </summary>
    /// <returns>The ARN of the new event bus.</returns>
    public async Task<string> CreateArchiveForS3Events(string archiveName, string eventBusArn)
    {
        string allS3EventsPattern = $@"{{
              'source': ['aws.s3']
            }}";

        var response = await _amazonEventBridge.CreateArchiveAsync(
            new CreateArchiveRequest()
            {
                ArchiveName = archiveName,
                EventSourceArn = eventBusArn,
                EventPattern = allS3EventsPattern,
                RetentionDays = 1
            });

        return response.ArchiveArn;
    }

    /// <summary>
    /// Create an archive for all S3 events.
    /// Events will only fire for buckets with EventBridge Events enabled.
    /// </summary>
    /// <returns>The ARN of the new event bus.</returns>
    public async Task<string> StartArchiveReplay(string eventBusArn)
    {
        var utcNow = DateTime.UtcNow;
        string replayName = "testReplay" + utcNow.ToLongTimeString();
        await _amazonEventBridge.StartReplayAsync(
            new StartReplayRequest()
            {
                EventStartTime = utcNow.AddMinutes(-5),
                EventEndTime = utcNow,
                EventSourceArn = eventBusArn,
                ReplayName = "TestReplay"
            });

        return replayName;
    }

    /// <summary>
    /// Create an archive for all S3 events.
    /// Events will only fire for buckets with EventBridge Events enabled.
    /// </summary>
    /// <returns>The ARN of the new event bus.</returns>
    public async Task<string> CheckReplayStatus(string replayName)
    {
        var response = await _amazonEventBridge.DescribeReplayAsync(
            new DescribeReplayRequest()
            {
                ReplayName = replayName
            });

        return response.State;
    }

    /// <summary>

    /// </summary>
    /// <returns></returns>
    public async Task<List<Target>> ListAllTargetsOnRule(string ruleName)
    {
        var results = new List<Target>();
        var request = new ListTargetsByRuleRequest()
        {
            Rule = ruleName
        };
        ListTargetsByRuleResponse response;
        do
        {
            response = await _amazonEventBridge.ListTargetsByRuleAsync(request);
            results.AddRange(response.Targets);
            request.NextToken = response.NextToken;

        } while (response.NextToken is not null);

        return results;
    }

    public async Task<List<string>> ListAllRuleNamesByTarget(string targetArn)
    {
        var results = new List<string>();
        var request = new ListRuleNamesByTargetRequest()
        {
            TargetArn = targetArn
        };
        ListRuleNamesByTargetResponse response;
        do
        {
            response = await _amazonEventBridge.ListRuleNamesByTargetAsync(request);
            results.AddRange(response.RuleNames);
            request.NextToken = response.NextToken;

        } while (response.NextToken is not null);

        return results;
    }

    /// <summary>
    /// Create a new event rule that triggers on an S3 upload.
    /// </summary>
    /// <returns>The Arn of the new rule.</returns>
    public async Task<string> PutS3UploadRule(string roleArn, string bucketName)
    {
        string eventPattern = $@"{{
              'source': ['aws.s3'],
              'detail-type': ['Object Created'],
              'detail': {{
                'bucket': {{
                  'name': '{bucketName}']
                }}
              }}
            }}";
        var response = await _amazonEventBridge.PutRuleAsync(
            new PutRuleRequest()
            {
                Name = "example-s3-upload-rule",
                Description = "Example S3 upload rule for EventBridge",
                RoleArn = roleArn,
                EventPattern = eventPattern
            });

        return response.RuleArn;
    }

    /// <summary>
    /// Create a new event rule that triggers on an S3 upload.
    /// </summary>
    /// <returns>The Arn of the new rule.</returns>
    public async Task<bool> UpdateS3UploadRuleTargetWithTransform(string eventBusArn, string ruleName, string roleArn, string targetArn)
    {
        var targets = new List<Target>
        {
            new Target()
            {
                Arn = targetArn,
                RoleArn = roleArn,
                InputTransformer = new InputTransformer()
                {
                    InputPathsMap = new Dictionary<string, string>()
                    {
                        {"bucket", "$.detail.bucket.name"},
                        {"time", "$.time"}
                    },
                    InputTemplate = "Notification: an object was uploaded to bucket <bucket> on <time>."
                }
            }
        };
        var response = await _amazonEventBridge.PutTargetsAsync(
            new PutTargetsRequest()
            {
                EventBusName = eventBusArn,
                Rule = ruleName,
                Targets = targets,
            });

        return response.FailedEntryCount == 0;
    }

    /// <summary>
    /// Add an event to the event bus.
    /// </summary>
    /// <returns>The Arn of the new rule.</returns>
    public async Task<bool> PutEvents(string email)
    {
        var eventDetail = new
        {
            UserEmail = email,
            Message = "This event was generated by example code.",
            UtcTime = DateTime.UtcNow.ToString("g")
        };
        var response = await _amazonEventBridge.PutEventsAsync(
            new PutEventsRequest()
            {
                Entries = new List<PutEventsRequestEntry>()
                {
                    new PutEventsRequestEntry()
                    {
                        Source = "ExampleSource",
                        Detail = JsonSerializer.Serialize(eventDetail),
                        DetailType = "ExampleType"
                    }
                }
            });

        return response.FailedEntryCount == 0;
    }

    /// <summary>
    /// Add an event to the event bus.
    /// </summary>
    /// <returns>The Arn of the new rule.</returns>
    public async Task<string> UpdateCustomEventPattern(string ruleName)
    {
        string customEventsPattern = $@"{{
              'source': ['ExampleSource'],
              'detail-type': ['ExampleType']
            }}";
        var response = await _amazonEventBridge.PutRuleAsync(
            new PutRuleRequest()
            {
                Name = ruleName,
                Description = "Custom test rule",
                EventPattern = customEventsPattern
            });

        return response.RuleArn;
    }

    /// <summary>
    /// Add a new rule.
    /// </summary>
    /// <returns>True if successful.</returns>
    public async Task<bool> AddSnsTargetToRule(string eventBusArn, string ruleName, string roleArn, string targetArn)
    {
        var targets = new List<Target>
        {
            new Target()
            {
                Arn = targetArn,
                RoleArn = roleArn,
            }
        };

        var response = await _amazonEventBridge.PutTargetsAsync(
            new PutTargetsRequest()
            {
                EventBusName = eventBusArn,
                Rule = ruleName,
                Targets = targets,
            });

        return response.FailedEntryCount == 0;
    }

    /// <summary>
    /// Add a new rule.
    /// </summary>
    /// <returns>True if successful.</returns>
    public async Task<bool> AddLambdaTargetToRule(string eventBusArn, string ruleName, string roleArn, string targetArn)
    {
        var targets = new List<Target>
        {
            new Target()
            {
                Arn = targetArn,
                RoleArn = roleArn,

            }
        };

        var response = await _amazonEventBridge.PutTargetsAsync(
            new PutTargetsRequest()
            {
                EventBusName = eventBusArn,
                Rule = ruleName,
                Targets = targets,
            });

        return response.FailedEntryCount == 0;
    }

    /// <summary>
    /// Delete an event bus by name.
    /// </summary>
    /// <returns>True if successful</returns>
    public async Task<bool> DeleteEventBusByName(string name)
    {
        var response = await _amazonEventBridge.DeleteEventBusAsync(
            new DeleteEventBusRequest()
            {
                Name = name
            });

        return response.HttpStatusCode == HttpStatusCode.OK;
    }

    /// <summary>
    /// Delete an event rule by name.
    /// </summary>
    /// <returns>True if successful</returns>
    public async Task<bool> DeleteRuleByName(string name)
    {
        var response = await _amazonEventBridge.DeleteRuleAsync(
            new DeleteRuleRequest()
            {
                Name = name
            });

        return response.HttpStatusCode == HttpStatusCode.OK;
    }

    /// <summary>
    /// Delete an event archive by name.
    /// </summary>
    /// <returns>True if successful</returns>
    public async Task<bool> DeleteArchiveByName(string name)
    {
        var response = await _amazonEventBridge.DeleteArchiveAsync(
            new DeleteArchiveRequest()
            {
                ArchiveName = name
            });

        return response.HttpStatusCode == HttpStatusCode.OK;
    }
}
