// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.identitystore.*;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.identitystore.IdentitystoreClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import java.io.*;
import java.util.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IdentitystoreServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(IdentitystoreServiceTest.class);
    private static IdentitystoreClient identitystore;
    private static String identitystoreId = "";
    private static String groupName = "";
    private static String groupDesc = "";
    private static String groupId = "";
    private static String userName = "";
    private static String givenName = "";
    private static String familyName = "";
    private static String userId = "";
    private static String membershipId = "";

    @BeforeAll
    public static void setUp() throws IOException {
        identitystore = IdentitystoreClient.builder()
                .build();

        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);

        // Populate the data members required for all tests
        identitystoreId = values.getIdentitystoreId();
        groupName = values.getGroupName();
        groupDesc = values.getGroupDesc();
        userName = values.getUserName();
        givenName = values.getGivenName();
        familyName = values.getFamilyName();
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testCreateGroup() {
        String result2 = CreateGroup.createGroup(identitystore, identitystoreId, groupName, groupDesc);
        assertTrue(!result2.isEmpty());
        logger.info("\n Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testGetGroupId() {
        groupId = GetGroupId.getGroupId(identitystore, identitystoreId, "DisplayName", groupName);
        assertTrue(!groupId.isEmpty());
        logger.info("\n Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testDescribeGroup() {
        String result4 = DescribeGroup.describeGroup(identitystore, identitystoreId, groupId);
        assertTrue(!result4.isEmpty());
        logger.info("\n Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void testUpdateGroup() {
        String result5 = UpdateGroup.updateGroup(identitystore, identitystoreId, groupId, "Description",
                "TestingUpdateAPI");
        assertTrue(!result5.isEmpty());
        logger.info("\n Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void testListGroups() {
        int result6 = ListGroups.listGroups(identitystore, identitystoreId);
        assertTrue(result6 >= 0);
        logger.info("\n Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void testCreateUser() {
        String result7 = CreateUser.createUser(identitystore, identitystoreId, userName, givenName, familyName);
        assertTrue(!result7.isEmpty());
        logger.info("\n Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void testGetUserId() {
        userId = GetUserId.getUserId(identitystore, identitystoreId, "UserName", userName);
        assertTrue(!userId.isEmpty());
        logger.info("\n Test 7 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
    public void testDescribeUser() {
        String result9 = DescribeUser.describeUser(identitystore, identitystoreId, userId);
        assertTrue(!result9.isEmpty());
        logger.info("\n Test 8 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(9)
    public void testUpdateUser() {
        String result10 = UpdateUser.updateUser(identitystore, identitystoreId, userId, "displayName",
                "TestingUpdateAPI");
        assertTrue(!result10.isEmpty());
        logger.info("\n Test 9 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(10)
    public void testListUsers() {
        int result11 = ListUsers.listUsers(identitystore, identitystoreId);
        assertTrue(result11 >= 0);
        logger.info("\n Test 10 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(11)
    public void testCreateGroupMembership() {
        String result12 = CreateGroupMembership.createGroupMembership(identitystore, identitystoreId, groupId, userId);
        assertTrue(!result12.isEmpty());
        logger.info("\n Test 11 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(12)
    public void testGetGroupMembershipId() {
        membershipId = GetGroupMembershipId.getGroupMembershipId(identitystore, identitystoreId, groupId, userId);
        assertTrue(!membershipId.isEmpty());
        logger.info("\n Test 12 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(13)
    public void testDescribeGroupMembership() {
        String result14 = DescribeGroupMembership.describeGroupMembershipId(identitystore, identitystoreId,
                membershipId);
        assertTrue(!result14.isEmpty());
        logger.info("\n Test 13 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(14)
    public void testIsMemberInGroups() {
        List<String> groupIdList = new ArrayList<>();
        groupIdList.add(groupId);
        String result15 = IsMemberInGroups.isMemberInGroups(identitystore, identitystoreId, userId, groupIdList);
        assertTrue(!result15.isEmpty());
        logger.info("\n Test 14 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(15)
    public void testListGroupMemberships() {
        int result16 = ListGroupMemberships.listGroupMemberships(identitystore, identitystoreId, groupId);
        assertTrue(result16 >= 0);
        logger.info("\n Test 15 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(16)
    public void testListGroupMembershipsForMember() {
        int result17 = ListGroupMembershipsForMember.listGroupMembershipsForMember(identitystore, identitystoreId,
                userId);
        assertTrue(result17 >= 0);
        logger.info("\n Test 16 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(17)
    public void testDeleteGroupMembership() {
        String result18 = DeleteGroupMembership.deleteGroupMembership(identitystore, identitystoreId, membershipId);
        assertTrue(!result18.isEmpty());
        logger.info("\n Test 17 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(18)
    public void testDeleteUser() {
        String result19 = DeleteUser.deleteUser(identitystore, identitystoreId, userId);
        assertTrue(!result19.isEmpty());
        logger.info("\n Test 18 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(19)
    public void testDeleteGroup() {
        String result20 = DeleteGroup.deleteGroup(identitystore, identitystoreId, groupId);
        assertTrue(!result20.isEmpty());
        logger.info("\n Test 19 passed");
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
                .region(Region.US_EAST_1)
                .build();
        String secretName = "test/identitystore";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/firehose (an AWS Secrets Manager secret)")
    class SecretValues {
        private String identitystoreId;
        private String groupName;
        private String groupDesc;
        private String userName;
        private String givenName;
        private String familyName;

        public String getIdentitystoreId() {
            return identitystoreId;
        }

        public String getGroupName() {
            return groupName;
        }

        public String getGroupDesc() {
            return groupDesc;
        }

        public String getUserName() {
            return userName;
        }

        public String getGivenName() {
            return givenName;
        }

        public String getFamilyName() {
            return familyName;
        }
    }
}
