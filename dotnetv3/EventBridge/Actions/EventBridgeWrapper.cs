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

    // snippet-start:[EventBridge.dotnetv3.DescribeRule]
    /// <summary>
    /// Get the state for a rule by the rule name.
    /// </summary>
    /// <param name="ruleName">The name of the rule.</param>
    /// <param name="eventBusName">The optional name of the event bus. If empty, uses the default event bus.</param>
    /// <returns>The state of the rule.</returns>
    public async Task<RuleState> GetRuleStateByRuleName(string ruleName, string? eventBusName = null)
    {
        var ruleResponse = await _amazonEventBridge.DescribeRuleAsync(
            new DescribeRuleRequest()
            {
                Name = ruleName,
                EventBusName = eventBusName
            });
        return ruleResponse.State;
    }
    // snippet-end:[EventBridge.dotnetv3.DescribeRule]

    // snippet-start:[EventBridge.dotnetv3.EnableRule]
    /// <summary>
    /// Enable a particular rule on an event bus.
    /// </summary>
    /// <param name="ruleName">The name of the rule.</param>
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
    // snippet-end:[EventBridge.dotnetv3.EnableRule]

    // snippet-start:[EventBridge.dotnetv3.DisableRule]
    /// <summary>
    /// Disable a particular rule on an event bus.
    /// </summary
    /// <param name="ruleName">The name of the rule.</param>
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
    // snippet-end:[EventBridge.dotnetv3.DisableRule]

    // snippet-start:[EventBridge.dotnetv3.ListRules]
    /// <summary>
    /// List the rules on an event bus.
    /// </summary>
    /// <param name="eventBusArn">The optional Arn of the event bus. If empty, uses the default event bus.</param>
    /// <returns>The list of rules.</returns>
    public async Task<List<Rule>> ListAllRulesForEventBus(string? eventBusArn = null)
    {
        var results = new List<Rule>();
        var request = new ListRulesRequest()
        {
            EventBusName = eventBusArn
        };
        // Get all of the pages of rules.
        ListRulesResponse response;
        do
        {
            response = await _amazonEventBridge.ListRulesAsync(request);
            results.AddRange(response.Rules);
            request.NextToken = response.NextToken;

        } while (response.NextToken is not null);

        return results;
    }
    // snippet-end:[EventBridge.dotnetv3.ListRules]

    // snippet-start:[EventBridge.dotnetv3.ListTargetsByRule]
    /// <summary>
    /// List all of the targets matching a rule by name.
    /// </summary>
    /// <param name="ruleName">The name of the rule.</param>
    /// <returns>The list of targets.</returns>
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
    // snippet-end:[EventBridge.dotnetv3.ListTargetsByRule]

    // snippet-start:[EventBridge.dotnetv3.ListRuleNamesByTarget]
    /// <summary>
    /// List names of all rules matching a target.
    /// </summary>
    /// <param name="targetArn">The Arn of the target.</param>
    /// <returns>The list of rule names.</returns>
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
    // snippet-end:[EventBridge.dotnetv3.ListRuleNamesByTarget]

    // snippet-start:[EventBridge.dotnetv3.PutRule]
    /// <summary>
    /// Create a new event rule that triggers when an S3 object is created in a bucket.
    /// </summary>
    /// <param name="roleArn">The Arn of the role.</param>
    /// <param name="ruleName">The name to give the rule.</param>
    /// <param name="bucketName">The name of the bucket to trigger the event.</param>
    /// <returns>The Arn of the new rule.</returns>
    public async Task<string> PutS3UploadRule(string roleArn, string ruleName, string bucketName)
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
                Name = ruleName,
                Description = "Example S3 upload rule for EventBridge",
                RoleArn = roleArn,
                EventPattern = eventPattern
            });

        return response.RuleArn;
    }
    // snippet-end:[EventBridge.dotnetv3.PutRule]

    // snippet-start:[EventBridge.dotnetv3.PutTargetsTransform]
    /// <summary>
    /// Update an S3 object created rule with a transform on the target.
    /// </summary>
    /// <param name="ruleName">The name of the rule.</param>
    /// <param name="roleArn">The Arn of the role.</param>
    /// <param name="targetArn">The Arn of the target.</param>
    /// <param name="eventBusArn">Optional event bus Arn. If empty, uses the default event bus.</param>
    /// <returns>True if successful.</returns>
    public async Task<bool> UpdateS3UploadRuleTargetWithTransform(string ruleName, string roleArn, string targetArn, string? eventBusArn = null)
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
    // snippet-end:[EventBridge.dotnetv3.PutTargetsTransform]

    // snippet-start:[EventBridge.dotnetv3.PutEvents]
    /// <summary>
    /// Add an event to the event bus that includes an email, message, and time.
    /// </summary>
    /// <param name="email">The email to use in the event detail of the custom event.</param>
    /// <returns>True if successful.</returns>
    public async Task<bool> PutCustomEmailEvent(string email)
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
    // snippet-end:[EventBridge.dotnetv3.PutEvents]

    // snippet-start:[EventBridge.dotnetv3.PutCustomRulePattern]
    /// <summary>
    /// Update a rule to use a custom defined event pattern.
    /// </summary>
    /// <param name="ruleName">The name of the rule to update.</param>
    /// <returns>The Arn of the updated rule.</returns>
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
    // snippet-end:[EventBridge.dotnetv3.PutCustomRulePattern]

    // snippet-start:[EventBridge.dotnetv3.PutSnsTarget]
    /// <summary>
    /// Add an SNS target topic to a rule.
    /// </summary>
    /// <param name="ruleName">The name of the rule to update.</param>
    /// <param name="roleArn">The Arn of the role to use.</param>
    /// <param name="targetArn">The Arn of the SNS target.</param>
    /// <param name="eventBusArn">The optional event bus name, uses default if empty.</param>
    /// <returns>True if successful.</returns>
    public async Task<bool> AddSnsTargetToRule(string ruleName, string roleArn, string targetArn, string? eventBusArn = null)
    {
        // Create the list of targets.
        var targets = new List<Target>
        {
            new Target()
            {
                Arn = targetArn,
                RoleArn = roleArn,
            }
        };

        // Add the targets to the rule.
        var response = await _amazonEventBridge.PutTargetsAsync(
            new PutTargetsRequest()
            {
                EventBusName = eventBusArn,
                Rule = ruleName,
                Targets = targets,
            });

        return response.FailedEntryCount == 0;
    }
    // snippet-end:[EventBridge.dotnetv3.PutSnsTarget]

    // snippet-start:[EventBridge.dotnetv3.DeleteRule]
    /// <summary>
    /// Delete an event rule by name.
    /// </summary>
    /// <param name="name">The name of the event rule.</param>
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
    // snippet-end:[EventBridge.dotnetv3.DeleteRule]
}
