/*
   Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

// Import a non-modular IAM client
const { IAMClient, CreateRoleCommand, AttachRolePolicyCommand } = require('@aws-sdk/client-iam');
// Instantiate the IAM client
const iam = new IAMClient({region: 'us-west-2'});

const ROLE = 'ROLE';

const myPolicy = {
  'Version': '2012-10-17',
  'Statement': [
    {
      'Effect': 'Allow',
      'Principal': {
        'Service': 'lambda.amazonaws.com'
      },
      'Action': 'sts:AssumeRole'
    }
  ]
};

const createParams = {
 AssumeRolePolicyDocument: JSON.stringify(myPolicy),
 RoleName: ROLE
};

const lambdaPolicyParams = {
 PolicyArn: 'arn:aws:iam::aws:policy/service-role/AWSLambdaRole',
 RoleName: ROLE
};

const dynamoPolicyParams = {
 PolicyArn: 'arn:aws:iam::aws:policy/AmazonDynamoDBReadOnlyAccess',
 RoleName: ROLE
};

async function run() {
  try {
    const data = await iam.send(new CreateRoleCommand(createParams));
    console.log('Role ARN is', data.Role.Arn);  // successful response
  } catch(err) {
    console.log('Error when creating role.'); // an error occurred
    throw err;
  }
  try {
    await iam.send(new AttachRolePolicyCommand(lambdaPolicyParams));
    console.log('AWSLambdaRole policy attached');  // successful response
  } catch (err) {
    console.log('Error when attaching Lambda policy to role.'); // an error occurred
    throw err;
  }
  try {
    await iam.send(new AttachRolePolicyCommand(dynamoPolicyParams));
    console.log('DynamoDB read-only policy attached');  // successful response
  } catch (err) {
    console.log('Error when attaching dynamodb policy to role.'); // an error occurred
    throw err;
  }
}

run();