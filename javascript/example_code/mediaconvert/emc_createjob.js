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
*/

//snippet-sourcedescription:[emc_createjob.js demonstrates how to create a transcoding job.]
//snippet-service:[mediaconvert]
//snippet-keyword:[JavaScript]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS Elemental MediaConvert]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-06-02]
//snippet-sourceauthor:[AWS-JSDG]

// ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
// https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/emc-examples-jobs.html
//
// NOTE:
// This is part 3 of 3 for this example.
// The first part is emc_createjob_config.js.
// The second part is emc_createjob_define.js.
// The third part is emc_createjob.js. (this file)
//

// snippet-start:[mediaconvert.JavaScript.jobs.createJob]
//
// Part 3 of 3
//
// Create a promise on a MediaConvert object
var endpointPromise = new AWS.MediaConvert({apiVersion: '2017-08-29'}).createJob(params).promise();

// Handle promise's fulfilled/rejected status
endpointPromise.then(
  function(data) {
    console.log("Job created! ", data);
  },
  function(err) {
    console.log("Error", err);
  }
);
// snippet-end:[mediaconvert.JavaScript.jobs.createJob]
