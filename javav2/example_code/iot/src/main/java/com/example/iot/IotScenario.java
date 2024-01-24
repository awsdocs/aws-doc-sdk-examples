//snippet-sourcedescription:[IotScenario.java demonstrates how to perform device management use cases using the IotClient.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:AWS IoT]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.iot;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iot.model.Action;
import software.amazon.awssdk.services.iot.model.AttachThingPrincipalRequest;
import software.amazon.awssdk.services.iot.model.AttachThingPrincipalResponse;
import software.amazon.awssdk.services.iot.model.AttributePayload;
import software.amazon.awssdk.services.iot.model.CreateKeysAndCertificateResponse;
import software.amazon.awssdk.services.iot.model.CreateThingRequest;
import software.amazon.awssdk.services.iot.model.CreateTopicRuleRequest;
import software.amazon.awssdk.services.iot.model.CreateTopicRuleResponse;
import software.amazon.awssdk.services.iot.model.DeleteCertificateRequest;
import software.amazon.awssdk.services.iot.model.CreateThingResponse;
import software.amazon.awssdk.services.iot.model.DeleteThingRequest;
import software.amazon.awssdk.services.iot.model.DeleteTopicRuleRequest;
import software.amazon.awssdk.services.iot.model.DescribeEndpointRequest;
import software.amazon.awssdk.services.iot.model.DescribeEndpointResponse;
import software.amazon.awssdk.services.iot.model.DescribeThingRequest;
import software.amazon.awssdk.services.iot.model.DescribeThingResponse;
import software.amazon.awssdk.services.iot.model.DetachThingPrincipalRequest;
import software.amazon.awssdk.services.iot.model.DisableTopicRuleRequest;
import software.amazon.awssdk.services.iot.model.IotException;
import software.amazon.awssdk.services.iot.model.ListTopicRulesRequest;
import software.amazon.awssdk.services.iot.model.ListTopicRulesResponse;
import software.amazon.awssdk.services.iot.model.SnsAction;
import software.amazon.awssdk.services.iot.model.TopicRuleListItem;
import software.amazon.awssdk.services.iot.model.TopicRulePayload;
import software.amazon.awssdk.services.iot.model.UpdateThingRequest;
import software.amazon.awssdk.services.iotdataplane.IotDataPlaneClient;
import software.amazon.awssdk.services.iotdataplane.model.GetThingShadowRequest;
import software.amazon.awssdk.services.iotdataplane.model.GetThingShadowResponse;
import software.amazon.awssdk.services.iotdataplane.model.UpdateThingShadowRequest;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 * This Java example performs these tasks:
 *
 * 1. Creates an AWS IoT Thing.
 * 2. Generate a device certificate.
 * 3. Attach the certificate to the AWS IoT Thing.
 * 4. Update an AWS IoT Thing with Attributes.
 * 5. Get an AWS IoT Endpoint.
 * 6 Detach the certificate from the AWS IoT thing.
 * 7. Delete the certificate.
 * 8. Updates the shadow for the specified thing.
 * 9. Write out the state information, in JSON format.
 * 10. List rules
 * 11. Delete Thing.
 */
public class IotScenario {
    public static final String DASHES = new String(new char[80]).replace("\0", "-");
    private static final String TOPIC = "your-iot-topic";
    public static void main(String[] args) {
        final String usage = """

            Usage:
                <thingName>

            Where:
                thingName - The name of the AWS IoT Thing.
                roleARN - The ARN of an IAM role that has permission to work with AWS IOT.  
                ruleName  - The name of the AWS IoT rule.
            """;

        //   if (args.length != 2) {
        //        System.out.println(usage);
        //       System.exit(1);
        //  }

        // Specify the thing name
        String thingName = "foo125";
        String roleARN = "arn:aws:iam::814548047983:role/AssumeRoleSNS";
        String ruleName = "YourRuleName";

        IotClient iotClient = IotClient.builder()
            .region(Region.US_EAST_1)
            .build();

        System.out.println(DASHES);
        System.out.println("Welcome to the AWS IoT example scenario.");
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("1. Create an AWS IoT Thing.");
        System.out.println("""
            An AWS IoT Thing represents a virtual entity in the AWS IoT service that can be associated with a physical device.
            """);
        createIoTThing(iotClient, thingName);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("2. Generate a device certificate.");
        System.out.println("""
            A device certificate performs a role in securing the communication between devices (Things) and the AWS IoT platform.
            """);
        String certificateArn = createCertificate(iotClient);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("3. Attach the certificate to the AWS IoT Thing.");
        attachCertificateToThing(iotClient, thingName, certificateArn);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("4. Update an AWS IoT Thing with Attributes.");
        System.out.println("""
            Attributes are key-value pairs that can be searchable.
            """);
        updateThing(iotClient, thingName);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("5 Return a unique endpoint specific to the Amazon Web Services account.");
        String endpointUrl = describeEndpoint(iotClient);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("6. Detach amd remove the certificate.");
        detachThingPrincipal(iotClient, thingName, certificateArn);
        deleteCertificate(iotClient, certificateArn);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("7. Create an IoT shadow that refers to a digital representation or virtual twin of a physical IoT device");
        IotDataPlaneClient iotPlaneClient = IotDataPlaneClient.builder()
            .region(Region.US_EAST_1)
            .endpointOverride(URI.create(endpointUrl))
            .build();

        updateShawdowThing(iotPlaneClient, thingName);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("8. Write out the state information, in JSON format.");
        getPayload(iotPlaneClient, thingName);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("9. Creates a rule");
        System.out.println("""
        Creates a rule that is an administrator-level action. 
        Any user who has permission to create rules will be able to access data processed by the rule.
        """);
        createIoTRule(iotClient, roleARN, ruleName);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("10. List your rules.");
        listIoTRules(iotClient);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("12. Delete the AWS IoT Thing.");
        deleteIoTThing(iotClient, thingName);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("The AWS IoT Scenario has successfully completed.");
        System.out.println(DASHES);
    }

    private static void listIoTRules(IotClient iotClient) {
        ListTopicRulesRequest listTopicRulesRequest = ListTopicRulesRequest.builder().build();
        ListTopicRulesResponse listTopicRulesResponse = iotClient.listTopicRules(listTopicRulesRequest);

        System.out.println("List of IoT Rules:");
        List<TopicRuleListItem> ruleList = listTopicRulesResponse.rules();
        for (TopicRuleListItem rule : ruleList) {
            System.out.println("Rule Name: " + rule.ruleName());
            System.out.println("Rule ARN: " + rule.ruleArn());
            System.out.println("--------------");
        }
    }

    private static void createIoTRule(IotClient iotClient, String roleARN, String ruleName) {
        try {
            // Set the rule SQL statement
            String sql = "SELECT * FROM '" + TOPIC + "'";

            // Set the action to be taken when the rule is triggered
            String action = "arn:aws:sns:us-east-1:814548047983:scott1111";


            SnsAction action1 = SnsAction.builder()
                .targetArn(action)
                .roleArn(roleARN)
                .build();

            // Create the action
            Action myAction = Action.builder()
                .sns(action1)
                .build();

            // Create the topic rule payload
            TopicRulePayload topicRulePayload = TopicRulePayload.builder()
                .sql(sql)
                .actions(myAction)
                .build();

            // Create the topic rule request
            CreateTopicRuleRequest topicRuleRequest = CreateTopicRuleRequest.builder()
                .ruleName(ruleName)
                .topicRulePayload(topicRulePayload)
                .build();

            // Create the rule
            iotClient.createTopicRule(topicRuleRequest);
            System.out.println("IoT Rule created successfully. ");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getPayload(IotDataPlaneClient iotPlaneClient, String thingName) {
        try {
            GetThingShadowRequest getThingShadowRequest = GetThingShadowRequest.builder()
                .thingName(thingName)
                .build();

            GetThingShadowResponse getThingShadowResponse = iotPlaneClient.getThingShadow(getThingShadowRequest);

            // Extracting payload from response
            SdkBytes payload = getThingShadowResponse.payload();
            String payloadString = payload.asUtf8String();

            System.out.println("Received Shadow Data: " + payloadString);

        } catch (IotException e) {
            System.err.println("Error reading from IoT Thing: " + e.getMessage());
        }

    }

    public static void updateShawdowThing(IotDataPlaneClient iotPlaneClient, String thingName) {
        try {
            // Create Thing Shadow State Document
            String stateDocument = "{\"state\":{\"reported\":{\"temperature\":25, \"humidity\":50}}}";
            SdkBytes data= SdkBytes.fromString(stateDocument, StandardCharsets.UTF_8 );

            // Update Thing Shadow Request
            UpdateThingShadowRequest updateThingShadowRequest = UpdateThingShadowRequest.builder()
                .thingName(thingName)
                .payload(data)
                .build();

            // Update Thing Shadow
            iotPlaneClient.updateThingShadow(updateThingShadowRequest);
            System.out.println("Thing Shadow updated successfully.");

        } catch (Exception e) {
            System.err.println("Error updating Thing Shadow: " + e.getMessage());
        }
    }

    public static void updateThing(IotClient iotClient, String thingName) {
        // Specify the new attribute values
        String newLocation = "Office";
        String newFirmwareVersion = "v2.0";

        Map<String, String> attMap = new HashMap<>();
        attMap.put("location", newLocation);
        attMap.put("firmwareVersion", newFirmwareVersion);

        // Build the update request
        AttributePayload attributePayload = AttributePayload.builder()
            .attributes(attMap)
            .build();

        UpdateThingRequest updateThingRequest = UpdateThingRequest.builder()
            .thingName(thingName)
            .attributePayload(attributePayload)
            .build();

        try {
            // Update the IoT Thing attributes
            iotClient.updateThing(updateThingRequest);
            System.out.println("Thing attributes updated successfully.");

        } catch (IotException e) {
            System.err.println("Error updating Thing attributes: " + e.getMessage());
        }
    }

    public static String describeEndpoint(IotClient iotClient) {
       // Describe the endpoint
    DescribeEndpointResponse endpointResponse = iotClient.describeEndpoint(
        DescribeEndpointRequest.builder().build());

    // Get the endpoint URL
    String endpointUrl = endpointResponse.endpointAddress();
    String exString = getValue(endpointUrl);
    String fullEndpoint = "https://"+exString+"-ats.iot.us-east-1.amazonaws.com";

    System.out.println("Full Endpoint URL: "+fullEndpoint);
    return fullEndpoint;
    }

    public static void detachThingPrincipal(IotClient iotClient, String thingName, String certificateArn){
        DetachThingPrincipalRequest thingPrincipalRequest = DetachThingPrincipalRequest.builder()
            .principal(certificateArn)
            .thingName(thingName)
            .build();
        iotClient.detachThingPrincipal(thingPrincipalRequest);
        System.out.println(certificateArn +" was successfully removed from " +thingName);
    }

    public static void deleteCertificate(IotClient iotClient, String certificateArn ) {
        DeleteCertificateRequest certificateProviderRequest = DeleteCertificateRequest.builder()
            .certificateId(extractCertificateId(certificateArn))
            .build();

        iotClient.deleteCertificate(certificateProviderRequest);
        System.out.println(certificateArn +" was successfully deleted.");
    }


    // Get the cert Id  from the Cert ARN value.
    private static String extractCertificateId(String certificateArn) {
        // Example ARN: arn:aws:iot:region:account-id:cert/certificate-id
        String[] arnParts = certificateArn.split(":");
        String certificateIdPart = arnParts[arnParts.length - 1];
        return certificateIdPart.substring(certificateIdPart.lastIndexOf("/") + 1);
    }


    public static String createCertificate(IotClient iotClient) {
        try {
            // Create keys and certificate
            CreateKeysAndCertificateResponse response = iotClient.createKeysAndCertificate();

            // Extract key, certificate, and certificate ARN
            String privateKey = response.keyPair().privateKey();
            String certificatePem = response.certificatePem();
            String certificateArn = response.certificateArn();

            // Print the details
            System.out.println("Private Key:");
            System.out.println(privateKey);
            System.out.println("\nCertificate:");
            System.out.println(certificatePem);
            System.out.println("\nCertificate ARN:");
            System.out.println(certificateArn);
            return certificateArn;

        } catch (IotException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return "";
    }

    private static void attachCertificateToThing(IotClient iotClient, String thingName, String certificateArn) {
        // Attach the certificate to the thing
        AttachThingPrincipalRequest principalRequest = AttachThingPrincipalRequest.builder()
            .thingName(thingName)
            .principal(certificateArn)
            .build();

        AttachThingPrincipalResponse attachResponse = iotClient.attachThingPrincipal(principalRequest);

        // Verify the attachment was successful.
        if (attachResponse.sdkHttpResponse().isSuccessful()) {
            System.out.println("Certificate attached to Thing successfully.");

            // Print additional information about the Thing.
            describeThing(iotClient, thingName);
        } else {
            System.err.println("Failed to attach certificate to Thing. HTTP Status Code: " +
                attachResponse.sdkHttpResponse().statusCode());
        }
    }

    private static void describeThing(IotClient iotClient, String thingName) {
        // Describe the Thing to get more information.
        DescribeThingRequest thingRequest = DescribeThingRequest.builder()
            .thingName(thingName)
            .build() ;

        DescribeThingResponse describeResponse = iotClient.describeThing(thingRequest);

        // Print Thing details.
        System.out.println("Thing Details:");
        System.out.println("Thing Name: " + describeResponse.thingName());
        System.out.println("Thing ARN: " + describeResponse.thingArn());
        // Add more details as needed
    }

     public static void deleteIoTThing(IotClient iotClient, String thingName) {
         try {
             // Create Thing Request
             DeleteThingRequest deleteThingRequest = DeleteThingRequest.builder()
                 .thingName(thingName)
                 .build();

             // Delete Thing Response
             iotClient.deleteThing(deleteThingRequest);

             // Print ARN of the created thing
             System.out.println("Deleted Thing " + thingName);

         } catch (IotException e) {
             System.err.println(e.awsErrorDetails().errorMessage());
             System.exit(1);
         }
     }


    // snippet-start:[iot.java2.create_thing.main]
    public static void createIoTThing(IotClient iotClient, String thingName) {
        try {
            // Create Thing Request.
            CreateThingRequest createThingRequest = CreateThingRequest.builder()
                .thingName(thingName)
                .build();

            CreateThingResponse createThingResponse = iotClient.createThing(createThingRequest);

            // Print ARN of the created thing.
            System.out.println("Created Thing ARN: " + createThingResponse.thingArn());

        } catch (IotException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[iot.java2.create_thing.main]

    private static String getValue(String input) {

        // Define a regular expression pattern for extracting the subdomain
        Pattern pattern = Pattern.compile("^(.*?)\\.iot\\.us-east-1\\.amazonaws\\.com");

        // Match the pattern against the input string
        Matcher matcher = pattern.matcher(input);

        // Check if a match is found
        if (matcher.find()) {
            // Extract the subdomain from the first capturing group
            String subdomain = matcher.group(1);
            System.out.println("Extracted subdomain: " + subdomain);
            return  subdomain ;
        } else {
            System.out.println("No match found");
        }
        return "" ;
    }
}
