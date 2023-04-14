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
  // The AWS Region where the Amazon Simple Storage Service (Amazon S3) bucket will be created. Replace this with your Region.
  region: 'us-east-2',
  credentials: fromCognitoIdentityPool({
    // Replace the value of 'identityPoolId' with the ID of an Amazon Cognito identity pool in your Amazon Cognito Region.
    identityPoolId: 'us-east-2:fa8837b4-d4e6-49b2-98d3-1c0a6683e3aa',
    // Replace the value of 'region' with your Amazon Cognito Region.
    clientConfig: {region: 'us-east-2'},
  }),
});

enum MessageType {
  SUCCESS,
  FAILURE,
  EMPTY,
}

const App = () => {
  const [bucketName, setBucketName] = useState('');
  const [msg, setMsg] = useState<{message: string; type: MessageType}>({
    message: '',
    type: MessageType.EMPTY,
  });

  const createBucket = useCallback(async () => {
    setMsg({message: '', type: MessageType.EMPTY});

    try {
      await client.send(new CreateBucketCommand({Bucket: bucketName}));
      setMsg({
        message: `Bucket "${bucketName}" created.`,
        type: MessageType.SUCCESS,
      });
    } catch (e) {
      console.error(e);
      setMsg({
        message: e instanceof Error ? e.message : 'Unknown error',
        type: MessageType.FAILURE,
      });
    }
  }, [bucketName]);

  const deleteBucket = useCallback(async () => {
    setMsg({message: '', type: MessageType.EMPTY});

    try {
      await client.send(new DeleteBucketCommand({Bucket: bucketName}));
      setMsg({
        message: `Bucket "${bucketName}" deleted.`,
        type: MessageType.SUCCESS,
      });
    } catch (e) {
      setMsg({
        message: e instanceof Error ? e.message : 'Unknown error',
        type: MessageType.FAILURE,
      });
    }
  }, [bucketName]);

  return (
    <View style={styles.container}>
      {msg.type !== MessageType.EMPTY && (
        <Text
          style={
            msg.type === MessageType.SUCCESS
              ? styles.successText
              : styles.failureText
          }>
          {msg.message}
        </Text>
      )}
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
