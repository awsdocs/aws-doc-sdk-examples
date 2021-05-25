/*Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
    SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the
'AWS SDK for JavaScript v3 Developer Guide' at https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/lex-bot-example.html.

Purpose:
index.ts is part of a tutorial demonstrating how to build and deploy an Amazon Lex chatbot
within a web application to engage your web site visitors. To run the full tutorial, see
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/lex-bot-example.html.

Inputs (replace in code):
- REGION
- IDENTITY_POOL_ID
- BOT_ALIAS
- BOT_NAME
- USER_ID
*/

// snippet-start:[cross-service.JavaScript.lex-app.backendV3]
// ES Modules import
import { CognitoIdentityClient } from "@aws-sdk/client-cognito-identity";
// CommonJS import
// const { CognitoIdentityClient } = require("@aws-sdk/client-cognito-identity");

// ES Modules import
import {
  fromCognitoIdentityPool,
} from "@aws-sdk/credential-provider-cognito-identity";
// CommonJS import
/* const {
  fromCognitoIdentityPool,
} = require("@aws-sdk/credential-provider-cognito-identity");*/

// ES Modules import
import {
  ComprehendClient,
  DetectDominantLanguageCommand
} from "@aws-sdk/client-comprehend";
// CommonJS import
/* const {
  ComprehendClient,
  DetectDominantLanguageCommand
} = require("@aws-sdk/client-comprehend");*/

// ES Modules import
import {
  TranslateClient,
  TranslateTextCommand
} from "@aws-sdk/client-translate";
// CommonJS import
/* const {
  TranslateClient,
  TranslateTextCommand
} = require("@aws-sdk/client-translate");*/

// ES Modules import
import {
  LexRuntimeServiceClient,
  PostTextCommand
} from "@aws-sdk/client-lex-runtime-service";
// CommonJS import
/* const {
  LexRuntimeServiceClient,
  PostTextCommand
} = require("@aws-sdk/client-lex-runtime-service");*/


const REGION = "REGION"; //e.g. "us-east-1"
const IdentityPoolId = "IDENTITY_POOL_ID";
const comprehendClient = new ComprehendClient({
  region: REGION,
  credentials: fromCognitoIdentityPool({
    client: new CognitoIdentityClient({ region: REGION }),
    identityPoolId: IdentityPoolId
  }),
});
const lexClient = new LexRuntimeServiceClient({
  region: REGION,
  credentials: fromCognitoIdentityPool({
    client: new CognitoIdentityClient({ region: REGION }),
    identityPoolId: IdentityPoolId
  }),
});
const translateClient = new TranslateClient({
  region: REGION,
  credentials: fromCognitoIdentityPool({
    client: new CognitoIdentityClient({ region: REGION }),
    identityPoolId: IdentityPoolId
  }),
});

var g_text = "";
// set the focus to the input box
document.getElementById("wisdom").focus();

function showRequest(daText) {
  var conversationDiv = document.getElementById("conversation");
  var requestPara = document.createElement("P");
  requestPara.className = "userRequest";
  requestPara.appendChild(document.createTextNode(g_text));
  conversationDiv.appendChild(requestPara);
  conversationDiv.scrollTop = conversationDiv.scrollHeight;
};

function showResponse(lexResponse) {
  var conversationDiv = document.getElementById("conversation");
  var responsePara = document.createElement("P");
  responsePara.className = "lexResponse";

  var lexTextResponse = lexResponse;

  responsePara.appendChild(document.createTextNode(lexTextResponse));
  responsePara.appendChild(document.createElement("br"));
  conversationDiv.appendChild(responsePara);
  conversationDiv.scrollTop = conversationDiv.scrollHeight;
};

function handletext(text) {
  g_text = text;
  var xhr = new XMLHttpRequest();
  xhr.addEventListener("load", loadNewItems, false);
  xhr.open("POST", "../text", true); // A Spring MVC controller
  xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded"); //necessary
  xhr.send("text=" + text);
};

function loadNewItems(event) {
  var msg = event.target.responseText;
  showRequest();

  // re-enable input
  var wisdomText = document.getElementById("wisdom");
  wisdomText.value = "";
  wisdomText.locked = false;
};

// Respond to user's input.
const createResponse = async () => {
  // Confirm there is text to submit.
  var wisdomText = document.getElementById("wisdom");
  if (wisdomText && wisdomText.value && wisdomText.value.trim().length > 0) {
    // Disable input to show it is being sent.
    var wisdom = wisdomText.value.trim();
    wisdomText.value = "...";
    wisdomText.locked = true;

    const comprehendParams = {
      Text: wisdom
    };
    try {
      const data = await comprehendClient.send(
        new DetectDominantLanguageCommand(comprehendParams)
      );
      console.log("Success. The language code is: ", data.Languages[0].LanguageCode);
      const translateParams = {
        SourceLanguageCode: data.Languages[0].LanguageCode,
        TargetLanguageCode: "en", // For example, "en" for English.
        Text: wisdom
      };
      try {
        const data = await translateClient.send(
          new TranslateTextCommand(translateParams)
        );
        console.log("Success. Translated text: ", data.TranslatedText);
        const lexParams = {
          botAlias: "BOT_ALIAS",
          botName: "BOT_NAME",
          inputText: data.TranslatedText,
          userId: "USER_ID" // For example, 'chatbot-demo'.
        };
        try {
          const data = await lexClient.send(new PostTextCommand(lexParams));
          console.log("Success. Response is: ", data.message);
          var msg = data.message;
          showResponse(msg);
        } catch (err) {
          console.log("Error responding to message. ", err);
        }
      } catch (err) {
        console.log("Error translating text. ", err);
      }
    } catch (err) {
      console.log("Error identifying language. ", err);
    }
  }
};
// Make the function available to the browser.
window.createResponse = createResponse;
// snippet-end:[cross-service.JavaScript.lex-app.backendV3]
