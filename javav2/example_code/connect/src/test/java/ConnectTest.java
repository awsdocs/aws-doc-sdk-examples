/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.connect.CreateInstance;
import com.example.connect.DeleteInstance;
import com.example.connect.DescribeContact;
import com.example.connect.DescribeInstance;
import com.example.connect.DescribeInstanceAttribute;
import com.example.connect.GetContactAttributes;
import com.example.connect.ListInstances;
import com.example.connect.ListPhoneNumbers;
import com.example.connect.ListUsers;
import com.example.connect.SearchQueues;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.connect.ConnectClient;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import static org.junit.jupiter.api.Assertions.assertFalse;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ConnectTest {
    private static ConnectClient connectClient;
    private static String instanceAlias = "";
    private static String instanceId = "" ;
    private static String contactId = "" ;
    private static String existingInstanceId = "" ;
    private static String targetArn = "" ;

    @BeforeAll
    public static void setUp() {
        connectClient = ConnectClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        try (InputStream input = ConnectTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            // Load a properties file.
            prop.load(input);
            instanceAlias = prop.getProperty("instanceAlias");
            contactId = prop.getProperty("contactId");
            existingInstanceId = prop.getProperty("existingInstanceId");
            targetArn = prop.getProperty("targetArn");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void createInstance() {
        instanceId = CreateInstance.createConnectInstance(connectClient, instanceAlias);
        assertFalse(instanceId.isEmpty());
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void describeInstance() throws InterruptedException {
        DescribeInstance.describeSpecificInstance(connectClient, instanceId);
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void listInstances() {
        ListInstances.listAllInstances(connectClient);
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void deleteInstance() {
        DeleteInstance.deleteSpecificInstance(connectClient, instanceId);
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void describeContact() {
        DescribeContact.describeSpecificContact(connectClient, existingInstanceId, contactId);
        System.out.println("Test 5 passed");
   }

    @Test
    @Order(6)
    public void describeInstanceAttribute() {
        DescribeInstanceAttribute.describeAttribute(connectClient, existingInstanceId);
        System.out.println("Test 6 passed");
    }

    @Test
    @Order(7)
    public void getContactAttributes() {
        GetContactAttributes.getContactAttrs(connectClient, existingInstanceId, contactId);
        System.out.println("Test 7 passed");
    }

    @Test
    @Order(8)
    public void listPhoneNumbers() {
        ListPhoneNumbers.getPhoneNumbers(connectClient, targetArn);
        System.out.println("Test 8 passed");
    }

    @Test
    @Order(9)
    public void listUsers() {
        ListUsers.getUsers(connectClient, existingInstanceId);
        System.out.println("Test 9 passed");
    }

    @Test
    @Order(10)
    public void searchQueues() {
        SearchQueues.searchQueue(connectClient, existingInstanceId);
        System.out.println("Test 10 passed");
    }
}
