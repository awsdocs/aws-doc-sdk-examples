// snippet-start:[javascript.v3.reactnative.GettingStarted]
import React, {useCallback, useState} from 'react';
import {Button, StyleSheet, Text, TextInput, View} from 'react-native';
import 'react-native-get-random-values';
import 'react-native-url-polyfill/auto';

import {
  S3Client,
  CreateBucketCommand,
  DeleteBucketCommand,
} from '@aws-sdk/client-s3';
import {fromCognitoIdentityPool} from '@aws-sdk/credential-providers';

const client = new S3Client({
  // The region where the S3 bucket will be created. Replace this with your region.
  region: 'us-east-2',
  credentials: fromCognitoIdentityPool({
    // Replace the value of 'identityPoolId' with the ID of an Amazon Cognito identity pool in your Cognito region.
    identityPoolId: 'us-east-2:fa8837b4-d4e6-49b2-98d3-1c0a6683e3aa',
    // Replace the value of 'region' with your Cognito region.
    clientConfig: {region: 'us-east-2'},
  }),
});

const App = () => {
  const [bucketName, setBucketName] = useState('');
  const [successMsg, setSuccessMsg] = useState('');
  const [errorMsg, setErrorMsg] = useState('');

  const createBucket = useCallback(async () => {
    setSuccessMsg('');
    setErrorMsg('');
    console.log(bucketName);

    try {
      await client.send(new CreateBucketCommand({Bucket: bucketName}));
      setSuccessMsg(`Bucket "${bucketName}" created.`);
    } catch (e) {
      console.error(e);
      setErrorMsg(e instanceof Error ? e.message : 'Unknown error');
    }
  }, [bucketName]);

  const deleteBucket = useCallback(async () => {
    setSuccessMsg('');
    setErrorMsg('');
    console.log(bucketName);
    try {
      await client.send(new DeleteBucketCommand({Bucket: bucketName}));
      setSuccessMsg(`Bucket "${bucketName}" deleted.`);
    } catch (e) {
      setErrorMsg(e instanceof Error ? e.message : 'Unknown error');
    }
  }, [bucketName]);

  return (
    <View style={styles.container}>
      <Text style={styles.successText}>
        {successMsg ? `Success: ${successMsg}` : ''}
      </Text>
      <Text style={styles.failureText}>
        {errorMsg ? `Error: ${errorMsg}` : ''}
      </Text>
      <View>
        <TextInput
          onChangeText={text => setBucketName(text)}
          autoCapitalize={'none'}
          value={bucketName}
          placeholder={'Enter Bucket Name'}
        />
        <Button color="#68a0cf" title="Create Bucket" onPress={createBucket} />
        <Button color="#68a0cf" title="Delete Bucket" onPress={deleteBucket} />
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  successText: {
    color: 'green',
  },
  failureText: {
    color: 'red',
  },
});

export default App;
// snippet-end:[javascript.v3.reactnative.GettingStarted]
