/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { fileURLToPath } from "url";

// snippet-start:[javascript.iam_scenarios.iam_basics]
import {
  CreateUserCommand,
  CreateAccessKeyCommand,
  CreatePolicyCommand,
  CreateRoleCommand,
  AttachRolePolicyCommand,
  DeleteAccessKeyCommand,
  DeleteUserCommand,
  DeleteRoleCommand,
  DeletePolicyCommand,
  DetachRolePolicyCommand,
  IAMClient,
} from "@aws-sdk/client-iam";
import { ListBucketsCommand, S3Client } from "@aws-sdk/client-s3";
import { AssumeRoleCommand, STSClient } from "@aws-sdk/client-sts";
import { wait } from "libs/utils/util-timers.js";

// Set the parameters.
const iamClient = new IAMClient({});
const userName = "test_name";
const policyName = "test_policy";
const roleName = "test_role";

export const main = async () => {
  try {
    // Create a user. The user has no permissions by default.
    const { User } = await iamClient.send(
      new CreateUserCommand({ UserName: userName })
    );

    // Create an access key. This key is used to authenticate the new user to
    // Amazon S3 and AWS Security Token Service (AWS STS).
    // It's not best practice to use access keys. For more information, see https://aws.amazon.com/iam/resources/best-practices/.
    const {
      AccessKey: { AccessKeyId, SecretAccessKey },
    } = await iamClient.send(
      new CreateAccessKeyCommand({ UserName: userName })
    );

    let s3Client = new S3Client({
      credentials: {
        accessKeyId: AccessKeyId,
        secretAccessKey: SecretAccessKey,
      },
    });

    // Wait for the user and access key to exist.
    await wait(10);

    // This fails and logs an error because the user does not have permission
    // to list Amazon S3 buckets.
    await listBuckets(s3Client);

    // Create a role that will be granted to the user.
    const { Role } = await iamClient.send(
      new CreateRoleCommand({
        AssumeRolePolicyDocument: JSON.stringify({
          Version: "2012-10-17",
          Statement: [
            {
              Effect: "Allow",
              Principal: {
                // Allow the previously created user to assume this role.
                AWS: User.Arn,
              },
              Action: "sts:AssumeRole",
            },
          ],
        }),
        RoleName: roleName,
      })
    );

    // Create a policy that allows the user to list Amazon S3 buckets.
    const { Policy: listBucketPolicy } = await iamClient.send(
      new CreatePolicyCommand({
        PolicyDocument: JSON.stringify({
          Version: "2012-10-17",
          Statement: [
            {
              Effect: "Allow",
              Action: ["s3:ListAllMyBuckets"],
              Resource: "*",
            },
          ],
        }),
        PolicyName: policyName,
      })
    );

    // Attach the policy granting the 's3:ListAllMyBuckets' action to the role.
    await iamClient.send(
      new AttachRolePolicyCommand({
        PolicyArn: listBucketPolicy.Arn,
        RoleName: Role.RoleName,
      })
    );

    // Assume the role.
    const stsClient = new STSClient({
      credentials: {
        accessKeyId: AccessKeyId,
        secretAccessKey: SecretAccessKey,
      },
    });

    // Wait for the role to be attached.
    await wait(10);

    const { Credentials } = await stsClient.send(
      new AssumeRoleCommand({
        RoleArn: Role.Arn,
        RoleSessionName: `iamBasicScenarioSession-${Math.floor(
          Math.random() * 1000000
        )}`,
        DurationSeconds: 900,
      })
    );

    s3Client = new S3Client({
      credentials: {
        accessKeyId: Credentials.AccessKeyId,
        secretAccessKey: Credentials.SecretAccessKey,
        sessionToken: Credentials.SessionToken,
      },
    });

    // List the Amazon S3 buckets again.
    await listBuckets(s3Client);

    // Clean up.
    await iamClient.send(
      new DetachRolePolicyCommand({
        PolicyArn: listBucketPolicy.Arn,
        RoleName: Role.RoleName,
      })
    );

    await iamClient.send(
      new DeletePolicyCommand({
        PolicyArn: listBucketPolicy.Arn,
      })
    );

    await iamClient.send(
      new DeleteRoleCommand({
        RoleName: Role.RoleName,
      })
    );

    await iamClient.send(
      new DeleteAccessKeyCommand({
        UserName: userName,
        AccessKeyId,
      })
    );

    await iamClient.send(
      new DeleteUserCommand({
        UserName: userName,
      })
    );
  } catch (err) {
    console.error(err);
  }
};

/**
 *
 * @param {S3Client} s3Client
 */
const listBuckets = async (s3Client) => {
  try {
    const { Buckets } = await s3Client.send(new ListBucketsCommand({}));
    console.log(Buckets.map((bucket) => bucket.Name).join("\n"));
  } catch (err) {
    console.error(err);
  }
};

// snippet-end:[javascript.iam_scenarios.iam_basics]

// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  main();
}
