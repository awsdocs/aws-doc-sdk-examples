import { describe, it, expect, beforeAll, afterAll } from "vitest";
import { S3Client, CreateBucketCommand } from "@aws-sdk/client-s3";
import { DeleteBucketCommand } from "@aws-sdk/client-s3";
import {
  AttachRolePolicyCommand,
  CreateRoleCommand,
  IAMClient,
  DeleteRoleCommand,
  DetachRolePolicyCommand,
} from "@aws-sdk/client-iam";
import { createProject } from "../actions/create-project.js";
import {
  CodeBuildClient,
  DeleteProjectCommand,
  paginateListProjects,
} from "@aws-sdk/client-codebuild";
import { retry } from "@aws-sdk-examples/libs/utils/util-timers.js";

describe(
  "create-project",
  () => {
    const suffix = Math.floor(Math.random() * 1000);
    const roleName = `CodeBuildAdmin-${suffix}`;
    let roleArn;
    const bucketName = `codebuild-bucket-${suffix}`;
    const iamClient = new IAMClient({});
    const s3Client = new S3Client({});
    const codebuildClient = new CodeBuildClient({});
    const projectName = `codebuild-project-${suffix}`;

    beforeAll(async () => {
      roleArn = await createIamRole(roleName);
      await createS3Bucket(bucketName);
    });

    afterAll(async () => {
      await iamClient.send(
        new DetachRolePolicyCommand({
          RoleName: roleName,
          PolicyArn: "arn:aws:iam::aws:policy/CloudWatchLogsFullAccess",
        }),
      );

      await iamClient.send(
        new DetachRolePolicyCommand({
          RoleName: roleName,
          PolicyArn: "arn:aws:iam::aws:policy/AWSCodeBuildAdminAccess",
        }),
      );

      await iamClient.send(
        new DeleteRoleCommand({
          RoleName: roleName,
        }),
      );

      await s3Client.send(
        new DeleteBucketCommand({
          Bucket: bucketName,
        }),
      );

      await codebuildClient.send(
        new DeleteProjectCommand({
          name: projectName,
        }),
      );
    });

    it("should create a CodeBuild project", async () => {
      await retry({ intervalInMs: 1000, maxRetries: 20 }, async () => {
        await createProject(
          projectName,
          roleArn,
          bucketName,
          "https://github.com/awsdocs/aws-doc-sdk-examples.git",
        );
      });

      const listProjectsPaginator = paginateListProjects(
        {
          client: codebuildClient,
        },
        {},
      );

      const projectNames = [];

      for await (const page of listProjectsPaginator) {
        projectNames.push(...page.projects);
      }

      expect(projectNames).toContain(projectName);
    });
  },
  { timeout: 30000 },
);

async function createIamRole(name) {
  const iamClient = new IAMClient({});

  const { Role } = await iamClient.send(
    new CreateRoleCommand({
      RoleName: name,
      AssumeRolePolicyDocument: JSON.stringify({
        Version: "2012-10-17",
        Statement: [
          {
            Effect: "Allow",
            Principal: {
              Service: "codebuild.amazonaws.com",
            },
            Action: "sts:AssumeRole",
          },
        ],
      }),
    }),
  );

  await iamClient.send(
    new AttachRolePolicyCommand({
      RoleName: name,
      PolicyArn: "arn:aws:iam::aws:policy/CloudWatchLogsFullAccess",
    }),
  );

  await iamClient.send(
    new AttachRolePolicyCommand({
      RoleName: name,
      PolicyArn: "arn:aws:iam::aws:policy/AWSCodeBuildAdminAccess",
    }),
  );

  return Role.Arn;
}

async function createS3Bucket(name) {
  const client = new S3Client({});

  await client.send(
    new CreateBucketCommand({
      Bucket: name,
    }),
  );
}
