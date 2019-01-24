# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of
# the License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
# CONDITIONS OF ANY KIND, either express or implied. See the License for the
# specific language governing permissions and limitations under the License.

# snippet-sourcedescription:[MyCodePipelineFunction.js demonstrates how to use an AWS Lambda function to invoke a Lambda event in CodePipeline.]
# snippet-service:[codepipeline]
# snippet-keyword:[Javascript]
# snippet-keyword:[AWS CodePipeline]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Invoke]
# snippet-sourcetype:[full-example]
# snippet-sourceauthor:[AWS]
# snippet-sourcedate:[2016-03-07]
# snippet-start:[codepipeline.javascript.MyCodePipelineFunction.complete]

var assert = require('assert');
var AWS = require('aws-sdk');
var http = require('http');

exports.handler = function(event, context) {

    var codepipeline = new AWS.CodePipeline();
    
    // Retrieve the Job ID from the Lambda action
    var jobId = event["CodePipeline.job"].id;
    
    // Retrieve the value of UserParameters from the Lambda action configuration in AWS CodePipeline, in this case a URL which will be
    // health checked by this function.
    var url = event["CodePipeline.job"].data.actionConfiguration.configuration.UserParameters; 
    
    // Notify AWS CodePipeline of a successful job
    var putJobSuccess = function(message) {
        var params = {
            jobId: jobId
        };
        codepipeline.putJobSuccessResult(params, function(err, data) {
            if(err) {
                context.fail(err);      
            } else {
                context.succeed(message);      
            }
        });
    };
    
    // Notify AWS CodePipeline of a failed job
    var putJobFailure = function(message) {
        var params = {
            jobId: jobId,
            failureDetails: {
                message: JSON.stringify(message),
                type: 'JobFailed',
                externalExecutionId: context.invokeid
            }
        };
        codepipeline.putJobFailureResult(params, function(err, data) {
            context.fail(message);      
        });
    };
    
    // Validate the URL passed in UserParameters
    if(!url || url.indexOf('http://') === -1) {
        putJobFailure('The UserParameters field must contain a valid URL address to test, including http:// or https://');  
        return;
    }
    
    // Helper function to make a HTTP GET request to the page.
    // The helper will test the response and succeed or fail the job accordingly 
    var getPage = function(url, callback) {
        var pageObject = {
            body: '',
            statusCode: 0,
            contains: function(search) {
                return this.body.indexOf(search) > -1;    
            }
        };
        http.get(url, function(response) {
            pageObject.body = '';
            pageObject.statusCode = response.statusCode;
            
            response.on('data', function (chunk) {
                pageObject.body += chunk;
            });
            
            response.on('end', function () {
                callback(pageObject);
            });
            
            response.resume(); 
        }).on('error', function(error) {
            // Fail the job if our request failed
            putJobFailure(error);    
        });           
    };
    
    getPage(url, function(returnedPage) {
        try {
            // Check if the HTTP response has a 200 status
            assert(returnedPage.statusCode === 200);
            // Check if the page contains the text "Congratulations"
            // You can change this to check for different text, or add other tests as required
            assert(returnedPage.contains('Congratulations'));  
            
            // Succeed the job
            putJobSuccess("Tests passed.");
        } catch (ex) {
            // If any of the assertions failed then fail the job
            putJobFailure(ex);    
        }
    });     
};

# snippet-end:[codecommit.python.MyCodeCommitFunction.complete]
