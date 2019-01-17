// Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
//
// This file is licensed under the Apache License, Version 2.0 (the "License").
// You may not use this file except in compliance with the License. A copy of
// the License is located at
//
// http://aws.amazon.com/apache2.0/
//
// This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
// CONDITIONS OF ANY KIND, either express or implied. See the License for the
// specific language governing permissions and limitations under the License.

// snippet-sourcedescription:[MyCodeCommitFunction.js demonstrates how to use an AWS Lambda function to return the URLs used for cloning a repository to a CloudWatch log.]
// snippet-service:[codecommit]
// snippet-keyword:[NodeJS]
// snippet-keyword:[AWS CodeCommit]
// snippet-keyword:[Code Sample]
// snippet-keyword:[GetRepository]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2016-03-07]
// snippet-sourceauthor:[AWS]
// snippet-start:[codecommit.nodejs.MyCodeCommitFunction_js.complete]

var aws = require('aws-sdk');
var codecommit = new aws.CodeCommit({ apiVersion: '2015-04-13' });

exports.handler = function(event, context) {
    
    //Log the updated references from the event
    var references = event.Records[0].codecommit.references.map(function(reference) {return reference.ref;});
    console.log('References:', references);
    
    //Get the repository from the event and show its git clone URL
    var repository = event.Records[0].eventSourceARN.split(":")[5];
    var params = {
        repositoryName: repository
    };
    codecommit.getRepository(params, function(err, data) {
        if (err) {
            console.log(err);
            var message = "Error getting repository metadata for repository " + repository;
            console.log(message);
            context.fail(message);
        } else {
            console.log('Clone URL:', data.repositoryMetadata.cloneUrlHttp);
            context.succeed(data.repositoryMetadata.cloneUrlHttp);
        }
    });
};
// snippet-end:[codecommit.nodejs.MyCodeCommitFunction_js.complete]
