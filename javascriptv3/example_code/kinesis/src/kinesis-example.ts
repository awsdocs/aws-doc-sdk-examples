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
// snippet-sourcedescription:[kinesis-example.js demonstrates how to capture web page scroll progress]
// snippet-service:[kinesis]
// snippet-keyword:[JavaScript]
// snippet-sourcesyntax:[javascript]
// snippet-keyword:[Amazon Kinesis]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[]
// snippet-sourceauthor:[AWS_JSDG]

// ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
// https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/kinesis-examples-capturing-page-scrolling-full.html

// snippet-start:[kinesis.JavaScript.kinesis-example.complete]
// snippet-start:[kinesis.JavaScript.kinesis-example.config]
// Configure Credentials to use Cognito
AWS.config.credentials = new AWS.CognitoIdentityCredentials({
  IdentityPoolId: "IDENTITY_POOL_ID",
});

AWS.config.region = "REGION";
// We're going to partition Amazon Kinesis records based on an identity.
// We need to get credentials first, then attach our event listeners.
AWS.config.credentials.get(function (err) {
  // attach event listener
  if (err) {
    alert("Error retrieving credentials.");
    console.error(err);
    return;
  }
  // create Amazon Kinesis service object
  const kinesis = new AWS.Kinesis({
    apiVersion: "2013-12-02",
  });
  // snippet-end:[kinesis.JavaScript.kinesis-example.config]

  // snippet-start:[kinesis.JavaScript.kinesis-example.addEventListener]
  // Get the ID of the Web page element.
  const blogContent = document.getElementById("BlogContent");

  // Get Scrollable height
  const scrollableHeight = blogContent.clientHeight;

  const recordData = [];
  let TID = null;
  blogContent.addEventListener("scroll", function (event) {
    clearTimeout(TID);
    // Prevent creating a record while a user is actively scrolling
    TID = setTimeout(function () {
      // calculate percentage
      const scrollableElement = event.target;
      const scrollHeight = scrollableElement.scrollHeight;
      const scrollTop = scrollableElement.scrollTop;

      const scrollTopPercentage = Math.round((scrollTop / scrollHeight) * 100);
      const scrollBottomPercentage = Math.round(
        ((scrollTop + scrollableHeight) / scrollHeight) * 100
      );

      // Create the Amazon Kinesis record
      const record = {
        Data: JSON.stringify({
          blog: window.location.href,
          scrollTopPercentage: scrollTopPercentage,
          scrollBottomPercentage: scrollBottomPercentage,
          time: new Date(),
        }),
        PartitionKey: "partition-" + AWS.config.credentials.identityId,
      };
      recordData.push(record);
    }, 100);
  });
  // snippet-end:[kinesis.JavaScript.kinesis-example.addEventListener]

  // snippet-start:[kinesis.JavaScript.kinesis-example.putRecords]
  // upload data to Amazon Kinesis every second if data exists
  setInterval(function () {
    if (!recordData.length) {
      return;
    }
    // upload data to Amazon Kinesis
    kinesis.putRecords(
      {
        Records: recordData,
        StreamName: "NAME_OF_STREAM",
      },
      function (err, data) {
        if (err) {
          console.error(err);
        }
      }
    );
    // clear record data
    recordData = [];
  }, 1000);
});
// snippet-end:[kinesis.JavaScript.kinesis-example.putRecords]
// snippet-end:[kinesis.JavaScript.kinesis-example.complete]
