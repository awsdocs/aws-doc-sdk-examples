/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
This file handles the translation of the transcribed text using AWS Translate

*/
// snippet-start:[translateClient.JavaScript.streaming.createclientv3]
import { CognitoIdentityClient } from "@aws-sdk/client-cognito-identity";
import { fromCognitoIdentityPool } from "@aws-sdk/credential-provider-cognito-identity";
import { TranslateClient, TranslateTextCommand } from "@aws-sdk/client-translate";
import { ComprehendClient, DetectDominantLanguageCommand } from "@aws-sdk/client-comprehend";
import * as awsID from "./awsID.js";

export const translateTextToLanguage = async (text, targetLanguage) => {
  let sourceLanguage = await detectLanguageOfText(text);
  return await translateTextFromLanguageToLanguage(text, sourceLanguage, targetLanguage);
};

const detectLanguageOfText = async(text) => {
  let comprehendClient = createComprehendClient();
  let data = await comprehendClient.send(
    new DetectDominantLanguageCommand({ Text: text })
  );
  return data.Languages[0].LanguageCode;
}

const createComprehendClient = () => {
  return new ComprehendClient({
    region: awsID.REGION,
    credentials: fromCognitoIdentityPool({
      client: new CognitoIdentityClient({ region: awsID.REGION }),
      identityPoolId: awsID.IDENTITY_POOL_ID,
    }),
  });
}

const translateTextFromLanguageToLanguage = async (text, sourceLanguage, targetLanguage) => {
  let translateClient = createTranslateClient();
  const translateParams = {
    Text: text,
    SourceLanguageCode: sourceLanguage,
    TargetLanguageCode: targetLanguage,
  };
  let data = await translateClient.send(
    new TranslateTextCommand(translateParams)
  );
  return data.TranslatedText;

}

const createTranslateClient = () => {
  return new TranslateClient({
    region: awsID.REGION,
    credentials: fromCognitoIdentityPool({
      client: new CognitoIdentityClient({ region: awsID.REGION }),
      identityPoolId: awsID.IDENTITY_POOL_ID,
    }),
  });
}

// snippet-end:[translateClient.JavaScript.streaming.createclientv3]
