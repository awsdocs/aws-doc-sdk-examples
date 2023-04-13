/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.eventbridge.EventbridgeMVP;
import com.example.eventbridge.HelloEventBridge;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sns.SnsClient;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EventBridgeTest {

    private static  EventBridgeClient eventBrClient;
    private static Region region;
    private static String roleNameSc = "";
    private static String bucketNameSc = "";
    private static String topicNameSc = "";
    private static String eventRuleNameSc = "";


    public static final String DASHES = new String(new char[80]).replace("\0", "-");

    @BeforeAll
    public static void setUp() throws IOException {
        region = Region.US_WEST_2;
        eventBrClient = EventBridgeClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        try (InputStream input = EventBridgeTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            prop.load(input);

            // Populate the data members required for all tests.
            roleNameSc = prop.getProperty("roleNameSc");
            bucketNameSc = prop.getProperty("bucketNameSc");
            topicNameSc = prop.getProperty("topicNameSc");
            eventRuleNameSc = prop.getProperty("eventRuleNameSc");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingService_thenNotNull() {
        assertNotNull(eventBrClient);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void helloEventBridge() {
        HelloEventBridge.listBuses(eventBrClient);
        System.out.println("Test 5 passed");
    }

    @Test
    @Order(3)
    public void eventBridgeTest() throws InterruptedException, IOException {
        String polJSON = "{" +
            "\"Version\": \"2012-10-17\"," +
            "\"Statement\": [{" +
            "\"Effect\": \"Allow\"," +
            "\"Principal\": {" +
            "\"Service\": \"events.amazonaws.com\"" +
            "}," +
            "\"Action\": \"sts:AssumeRole\"" +
            "}]" +
            "}";

        Scanner sc = new Scanner(System.in);
        Region region = Region.US_EAST_1;
        S3Client s3Client = S3Client.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        Region regionGl = Region.AWS_GLOBAL;
        IamClient iam = IamClient.builder()
            .region(regionGl)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        SnsClient snsClient = SnsClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        System.out.println(DASHES);
        System.out.println("1. Create an AWS Identity and Access Management (IAM) role to use with Amazon EventBridge.");
        String roleArn = EventbridgeMVP.createIAMRole(iam, roleNameSc, polJSON);
        assertFalse(roleArn.isEmpty());
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("2. Create an S3 bucket with EventBridge events enabled.");
        if (EventbridgeMVP.checkBucket(s3Client, bucketNameSc)) {
            System.out.println("Bucket "+ bucketNameSc +" already exists. Ending this scenario.");
            System.exit(1);
        }

        EventbridgeMVP.createBucket(s3Client, bucketNameSc);
        Thread.sleep(3000);
        EventbridgeMVP.setBucketNotification(s3Client, bucketNameSc);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("3. Create a rule that triggers when an object is uploaded to Amazon S3.");
        Thread.sleep(10000);
        EventbridgeMVP.addEventRule(eventBrClient, roleArn, bucketNameSc, eventRuleNameSc);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("4. List rules on the event bus.");
        EventbridgeMVP.listRules(eventBrClient);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("5. Create a new SNS topic for testing and let the user subscribe to the topic.");
        String topicArn = EventbridgeMVP.createSnsTopic(snsClient, topicNameSc);
        assertFalse(topicArn.isEmpty());
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("6. Add a target to the rule that sends an email to the specified topic");
        System.out.println("Enter your email to subscribe to the Amazon SNS topic:");
        String email = sc.nextLine();
        EventbridgeMVP.subEmail(snsClient, topicArn, email);
        System.out.println("Use the link in the email you received to confirm your subscription. Then, press Enter to continue.");
        sc.nextLine();
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("7. Create an EventBridge event that sends an email when an Amazon S3 object is created.");
        EventbridgeMVP.addSnsEventRule(eventBrClient, eventRuleNameSc, topicArn, topicNameSc, eventRuleNameSc, bucketNameSc);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println(" 8. List Targets.");
        EventbridgeMVP.listTargets(eventBrClient, eventRuleNameSc);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println(" 9. List the rules for the same target.");
        EventbridgeMVP.listTargetRules(eventBrClient, topicArn);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println(" 10. Trigger the rule by uploading a file to the S3 bucket.");
        System.out.println("Press Enter to continue.");
        sc.nextLine();
        EventbridgeMVP.uploadTextFiletoS3(s3Client, bucketNameSc);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("11. Disable a specific rule.");
        EventbridgeMVP.changeRuleState(eventBrClient, eventRuleNameSc, false);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("12. Check and print the state of the rule.");
        EventbridgeMVP.checkRule(eventBrClient, eventRuleNameSc);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("13. Add a transform to the rule to change the text of the email.");
        EventbridgeMVP.updateSnsEventRule(eventBrClient, topicArn, eventRuleNameSc);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("14. Enable a specific rule.");
        EventbridgeMVP.changeRuleState(eventBrClient, eventRuleNameSc, true);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println(" 15. Trigger the updated rule by uploading a file to the S3 bucket.");
        System.out.println("Press Enter to continue.");
        sc.nextLine();
        EventbridgeMVP.uploadTextFiletoS3(s3Client, bucketNameSc);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println(" 16. Update the rule to be a custom rule pattern.");
        EventbridgeMVP.updateToCustomRule(eventBrClient, eventRuleNameSc);
        System.out.println("Updated event rule "+eventRuleNameSc +" to use a custom pattern.");
        EventbridgeMVP.updateCustomRuleTargetWithTransform(eventBrClient, topicArn, eventRuleNameSc);
        System.out.println("Updated event target "+topicArn +".");
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("17. Sending an event to trigger the rule. This will trigger a subscription email.");
        EventbridgeMVP.triggerCustomRule(eventBrClient, email);
        System.out.println("Events have been sent. Press Enter to continue.");
        sc.nextLine();
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("18. Clean up resources.");
        System.out.println("Do you want to clean up resources (y/n)");
        String ans = sc.nextLine();
        if (ans.compareTo("y") == 0) {
            EventbridgeMVP.cleanupResources(eventBrClient, snsClient, s3Client, iam, topicArn, eventRuleNameSc, bucketNameSc, roleNameSc );
        } else {
            System.out.println("The resources will not be cleaned up. ");
        }
        System.out.println(DASHES);
    }
}
