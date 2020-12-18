import React, { useState } from "react";
import { Button, StyleSheet, Text, TextInput, View } from "react-native";

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

  const region = "REGION"; // REGION
  const client = new S3Client({
    region,
    credentials: fromCognitoIdentityPool({
      client: new CognitoIdentityClient({ region }),
      identityPoolId: "IDENTITY_POOL_ID", // IDENTITY_POOL_ID
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
