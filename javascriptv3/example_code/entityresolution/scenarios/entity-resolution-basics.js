// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
Before running this JavaScript code example, set up your development environment, including your credentials.
This demo illustrates how to use the AWS SDK for JavaScript (v3) to work with AWS Entity Resolution.

The default inputs for this demo are read from the ../inputs.json.

For more information, see the following documentation topic:

https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/getting-started.html
*/
// snippet-start:[entity-resolution.JavaScriptv3.scenario.basics]

import {
  Scenario,
  ScenarioAction,
  ScenarioInput,
  ScenarioOutput,
} from "@aws-doc-sdk-examples/lib/scenario/index.js";
import {
  CloudFormationClient,
  CreateStackCommand,
  DeleteStackCommand,
  DescribeStacksCommand,
  waitUntilStackExists,
  waitUntilStackCreateComplete,
} from "@aws-sdk/client-cloudformation";
import {
  EntityResolutionClient,
  CreateSchemaMappingCommand,
  CreateMatchingWorkflowCommand,
  GetMatchingJobCommand,
  StartMatchingJobCommand,
  GetSchemaMappingCommand,
  ListSchemaMappingsCommand,
  TagResourceCommand,
  DeleteMatchingWorkflowCommand,
  DeleteSchemaMappingCommand,
  ConflictException,
  ValidationException,
} from "@aws-sdk/client-entityresolution";
import {
  DeleteObjectsCommand,
  DeleteBucketCommand,
  PutObjectCommand,
  S3Client,
  ListObjectsCommand,
} from "@aws-sdk/client-s3";
import { wait } from "@aws-doc-sdk-examples/lib/utils/util-timers.js";

import { readFile } from "node:fs/promises";
import { parseArgs } from "node:util";
import { readFileSync } from "node:fs";
import { fileURLToPath } from "node:url";
import { dirname } from "node:path";

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);
const stackName = `${data.inputs.entityResolutionStack}`;

/*The inputs for this example can be edited in the ../input.json.*/
import data from "../inputs.json" with { type: "json" };
const skipWhenErrors = (state) => state.errors.length > 0;
/**
 * Used repeatedly to have the user press enter.
 * @type {ScenarioInput}
 */
/* v8 ignore next 3 */
const pressEnter = new ScenarioInput("continue", "Press Enter to continue", {
  type: "input",
  verbose: "false",
  skipWhen: skipWhenErrors,
});

const region = "eu-west-1";

const entityResolutionClient = new EntityResolutionClient({ region: region });
const cloudFormationClient = new CloudFormationClient({ region: region });
const s3Client = new S3Client({ region: region });

const greet = new ScenarioOutput(
  "greet",
  "AWS Entity Resolution is a fully-managed machine learning service provided by " +
    "Amazon Web Services (AWS) that helps organizations extract, link, and " +
    "organize information from multiple data sources. It leverages natural " +
    "language processing and deep learning models to identify and resolve " +
    "entities, such as people, places, organizations, and products, " +
    "across structured and unstructured data.\n" +
    "\n" +
    "With Entity Resolution, customers can build robust data integration " +
    "pipelines to combine and reconcile data from multiple systems, databases, " +
    "and documents. The service can handle ambiguous, incomplete, or conflicting " +
    "information, and provide a unified view of entities and their relationships. " +
    "This can be particularly valuable in applications such as customer 360, " +
    "fraud detection, supply chain management, and knowledge management, where " +
    "accurate entity identification is crucial.\n" +
    "\n" +
    "The `EntityResolutionAsyncClient` interface in the AWS SDK for Java 2.x " +
    "provides a set of methods to programmatically interact with the AWS Entity " +
    "Resolution service. This allows developers to automate the entity extraction, " +
    "linking, and deduplication process as part of their data processing workflows. " +
    "With Entity Resolution, organizations can unlock the value of their data, " +
    "improve decision-making, and enhance customer experiences by having a reliable, " +
    "comprehensive view of their key entities.",

  { header: true },
);
const displayBuildCloudFormationStack = new ScenarioOutput(
  "displayBuildCloudFormationStack",
  "To prepare the AWS resources needed for this scenario application, the next step uploads " +
    "a CloudFormation template whose resulting stack creates the following resources:\n" +
    "- An AWS Glue Data Catalog table \n" +
    "- An AWS IAM role \n" +
    "- An AWS S3 bucket \n" +
    "- An AWS Entity Resolution Schema \n" +
    "It can take a couple minutes for the Stack to finish creating the resources.",
);

const sdkBuildCloudFormationStack = new ScenarioAction(
  "sdkBuildCloudFormationStack",
  async (/** @type {State} */ state) => {
    try {
      const data = readFileSync(
        `${__dirname}/../../../../resources/cfn/entity-resolution-basics/entity-resolution-basics-template.yml`,
        "utf8",
      );
      await cloudFormationClient.send(
        new CreateStackCommand({
          StackName: stackName,
          TemplateBody: data,
          Capabilities: ["CAPABILITY_IAM"],
        }),
      );
      await waitUntilStackExists(
        { client: cloudFormationClient },
        { StackName: stackName },
      );
      await waitUntilStackCreateComplete(
        { client: cloudFormationClient },
        { StackName: stackName },
      );
      const stack = await cloudFormationClient.send(
        new DescribeStacksCommand({
          StackName: stackName,
        }),
      );

      state.entityResolutionRole = stack.Stacks[0].Outputs[1];
      state.jsonGlueTable = stack.Stacks[0].Outputs[2];
      state.CSVGlueTable = stack.Stacks[0].Outputs[3];
      state.glueDataBucket = stack.Stacks[0].Outputs[0];
      state.stackName = stack.StackName;
      console.log(state.glueDataBucket);
      console.log(
        `The  ARN of the EntityResolution Role is ${state.entityResolutionRole.OutputValue}`,
      );
      console.log(
        `The ARN of the Json Glue Table is ${state.jsonGlueTable.OutputValue}`,
      );
      console.log(
        `The ARN of the CSV Glue Table is ${state.CSVGlueTable.OutputValue}`,
      );
      console.log(
        `The name of the Glue Data Bucket is ${state.glueDataBucket.OutputValue}\n`,
      );
    } catch (caught) {
      console.error(caught.message);
      throw caught;
    }
    try {
      console.log(
        `Uploading the following JSON in ../data.json to the ${state.glueDataBucket.OutputValue} S3 bucket...`,
      );
      const bucketName = state.glueDataBucket.OutputValue;

      const putObjectParams = {
        Bucket: bucketName,
        Key: "jsonData/data.json",
        Body: await readFileSync(
          `${__dirname}/../../../../javascriptv3/example_code/entityresolution/data.json`,
        ),
      };
      const command = new PutObjectCommand(putObjectParams);
      const response = await s3Client.send(command);
      console.log(
        `../data.json file data uploaded to the ${state.glueDataBucket.OutputValue} S3 bucket.\n`,
      );
    } catch (caught) {
      console.error(caught.message);
      throw caught;
    }
    try {
      console.log(
        `Uploading the CSV data in ../data.csv to the ${state.glueDataBucket.OutputValue} S3 bucket...`,
      );

      const bucketName = state.glueDataBucket.OutputValue;
      const putObjectParams = {
        Bucket: bucketName,
        Key: "csvData/data.csv",
        Body: await readFileSync(
          `${__dirname}/../../../../javascriptv3/example_code/entityresolution/data.csv`,
        ),
      };
      const command = new PutObjectCommand(putObjectParams);
      const response = await s3Client.send(command);
      console.log(
        `../data.csv file data uploaded to the ${state.glueDataBucket.OutputValue} S3 bucket.`,
      );
    } catch (caught) {
      console.error(caught.message);
      throw caught;
    }
  },
);

const displayCreateSchemaMapping = new ScenarioOutput(
  "displayCreateSchemaMapping",
  "1. Create Schema Mapping" +
    "Entity Resolution schema mapping aligns and integrates data from " +
    "multiple sources by identifying and matching corresponding entities " +
    "like customers or products. It unifies schemas, resolves conflicts, " +
    "and uses machine learning to link related entities, enabling a " +
    "consolidated, accurate view for improved data quality and decision-making." +
    "\n" +
    "In this example, the schema mapping lines up with the fields in the JSON and CSV objects. That is, " +
    " it contains these fields: id, name, and email. ",
);

const sdkCreateSchemaMapping = new ScenarioAction(
  "sdkCreateSchemaMapping",
  async (/** @type {State} */ state) => {
    const createSchemaMappingParamsJson = {
      schemaName: `${data.inputs.schemaNameJson}`,
      mappedInputFields: [
        {
          fieldName: "id",
          type: "UNIQUE_ID",
        },
        {
          fieldName: "name",
          type: "NAME",
        },
        {
          fieldName: "email",
          type: "EMAIL_ADDRESS",
        },
      ],
    };
    const createSchemaMappingParamsCSV = {
      schemaName: `${data.inputs.schemaNameCSV}`,
      mappedInputFields: [
        {
          fieldName: "id",
          type: "UNIQUE_ID",
        },
        {
          fieldName: "name",
          type: "NAME",
        },
        {
          fieldName: "email",
          type: "EMAIL_ADDRESS",
        },
        {
          fieldName: "phone",
          type: "PROVIDER_ID",
          subType: "STRING",
        },
      ],
    };
    try {
      const command = new CreateSchemaMappingCommand(
        createSchemaMappingParamsJson,
      );
      const response = await entityResolutionClient.send(command);
      state.schemaNameJson = response.schemaName;
      state.schemaArn = response.schemaArn;
      state.idOutputAttribute = response.mappedInputFields[0].fieldName;
      state.nameOutputAttribute = response.mappedInputFields[1].fieldName;
      state.emailOutputAttribute = response.mappedInputFields[2].fieldName;

      console.log("The JSON schema mapping name is ", state.schemaNameJson);
    } catch (caught) {
      if (caught instanceof ConflictException) {
        console.error(
          `The schema mapping already exists: ${caught.message} \n Exiting program.`,
        );
        return;
      }
    }
    try {
      const command = new CreateSchemaMappingCommand(
        createSchemaMappingParamsCSV,
      );
      const response = await entityResolutionClient.send(command);
      state.schemaNameCSV = response.schemaName;
      state.phoneOutputAttribute = response.mappedInputFields[3].fieldName;
      console.log("The CSV schema mapping name is ", state.schemaNameCSV);
    } catch (caught) {
      if (caught instanceof ConflictException) {
        console.error(
          `An unexpected error occurred while creating the geofence collection: ${caught.message} \n Exiting program.`,
        );
        return;
      }
    }
  },
);
const displayCreateMatchingWorkflow = new ScenarioOutput(
  "displayCreateMatchingWorkflow",
  "2. Create an AWS Entity Resolution Workflow. " +
    "An Entity Resolution matching workflow identifies and links records " +
    "across datasets that represent the same real-world entity, such as " +
    "customers or products. Using techniques like schema mapping, " +
    "data profiling, and machine learning algorithms, " +
    "it evaluates attributes like names or emails to detect duplicates " +
    "or relationships, even with variations or inconsistencies. " +
    "The workflow outputs consolidated, de-duplicated data." +
    "\n" +
    "We will use the machine learning-based matching technique.",
);

const sdkCreateMatchingWorkflow = new ScenarioAction(
  "sdkCreateMatchingWorkflow",
  async (/** @type {State} */ state) => {
    const createMatchingWorkflowParams = {
      roleArn: `${state.entityResolutionRole.OutputValue}`,
      workflowName: `${data.inputs.workflowName}`,
      description: "Created by using the AWS SDK for JavaScript (v3).",
      inputSourceConfig: [
        {
          inputSourceARN: `${state.jsonGlueTable.OutputValue}`,
          schemaName: `${data.inputs.schemaNameJson}`,
          applyNormalization: false,
        },
        {
          inputSourceARN: `${state.CSVGlueTable.OutputValue}`,
          schemaName: `${data.inputs.schemaNameCSV}`,
          applyNormalization: false,
        },
      ],
      outputSourceConfig: [
        {
          outputS3Path: `s3://${state.glueDataBucket.OutputValue}/eroutput`,
          output: [
            {
              name: state.idOutputAttribute,
            },
            {
              name: state.nameOutputAttribute,
            },
            {
              name: state.emailOutputAttribute,
            },
            {
              name: state.phoneOutputAttribute,
            },
          ],
          applyNormalization: false,
        },
      ],
      resolutionTechniques: { resolutionType: "ML_MATCHING" },
    };
    try {
      const command = new CreateMatchingWorkflowCommand(
        createMatchingWorkflowParams,
      );
      const response = await entityResolutionClient.send(command);
      state.workflowArn = response.workflowArn;
      console.log(
        `Workflow created successfully.\n The workflow ARN is: ${response.workflowArn}`,
      );
    } catch (caught) {
      if (caught instanceof ConflictException) {
        console.error(
          `The matching workflow already exists: ${caught.message} \n Exiting program.`,
        );
        return;
      }
      if (caught instanceof ValidationException) {
        console.error(
          `There was a validation exception: ${caught.message} \n Exiting program.`,
        );
        return;
      }
    }
  },
);
const displayMatchingJobOfWorkflow = new ScenarioOutput(
  "displayMatchingJobOfWorkflow",
  "3. Start the matching job of the workflow",
);

const sdkMatchingJobOfWorkflow = new ScenarioAction(
  "sdk",
  async (/** @type {State} */ state) => {
    const matchingJobOfWorkflowParams = {
      workflowName: `${data.inputs.workflowName}`,
    };
    try {
      const command = new StartMatchingJobCommand(matchingJobOfWorkflowParams);
      const response = await entityResolutionClient.send(command);
      state.jobID = response.jobId;
      console.log(`Job ID: ${state.jobID} \n
The matching job was successfully started.`);
    } catch (caught) {
      if (caught instanceof ConflictException) {
        console.error(
          `The matching workflow already exists: ${caught.message} \n Exiting program.`,
        );
        return;
      }
    }
  },
);

const displayGetDetailsforJob = new ScenarioOutput(
  "displayGetDetailsforJob",
  `4. While the matching job is running, let's look at other API methods. First, let's get details for the job `,
);

const sdkGetDetailsforJob = new ScenarioAction(
  "sdkGetDetailsforJob",
  async (/** @type {State} */ state) => {
    const getDetailsforJobParams = {
      workflowName: `${data.inputs.workflowName}`,
      jobId: `${state.jobID}`,
    };
    try {
      const command = new GetMatchingJobCommand(getDetailsforJobParams);
      const response = await entityResolutionClient.send(command);
      state.Status = response.status;
      state.response = response;
      console.log(`Job status: ${state.Status} `);
    } catch (caught) {
      console.error(caught.message);
      throw caught;
    }
  },
);

const displayGetSchemaMappingJson = new ScenarioOutput(
  "displayGetSchemaMappingJson",
  "5. Get the schema mapping for the JSON data.",
);

const sdkGetSchemaMappingJson = new ScenarioAction(
  "sdkGetSchemaMappingJson",
  async (/** @type {State} */ state) => {
    const getSchemaMappingJsonParams = {
      schemaName: `${data.inputs.schemaNameJson}`,
    };
    try {
      const command = new GetSchemaMappingCommand(getSchemaMappingJsonParams);
      const response = await entityResolutionClient.send(command);
      console.log("Schema·mapping·ARN·is:·", response.schemaArn);
      const resultMappings = response.mappedInputFields;
      const noOfResultMappings = resultMappings.length;
      for (let i = 0; i < noOfResultMappings; i++) {
        console.log(
          `Attribute name: ${resultMappings[i].fieldName} `,
          `Attribute type: ${resultMappings[i].type}`,
        );
      }
    } catch (caught) {
      console.error(caught.message);
      throw caught;
    }
  },
);

const displayListSchemaMappings = new ScenarioOutput(
  "displayListSchemaMappings",
  "6. List Schema Mappings.",
);

const sdkListSchemaMappings = new ScenarioAction(
  "sdkListSchemaMappings",
  async (/** @type {State} */ state) => {
    try {
      const command = new ListSchemaMappingsCommand({});
      const response = await entityResolutionClient.send(command);
      const noOfSchemas = response.schemaList.length;
      for (let i = 0; i < noOfSchemas; i++) {
        console.log(
          `Schema Mapping Name: ${response.schemaList[i].schemaName} `,
        );
      }
    } catch (caught) {
      console.error(caught.message);
      throw caught;
    }
  },
);

const displayTagTheJsonSchema = new ScenarioOutput(
  "display",
  "7. Tag the resource. \n" +
    "Tags can help you organize and categorize your Entity Resolution resources. " +
    "You can also use them to scope user permissions by granting a user permission " +
    "to access or change only resources with certain tag values. " +
    "In Entity Resolution, SchemaMapping and MatchingWorkflow can be tagged. For this example, " +
    "the SchemaMapping is tagged.",
);

const sdkTagTheJsonSchema = new ScenarioAction(
  "sdkGetSchemaMappingJson",
  async (/** @type {State} */ state) => {
    const tagResourceCommandParams = {
      resourceArn: state.schemaArn,
      tags: {
        tag1: "tag1Value",
        tag2: "tag2Value",
      },
    };
    try {
      const command = new TagResourceCommand(tagResourceCommandParams);
      const response = await entityResolutionClient.send(command);
      console.log("Successfully tagged the resource.");
    } catch (caught) {
      console.error(caught.message);
      throw caught;
    }
  },
);

const displayGetJobInfo = new ScenarioOutput(
  "displayGetJobInfo",
  "8. View the results of the AWS Entity Resolution Workflow.\n " +
    "Please perform this task manually in the AWS Management Console. ",
);

const displayDeleteResources = new ScenarioOutput(
  "displayDeleteResources",
  "9. Delete the resources \n" +
    "You cannot delete a workflow that is in a running state. So this will take ~30 minutes.\n" +
    "If you don't want to delete the resources, simply exit this application.",
);

const sdkDeleteResources = new ScenarioAction(
  "sdkDeleteResources",
  async (/** @type {State} */ state) => {
    console.log(
      "You selected to delete the resources. This will take about 30 minutes.",
    );
    await wait(1800);
    const bucketName = state.glueDataBucket.OutputValue;
    try {
      const emptyBucket = async ({ bucketName }) => {
        const listObjectsCommand = new ListObjectsCommand({
          Bucket: bucketName,
        });
        const { Contents } = await s3Client.send(listObjectsCommand);
        const keys = Contents.map((c) => c.Key);

        const deleteObjectsCommand = new DeleteObjectsCommand({
          Bucket: bucketName,
          Delete: { Objects: keys.map((key) => ({ Key: key })) },
        });
        await s3Client.send(deleteObjectsCommand);
        console.log(`Bucket ${bucketName} emptied successfully.\n`);
      };
      await emptyBucket({ bucketName });
    } catch (error) {
      console.log("error ", error);
    }
    try {
      const deleteBucket = async ({ bucketName }) => {
        const command = new DeleteBucketCommand({ Bucket: bucketName });
        await s3Client.send(command);
        console.log(`Bucket ${bucketName} deleted successfully.\n`);
      };
      await deleteBucket({ bucketName });
    } catch (error) {
      console.log("error ", error);
    }
    try {
      console.log(
        "Now we will delete the CloudFormation stack, which deletes the resources that were created at the beginning of the scenario.",
      );
      const deleteStackParams = { StackName: `${state.stackName}` };
      const command = new DeleteStackCommand(deleteStackParams);
      const response = await cloudFormationClient.send(command);
      console.log("CloudFormation stack deleted successfully.");
    } catch (error) {
      console.log("error ", error);
    }
    try {
      const deleteWorkflowParams = {
        workflowName: `${data.inputs.workflowName}`,
      };
      const command = new DeleteMatchingWorkflowCommand(deleteWorkflowParams);
      const response = await entityResolutionClient.send(command);
      console.log("Workflow deleted successfully!");
    } catch (caught) {
      if (caught instanceof ConflictException) {
        console.error(
          `Job associated with workflow ${data.inputs.workflowName} is still running, so can't be deleted. 
          Neither can schemas ${data.inputs.schemaNameJson} and ${data.inputs.schemaNameCSV} associated with it. Please confirm this workflow is finished in the AWS Management Console, then delete it manually.`,
        );
        throw caught;
      }
    }
    try {
      const deleteJSONschemaMapping = {
        schemaName: `${data.inputs.schemaNameJson}`,
      };
      const command = new DeleteSchemaMappingCommand(deleteJSONschemaMapping);
      const response = await entityResolutionClient.send(command);
      console.log("Schema mapping deleted successfully. ");
    } catch (caught) {
      if (caught instanceof ConflictException) {
        console.error(
          `The schema ${data.inputs.schemaNameJson} can't be deleted because it is associated with workflow
           ${data.inputs.workflowName}, which is still running. Please confirm this workflow is finished in the AWS Management Console, then delete it manually.`,
        );
        throw caught;
      }
    }
    try {
      const deleteCSVschemaMapping = {
        schemaName: `${data.inputs.schemaNameCSV}`,
      };
      const command = new DeleteSchemaMappingCommand(deleteCSVschemaMapping);
      const response = await entityResolutionClient.send(command);
      console.log("Schema mapping deleted successfully.");
    } catch (caught) {
      if (caught instanceof ConflictException) {
        console.error(
          `The schema ${data.inputs.schemaNameCSV} can't be deleted because it is associated with workflow ${data.inputs.workflowName}, which is still running. Please confirm this workflow is finished in the AWS Management Console, then delete it manually.`,
        );
        throw caught;
      }
    }
  },
  {
    skipWhen: (/** @type {State} */ state) =>
      state.confirmDeleteResources === "",
  },
);

const goodbye = new ScenarioOutput(
  "goodbye",
  "Thank you for checking out the Amazon Location Service Use demo. We hope you " +
    "learned something new, or got some inspiration for your own apps today!" +
    " For more Amazon Location Services examples in different programming languages, have a look at: " +
    "https://docs.aws.amazon.com/code-library/latest/ug/location_code_examples.html",
);

const myScenario = new Scenario("Entity Resolution Basics Scenario", [
  greet,
  pressEnter,
  displayBuildCloudFormationStack,
  sdkBuildCloudFormationStack,
  pressEnter,
  displayCreateSchemaMapping,
  sdkCreateSchemaMapping,
  pressEnter,
  displayCreateMatchingWorkflow,
  sdkCreateMatchingWorkflow,
  pressEnter,
  displayMatchingJobOfWorkflow,
  sdkMatchingJobOfWorkflow,
  pressEnter,
  displayGetDetailsforJob,
  sdkGetDetailsforJob,
  pressEnter,
  displayGetSchemaMappingJson,
  sdkGetSchemaMappingJson,
  pressEnter,
  displayListSchemaMappings,
  sdkListSchemaMappings,
  pressEnter,
  displayTagTheJsonSchema,
  sdkTagTheJsonSchema,
  pressEnter,
  displayGetJobInfo,
  pressEnter,
  displayDeleteResources,
  pressEnter,
  sdkDeleteResources,
  pressEnter,
  goodbye,
]);

/** @type {{ stepHandlerOptions: StepHandlerOptions }} */
export const main = async (stepHandlerOptions) => {
  await myScenario.run(stepHandlerOptions);
};

// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  const { values } = parseArgs({
    options: {
      yes: {
        type: "boolean",
        short: "y",
      },
    },
  });
  main({ confirmAll: values.yes });
}
// snippet-end:[entity-resolution.JavaScriptv3.scenario.basics]
