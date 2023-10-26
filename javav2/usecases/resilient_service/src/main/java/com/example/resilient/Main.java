/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.resilient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import software.amazon.awssdk.services.autoscaling.model.AutoScalingException;
import software.amazon.awssdk.services.ec2.model.Subnet;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.TargetHealthDescription;
/**
 *  Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 *  For more information, see the following documentation topic:
 *
 *  <a href="https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html">...</a>
 *
 *  In addition, set these values:
 *
 * 1. fileName - The location of the recommendations.json (you can locate this file in python/cross_service/resilient_service).
 * 2. tableName - The name of the Amazon DynamoDB table.
 * 3. startScript - The location of the server_startup_script.sh script (you can locate this file in workflows/resilient_service/resources).
 * 4. policyFile - the location of the instance_policy.json  (you can locate this file in workflows/resilient_service/resources).
 * 5. ssmJSON - the location of the ssm_only_policy.json (you can locate this file in workflows/resilient_service/resources).
 * 6. templateName - The name of the template.
 * 7. roleName - The name of the role.
 * 8. policyName - The name of the policy.
 * 9. profileName - The name of the profile.
 * 10. targetGroupName - The name of the target group.
 * 11. autoScalingGroupName - The name of the auto-scaling group.
 * 12. lbName - The name of the load balancer.
 */


// snippet-start:[javav2.example_code.workflow.ResilientService_Runner]
public class Main {

    public static final String fileName = "C:\\AWS\\resworkflow\\recommendations.json"; // Modify file location.
    public static final String tableName = "doc-example-recommendation-service";
    public static final String startScript = "C:\\AWS\\resworkflow\\server_startup_script.sh"; // Modify file location.
    public static final String policyFile = "C:\\AWS\\resworkflow\\instance_policy.json"; // Modify file location.
    public static final String ssmJSON = "C:\\AWS\\resworkflow\\ssm_only_policy.json"; // Modify file location.
    public static final String failureResponse = "doc-example-resilient-architecture-failure-response";
    public static final String healthCheck = "doc-example-resilient-architecture-health-check";
    public static final String templateName = "doc-example-resilience-template" ;
    public static final String roleName = "doc-example-resilience-role";
    public static final String policyName = "doc-example-resilience-pol";
    public static final String profileName ="doc-example-resilience-prof" ;

    public static final String badCredsProfileName ="doc-example-resilience-prof-bc" ;

    public static final String targetGroupName = "doc-example-resilience-tg" ;
    public static final String autoScalingGroupName = "doc-example-resilience-group";
    public static final String lbName = "doc-example-resilience-lb" ;
    public static final String protocol = "HTTP" ;
    public static final int port = 80 ;

    public static final String DASHES = new String(new char[80]).replace("\0", "-");
    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner in = new Scanner(System.in);
        Database database = new Database();
        AutoScaler autoScaler = new AutoScaler();
        LoadBalancer loadBalancer = new LoadBalancer();

        System.out.println(DASHES);
        System.out.println("Welcome to the demonstration of How to Build and Manage a Resilient Service!");
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("A - SETUP THE RESOURCES");
        System.out.println("Press Enter when you're ready to start deploying resources.");
        in.nextLine();
        deploy(loadBalancer);
        System.out.println(DASHES);
        System.out.println(DASHES);
        System.out.println("B - DEMO THE RESILIENCE FUNCTIONALITY");
        System.out.println("Press Enter when you're ready.");
        in.nextLine();
        demo(loadBalancer);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("C - DELETE THE RESOURCES");
        System.out.println("""
        This concludes the demo of how to build and manage a resilient service.
        To keep things tidy and to avoid unwanted charges on your account, we can clean up all AWS resources
        that were created for this demo.
        """);

        System.out.println("\n Do you want to delete the resources (y/n)? ");
        String userInput = in.nextLine().trim().toLowerCase(); // Capture user input

        if (userInput.equals("y")) {
            // Delete resources here
            deleteResources(loadBalancer, autoScaler, database);
            System.out.println("Resources deleted.");
        } else {
            System.out.println("""
            Okay, we'll leave the resources intact.
            Don't forget to delete them when you're done with them or you might incur unexpected charges.
            """);
        }
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("The example has completed. ");
        System.out.println("\n Thanks for watching!");
        System.out.println(DASHES);
    }

    // Deletes the AWS resources used in this example.
    private static void deleteResources(LoadBalancer loadBalancer, AutoScaler autoScaler, Database database) throws IOException, InterruptedException {
        loadBalancer.deleteLoadBalancer(lbName);
        System.out.println("*** Wait 30 secs for resource to be deleted");
        TimeUnit.SECONDS.sleep(30);
        loadBalancer.deleteTargetGroup(targetGroupName);
        autoScaler.deleteAutoScaleGroup(autoScalingGroupName);
        autoScaler.deleteRolesPolicies(policyName, roleName, profileName );
        autoScaler.deleteTemplate(templateName);
        database.deleteTable(tableName);
    }

    private static void deploy(LoadBalancer loadBalancer) throws InterruptedException, IOException {
        Scanner in = new Scanner(System.in);
        System.out.println("""
            For this demo, we'll use the AWS SDK for Java (v2) to create several AWS resources
            to set up a load-balanced web service endpoint and explore some ways to make it resilient
            against various kinds of failures.

            Some of the resources create by this demo are:
            \t* A DynamoDB table that the web service depends on to provide book, movie, and song recommendations.
            \t* An EC2 launch template that defines EC2 instances that each contain a Python web server.
            \t* An EC2 Auto Scaling group that manages EC2 instances across several Availability Zones.
            \t* An Elastic Load Balancing (ELB) load balancer that targets the Auto Scaling group to distribute requests.
            """);

        System.out.println("Press Enter when you're ready.");
        in.nextLine();
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("Creating and populating a DynamoDB table named "+tableName);
        Database database = new Database();
        database.createTable(tableName, fileName);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println(""" 
        Creating an EC2 launch template that runs '{startup_script}' when an instance starts.
        This script starts a Python web server defined in the `server.py` script. The web server
        listens to HTTP requests on port 80 and responds to requests to '/' and to '/healthcheck'.
        For demo purposes, this server is run as the root user. In production, the best practice is to
        run a web server, such as Apache, with least-privileged credentials.
        
        The template also defines an IAM policy that each instance uses to assume a role that grants
        permissions to access the DynamoDB recommendation table and Systems Manager parameters
        that control the flow of the demo.
        """);

        LaunchTemplateCreator templateCreator = new LaunchTemplateCreator();
        templateCreator.createTemplate(policyFile, policyName, profileName, startScript, templateName, roleName);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("Creating an EC2 Auto Scaling group that maintains three EC2 instances, each in a different Availability Zone.");
        System.out.println("*** Wait 30 secs for the VPC to be created");
        TimeUnit.SECONDS.sleep(30);
        AutoScaler autoScaler = new AutoScaler();
        String[] zones = autoScaler.createGroup(3, templateName, autoScalingGroupName);

        System.out.println("""
        At this point, you have EC2 instances created. Once each instance starts, it listens for
        HTTP requests. You can see these instances in the console or continue with the demo.
        Press Enter when you're ready to continue.
        """);

        in.nextLine();
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("Creating variables that control the flow of the demo.");
        ParameterHelper paramHelper = new ParameterHelper();
        paramHelper.reset();
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("""
        Creating an Elastic Load Balancing target group and load balancer. The target group
        defines how the load balancer connects to instances. The load balancer provides a
        single endpoint where clients connect and dispatches requests to instances in the group.
        """);

        String vpcId = autoScaler.getDefaultVPC();
        List<Subnet> subnets = autoScaler.getSubnets(vpcId, zones);
        System.out.println("You have retrieved a list with "+subnets.size() +" subnets");
        String targetGroupArn = loadBalancer.createTargetGroup(protocol, port, vpcId, targetGroupName);
        String elbDnsName = loadBalancer.createLoadBalancer(subnets, targetGroupArn, lbName, port, protocol);
        autoScaler.attachLoadBalancerTargetGroup(autoScalingGroupName, targetGroupArn);
        System.out.println("Verifying access to the load balancer endpoint...");
        boolean wasSuccessul = loadBalancer.verifyLoadBalancerEndpoint(elbDnsName);
        if (!wasSuccessul) {
            System.out.println("Couldn't connect to the load balancer, verifying that the port is open...");
            CloseableHttpClient httpClient = HttpClients.createDefault();

            // Create an HTTP GET request to "http://checkip.amazonaws.com"
            HttpGet httpGet = new HttpGet("http://checkip.amazonaws.com");
            try {
                // Execute the request and get the response
                HttpResponse response = httpClient.execute(httpGet);

                // Read the response content.
                String ipAddress = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8).trim();

                // Print the public IP address.
                System.out.println("Public IP Address: " + ipAddress);
                GroupInfo groupInfo = autoScaler.verifyInboundPort(vpcId, port, ipAddress);
                if (!groupInfo.isPortOpen()) {
                    System.out.println("""
                    For this example to work, the default security group for your default VPC must
                    allow access from this computer. You can either add it automatically from this
                    example or add it yourself using the AWS Management Console.
                    """);

                    System.out.println("Do you want to add a rule to security group "+groupInfo.getGroupName() +" to allow");
                    System.out.println("inbound traffic on port "+port +" from your computer's IP address (y/n) ");
                    String ans = in.nextLine();
                    if ("y".equalsIgnoreCase(ans)) {
                        autoScaler.openInboundPort(groupInfo.getGroupName(), String.valueOf(port), ipAddress);
                        System.out.println("Security group rule added.");
                    } else {
                        System.out.println("No security group rule added.");
                    }
                }

            } catch (AutoScalingException e) {
                e.printStackTrace();
            }
        } else if (wasSuccessul) {
            System.out.println("Your load balancer is ready. You can access it by browsing to:");
            System.out.println("\t http://"+elbDnsName);
        } else {
            System.out.println("Couldn't get a successful response from the load balancer endpoint. Troubleshoot by");
            System.out.println("manually verifying that your VPC and security group are configured correctly and that");
            System.out.println( "you can successfully make a GET request to the load balancer.");
        }

        System.out.println("Press Enter when you're ready to continue with the demo.");
        in.nextLine();
    }
    // A method that controls the demo part of the Java program.
    public static void demo( LoadBalancer loadBalancer) throws IOException, InterruptedException {
        ParameterHelper paramHelper = new ParameterHelper();
        System.out.println("Read the ssm_only_policy.json file");
        String ssmOnlyPolicy = readFileAsString(ssmJSON);

        System.out.println("Resetting parameters to starting values for demo.");
        paramHelper.reset();

        System.out.println("""
        This part of the demonstration shows how to toggle different parts of the system
        to create situations where the web service fails, and shows how using a resilient
        architecture can keep the web service running in spite of these failures.
       
        At the start, the load balancer endpoint returns recommendations and reports that all targets are healthy.
       """);
        demoChoices(loadBalancer);

        System.out.println("""
        The web service running on the EC2 instances gets recommendations by querying a DynamoDB table.
        The table name is contained in a Systems Manager parameter named self.param_helper.table.
        To simulate a failure of the recommendation service, let's set this parameter to name a non-existent table.
       """);
        paramHelper.put(paramHelper.tableName, "this-is-not-a-table");

        System.out.println("""
        \nNow, sending a GET request to the load balancer endpoint returns a failure code. But, the service reports as
        healthy to the load balancer because shallow health checks don't check for failure of the recommendation service.
       """);
        demoChoices(loadBalancer);

        System.out.println("""
        Instead of failing when the recommendation service fails, the web service can return a static response.
        While this is not a perfect solution, it presents the customer with a somewhat better experience than failure.
        """);
        paramHelper.put(paramHelper.failureResponse, "static");

        System.out.println("""
        Now, sending a GET request to the load balancer endpoint returns a static response.
        The service still reports as healthy because health checks are still shallow.
        """);
        demoChoices(loadBalancer);

        System.out.println("Let's reinstate the recommendation service.");
        paramHelper.put(paramHelper.tableName, paramHelper.dyntable);

        System.out.println("""
        Let's also substitute bad credentials for one of the instances in the target group so that it can't
        access the DynamoDB recommendation table. We will get an instance id value. 
        """);

        LaunchTemplateCreator templateCreator = new LaunchTemplateCreator();
        AutoScaler autoScaler = new AutoScaler();

        //Create a new instance profile based on badCredsProfileName.
        templateCreator.createInstanceProfile(policyFile, policyName, badCredsProfileName, roleName);
        String badInstanceId = autoScaler.getBadInstance(autoScalingGroupName);
        System.out.println("The bad instance id values used for this demo is "+badInstanceId);

        String profileAssociationId = autoScaler.getInstanceProfile(badInstanceId);
        System.out.println("The association Id value is "+profileAssociationId);
        System.out.println("Replacing the profile for instance " + badInstanceId + " with a profile that contains bad credentials");
        autoScaler.replaceInstanceProfile(badInstanceId, badCredsProfileName, profileAssociationId) ;

        System.out.println("""
        Now, sending a GET request to the load balancer endpoint returns either a recommendation or a static response,
        depending on which instance is selected by the load balancer.
        """);

        demoChoices(loadBalancer);

        System.out.println("""
        Let's implement a deep health check. For this demo, a deep health check tests whether
        the web service can access the DynamoDB table that it depends on for recommendations. Note that
        the deep health check is only for ELB routing and not for Auto Scaling instance health.
        This kind of deep health check is not recommended for Auto Scaling instance health, because it
        risks accidental termination of all instances in the Auto Scaling group when a dependent service fails.
        """);

        System.out.println("""
        By implementing deep health checks, the load balancer can detect when one of the instances is failing
        and take that instance out of rotation.
        """);

        paramHelper.put(paramHelper.healthCheck, "deep");

        System.out.println("""
        Now, checking target health indicates that the instance with bad credentials 
        is unhealthy. Note that it might take a minute or two for the load balancer to detect the unhealthy 
        instance. Sending a GET request to the load balancer endpoint always returns a recommendation, because
        the load balancer takes unhealthy instances out of its rotation.
        """);

        demoChoices(loadBalancer);

        System.out.println("""
        Because the instances in this demo are controlled by an auto scaler, the simplest way to fix an unhealthy
        instance is to terminate it and let the auto scaler start a new instance to replace it.
        """);
        autoScaler.terminateInstance(badInstanceId);

        System.out.println("""
        Even while the instance is terminating and the new instance is starting, sending a GET
        request to the web service continues to get a successful recommendation response because
        the load balancer routes requests to the healthy instances. After the replacement instance
        starts and reports as healthy, it is included in the load balancing rotation.
        Note that terminating and replacing an instance typically takes several minutes, during which time you
        can see the changing health check status until the new instance is running and healthy.
        """);

        demoChoices(loadBalancer);
        System.out.println("If the recommendation service fails now, deep health checks mean all instances report as unhealthy.");
        paramHelper.put(paramHelper.tableName, "this-is-not-a-table");

        demoChoices(loadBalancer);
        paramHelper.reset();
    }

    public static void demoChoices(LoadBalancer loadBalancer) throws IOException, InterruptedException {
        String[] actions = {
            "Send a GET request to the load balancer endpoint.",
            "Check the health of load balancer targets.",
            "Go to the next part of the demo."
        };
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("-".repeat(88));
            System.out.println("See the current state of the service by selecting one of the following choices:");
            for (int i = 0; i < actions.length; i++) {
                System.out.println(i + ": " + actions[i]);
            }

            try {
                System.out.print("\nWhich action would you like to take? ");
                int choice = scanner.nextInt();
                System.out.println("-".repeat(88));

                switch (choice) {
                    case 0 -> {
                        System.out.println("Request:\n");
                        System.out.println("GET http://" + loadBalancer.getEndpoint(lbName));
                        CloseableHttpClient httpClient = HttpClients.createDefault();

                        // Create an HTTP GET request to the ELB.
                        HttpGet httpGet = new HttpGet("http://" + loadBalancer.getEndpoint(lbName));

                        // Execute the request and get the response.
                        HttpResponse response = httpClient.execute(httpGet);
                        int statusCode = response.getStatusLine().getStatusCode();
                        System.out.println("HTTP Status Code: " + statusCode);

                        // Display the JSON response
                        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                        StringBuilder jsonResponse = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            jsonResponse.append(line);
                        }
                        reader.close();

                        // Print the formatted JSON response.
                        System.out.println("Full Response:\n");
                        System.out.println(jsonResponse.toString());

                        // Close the HTTP client.
                        httpClient.close();


                    }
                    case 1 -> {
                        System.out.println("\nChecking the health of load balancer targets:\n");
                        List<TargetHealthDescription> health = loadBalancer.checkTargetHealth(targetGroupName);
                        for (TargetHealthDescription target : health) {
                            System.out.printf("\tTarget %s on port %d is %s%n", target.target().id(), target.target().port(), target.targetHealth().stateAsString());
                        }
                        System.out.println("""
                            Note that it can take a minute or two for the health check to update
                            after changes are made.
                            """);
                    }
                    case 2 -> {
                        System.out.println("\nOkay, let's move on.");
                        System.out.println("-".repeat(88));
                        return; // Exit the method when choice is 2
                    }
                    default -> System.out.println("You must choose a value between 0-2. Please select again.");
                }

            } catch (java.util.InputMismatchException e) {
                System.out.println("Invalid input. Please select again.");
                scanner.nextLine(); // Clear the input buffer.
            }
        }
    }

    public static String readFileAsString(String filePath) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(filePath));
        return new String(bytes);
    }
}
// snippet-end:[javav2.example_code.workflow.ResilientService_Runner]