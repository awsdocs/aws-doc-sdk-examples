/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
Shows how to use the AWS SDK for JavaScript (v3) to create an AWS Identity and Access Management (IAM) user, assume a role,
and perform AWS actions.
1. Create a user who has no permissions.
2. Create a role that grants the user permission to list Amazon S3 buckets for the account.
3. Add a policy to let the user assume the role.
4. Assume the role and list S3 buckets using temporary credentials.
5. Delete the policy, role, and user.


Inputs (in command line):
node iam_basics.js <user name> <s3 policy name> <role name> <assume policy name>

Running the code:
node iam_basics.js
*/
// snippet-start:[javascript.iam_scenarios.iam_basics]
// Import required AWS SDK clients and commands for Node.js.
import { iamClient, REGION } from "../libs/iamClient.js"; // Helper function that creates an IAM service client module.
import {
  CreateUserCommand,
  CreateAccessKeyCommand,
  CreatePolicyCommand,
  CreateRoleCommand,
  AttachRolePolicyCommand,
  AttachUserPolicyCommand,
  DeleteAccessKeyCommand,
  DeleteUserCommand,
  DeleteRoleCommand,
  DeletePolicyCommand,
  DetachUserPolicyCommand,
  DetachRolePolicyCommand,
} from "@aws-sdk/client-iam";
import { ListBucketsCommand, S3Client } from "@aws-sdk/client-s3";
import { AssumeRoleCommand, STSClient } from "@aws-sdk/client-sts";

if (process.argv.length < 6) {
  console.log(
      "Usage: node iam_basics.js <user name> <s3 policy name> <role name> <assume policy name>\n" +
      "Example: node iam_basics.js test-user my-s3-policy my-iam-role my-assume-role"
  );
}
// Set the parameters.
const region = REGION;
const userName = process.argv[2];
const s3_policy_name = process.argv[3];
const role_name = process.argv[4];
const assume_policy_name = process.argv[5];

// Helper function to delay running the code while the AWS service calls wait for responses.
function wait(ms) {
  var start = Date.now();
  var end = start
  while (end < start + ms){
    end = Date.now()
  }
}

export const run = async (
    userName,
    s3_policy_name,
    role_name,
    assume_policy_name
) => {
  try {
    // Create a new user.
    const user_params = { UserName: userName };
    console.log("\nCreating a user name " + user_params.UserName + "...\n");
    const data = await iamClient.send(
        new CreateUserCommand({ UserName: userName })
    );
    const user_arn = data.User.Arn;
    const user_name = data.User.UserName;
    console.log(
        "User with name" + user_name + " and ARN " + user_arn + " created."
    );
    try {
      // Create access keys for the new user.
      console.log(
          "\nCreating access keys for " + user_params.UserName + "...\n"
      );
      const access_key_params = { UserName: user_name };
      const data = await iamClient.send(
          new CreateAccessKeyCommand(access_key_params)
      );
      console.log("Success. Access key created: ", data.AccessKey.AccessKeyId);
      var myAccessKey = data.AccessKey.AccessKeyId;
      var mySecretAccessKey = data.AccessKey.SecretAccessKey;

      try {
        // Attempt to list S3 buckets.
        console.log(
            "\nWaiting 10 seconds for user and access keys to be created...\n"
        );
        wait(10000);
        console.log(
            "Attempt to list S3 buckets with the new user (without permissions)...\n"
        );
        // Use the credentials for the new user that you created.
        var user_creds = {
          accessKeyId: myAccessKey,
          secretAccessKey: mySecretAccessKey,
        };
        const s3Client = new S3Client({
          credentials: user_creds,
          region: region,
        });
        await s3Client.send(new ListBucketsCommand({}));
      } catch (err) {
        console.log(
            "Error. As expected the new user has no permissions to list buckets. ",
            err.stack
        );
        console.log(
            "\nCreating policy to allow the new user to list all buckets, and to assume an STS role...\n"
        );
        const myManagedPolicy = {
          Version: "2012-10-17",
          Statement: [
            {
              Effect: "Allow",
              Action: ["s3:ListAllMyBuckets", "sts:AssumeRole"],
              Resource: "*",
            },
          ],
        };
        const policy_params = {
          PolicyDocument: JSON.stringify(myManagedPolicy),
          PolicyName: s3_policy_name, // Name of the new policy.
        };
        const data = await iamClient.send(
            new CreatePolicyCommand(policy_params)
        );
        console.log(
            "Success. Policy created that allows listing of all S3 buckets.\n" +
            "Policy ARN: " +
            data.Policy.Arn +
            "\n" +
            "Policy name: " +
            data.Policy.PolicyName +
            "\n"
        );

        var s3_policy_arn = data.Policy.Arn;

        try {
          console.log(
              "\nCreating a role with a trust policy that lets the user assume the role....\n"
          );

          const role_json = {
            Version: "2012-10-17",
            Statement: [
              {
                Effect: "Allow",
                Principal: {
                  AWS: user_arn, // The ARN of the user.
                },
                Action: "sts:AssumeRole",
              },
            ],
          };
          const myJson = JSON.stringify(role_json);

          const role_params = {
            AssumeRolePolicyDocument: myJson, // Trust relationship policy document.
            Path: "/",
            RoleName: role_name // The name of the new role.
          };
          const data = await iamClient.send(new CreateRoleCommand(role_params));
          console.log("Success. Role created. Role Arn: ", data.Role.Arn);
          const role_arn = data.Role.Arn;
          try {
            console.log(
                "\nAttaching to the role the policy with permissions to list all buckets....\n"
            );
            const params = {
              PolicyArn: s3_policy_arn,
              RoleName: role_name,
            };
            await iamClient.send(new AttachRolePolicyCommand(params));
            console.log("Success. Policy attached successfully to role.");
            try {
              console.log(
                  "\nCreate a policy that enables the user to assume the role ....\n"
              );
              const myNewPolicy = {
                Version: "2012-10-17",
                Statement: [
                  {
                    Effect: "Allow",
                    Action: ["sts:AssumeRole"],
                    Resource: role_arn,
                  },
                ],
              };
              const policy_params = {
                PolicyDocument: JSON.stringify(myNewPolicy),
                PolicyName: assume_policy_name,
              };
              const data = await iamClient.send(
                  new CreatePolicyCommand(policy_params)
              );
              console.log(
                  "Success. Policy created. Policy ARN: " + data.Policy.Arn
              );

              const assume_policy_arn = data.Policy.Arn;
              try {
                console.log("\nAttaching the policy to the user.....\n");
                const attach_policy_to_user_params = {
                  PolicyArn: assume_policy_arn,
                  UserName: user_name,
                };
                const data = await iamClient.send(
                    new AttachUserPolicyCommand(attach_policy_to_user_params)
                );
                console.log(
                    "\nWaiting 10 seconds for policy to be attached...\n"
                );
                wait(10000);
                console.log(
                    "Success. Policy attached to user " + user_name + "."
                );
                try {
                  console.log(
                      "\nAssume for the user the role with permission to list all buckets....\n"
                  );
                  const assume_role_params = {
                    RoleArn: role_arn, //ARN_OF_ROLE_TO_ASSUME
                    RoleSessionName: "session1",
                    DurationSeconds: 900,
                  };
                  // Create an AWS STS client with the credentials for the user. Remember, the user has permissions to assume roles using AWS STS.
                  const stsClientWithUsersCreds = new STSClient({
                    credentials: user_creds,
                    region: REGION,
                  });

                  const data = await stsClientWithUsersCreds.send(
                      new AssumeRoleCommand(assume_role_params)
                  );
                  console.log(
                      "Success assuming role. Access key id is " +
                      data.Credentials.AccessKeyId +
                      "\n" +
                      "Secret access key is " +
                      data.Credentials.SecretAccessKey
                  );

                  const newAccessKey = data.Credentials.AccessKeyId;
                  const newSecretAccessKey = data.Credentials.SecretAccessKey;

                  console.log(
                      "\nWaiting 10 seconds for the user to assume the role with permission to list all buckets...\n"
                  );
                  wait(10000);
                  // Set the parameters for the temporary credentials. This grants permission to list S3 buckets.
                  var new_role_creds = {
                    accessKeyId: newAccessKey,
                    secretAccessKey: newSecretAccessKey,
                    sessionToken: data.Credentials.SessionToken,
                  };
                  try {
                    console.log(
                        "Listing the S3 buckets using the credentials of the assumed role... \n"
                    );
                    // Create an S3 client with the temporary credentials.
                    const s3ClientWithNewCreds = new S3Client({
                      credentials: new_role_creds,
                      region: REGION,
                    });
                    const data = await s3ClientWithNewCreds.send(
                        new ListBucketsCommand({})
                    );
                    console.log("Success. Your S3 buckets are:", data.Buckets);
                    try {
                      console.log(
                          "Detaching s3 policy from user " + userName + " ... \n"
                      );
                      const data = await iamClient.send(
                          new DetachUserPolicyCommand({
                            PolicyArn: assume_policy_arn,
                            UserName: userName,
                          })
                      );
                      console.log("Success, S3 policy detached from user.");
                      try {
                        console.log(
                            "Detaching role policy from " + role_name + " ... \n"
                        );
                        const data = await iamClient.send(
                            new DetachRolePolicyCommand({
                              PolicyArn: s3_policy_arn,
                              RoleName: role_name,
                            })
                        );
                        console.log(
                            "Success, assume policy detached from role."
                        );
                        try {
                          console.log("Deleting s3 policy ... \n");
                          const data = await iamClient.send(
                              new DeletePolicyCommand({
                                PolicyArn: s3_policy_arn,
                              })
                          );
                          console.log("Success, S3 policy deleted.");
                          try {
                            console.log("Deleting assume role policy ... \n");
                            const data = await iamClient.send(
                                new DeletePolicyCommand({
                                  PolicyArn: assume_policy_arn,
                                })
                            );
                            try {
                              console.log("Deleting access keys ... \n");
                              const data = await iamClient.send(
                                  new DeleteAccessKeyCommand({
                                    UserName: userName,
                                    AccessKeyId: myAccessKey,
                                  })
                              );
                              try {
                                console.log(
                                    "Deleting user " + user_name + " ... \n"
                                );
                                const data = await iamClient.send(
                                    new DeleteUserCommand({ UserName: userName })
                                );
                                console.log("Success, user deleted.");
                                try {
                                  console.log(
                                      "Deleting role " + role_name + " ... \n"
                                  );
                                  const data = await iamClient.send(
                                      new DeleteRoleCommand({
                                        RoleName: role_name,
                                      })
                                  );
                                  console.log("Success, role deleted.");
                                  return "Run successfully"; // For unit tests.
                                } catch (err) {
                                  console.log("Error deleting  role .", err);
                                }
                              } catch (err) {
                                console.log("Error deleting user.", err);
                              }
                            } catch (err) {
                              console.log("Error deleting access keys.", err);
                            }
                          } catch (err) {
                            console.log(
                                "Error detaching assume role policy from user.",
                                err
                            );
                          }
                        } catch (err) {
                          console.log("Error deleting role.", err);
                        }
                      } catch (err) {
                        console.log("Error deleting user.", err);
                      }
                    } catch (err) {
                      console.log("Error detaching S3 policy from role.", err);
                      process.exit(1);
                    }
                  } catch (err) {
                    console.log("Error listing S3 buckets.", err);
                    process.exit(1);
                  }
                } catch (err) {
                  console.log("Error assuming role.", err);
                  process.exit(1);
                }
              } catch (err) {
                console.log(
                    "Error adding permissions to user to assume role.",
                    err
                );
                process.exit(1);
              }
            } catch (err) {
              console.log("Error assuming role.", err);
              process.exit(1);
            }
          } catch (err) {
            console.log("Error creating policy. ", err);
            process.exit(1);
          }
        } catch (err) {
          console.log("Error attaching policy to role.", err);
          process.exit(1);
        }
      }
    } catch (err) {
      console.log("Error creating access keys. ", err);
      process.exit(1);
    }
  } catch (err) {
    console.log("Error creating user. ", err);
  }
};
run(userName, s3_policy_name, role_name, assume_policy_name);
// snippet-end:[javascript.iam_scenarios.iam_basics]
