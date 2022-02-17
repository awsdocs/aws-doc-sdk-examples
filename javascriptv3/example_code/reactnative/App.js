/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide
topic https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/getting-started-react.html.

Purpose:
App.js contains code enabling a React Native app to create and delete Amazon Simple Storage Service (Amazon S3) buckets.

Inputs:
- REGION
- IDENTITY_POOL_ID

Running the code:
Refer to the topic in the AWS SDK for JavaScript Developer Guide (https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/getting-started-react.html.
 */
// snippet-start:[s3.JavaScript.react-native-example.codev3]
import React, { useState } from "react";
import { Button, StyleSheet, Text, TextInput, View } from "react-native";

import "react-native-get-random-values";
import "react-native-url-polyfill/auto";
import {
  S3Client,
  CreateBucketCommand,
  DeleteBucketCommand,
} from "@aws-sdk/client-s3";
import { CognitoIdentityClient } from "@aws-sdk/client-cognito-identity";
import { fromCognitoIdentityPool } from "@aws-sdk/credential-provider-cognito-identity";

const App = () => {
  const [bucketName, setBucketName] = useState("");
  const [successMsg, setSuccessMsg] = useState("");
  const [errorMsg, setErrorMsg] = useState("");

  // Replace REGION with the appropriate AWS Region, such as 'us-east-1'.
  const region = "REGION";
  const client = new S3Client({
    region,
    credentials: fromCognitoIdentityPool({
      client: new CognitoIdentityClient({ region }),
      // Replace IDENTITY_POOL_ID with an appropriate Amazon Cognito Identity Pool ID for, such as 'us-east-1:xxxxxx-xxx-4103-9936-b52exxxxfd6'.
      identityPoolId: "IDENTITY_POOL_ID",
    }),
  });

  const createBucket = async () => {
    setSuccessMsg("");
    setErrorMsg("");

    try {
      await client.send(new CreateBucketCommand({ Bucket: bucketName }));
      setSuccessMsg(`Bucket "${bucketName}" created.`);
    } catch (e) {
      setErrorMsg(e);
    }
  };

  const deleteBucket = async () => {
    setSuccessMsg("");
    setErrorMsg("");

    try {
      await client.send(new DeleteBucketCommand({ Bucket: bucketName }));
      setSuccessMsg(`Bucket "${bucketName}" deleted.`);
    } catch (e) {
      setErrorMsg(e);
    }
  };

  return (
    <View style={styles.container}>
      <Text style={{ color: "green" }}>
        {successMsg ? `Success: ${successMsg}` : ``}
      </Text>
      <Text style={{ color: "red" }}>
        {errorMsg ? `Error: ${errorMsg}` : ``}
      </Text>
      <View>
        <TextInput
          style={styles.textInput}
          onChangeText={(text) => setBucketName(text)}
          autoCapitalize={"none"}
          value={bucketName}
          placeholder={"Enter Bucket Name"}
        />
        <Button
          backroundColor="#68a0cf"
          title="Create Bucket"
          onPress={createBucket}
        />
        <Button
          backroundColor="#68a0cf"
          title="Delete Bucket"
          onPress={deleteBucket}
        />
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
  },
});

export default App;

// snippet-end:[s3.JavaScript.react-native-example.codev3]
