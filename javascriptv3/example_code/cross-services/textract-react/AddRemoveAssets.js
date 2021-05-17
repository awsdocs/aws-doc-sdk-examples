// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/**
 * This script adds ore removes additional assets for the example after the
 * primary resources have been deployed by running the setup.yaml AWS CloudFormation
 * script.
 *
 * Before running this script, deploy the CloudFormation stack by running the following
 * at a command prompt:
 *
 *     aws cloudformation create-stack --stack-name <stack-name> --template-body
 *     file://setup.yaml --capabilities CAPABILITY_IAM
 *
 * ** Add assets **
 * Add additional demo assets by running the following at a command prompt:
 *
 *     node AddRemoveAssets.js add <stack-name>
 *
 * Remember to substitute the name of the CloudFormation stack for `<stack-name>`.
 *
 * ** Remove assets **
 * Remove the additional demo assets by running the following at a command prompt:
 *
 *     node AddRemoveAssets.js remove
 *
 * Remember to destroy the CloudFormation stack after running this script to completely
 * remove resources created for the demo.
 */

import fs from "fs";
import { Config } from "./src/Config.js";
import {
  S3Client,
  PutObjectCommand,
  DeleteObjectCommand,
} from "@aws-sdk/client-s3";
import {
  CognitoIdentityProviderClient,
  AdminDeleteUserCommand,
  ListUsersCommand,
} from "@aws-sdk/client-cognito-identity-provider";
import {
  CognitoIdentityClient,
  DeleteIdentitiesCommand,
  ListIdentitiesCommand,
} from "@aws-sdk/client-cognito-identity";
import {
  CloudFormationClient,
  DescribeStacksCommand,
} from "@aws-sdk/client-cloudformation";

const ConfigError =
  "Demo resources not initialized. You must deploy AWS resources " +
  "and demo elements before running this application. See the README for details.";
const DefaultImageName = "default_document_3.png";

const mode = process.argv[2];
const stackName = process.argv[3];
const s3 = new S3Client({});
if (mode === "add") {
  console.log(`Updating Config.js with outputs from stack ${stackName}.`);
  const cform = new CloudFormationClient({});
  (async () => {
    try {
      const { Stacks } = await cform.send(
        new DescribeStacksCommand({
          StackName: stackName,
        })
      );
      let configOutputs =
        `  StackName: '${stackName}',\n` +
        `  DefaultImageName: '${DefaultImageName}',\n`;
      Stacks[0].Outputs.forEach((current) => {
        configOutputs += `  ${current.OutputKey}: '${current.OutputValue}',\n`;
        Config[current.OutputKey] = current.OutputValue;
      });
      fs.writeFileSync(
        "src/Config.js",
        `export const Config = {\n${configOutputs}};`
      );

      console.log(
        `Uploading demo image ${DefaultImageName} to ` +
          `bucket ${Config.DefaultBucketName}.`
      );
      const imageFile = fs.readFileSync(`src/.media/${DefaultImageName}`);
      await s3.send(
        new PutObjectCommand({
          Bucket: Config.DefaultBucketName,
          Key: DefaultImageName,
          Body: imageFile,
        })
      );

      console.log("Demo assets successfully added!");
    } catch (error) {
      console.log(error.message);
    }
  })();
} else if (mode === "remove") {
  console.log(`Emptying bucket ${Config.DefaultBucketName}.`);
  (async () => {
    try {
      await s3.send(
        new DeleteObjectCommand({
          Bucket: Config.DefaultBucketName,
          Key: Config.DefaultImageName,
        })
      );
    } catch (error) {
      console.log(error.message);
    }
  })();

  console.log(`Removing users from user pool ${Config.CognitoUserPoolId}.`);
  const cogProvider = new CognitoIdentityProviderClient({});
  (async () => {
    try {
      const { Users } = await cogProvider.send(
        new ListUsersCommand({
          UserPoolId: Config.CognitoUserPoolId,
        })
      );
      Users.forEach((user) => {
        console.log(`Deleting user ${user.Username}`);
        cogProvider.send(
          new AdminDeleteUserCommand({
            UserPoolId: Config.CognitoUserPoolId,
            Username: user.Username,
          })
        );
      });
    } catch (error) {
      console.log(error.message);
    }
  })();

  const cogIdentity = new CognitoIdentityClient({});
  (async () => {
    try {
      const { Identities } = await cogIdentity.send(
        new ListIdentitiesCommand({
          IdentityPoolId: Config.CognitoIdentityPoolId,
          MaxResults: 10,
        })
      );
      const idList = Identities.map((id) => id.IdentityId);
      console.log(
        `Removing identities ${idList} from identity` +
          `pool ${Config.CognitoIdentityPoolId}.`
      );
      await cogIdentity.send(
        new DeleteIdentitiesCommand({
          IdentityIdsToDelete: idList,
        })
      );
    } catch (error) {
      console.log(error.message);
    }

    let configOutputs = `  ConfigError: '${ConfigError}'\n`;
    fs.writeFileSync(
      "src/Config.js",
      `export const Config = {\n${configOutputs}};`
    );

    console.log(
      "Demo assets removed. Now run `aws cloudformation delete-stack " +
        "stack-name <stack-name>` to delete resources deployed for the demo."
    );
  })();
} else {
  console.log(
    "Run with `add <stack-name>` to add demo assets.\n" +
      "Run with `remove` to remove them."
  );
}
