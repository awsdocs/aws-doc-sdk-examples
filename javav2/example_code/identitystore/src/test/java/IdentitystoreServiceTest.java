/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.identitystore.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import software.amazon.awssdk.services.identitystore.IdentitystoreClient;
import software.amazon.awssdk.services.identitystore.model.IdentitystoreException;
import software.amazon.awssdk.services.identitystore.model.Group;


import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IdentitystoreServiceTest {

    private static IdentitystoreClient identitystore;
    private static String identitystoreId="";
    private static String groupName="";
    private static String groupDesc="";
    private static String groupId="";
    private static String userName="";
    private static String givenName="";
    private static String familyName="";
    private static String userId="";
    private static String membershipId="";

    @BeforeAll
    public static void setUp() throws IOException {
        identitystore =  IdentitystoreClient.builder()
                        .build();


        try (InputStream input = IdentitystoreServiceTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();
            prop.load(input);
            // Populate the data members required for all tests
            identitystoreId = prop.getProperty("identitystoreId");
            groupName = prop.getProperty("groupName");
            groupDesc = prop.getProperty("groupDesc");
            groupId = prop.getProperty("groupId");
            userName = prop.getProperty("userName");
            givenName = prop.getProperty("givenName");
            familyName = prop.getProperty("familyName");
            userId = prop.getProperty("userId");
            membershipId = prop.getProperty("membershipId");

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSService_thenNotNull() {
        assertNotNull(identitystore);
        System.out.printf("\n Test 1 passed");
    }

    @Test
    @Order(2)
    public void CreateGroup() {
        String result2 = CreateGroup.createGroup(identitystore, identitystoreId, groupName, groupDesc);
        assertTrue(!result2.isEmpty());
        System.out.println("\n Test 2 passed");
    }

    @Test
    @Order(3)
    public void GetGroupId() {
        groupId = GetGroupId.getGroupId(identitystore, identitystoreId, "DisplayName", groupName);
        assertTrue(!groupId.isEmpty());
        System.out.println("\n Test 3 passed");
    }
  
    @Test
    @Order(4)
    public void DescribeGroup() {
        String result4 = DescribeGroup.describeGroup(identitystore, identitystoreId, groupId);
        assertTrue(!result4.isEmpty());
        System.out.println("\n Test 4 passed");
    }

    @Test
    @Order(5)
    public void UpdateGroup() {
        String result5 = UpdateGroup.updateGroup(identitystore, identitystoreId, groupId, "Description", "TestingUpdateAPI");
        assertTrue(!result5.isEmpty());
        System.out.println("\n Test 5 passed");
    }

    @Test
    @Order(6)
    public void ListGroups() {
        int result6 = ListGroups.listGroups(identitystore, identitystoreId);
        assertTrue(result6>=0);
        System.out.println("\n Test 6 passed");
    }

    @Test
    @Order(7)
    public void CreateUser() {
        String result7 = CreateUser.createUser(identitystore, identitystoreId, userName, givenName, familyName);
        assertTrue(!result7.isEmpty());
        System.out.println("\n Test 7 passed");
    }

    @Test
    @Order(8)
    public void GetUserId() {
        userId = GetUserId.getUserId(identitystore, identitystoreId, "UserName", userName);
        assertTrue(!userId.isEmpty());
        System.out.println("\n Test 8 passed");
    }

    @Test
    @Order(9)
    public void DescribeUser() {
        String result9 = DescribeUser.describeUser(identitystore, identitystoreId, userId);
        assertTrue(!result9.isEmpty());
        System.out.println("\n Test 9 passed");
    }

    @Test
    @Order(10)
    public void UpdateUser() {
        String result10 = UpdateUser.updateUser(identitystore, identitystoreId, userId, "displayName", "TestingUpdateAPI");
        assertTrue(!result10.isEmpty());
        System.out.println("\n Test 10 passed");
    }

    @Test
    @Order(11)
    public void ListUsers() {
        int result11 = ListUsers.listUsers(identitystore, identitystoreId);
        assertTrue(result11>=0);
        System.out.println("\n Test 11 passed");
    }

    @Test
    @Order(12)
    public void CreateGroupMembership() {
        String result12 = CreateGroupMembership.createGroupMembership(identitystore, identitystoreId, groupId, userId);
        assertTrue(!result12.isEmpty());
        System.out.println("\n Test 12 passed");
    }

    @Test
    @Order(13)
    public void GetGroupMembershipId() {
        membershipId = GetGroupMembershipId.getGroupMembershipId(identitystore, identitystoreId, groupId, userId);
        assertTrue(!membershipId.isEmpty());
        System.out.println("\n Test 13 passed");
    }

    @Test
    @Order(14)
    public void DescribeGroupMembership() {
        String result14 = DescribeGroupMembership.describeGroupMembershipId(identitystore, identitystoreId, membershipId);
        assertTrue(!result14.isEmpty());
        System.out.println("\n Test 14 passed");
    }

    @Test
    @Order(15)
    public void IsMemberInGroups() {
        List<String> groupIdList = new ArrayList<>();
        groupIdList.add(groupId);
        String result15 = IsMemberInGroups.isMemberInGroups(identitystore, identitystoreId, userId, groupIdList);
        assertTrue(!result15.isEmpty());
        System.out.println("\n Test 15 passed");
    }

    @Test
    @Order(16)
    public void ListGroupMemberships() {
        int result16 = ListGroupMemberships.listGroupMemberships(identitystore, identitystoreId, groupId);
        assertTrue(result16>=0);
        System.out.println("\n Test 16 passed");
    }

    @Test
    @Order(17)
    public void ListGroupMembershipsForMember() {
        int result17 = ListGroupMembershipsForMember.listGroupMembershipsForMember(identitystore, identitystoreId, userId);
        assertTrue(result17>=0);
        System.out.println("\n Test 17 passed");
    }

    @Test
    @Order(18)
    public void DeleteGroupMembership() {
        String result18 = DeleteGroupMembership.deleteGroupMembership(identitystore, identitystoreId, membershipId);
        assertTrue(!result18.isEmpty());
        System.out.println("\n Test 18 passed");
    }

    @Test
    @Order(19)
    public void DeleteUser() {

        String result19 = DeleteUser.deleteUser(identitystore, identitystoreId, userId);
        assertTrue(!result19.isEmpty());
        System.out.println("\n Test 19 passed");
    }

    @Test
    @Order(20)
    public void DeleteGroup() {

        String result20 = DeleteGroup.deleteGroup(identitystore, identitystoreId, groupId);
        assertTrue(!result20.isEmpty());
        System.out.println("\n Test 20 passed");
    }

}
