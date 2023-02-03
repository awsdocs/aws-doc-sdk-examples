using Amazon.EventBridge;
using Amazon.EventBridge.Model;

var client = new AmazonEventBridgeClient();

var request = new PutRuleRequest
{
    Name = "ExampleScheduledRule",
    ScheduleExpression = "rate(1 hour)",
    State = RuleState.ENABLED
};

var response = await client.PutRuleAsync(request);


