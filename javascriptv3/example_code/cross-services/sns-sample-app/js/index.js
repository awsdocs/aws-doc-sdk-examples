/*Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
    SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
index.js contains the JavaScript for a tutorial demonstrating subscription and publish functionality by using the
Amazon Simple Notification Service (Amazon SNS) using the JavaScript SDK for JavaScript v3.
To run the full tutorial, see https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/javascriptv3/example_code/cross-services/sns-sample-app.
-->
<!-- snippet-start:[sns-functions.JavaScript.frontend.complete] -->
<!-- snippet-start:[sns-functions.JavaScript.frontend.config] -->
Inputs:
- TOPIC_ARN
 */
import {
  SubscribeCommand,
  ListSubscriptionsByTopicCommand,
  ListSubscriptionsCommand,
  UnsubscribeCommand,
    PublishCommand
} from "@aws-sdk/client-sns";
import { snsClient } from "../libs/snsClient.js";

// Set global parameters.
const TOPIC_ARN = "TOPIC_ARN";

/*<!-- snippet-end:[sns-functions.JavaScript.frontend.config] -->
<!-- snippet-start:[sns-functions.JavaScript.frontend.functions] -->*/

// Subscribe the email address to the Amazon SNS topic.
const subEmail = async () => {
  const userEmail = document.getElementById("inputEmail1").value;
  var result = validate(userEmail);
  if (result == false) {
    alert(userEmail + " is not valid. Please specify a valid email");
    return;
  }
  // Set the parameters
  const params = {
    Protocol: "email" /* required */,
    TopicArn: TOPIC_ARN, //TOPIC_ARN
    Endpoint: userEmail, //EMAIL_ADDRESS
  };
  try {
    const data = await snsClient.send(new SubscribeCommand(params));
    console.log("Success.", data);
    return data; // For unit tests.
  } catch (err) {
    console.log("Error", err.stack);
  }
};
// Expose the function to the browser.
window.subEmail = subEmail;

// Helper function to validate email addresses.
function validateEmail(email) {
  const re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
  return re.test(email);
}

// Helper function to validate email addresses.
function validate(email) {
  if (validateEmail(email)) {
    return true;
  } else {
    return false;
  }
}

// List the subscriptions to the Amazon SNS topic.
const getSubs = async () => {
  const params = {
    TopicArn: TOPIC_ARN,
  };
  try {
    const data = await snsClient.send(
      new ListSubscriptionsByTopicCommand(params)
    );
    console.log("Success.", data.Subscriptions[0].SubscriptionArn);
    var alertBody = "";
    for (let i = 0; i < data.Subscriptions.length; i++) {
      alertBody +=
        "Owner: " +
        data.Subscriptions[i].Owner +
        "\n" +
        "Email: " +
        data.Subscriptions[i].Endpoint +
        "\n" +
        "\n";
    }
    alert(alertBody);
  } catch (err) {
    console.log("Error", err.stack);
  }
};
// Expose the function to the browser.
window.getSubs = getSubs;

// Unsubscribe an email subscription from Amazon SNS topic.
const delSub = async () => {
  const userEmail = document.getElementById("inputEmail1").value;
  var result = validate(userEmail);
  if (result == false) {
    alert(userEmail + " is not valid. Please specify a valid email");
    return;
  }
  try {
    const data = await snsClient.send(new ListSubscriptionsCommand({}));
    console.log("Success.", data.Subscriptions[0].Endpoint);
    for (let i = 0; i < data.Subscriptions.length; i++) {
      if (data.Subscriptions[i].Endpoint == userEmail) {
        try {
          const params = {
            SubscriptionArn: data.Subscriptions[i].SubscriptionArn,
          };
          const result = await snsClient.send(new UnsubscribeCommand(params));
          console.log("Subscription deleted.", result);
        } catch (err) {
          console.log("Error", err.stack);
        }
      }
    }
  } catch (err) {
    console.log("Error", err.stack);
  }
};
// Expose the function to the browser.
window.delSub = delSub;

// Send a message to all emails subscribed to the Amazon SNS topic.
const sendMessage = async () => {
  const messageText = document.getElementById("body").value;
  if (messageText == "") {
    alert("Please enter text");
    return;
  }
  const params = {
    Message: messageText,
    TopicArn: TOPIC_ARN,
  };
  try {
    await snsClient.send(new PublishCommand(params));
    alert("Message published.");
  } catch (err) {
    console.log("Error", err.stack);
  }
};
// Expose the function to the browser.
window.sendMessage = sendMessage;
/*<!-- snippet-end:[sns-functions.JavaScript.frontend.functions] -->
<!-- snippet-end:[sns-functions.JavaScript.frontend.complete] -->*/
