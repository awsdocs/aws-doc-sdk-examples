// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

export const Config = {
  StackName: "textract-react",
  DefaultImageName: "default_document_3.png",
  CognitoIdentityPoolId: "us-east-1:e831f5d1-3bb1-4c38-80a6-ac4a41094e16",
  CognitoUserPoolId: "us-east-1_d0mAaaf9U",
  DeployRegion: "us-east-1",
  SNSTopicArn:
    "arn:aws:sns:us-east-1:901487484989:textract-react-textractcognitodemotopicEEA53D4C-kScOieHI8JEs",
  DefaultBucketName:
    "textract-react-textractcognitodemobucket90cf6a3d-ido8j5smr8wg",
  CognitoId: "cognito-idp.us-east-1.amazonaws.com/us-east-1_d0mAaaf9U",
  LoginUrl:
    "https://<DOMAIN>.auth.us-east-1.amazoncognito.com/login?client_id=4mnjju6vbc48jn4vtqgi7ug8d&response_type=token&scope=aws.cognito.signin.user.admin+email+openid+phone+profile&redirect_uri=http://localhost:3000",
  RoleArn:
    "arn:aws:iam::901487484989:role/textract-react-textractcognitodemotextractrole79875-Og6cWz9azWMs",
  QueueUrl:
    "https://sqs.us-east-1.amazonaws.com/901487484989/textract-react-textractcognitodemoqueue80660218-RrlFxsn3Hr4a",
};
