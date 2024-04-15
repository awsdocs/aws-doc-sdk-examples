// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.scenario;

// snippet-start:[ssm.java2.scenario.main]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.*;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class SSMScenario {
    public static final String DASHES = new String(new char[80]).replace("\0", "-");
    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        String documentName;
        String windowName;
        String instanceId = "i-0149338494ed95f06";
        String title = "Disk Space Alert" ; //args[0];
        String source = "EC2";
        String category = "Performance" ; //args[2];
        String severity = "2" ; // args[3];

        Region region = Region.US_EAST_1;
        SsmClient ssmClient = SsmClient.builder()
            .region(region)
            .build();

        System.out.println(DASHES);
        System.out.println("""
                Welcome to the AWS Systems Manager SDK Getting Started scenario.
                This Java program demonstrates how to interact with AWS Systems Manager using the AWS SDK for Java (v2).
                AWS Systems Manager is the operations hub for your AWS applications and resources and a secure end-to-end management solution.
                The program's primary functionalities include creating a maintenance window, creating a document, sending a command to a document,
                listing documents, listing commands, creating an OpsItem, modifying an OpsItem, and deleting AWS SSM resources.
                Upon completion of the program, all AWS resources are cleaned up.
                Let's get started...
                Please hit Enter
                """);
        scanner.nextLine();
        System.out.println(DASHES);

        System.out.println("Create an SSM maintenance window.");
        System.out.println("Please enter the maintenance window name (default is ssm-maintenance-window):");
        String win = scanner.nextLine();
        windowName = win.isEmpty() ? "ssm-maintenance-window" : win;
        String winId = createMaintenanceWindow(ssmClient, windowName);
        System.out.println(DASHES);

        System.out.println("Modify the maintenance window by changing the schedule");
        System.out.println("Please hit Enter");
        scanner.nextLine();
        updateSSMMaintenanceWindow(ssmClient, winId, windowName);
        System.out.println(DASHES);

        System.out.println("Create an SSM document that defines the actions that Systems Manager performs on your managed nodes.");
        System.out.println("Please enter the document name (default is ssmdocument):");
        String doc = scanner.nextLine();
        documentName = doc.isEmpty() ? "ssmdocument" : doc;
        createSSMDoc(ssmClient, documentName);

        System.out.println("Now we are going to run a command on an EC2 instance");
        System.out.println("Please hit Enter");
        scanner.nextLine();
        String commandId = sendSSMCommand(ssmClient, documentName, instanceId);
        System.out.println(DASHES);

        System.out.println("Lets get the time when the specific command was sent to the specific managed node");
        System.out.println("Please hit Enter");
        scanner.nextLine();
        displayCommands(ssmClient, commandId);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("""
             Now we will create an SSM OpsItem. 
             SSM OpsItem is a feature provided by Amazon's Systems Manager (SSM) service. 
             It is a type of operational data item that allows you to manage and track various operational issues, 
             events, or tasks within your AWS environment.
             
             You can create OpsItems to track and manage operational issues as they arise. 
             For example, you could create an OpsItem whenever your application detects a critical error 
             or an anomaly in your infrastructure.
            """);

        System.out.println("Please hit Enter");
        scanner.nextLine();
        String opsItemId = createSSMOpsItem(ssmClient, title, source, category, severity);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("Now we will update SSM OpsItem "+opsItemId);
        System.out.println("Please hit Enter");
        scanner.nextLine();
        String description = "An update to "+opsItemId ;
        updateOpsItem(ssmClient, opsItemId, title, description);
        System.out.println("Now we will get the status of SSM OpsItem "+opsItemId);
        System.out.println("Please hit Enter");
        scanner.nextLine();
        describeOpsItems(ssmClient, opsItemId);
        System.out.println("Now we will resolve the SSM OpsItem "+opsItemId);
        System.out.println("Please hit Enter");

        System.out.println("Now we will resolve the SSM OpsItem "+opsItemId);
        System.out.println("Please hit Enter");
        scanner.nextLine();
        resolveOpsItem(ssmClient, opsItemId);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("Would you like to delete the AWS Systems Manager resources? (y/n)");
        String delAns = scanner.nextLine().trim();
        if (delAns.equalsIgnoreCase("y")) {
            System.out.println("You selected to delete the resources.");
            System.out.print("Press Enter to continue...");
            scanner.nextLine();
            deleteMaintenanceWindow(ssmClient, winId);
            deleteDoc(ssmClient, documentName);
        } else {
            System.out.println("The AWS Systems Manager resources will not be deleted");
        }
        System.out.println(DASHES);

        System.out.println("This concludes the AWS Systems Manager SDK Getting Started scenario.");
        System.out.println(DASHES);

    }

    // snippet-start:[ssm.java2.describe_command.main]
    // Displays the date and time when the specific command was invoked.
    public static void displayCommands(SsmClient ssmClient, String commandId) {
        ListCommandInvocationsRequest commandInvocationsRequest = ListCommandInvocationsRequest.builder()
            .commandId(commandId)
            .build();

        ListCommandInvocationsResponse response = ssmClient.listCommandInvocations(commandInvocationsRequest);
        List<CommandInvocation> commandList = response.commandInvocations();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
        for (CommandInvocation invocation : commandList) {
            System.out.println("The time of the command invocation is " + formatter.format(invocation.requestedDateTime()));
        }
    }
    // snippet-end:[ssm.java2.describe_command.main]

    // snippet-start:[ssm.java2.create_ops.main]
    // Create an SSM OpsItem
    public static String createSSMOpsItem(SsmClient ssmClient, String title, String source, String category, String severity) {
        try {
            CreateOpsItemRequest opsItemRequest = CreateOpsItemRequest.builder()
                .description("Created by the SSM Java API")
                .title(title)
                .source(source)
                .category(category)
                .severity(severity)
                .build();

            CreateOpsItemResponse itemResponse = ssmClient.createOpsItem(opsItemRequest);
            return itemResponse.opsItemId();

        } catch (SsmException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
       return "";
    }
    // snippet-end:[ssm.java2.create_ops.main]

    // snippet-start:[ssm.java2.update_ops.main]
    // Update the AWS SSM OpsItem.
    public static void updateOpsItem(SsmClient ssmClient, String opsItemId, String title, String description) {
        Map<String, OpsItemDataValue> operationalData = new HashMap<>();
        operationalData.put("key1", OpsItemDataValue.builder().value("value1").build());
        operationalData.put("key2", OpsItemDataValue.builder().value("value2").build());

        try {
            UpdateOpsItemRequest request = UpdateOpsItemRequest.builder()
                .opsItemId(opsItemId)
                .title(title)
                .operationalData(operationalData)
                .status(getOpsItem(ssmClient, opsItemId).statusAsString())
                .description(description)
                .build();

            ssmClient.updateOpsItem(request);

        } catch (SsmException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[ssm.java2.update_ops.main]

    // snippet-start:[ssm.Java2.resolve_ops.main]
    public static void resolveOpsItem(SsmClient ssmClient, String opsID) {
        try {
            UpdateOpsItemRequest opsItemRequest = UpdateOpsItemRequest.builder()
                .opsItemId(opsID)
                .status(OpsItemStatus.RESOLVED)
                .build();

            ssmClient.updateOpsItem(opsItemRequest);

        } catch (SsmException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[ssm.Java2.resolve_ops.main]

    // snippet-start:[ssm.Java2.get_ops.main]
    // Gets a specific OpsItem.
    private static OpsItem getOpsItem(SsmClient ssmClient, String opsItemId) {
        GetOpsItemRequest itemRequest = GetOpsItemRequest.builder()
            .opsItemId(opsItemId)
            .build();

        try {
            GetOpsItemResponse response = ssmClient.getOpsItem(itemRequest);
            return response.opsItem();

        } catch (SsmException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }
    // snippet-end:[ssm.Java2.get_ops.main]

    // snippet-start:[ssm.Java2.send_command.main]
    // Sends a SSM command to a managed node.
    public static String sendSSMCommand(SsmClient ssmClient, String documentName, String instanceId) throws InterruptedException {
        // Before we use Document to send a command - make sure it is active.
        boolean isDocumentActive = false;
        DescribeDocumentRequest request = DescribeDocumentRequest.builder()
            .name(documentName)
            .build();

        while (!isDocumentActive) {
            DescribeDocumentResponse response = ssmClient.describeDocument(request);
            String documentStatus = response.document().statusAsString();
            if (documentStatus.equals("Active")) {
                System.out.println("The SSM document is active and ready to use.");
                isDocumentActive = true;
            } else {
                System.out.println("The SSM document is not active. Status: " + documentStatus);
                try {
                    // Add a delay to avoid making too many requests.
                    Thread.sleep(5000); // Wait for 5 seconds before checking again
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // Create the SendCommandRequest.
        SendCommandRequest commandRequest = SendCommandRequest.builder()
            .documentName(documentName)
            .instanceIds(instanceId)
            .build();

        // Send the command.
        SendCommandResponse commandResponse = ssmClient.sendCommand(commandRequest);
        String commandId = commandResponse.command().commandId();
        System.out.println("Command ID: " + commandId);

        // Wait for the command execution to complete.
        GetCommandInvocationRequest invocationRequest = GetCommandInvocationRequest.builder()
            .commandId(commandId)
            .instanceId(instanceId)
            .build();

        System.out.println("Wait 5 secs");
        TimeUnit.SECONDS.sleep(5);

        // Retrieve the command execution details.
        GetCommandInvocationResponse commandInvocationResponse = ssmClient.getCommandInvocation(invocationRequest);

        // Check the status of the command execution.
        CommandInvocationStatus status = commandInvocationResponse.status();
        if (status == CommandInvocationStatus.SUCCESS) {
            System.out.println("Command execution successful");
        } else {
            System.out.println("Command execution failed. Status: " + status);
        }
        return commandId;
    }
    // snippet-end:[ssm.Java2.send_command.main]

    // snippet-start:[ssm.Java2.delete_doc.main]
    // Deletes an AWS SSM document.
    public static void deleteDoc(SsmClient ssmClient, String documentName) {
        DeleteDocumentRequest documentRequest = DeleteDocumentRequest.builder()
            .name(documentName)
            .build();

        ssmClient.deleteDocument(documentRequest);
        System.out.println("The SSM document was successfully deleted");
    }
    // snippet-end:[ssm.Java2.delete_doc.main]

    // snippet-start:[ssm.java2.delete_window.main]
    public static void deleteMaintenanceWindow(SsmClient ssmClient, String winId) {
        DeleteMaintenanceWindowRequest windowRequest = DeleteMaintenanceWindowRequest.builder()
            .windowId(winId)
            .build();

        ssmClient.deleteMaintenanceWindow(windowRequest);
        System.out.println("The maintenance window was successfully deleted");
    }
    // snippet-end:[ssm.java2.delete_window.main]

    // snippet-start:[ssm.java2.update_window.main]
    public static void updateSSMMaintenanceWindow(SsmClient ssmClient, String id, String name) {
        // Update the maintenance window schedule
        UpdateMaintenanceWindowRequest updateRequest = UpdateMaintenanceWindowRequest.builder()
            .windowId(id)
            .allowUnassociatedTargets(true)
            .duration(24)
            .enabled(true)
            .name(name)
            .schedule("cron(0 0 ? * MON *)")
            .build();

        ssmClient.updateMaintenanceWindow(updateRequest);
        System.out.println("The SSM maintenance window was successfully updated");
    }
    // snippet-end:[ssm.java2.update_window.main]

    // snippet-start:[ssm.java2.create_window.main]
    public static String createMaintenanceWindow(SsmClient ssmClient, String winName) {
        CreateMaintenanceWindowRequest request = CreateMaintenanceWindowRequest.builder()
            .name(winName)
            .description("This is my maintenance window")
            .allowUnassociatedTargets(true)
            .duration(2)
            .cutoff(1)
            .schedule("cron(0 10 ? * MON-FRI *)")
            .build();

        try {
        CreateMaintenanceWindowResponse response = ssmClient.createMaintenanceWindow(request);
        String maintenanceWindowId = response.windowId();
        System.out.println("The maintenance window id is " + maintenanceWindowId);
        return maintenanceWindowId;

        } catch (DocumentAlreadyExistsException e) {
            System.err.println("The SSM maintenance window already exists. Moving on" );
        } catch (SsmException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        MaintenanceWindowFilter filter = MaintenanceWindowFilter.builder()
            .key("name")
            .values(winName)
            .build();

        // Get the existing window id
        DescribeMaintenanceWindowsRequest winRequest = DescribeMaintenanceWindowsRequest.builder()
            .filters(filter)
            .build();

        String windowId = "";
        DescribeMaintenanceWindowsResponse response = ssmClient.describeMaintenanceWindows(winRequest);
        List<MaintenanceWindowIdentity> windows = response.windowIdentities();
        if (!windows.isEmpty()) {
            windowId = windows.get(0).windowId();
            System.out.println("Window ID: " + windowId);
        } else {
            System.out.println("Window not found.");
        }
       return windowId;
    }
    // snippet-end:[ssm.java2.create_window.main]

    // snippet-start:[ssm.java2.create_doc.main]
    // Create an AWS SSM document to use in this scenario.
    public static void createSSMDoc(SsmClient ssmClient, String docName) {
        // Create JSON for the content
        String jsonData = """
            {
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
                }
            """;

        try {
            CreateDocumentRequest request = CreateDocumentRequest.builder()
                .content(jsonData)
                .name(docName)
                .documentType(DocumentType.COMMAND)
                .build();

            // Create the document.
            CreateDocumentResponse response = ssmClient.createDocument(request);
            System.out.println("The status of the SSM document is " + response.documentDescription().status());

        } catch (DocumentAlreadyExistsException e) {
            System.err.println("The SSM document already exists. Moving on" );
        } catch (SsmException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[ssm.java2.create_doc.main]

    // snippet-start:[ssm.java2.describe_ops.main]
    public static void describeOpsItems(SsmClient ssmClient, String key) {
        try {

            OpsItemFilter filter = OpsItemFilter.builder()
                .key(OpsItemFilterKey.OPS_ITEM_ID)
                .values(key)
                .operator(OpsItemFilterOperator.EQUAL)
                .build();

            DescribeOpsItemsRequest itemsRequest = DescribeOpsItemsRequest.builder()
                .maxResults(10)
                .opsItemFilters(filter)
                .build();

            DescribeOpsItemsResponse itemsResponse = ssmClient.describeOpsItems(itemsRequest);
            List<OpsItemSummary> items = itemsResponse.opsItemSummaries();
            for (OpsItemSummary item : items) {
                System.out.println("The item title is " + item.title() +" and the status is "+item.status().toString());
            }

        } catch (SsmException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[ssm.java2.describe_ops.main]
}
// snippet-end:[ssm.java2.scenario.main]