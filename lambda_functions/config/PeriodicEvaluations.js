/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 */

// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[PeriodicEvaluations.js demonstrates how AWS Config invokes a function for period evaluations]
// snippet-service:[config]
// snippet-keyword:[javascript]
// snippet-keyword:[AWS Config]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[]
// snippet-sourceauthor:[AWS]

// This example checks whether the total number of a specified resource exceeds a specified maximum. 
// https://docs.aws.amazon.com/config/latest/developerguide/evaluate-config_develop-rules_nodejs-sample.html#periodic-example-rule

// snippet-start:[config.javascript.PeriodicEvaluations.complete]
var aws = require('aws-sdk'), // Loads the AWS SDK for JavaScript.
    config = new aws.ConfigService(), // Constructs a service object to use the aws.ConfigService class.
    COMPLIANCE_STATES = {
        COMPLIANT : 'COMPLIANT',
        NON_COMPLIANT : 'NON_COMPLIANT',
        NOT_APPLICABLE : 'NOT_APPLICABLE'
    };

// Receives the event and context from AWS Lambda.
exports.handler = function(event, context, callback) {
    // Parses the invokingEvent and ruleParameters values, which contain JSON objects passed as strings.
    var invokingEvent = JSON.parse(event.invokingEvent), 
        ruleParameters = JSON.parse(event.ruleParameters),
        noOfResources = 0;

    if (isScheduledNotification(invokingEvent)) {
        countResourceTypes(ruleParameters.applicableResourceType, "", noOfResources, function(err, count) {
            if (err === null) {
                var putEvaluationsRequest;
                // Initializes the request that contains the evaluation results.
                putEvaluationsRequest = {
                    Evaluations : [ {
                        // Applies the evaluation result to the AWS account published in the event.
                        ComplianceResourceType : 'AWS::::Account',
                        ComplianceResourceId : event.accountId,
                        ComplianceType : evaluateCompliance(ruleParameters.maxCount, count),
                        OrderingTimestamp : new Date()
                    } ],
                    ResultToken : event.resultToken
                };
                // Sends the evaluation results to AWS Config.
                config.putEvaluations(putEvaluationsRequest, function(err, data) {
                    if (err) {
                        callback(err, null);
                    } else {
                        if (data.FailedEvaluations.length > 0) {
                            // Ends the function execution if evaluation results are not successfully reported
                            callback(JSON.stringify(data));
                        }
                        callback(null, data);
                    }
                });
            } else {
                callback(err, null);
            }
        });
    } else {
        console.log("Invoked for a notification other than Scheduled Notification... Ignoring.");
    }
};

// Checks whether the invoking event is ScheduledNotification.
function isScheduledNotification(invokingEvent) {
    return (invokingEvent.messageType === 'ScheduledNotification');
}

// Checks whether the compliance conditions for the rule are violated.
function evaluateCompliance(maxCount, actualCount) {
    if (actualCount > maxCount) {
        return COMPLIANCE_STATES.NON_COMPLIANT;
    } else {
        return COMPLIANCE_STATES.COMPLIANT;
    }
}

// Counts the applicable resources that belong to the AWS account.
function countResourceTypes(applicableResourceType, nextToken, count, callback) {
    config.listDiscoveredResources({resourceType : applicableResourceType, nextToken : nextToken}, function(err, data) {
        if (err) {
            callback(err, null);
        } else {
            count = count + data.resourceIdentifiers.length;
            if (data.nextToken !== undefined && data.nextToken != null) {
                countResourceTypes(applicableResourceType, data.nextToken, count, callback);
            }
            callback(null, count);
        }
    });
    return count;
}// snippet-end:[config.javascript.PeriodicEvaluations.complete]