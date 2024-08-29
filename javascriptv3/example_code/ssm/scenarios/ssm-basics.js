// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[ssm.JavaScript.Basics.scenario]
import {
  Scenario,
  ScenarioAction,
  ScenarioInput,
  ScenarioOutput,
} from "@aws-doc-sdk-examples/lib/scenario/index.js";
import { fileURLToPath } from "url";
import {
  CreateDocumentCommand,
  CreateMaintenanceWindowCommand,
  CreateOpsItemCommand,
  DeleteDocumentCommand,
  DeleteMaintenanceWindowCommand,
  DeleteOpsItemCommand,
  DescribeOpsItemsCommand,
  DocumentAlreadyExists,
  OpsItemStatus,
  paginateListCommandInvocations,
  SendCommandCommand,
  SSMClient,
  UpdateMaintenanceWindowCommand,
  UpdateOpsItemCommand,
} from "@aws-sdk/client-ssm";

const client = new SSMClient({});
const defaultMaintenanceWindow = "ssm-maintenance-window";
const defaultDocumentName = "ssmdocument";

const pressEnter = new ScenarioInput("continue", "Press Enter to continue", {
  type: "confirm",
});

const greet = new ScenarioOutput(
  "greet",
  `Welcome to the AWS Systems Manager SDK Getting Started scenario.
    This program demonstrates how to interact with Systems Manager using the AWS SDK for Java (v2).
    Systems Manager is the operations hub for your AWS applications and resources and a secure end-to-end management solution.
    The program's primary functions include creating a maintenance window, creating a document, sending a command to a document,
    listing documents, listing commands, creating an OpsItem, modifying an OpsItem, and deleting Systems Manager resources.
    Upon completion of the program, all AWS resources are cleaned up.
    Let's get started...`,
  { header: true }
);

const createMaintenanceWindow = new ScenarioOutput(
  "createMaintenanceWindow",
  `Step 1: Create a Systems Manager maintenance window.`
);

const getMaintenanceWindow = new ScenarioInput(
  "maintenanceWindow",
  "Please enter the maintenance window name:",
  { type: "input", default: defaultMaintenanceWindow }
);

export const sdkCreateMaintenanceWindow = new ScenarioAction(
  "sdkCreateMaintenanceWindow",
  /**
   * @param {{ maintenanceWindow: string }} c
   */
  async (c) => {
    const response = await client.send(
      new CreateMaintenanceWindowCommand({
        Name: c.maintenanceWindow,
        Schedule: "cron(0 10 ? * MON-FRI *)",
        Duration: 2,
        Cutoff: 1,
        AllowUnassociatedTargets: true,
      })
    );
    c.winId = response.WindowId;
  }
);

const modifyMaintenanceWindow = new ScenarioOutput(
  "modifyMaintenanceWindow",
  `Modify the maintenance window by changing the schedule.`
);

const sdkModifyMaintenanceWindow = new ScenarioAction(
  "sdkModifyMaintenanceWindow",
  /**
   * @param {{ winId: int }} c
   */
  async (c) => {
    const _response = await client.send(
      new UpdateMaintenanceWindowCommand({
        WindowId: c.winId,
        Schedule: "cron(0 0 ? * MON *)",
      })
    );
  }
);

const createSystemsManagerActions = new ScenarioOutput(
  "createSystemsManagerActions",
  `Create a document that defines the actions that Systems Manager performs on your EC2 instance.`
);

const getDocumentName = new ScenarioInput(
  "documentName",
  "Please enter the document name (default is ssmdocument):",
  { type: "input", default: defaultDocumentName }
);

const sdkCreateSSMDoc = new ScenarioAction(
  "sdkCreateSSMDoc",
  /**
   * @param {{ documentName: string }} c
   */
  async (c) => {
    const contentData = `{
                "schemaVersion": "2.2",
                "description": "Run a simple shell command",
                "mainSteps": [
                    {
                        "action": "aws:runShellScript",
                        "name": "runEchoCommand",
                        "inputs": {
                          "runCommand": [
                            "echo 'Hello, world!'"
                          ]
                        }
                    }
                ]
            }`;
    try {
      const _response = await client.send(
        new CreateDocumentCommand({
          Content: contentData,
          Name: c.documentName,
          DocumentType: "Command",
        })
      );
    } catch (e) {
      console.log("Exception type: (" + typeof e + ")");
      if (e instanceof DocumentAlreadyExists)
        console.log("Document already exists. Continuing...\n");
      else throw e;
    }
  }
);

const ec2HelloWorld = new ScenarioOutput(
  "ec2HelloWorld",
  `Now you have the option of running a command on an EC2 instance that echoes 'Hello, world!'. In order to run this command, you must provide the instance ID of a Linux EC2 instance. If you do not already have a running Linux EC2 instance in your account, you can create one using the AWS console. For information about creating an EC2 instance, see https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ec2-launch-instance-wizard.html.`
);

const confirmEC2HelloWorld = new ScenarioInput(
  "confirmEC2HelloWorld",
  "Enter your EC2 InstanceId or press enter to skip this step: ",
  { type: "input", default: "" }
);

const sdkEC2HelloWorld = new ScenarioAction(
  "sdkEC2HelloWorld",
  /**
   * @param {{ documentName: string, confirmEC2HelloWorld: string }} c
   */
  async (c) => {
    const response = await client.send(
      new SendCommandCommand({
        DocumentName: c.documentName,
        InstanceIds: [c.confirmEC2HelloWorld],
      })
    );
    c.CommandId = response.Command.CommandId;
  },
  {
    skipWhen: /** @param {{ confirmEC2HelloWorld: string }} c */ (c) =>
      c.confirmEC2HelloWorld === "",
  }
);

const getCommandTime = new ScenarioOutput(
  "getCommandTime",
  "Lets get the time when the specific command was sent to the specific managed node."
);

const sdkGetCommandTime = new ScenarioAction(
  "sdkGetCommandTime",
  /**
   * @param {{ requestedDateTime: Date }} c
   */
  async (c) => {
    let listInvocationsPaginated = [];
    for await (const page of paginateListCommandInvocations(
      { client },
      { CommandId: c.CommandId }
    )) {
      listInvocationsPaginated.push(...page.CommandInvocations);
    }
    /**
     * @type {import('@aws-sdk/client-ssm').CommandInvocation}
     */
    const commandInvocation = listInvocationsPaginated.shift();
    c.requestedDateTime = commandInvocation.RequestedDateTime;
  }
);

const showCommandTime = new ScenarioOutput(
  "showCommandTime",
  /**
   * @param {{ requestedDateTime: Date }} c
   */
  (c) => "The command invocation happened at: " + c.requestedDateTime
);

const createSSMOpsItem = new ScenarioOutput(
  "createSSMOpsItem",
  `Now we will create a Systems Manager OpsItem. 
    An OpsItem is a feature provided by the Systems Manager service. 
    It is a type of operational data item that allows you to manage and track various operational issues, 
    events, or tasks within your AWS environment.
    
    You can create OpsItems to track and manage operational issues as they arise. 
    For example, you could create an OpsItem whenever your application detects a critical error 
    or an anomaly in your infrastructure.`
);

const sdkCreateSSMOpsItem = new ScenarioAction(
  "sdkCreateSSMOpsItem",
  /**
   *
   * @param {{ opsItemId: string }} c
   */
  async (c) => {
    const response = await client.send(
      new CreateOpsItemCommand({
        Description: "Created by the System Manager Javascript API",
        Title: "Disk Space Alert",
        Source: "EC2",
        Category: "Performance",
        Severity: "2",
      })
    );
    c.opsItemId = response.OpsItemId;
  }
);

const updateOpsItem = new ScenarioOutput(
  "updateOpsItem",
  /**
   * @param {{ opsItemId: string }} c
   */
  (c) => "Now we will update the OpsItem: " + c.opsItemId
);

const sdkUpdateOpsItem = new ScenarioAction(
  "sdkUpdateOpsItem",
  /**
   * @param {{ opsItemId: string }} c
   */
  async (c) => {
    const _response = await client.send(
      new UpdateOpsItemCommand({
        OpsItemId: c.opsItemId,
        Description: "An update to " + c.opsItemId,
      })
    );
  }
);

const getOpsItemStatus = new ScenarioOutput(
  "getOpsItemStatus",
  /**
   * @param {{ opsItemId: string }} c
   */
  (c) => "Now we will get the status of the OpsItem: " + c.opsItemId
);

const sdkOpsItemStatus = new ScenarioAction(
  "sdkGetOpsItemStatus",
  /**
   * @param {{ opsItemId: string }} c
   */
  async (c) => {
    const response = await client.send(
      new DescribeOpsItemsCommand({
        OpsItemId: c.opsItemId,
      })
    );
    c.opsItemStatus = response.OpsItemStatus;
  }
);

const resolveOpsItem = new ScenarioOutput(
  "resolveOpsItem",
  /**
   * @param {{ opsItemId: string }} c
   */
  (c) => "Now we will resolve the OpsItem: " + c.opsItemId
);

const sdkResolveOpsItem = new ScenarioAction(
  "sdkResolveOpsItem",
  /**
   * @param {{ opsItemId: string }} c
   */
  async (c) => {
    const _response = await client.send(
      new UpdateOpsItemCommand({
        OpsItemId: c.opsItemId,
        Status: OpsItemStatus.RESOLVED,
      })
    );
  }
);

const askToDeleteResources = new ScenarioInput(
  "askToDeleteResources",
  "Would you like to delete the Systems Manager resources created during this example run?",
  { type: "confirm" }
);

const confirmDeleteChoice = new ScenarioOutput(
  "confirmDeleteChoice",
  /**
   * @param {{ askToDeleteResources: boolean }} state
   */
  (state) => {
    if (state.askToDeleteResources) return "You chose to delete the resources.";
    return "The Systems Manager resources will not be deleted. Please delete them manually to avoid charges.";
  }
);

export const sdkDeleteResources = new ScenarioAction(
  "sdkDeleteResources",
  /**
   * @param {{ }} c
   */
  async (c) => {
    //delete ops item
    const opsItemResponse = await client.send(
      new DeleteOpsItemCommand({
        OpsItemId: c.opsItemId,
      })
    );
    if (opsItemResponse.$metadata.httpStatusCode == 200) {
      console.log(
        "The ops item: " + c.opsItemId + " was successfully deleted."
      );
    } else {
      console.log(
        "There was a problem deleting the ops item: " +
          c.opsItemId +
          ". Please delete it manually."
      );
    }

    const maintenanceWindowResponse = await client.send(
      new DeleteMaintenanceWindowCommand({
        Name: c.maintenanceWindow,
        WindowId: c.winId,
      })
    );
    if (maintenanceWindowResponse.$metadata.httpStatusCode == 200) {
      console.log(
        "The maintenance window: " +
          c.maintenanceWindow +
          " was successfully deleted."
      );
    } else {
      console.log(
        "There was a problem deleting the maintenance window: " +
          c.opsItemId +
          ". Please delete it manually."
      );
    }

    const docResponse = await client.send(
      new DeleteDocumentCommand({
        Name: c.documentName,
      })
    );
    if (docResponse.$metadata.httpStatusCode == 200) {
      console.log(
        "The document: " + c.documentName + " was successfully deleted."
      );
    } else {
      console.log(
        "There was a problem deleting the document: " +
          c.documentName +
          ". Please delete it manually."
      );
    }
  },
  { skipWhen: (/** @type {{}} */ state) => !state.askToDeleteResources }
);

const goodbye = new ScenarioOutput(
  "goodbye",
  "This concludes the Systems Manager Basics scenario for the AWS Javascript SDK v3. Thank you!"
);

const myScenario = new Scenario("SSM Basics", [
  greet,
  pressEnter,
  createMaintenanceWindow,
  getMaintenanceWindow,
  sdkCreateMaintenanceWindow,
  modifyMaintenanceWindow,
  pressEnter,
  sdkModifyMaintenanceWindow,
  createSystemsManagerActions,
  getDocumentName,
  sdkCreateSSMDoc,
  ec2HelloWorld,
  confirmEC2HelloWorld,
  sdkEC2HelloWorld,
  pressEnter,
  getCommandTime,
  sdkGetCommandTime,
  pressEnter,
  showCommandTime,
  createSSMOpsItem,
  pressEnter,
  sdkCreateSSMOpsItem,
  updateOpsItem,
  pressEnter,
  sdkUpdateOpsItem,
  getOpsItemStatus,
  pressEnter,
  sdkOpsItemStatus,
  resolveOpsItem,
  pressEnter,
  sdkResolveOpsItem,
  askToDeleteResources,
  confirmDeleteChoice,
  pressEnter,
  sdkDeleteResources,
  goodbye,
]);

export const main = async (stepHandlerOptions) => {
  await myScenario.run(stepHandlerOptions);
};

// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  main();
}
// snippet-end:[ssm.JavaScript.Basics.scenario]
