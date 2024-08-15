// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.ec2.scenario;

// snippet-start:[ec2.java2.scenario.main]
import software.amazon.awssdk.services.ec2.model.CreateKeyPairResponse;
import software.amazon.awssdk.services.ec2.model.DeleteKeyPairResponse;
import software.amazon.awssdk.services.ec2.model.DescribeKeyPairsResponse;
import software.amazon.awssdk.services.ec2.model.DescribeSecurityGroupsResponse;
import software.amazon.awssdk.services.ec2.model.DisassociateAddressResponse;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
import software.amazon.awssdk.services.ec2.model.ReleaseAddressResponse;
import software.amazon.awssdk.services.ssm.model.GetParametersByPathResponse;
import software.amazon.awssdk.services.ssm.model.Parameter;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * Before running this Java (v2) code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 * This Java example performs the following tasks:
 *
 * 1. Creates an RSA key pair and saves the private key data as a .pem file.
 * 2. Lists key pairs.
 * 3. Creates a security group for the default VPC.
 * 4. Displays security group information.
 * 5. Gets a list of Amazon Linux 2 AMIs and selects one.
 * 6. Gets more information about the image.
 * 7. Gets a list of instance types that are compatible with the selected AMI’s
 * architecture.
 * 8. Creates an instance with the key pair, security group, AMI, and an
 * instance type.
 * 9. Displays information about the instance.
 * 10. Stops the instance and waits for it to stop.
 * 11. Starts the instance and waits for it to start.
 * 12. Allocates an Elastic IP address and associates it with the instance.
 * 13. Displays SSH connection info for the instance.
 * 14. Disassociates and deletes the Elastic IP address.
 * 15. Terminates the instance and waits for it to terminate.
 * 16. Deletes the security group.
 * 17. Deletes the key pair.
 */
public class EC2Scenario {

    public static final String DASHES = new String(new char[80]).replace("\0", "-");

    public static void main(String[] args) throws InterruptedException {

        final String usage = """

            Usage:
               <keyName> <fileName> <groupName> <groupDesc> <vpcId>

            Where:
               keyName -  A key pair name (for example, TestKeyPair).\s
               fileName -  A file name where the key information is written to.\s
               groupName - The name of the security group.\s
               groupDesc - The description of the security group.\s
               vpcId - A VPC Id value. You can get this value from the AWS Management Console.\s
               myIpAddress - The IP address of your development machine.\s

            """;

     //   if (args.length != 6) {
     //       System.out.println(usage);
     //       return;
     //   }

        String keyName = "TestKeyPair18" ; //args[0];
        String fileName = "ec2Key.pem"; //args[1];
        String groupName = "ScottSecurityGroup18" ; // args[2];
        String groupDesc = "Test Group" ; //args[3];
        String vpcId = "vpc-e97a4393" ; //args[4];
        String myIpAddress = "72.21.198.66" ; // args[5];
        Scanner scanner = new Scanner(System.in);
        EC2Actions ec2Actions = new EC2Actions();

        System.out.println("""
            Amazon Elastic Compute Cloud (EC2) is a web service that provides secure, resizable compute 
            capacity in the cloud. It allows developers and organizations to easily launch and manage 
            virtual server instances, known as EC2 instances, to run their applications.
                        
            EC2 provides a wide range of instance types, each with different compute, memory, 
            and storage capabilities, to meet the diverse needs of various workloads. Developers 
            can choose the appropriate instance type based on their application's requirements, 
            such as high-performance computing, memory-intensive tasks, or GPU-accelerated workloads.
                        
            The `Ec2AsyncClient` interface in the AWS SDK for Java 2.x provides a set of methods to 
            programmatically interact with the Amazon EC2 service. This allows developers to 
            automate the provisioning, management, and monitoring of EC2 instances as part of their 
            application deployment pipelines. With EC2, teams can focus on building and deploying 
            their applications without having to worry about the underlying infrastructure 
            required to host and manage physical servers.
            
            This scenario walks you through how to perform key operations for this service.  
            Let's get started...
            """);

        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("1. Create an RSA key pair and save the private key material as a .pem file.");
        System.out.println("""
            An RSA key pair for Amazon EC2 is a security mechanism used to authenticate and secure 
            access to your EC2 instances. It consists of a public key and a private key, 
            which are generated as a pair.
            """);
        waitForInputToContinue(scanner);
        try {
            CompletableFuture<CreateKeyPairResponse> future = ec2Actions.createKeyPairAsync(keyName, fileName);
            CreateKeyPairResponse response = future.join();
            System.out.println("Key Pair successfully created. Key Fingerprint: " + response.keyFingerprint());
        } catch (CompletionException ce) {
            Throwable cause = ce.getCause();
            if (cause instanceof Ec2Exception ec2Ex) {
                if ("InvalidKeyPair.Duplicate".equals(ec2Ex.awsErrorDetails().errorCode())) {
                    // Key pair already exists.
                    System.out.println("The key pair '" + keyName + "' already exists. Moving on...");
                } else {
                    // Handle other EC2 exceptions.
                    System.err.printf("EC2 error occurred: %s (Code: %s)%n", ec2Ex.getMessage(), ec2Ex.awsErrorDetails().errorCode());
                    return;
                }
            } else {
                System.err.println("An unexpected error occurred: " + (cause != null ? cause.getMessage() : ce.getMessage()));
                return; // End the execution
            }
        }
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("2. List key pairs.");
        waitForInputToContinue(scanner);
        try {
            CompletableFuture<DescribeKeyPairsResponse> future = ec2Actions.describeKeysAsync();
            future.join(); // Ensure the future completes and any exceptions are handled

        } catch (CompletionException ce) {
            Throwable cause = ce.getCause();
            if (cause instanceof Ec2Exception ec2Ex) {
                System.err.printf("EC2 error occurred: %s (Code: %s)%n", ec2Ex.getMessage(), ec2Ex.awsErrorDetails().errorCode());
                return;
            } else {
                System.err.printf("An unexpected error occurred: %s%n", (cause != null ? cause.getMessage() : ce.getMessage()));
                return;
            }
        }
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("3. Create a security group.");
        System.out.println("""
            An AWS EC2 Security Group is a virtual firewall that controls the 
            inbound and outbound traffic to an EC2 instance. It acts as a first line 
            of defense for your EC2 instances, allowing you to specify the rules that 
            govern the network traffic entering and leaving your instances.
           """);
        waitForInputToContinue(scanner);
        String groupId= "";
        try {
            CompletableFuture<String> future = ec2Actions.createSecurityGroupAsync(groupName, groupDesc, vpcId, myIpAddress);
            groupId = future.join();
            System.out.println("Created security group with ID: " + groupId);
        } catch (CompletionException ce) {
            Throwable cause = ce.getCause();
            if (cause instanceof Ec2Exception ec2Ex) {
                // Handle EC2 exceptions.
                System.err.printf("EC2 error occurred: %s (Code: %s)%n", ec2Ex.getMessage(), ec2Ex.awsErrorDetails().errorCode());
                return;
            } else {
                // Handle other unexpected errors.
                if (cause.getMessage().contains("already exists")) {
                    System.err.println("The Security Group already exists. Moving on... ");
                } else {
                    System.err.printf("An unexpected error occurred: %s%n", (cause != null ? cause.getMessage() : ce.getMessage()));
                    return;
                }
            }
        }
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("4. Display security group info for the newly created security group.");
        waitForInputToContinue(scanner);
        try {
            CompletableFuture<DescribeSecurityGroupsResponse> future = ec2Actions.describeSecurityGroupsAsync(groupId);
            future.join();
            System.out.println("Security groups described successfully.");

        } catch (CompletionException ce) {
            Throwable cause = ce.getCause();
            if (cause instanceof Ec2Exception ec2Ex) {
                // Handle EC2 exceptions.
                System.err.printf("EC2 error occurred: %s (Code: %s)%n", ec2Ex.getMessage(), ec2Ex.awsErrorDetails().errorCode());
                return;
            } else {
                System.err.printf("An unexpected error occurred: %s%n", (cause != null ? cause.getMessage() : ce.getMessage()));
                return;
            }
        }
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("5. Get a list of Amazon Linux 2 AMIs and selects one with amzn2 in the name.");
        System.out.println("""
            An Amazon EC2 AMI (Amazon Machine Image) is a pre-configured virtual machine image that 
            serves as a template for launching EC2 instances. It contains all the necessary software and 
            configurations required to run an application or operating system on an EC2 instance.
            """);
        waitForInputToContinue(scanner);
        String instanceId="";
        try {
            CompletableFuture<GetParametersByPathResponse> future = ec2Actions.getParaValuesAsync();
            GetParametersByPathResponse pathResponse = future.join();
            List<Parameter> parameterList = pathResponse.parameters();
            for (Parameter para : parameterList) {
                if (filterName(para.name())) {
                    instanceId = para.value();
                    break;
                }
            }
        } catch (CompletionException ce) {
            Throwable cause = ce.getCause();
            if (cause instanceof Ec2Exception) {
                Ec2Exception ec2Ex = (Ec2Exception) cause;
                System.err.printf("EC2 error occurred: %s (Code: %s)%n", ec2Ex.getMessage(), ec2Ex.awsErrorDetails().errorCode());
                return;
            } else {
                System.err.printf("An unexpected error occurred: %s%n", (cause != null ? cause.getMessage() : ce.getMessage()));
                return;
            }
        }
        System.out.println("The instance Id containing amzn2 is " + instanceId);
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("6. Get more information about an amzn2 image.");
        waitForInputToContinue(scanner);
        String amiValue = "";
        try {
            CompletableFuture<String> future = ec2Actions.describeImageAsync(instanceId);
            amiValue = future.join();
            System.out.println("Image ID: {}"+ amiValue);
            waitForInputToContinue(scanner);
        } catch (CompletionException ce) {
            Throwable cause = ce.getCause();
            if (cause instanceof Ec2Exception) {
                Ec2Exception ec2Ex = (Ec2Exception) cause;
                // Handle EC2 exceptions.
                System.err.printf("EC2 error occurred: %s (Code: %s)%n", ec2Ex.getMessage(), ec2Ex.awsErrorDetails().errorCode());
                return;
            } else {
                System.err.printf("An unexpected error occurred: %s%n", (cause != null ? cause.getMessage() : ce.getMessage()));
                return;
            }
        }
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("7. Get a list of instance types.");
        waitForInputToContinue(scanner);
        String instanceType;
        try {
            CompletableFuture<String> future = ec2Actions.getInstanceTypesAsync();
            instanceType = future.join();
            if (!instanceType.isEmpty()) {
                System.out.println("Found instance type: " + instanceType);
            } else {
                System.out.println("Desired instance type not found.");
            }
        } catch (CompletionException ce) {
            Throwable cause = ce.getCause();
            if (cause instanceof Ec2Exception) {
                Ec2Exception ec2Ex = (Ec2Exception) cause;
                System.err.printf("EC2 error occurred: %s (Code: %s)%n", ec2Ex.getMessage(), ec2Ex.awsErrorDetails().errorCode());
                return;
            } else {
                System.err.printf("An unexpected error occurred: %s%n", (cause != null ? cause.getMessage() : ce.getMessage()));
                return;
            }
        }
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("8. Create a new Amazon EC2 instance.");
        waitForInputToContinue(scanner);
        String newInstanceId;
        try {
            CompletableFuture<String> future = ec2Actions.runInstanceAsync(instanceType, keyName, groupName, amiValue);
            newInstanceId = future.join(); // Get the instance ID.
            System.out.println("EC2 instance ID: "+ newInstanceId);
        } catch (CompletionException ce) {
            Throwable cause = ce.getCause();
            if (cause instanceof Ec2Exception) {
                Ec2Exception ec2Ex = (Ec2Exception) cause;
                switch (ec2Ex.awsErrorDetails().errorCode()) {
                    case "InvalidParameterValue":
                        // Handle invalid parameter value.
                        System.err.printf("Invalid parameter value: %s%n", ec2Ex.getMessage(), ec2Ex.awsErrorDetails().errorCode());
                        break;
                    case "InsufficientInstanceCapacity":
                        // Handle insufficient instance capacity.
                        System.err.printf("Insufficient instance capacity: %s%n", ec2Ex.getMessage(), ec2Ex.awsErrorDetails().errorCode());
                        break;
                    case "InvalidGroup.NotFound":
                        // Handle security group not found.
                        System.err.printf("Security group not found: %s%n", ec2Ex.getMessage(), ec2Ex.awsErrorDetails().errorCode());
                        break;
                    default:
                        // Handle other EC2 exceptions.
                        System.err.printf("EC2 error occurred: %s (Code: %s)%n", ec2Ex.getMessage(), ec2Ex.awsErrorDetails().errorCode());
                        break;
                }
                return;
            } else {
                // Handle other unexpected exceptions.
                System.err.printf("An unexpected error occurred: %s%n", (cause != null ? cause.getMessage() : ce.getMessage()));
                return;
            }
        }
        System.out.println("The instance Id is " + newInstanceId);
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("9. Display information about the running instance. ");
        waitForInputToContinue(scanner);
        String publicIp = "";
        try {
            CompletableFuture<String> future = ec2Actions.describeEC2InstancesAsync(newInstanceId);
            publicIp = future.join(); // Get the public IP address.
            System.out.println("EC2 instance public IP: " + publicIp);
        } catch (CompletionException ce) {
            Throwable cause = ce.getCause();
            if (cause instanceof Ec2Exception ec2Ex) {
                // Handle EC2 exceptions.
                System.err.printf("EC2 error occurred: %s (Code: %s)%n", ec2Ex.getMessage(), ec2Ex.awsErrorDetails().errorCode());
                return;
            } else {
                System.err.printf("An unexpected error occurred: %s%n", (cause != null ? cause.getMessage() : ce.getMessage()));
                return;
            }
        }
        System.out.println("You can SSH to the instance using this command:");
        System.out.println("ssh -i " + fileName + "ec2-user@" + publicIp);
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("10. Stop the instance.");
        waitForInputToContinue(scanner);
        try {
            CompletableFuture<Void> future = ec2Actions.stopInstanceAsync(newInstanceId);
            future.join();
            System.out.println("Instance "+newInstanceId +" stopped successfully.");
        } catch (CompletionException ce) {
            Throwable cause = ce.getCause();
            if (cause instanceof Ec2Exception ec2Ex) {
                // Handle EC2 exceptions.
                System.err.printf("EC2 error occurred: %s (Code: %s)%n", ec2Ex.getMessage(), ec2Ex.awsErrorDetails().errorCode());
                return;
            } else {
                System.err.printf("An unexpected error occurred: %s%n", (cause != null ? cause.getMessage() : ce.getMessage()));
                return;
            }
        }
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("11. Start the instance.");
        try {
            CompletableFuture<Void> future = ec2Actions.startInstanceAsync(newInstanceId);
            future.join();
            System.out.println("Instance started successfully.");
        } catch (CompletionException ce) {
            Throwable cause = ce.getCause();
            if (cause instanceof Ec2Exception ec2Ex) {
                // Handle EC2 exceptions.
                System.err.printf("EC2 error occurred: %s (Code: %s)%n", ec2Ex.getMessage(), ec2Ex.awsErrorDetails().errorCode());
                return;
            } else {
                System.err.printf("An unexpected error occurred: %s%n", (cause != null ? cause.getMessage() : ce.getMessage()));
                return;
            }
        }
        waitForInputToContinue(scanner);
        String ipAddress = "";
        try {
            CompletableFuture<String> future = ec2Actions.describeEC2InstancesAsync(newInstanceId);
            publicIp = future.join();
            System.out.println("EC2 instance public IP: " + publicIp);
            System.out.println("You can SSH to the instance using this command:");
            System.out.println("ssh -i " + fileName + "ec2-user@" + publicIp);
        } catch (CompletionException ce) {
            Throwable cause = ce.getCause();
            if (cause instanceof Ec2Exception ec2Ex) {
                // Handle EC2 exceptions.
                System.err.printf("EC2 error occurred: %s (Code: %s)%n", ec2Ex.getMessage(), ec2Ex.awsErrorDetails().errorCode());
                return;
            } else {
                System.err.printf("An unexpected error occurred: %s%n", (cause != null ? cause.getMessage() : ce.getMessage()));
                return;
            }
        }
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("12. Allocate an Elastic IP address and associate it with the instance.");
        waitForInputToContinue(scanner);
        String allocationId = "";
        try {
            CompletableFuture<String> future = ec2Actions.allocateAddressAsync();
            allocationId = future.join();
            System.out.println("Successfully allocated address with ID: " +allocationId);
        } catch (CompletionException ce) {
            Throwable cause = ce.getCause();
            if (cause instanceof Ec2Exception ec2Ex) {
                // Handle EC2 exceptions.
                System.err.printf("EC2 error occurred: %s (Code: %s)%n", ec2Ex.getMessage(), ec2Ex.awsErrorDetails().errorCode());
                return;
            } else {
                System.err.printf("An unexpected error occurred: %s%n", (cause != null ? cause.getMessage() : ce.getMessage()));
                return;
            }
        }
        System.out.println("The allocation Id value is " + allocationId);
        waitForInputToContinue(scanner);
        String associationId = "";
        try {
            CompletableFuture<String> future = ec2Actions.associateAddressAsync(newInstanceId, allocationId);
            associationId = future.join(); // Wait for the result and get the association ID
            System.out.println("Successfully associated address with ID: " +associationId);
        } catch (CompletionException ce) {
            Throwable cause = ce.getCause();
            if (cause instanceof Ec2Exception ec2Ex) {
                // Handle EC2 exceptions.
                System.err.printf("EC2 error occurred: %s (Code: %s)%n", ec2Ex.getMessage(), ec2Ex.awsErrorDetails().errorCode());
                return;
            } else {
                System.err.printf("An unexpected error occurred: %s%n", (cause != null ? cause.getMessage() : ce.getMessage()));
                return;
            }
        }
        System.out.println("The associate Id value is " + associationId);
        waitForInputToContinue(scanner);
        System.out.println(DASHES);


        System.out.println(DASHES);
        System.out.println("13. Describe the instance again.");
        waitForInputToContinue(scanner);
        try {
            CompletableFuture<String> future = ec2Actions.describeEC2InstancesAsync(newInstanceId);
            publicIp = future.join();
            System.out.println("EC2 instance public IP: " + publicIp);
            System.out.println("You can SSH to the instance using this command:");
            System.out.println("ssh -i " + fileName + "ec2-user@" + publicIp);
        } catch (CompletionException ce) {
            Throwable cause = ce.getCause();
            if (cause instanceof Ec2Exception ec2Ex) {
                // Handle EC2 exceptions.
                System.err.printf("EC2 error occurred: %s (Code: %s)%n", ec2Ex.getMessage(), ec2Ex.awsErrorDetails().errorCode());
                return;
            } else {
                System.err.printf("An unexpected error occurred: %s%n", (cause != null ? cause.getMessage() : ce.getMessage()));
                return;
            }
        }
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("14. Disassociate and release the Elastic IP address.");
        waitForInputToContinue(scanner);
        try {
            CompletableFuture<DisassociateAddressResponse> future = ec2Actions.disassociateAddressAsync(associationId);
            future.join(); // Wait for the operation to complete
            System.out.println("Address successfully disassociated.");
        } catch (CompletionException ce) {
            Throwable cause = ce.getCause();
            if (cause instanceof Ec2Exception ec2Ex) {
                // Handle EC2 exceptions.
                System.err.printf("EC2 error occurred: %s (Code: %s)%n", ec2Ex.getMessage(), ec2Ex.awsErrorDetails().errorCode());
                return;
            } else {
                System.err.printf("An unexpected error occurred: %s%n", (cause != null ? cause.getMessage() : ce.getMessage()));
                return;
            }
        }
        waitForInputToContinue(scanner);
        try {
            CompletableFuture<ReleaseAddressResponse> future = ec2Actions.releaseEC2AddressAsync(allocationId);
            future.join(); // Wait for the operation to complete
            System.out.println("Elastic IP address successfully released.");
        } catch (RuntimeException rte) {
            System.err.println("An exception occurred: " + (rte.getCause() != null ? rte.getCause().getMessage() : rte.getMessage()));
            return;
        }
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("15. Terminate the instance.");
        waitForInputToContinue(scanner);
        try {
            CompletableFuture<Void> future = ec2Actions.terminateEC2Async(newInstanceId);
            future.join();
            System.out.println("EC2 instance successfully terminated.");
        } catch (CompletionException ce) {
            Throwable cause = ce.getCause();
            if (cause instanceof Ec2Exception ec2Ex) {
                // Handle EC2 exceptions.
                System.err.printf("EC2 error occurred: %s (Code: %s)%n", ec2Ex.getMessage(), ec2Ex.awsErrorDetails().errorCode());
                return;
            } else {
                System.err.printf("An unexpected error occurred: %s%n", (cause != null ? cause.getMessage() : ce.getMessage()));
                return;
            }
        }
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("16. Delete the security group.");
        waitForInputToContinue(scanner);
        try {
            CompletableFuture<Void> future = ec2Actions.deleteEC2SecGroupAsync(groupId);
            future.join(); // Wait for the operation to complete
            System.out.println("Security group successfully deleted.");
        } catch (CompletionException ce) {
            Throwable cause = ce.getCause();
            if (cause instanceof Ec2Exception ec2Ex) {
                // Handle EC2 exceptions.
                System.err.printf("EC2 error occurred: %s (Code: %s)%n", ec2Ex.getMessage(), ec2Ex.awsErrorDetails().errorCode());
                return;
            } else {
                System.err.printf("An unexpected error occurred: %s%n", (cause != null ? cause.getMessage() : ce.getMessage()));
                return;
            }
        }
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("17. Delete the key.");
        waitForInputToContinue(scanner);
        try {
            CompletableFuture<DeleteKeyPairResponse> future = ec2Actions.deleteKeysAsync(keyName);
            future.join();
        } catch (CompletionException ce) {
            Throwable cause = ce.getCause();
            if (cause instanceof Ec2Exception ec2Ex) {
                // Handle EC2 exceptions.
                System.err.printf("EC2 error occurred: %s (Code: %s)%n", ec2Ex.getMessage(), ec2Ex.awsErrorDetails().errorCode());
                return;
            } else {
                System.err.printf("An unexpected error occurred: %s%n", (cause != null ? cause.getMessage() : ce.getMessage()));
                return;
            }
        }
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("You successfully completed the Amazon EC2 scenario.");
        System.out.println(DASHES);
    }
    public static boolean filterName(String name) {
        String[] parts = name.split("/");
        String myValue = parts[4];
        return myValue.contains("amzn2");
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
// snippet-end:[ec2.java2.scenario.main]