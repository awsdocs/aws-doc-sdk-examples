// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.scenario;

// snippet-start:[ssm.java2.scenario.main]
import java.util.Scanner;
public class SSMScenario {
    public static final String DASHES = new String(new char[80]).replace("\0", "-");

    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        SSMActions actions = new SSMActions();
        String documentName;
        String windowName;
        String instanceId = "i-0fecb1fdbc2a1ecbd";
        String title = "Disk Space Alert" ; //args[0];
        String source = "EC2";
        String category = "Performance" ; //args[2];
        String severity = "2" ; // args[3];

        System.out.println(DASHES);
        System.out.println("""
                Welcome to the AWS Systems Manager SDK Basics scenario.
                This Java program demonstrates how to interact with AWS Systems Manager using the AWS SDK for Java (v2).
                AWS Systems Manager is the operations hub for your AWS applications and resources and a secure end-to-end management solution.
                The program's primary functionalities include creating a maintenance window, creating a document, sending a command to a document,
                listing documents, listing commands, creating an OpsItem, modifying an OpsItem, and deleting AWS SSM resources.
                Upon completion of the program, all AWS resources are cleaned up.
                Let's get started...
            
                """);
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println("1. Create an SSM maintenance window.");
        System.out.println("Please enter the maintenance window name (default is ssm-maintenance-window):");
        String win = scanner.nextLine();
        windowName = win.isEmpty() ? "ssm-maintenance-window" : win;
        String winId = String.valueOf(actions.createMaintenanceWindow(windowName));
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println("2. Modify the maintenance window by changing the schedule");
        waitForInputToContinue(scanner);
        actions.updateSSMMaintenanceWindow(winId, windowName);
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println("3. Create an SSM document that defines the actions that Systems Manager performs on your managed nodes.");
        System.out.println("Please enter the document name (default is ssmdocument):");
        String doc = scanner.nextLine();
        documentName = doc.isEmpty() ? "ssmdocument" : doc;
        actions.createSSMDoc(documentName);
        waitForInputToContinue(scanner);

        System.out.println("4. Now we are going to run a command on an EC2 instance");
        waitForInputToContinue(scanner);
        String commandId = actions.sendSSMCommand(documentName, instanceId);
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println("5. Lets get the time when the specific command was sent to the specific managed node");
        waitForInputToContinue(scanner);
        actions.displayCommands(commandId);
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("""
             6. Now we will create an SSM OpsItem. 
             A SSM OpsItem is a feature provided by Amazon's Systems Manager (SSM) service. 
             It is a type of operational data item that allows you to manage and track various operational issues, 
             events, or tasks within your AWS environment.
             
             You can create OpsItems to track and manage operational issues as they arise. 
             For example, you could create an OpsItem whenever your application detects a critical error 
             or an anomaly in your infrastructure.
            """);

        waitForInputToContinue(scanner);
        String opsItemId = String.valueOf(actions.createSSMOpsItem(title, source, category, severity));
        System.out.println(opsItemId + " was created");
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("7. Now we will update SSM OpsItem "+opsItemId);
        waitForInputToContinue(scanner);
        String description = "An update to "+opsItemId ;
        actions.updateOpsItem(opsItemId, title, description);

        System.out.println(DASHES);
        System.out.println("8. Now we will get the status of SSM OpsItem "+opsItemId);
        waitForInputToContinue(scanner);
        actions.describeOpsItems(opsItemId);

        System.out.println(DASHES);
        System.out.println("9. Now we will resolve the SSM OpsItem "+opsItemId);
        waitForInputToContinue(scanner);
        actions.resolveOpsItem(opsItemId);

        System.out.println(DASHES);
        System.out.println("10. Would you like to delete the AWS Systems Manager resources? (y/n)");
        String delAns = scanner.nextLine().trim();
        if (delAns.equalsIgnoreCase("y")) {
            System.out.println("You selected to delete the resources.");
            waitForInputToContinue(scanner);
            actions.deleteMaintenanceWindow(winId);
            actions.deleteDoc(documentName);
        } else {
            System.out.println("The AWS Systems Manager resources will not be deleted");
        }
        System.out.println(DASHES);

        System.out.println("This concludes the AWS Systems Manager SDK Basics scenario.");
        System.out.println(DASHES);
    }

    private static void waitForInputToContinue(Scanner scanner) {
        while (true) {
            System.out.println("");
            System.out.println("Enter 'c' followed by <ENTER> to continue:");
            String input = scanner.nextLine();

            if (input.trim().equalsIgnoreCase("c")) {
                System.out.println("Continuing with the program...");
                System.out.println("");
                break;
            } else {
                // Handle invalid input.
                System.out.println("Invalid input. Please try again.");
            }
        }
    }
}
// snippet-end:[ssm.java2.scenario.main]