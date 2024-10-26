// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[codepipeline.javascript.MyCodePipelineFunction.complete]

const assert = require("node:assert");
const AWS = require("aws-sdk");
const http = require("node:http");

exports.handler = (event, context) => {
  const codepipeline = new AWS.CodePipeline();

  // Retrieve the Job ID from the Lambda action
  const jobId = event["CodePipeline.job"].id;

  // Retrieve the value of UserParameters from the Lambda action configuration in AWS CodePipeline, in this case a URL which will be
  // health checked by this function.
  const url =
    event["CodePipeline.job"].data.actionConfiguration.configuration
      .UserParameters;

  // Notify AWS CodePipeline of a successful job
  const putJobSuccess = (message) => {
    const params = {
      jobId: jobId,
    };
    codepipeline.putJobSuccessResult(params, (err, data) => {
      if (err) {
        context.fail(err);
      } else {
        context.succeed(message);
      }
    });
  };

  // Notify AWS CodePipeline of a failed job
  const putJobFailure = (message) => {
    const params = {
      jobId: jobId,
      failureDetails: {
        message: JSON.stringify(message),
        type: "JobFailed",
        externalExecutionId: context.invokeid,
      },
    };
    codepipeline.putJobFailureResult(params, (err, data) => {
      context.fail(message);
    });
  };

  // Validate the URL passed in UserParameters
  if (!url || url.indexOf("http://") === -1) {
    putJobFailure(
      "The UserParameters field must contain a valid URL address to test, including http:// or https://",
    );
    return;
  }

  // Helper function to make a HTTP GET request to the page.
  // The helper will test the response and succeed or fail the job accordingly
  const getPage = (url, callback) => {
    const pageObject = {
      body: "",
      statusCode: 0,
      contains: function (search) {
        return this.body.indexOf(search) > -1;
      },
    };
    http
      .get(url, (response) => {
        pageObject.body = "";
        pageObject.statusCode = response.statusCode;

        response.on("data", (chunk) => {
          pageObject.body += chunk;
        });

        response.on("end", () => {
          callback(pageObject);
        });

        response.resume();
      })
      .on("error", (error) => {
        // Fail the job if our request failed
        putJobFailure(error);
      });
  };

  getPage(url, (returnedPage) => {
    try {
      // Check if the HTTP response has a 200 status
      assert(returnedPage.statusCode === 200);
      // Check if the page contains the text "Congratulations"
      // You can change this to check for different text, or add other tests as required
      assert(returnedPage.contains("Congratulations"));

      // Succeed the job
      putJobSuccess("Tests passed.");
    } catch (ex) {
      // If any of the assertions failed then fail the job
      putJobFailure(ex);
    }
  });
};

// snippet-end:[codepipeline.javascript.MyCodePipelineFunction.complete]
