// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.cognito.*;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentity.CognitoIdentityClient;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("integ")
public class AmazonCognitoTest {
    private static final Logger logger = LoggerFactory.getLogger(AmazonCognitoTest.class);
    private static CognitoIdentityProviderClient cognitoclient;
    private static CognitoIdentityProviderClient cognitoIdentityProviderClient;
    private static CognitoIdentityClient cognitoIdclient;
    private static String userPoolName = "";
    private static String identityId = "";
    private static String userPoolId = "";
    private static String identityPoolId = "";
    private static String username = "";
    private static String email = "";
    private static String clientName = "";
    private static String identityPoolName = "";
    private static String appId = "";
    private static String existingUserPoolId = "";
    private static String existingIdentityPoolId = "";
    private static String providerName = "";
    private static String existingPoolName = "";
    private static String clientId = "";
    private static String secretkey = "";
    private static String password = "";
    private static String poolIdMVP = "";
    private static String clientIdMVP = "";
    private static String userNameMVP = "";
    private static String passwordMVP = "";
    private static String emailMVP = "";
    private static String confirmationCode = "";
    private static String authFlow = "";

    @BeforeAll
    public static void setUp() throws IOException {
        // Run tests on Real AWS Resources
        cognitoclient = CognitoIdentityProviderClient.builder()
                .region(Region.US_EAST_1)
                .build();

        cognitoIdclient = CognitoIdentityClient.builder()
                .region(Region.US_EAST_1)
                .build();

        cognitoIdentityProviderClient = CognitoIdentityProviderClient.builder()
                .region(Region.US_EAST_1)
                .build();

        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        userPoolName = values.getUserPoolName();
        username = values.getUsername() + "_" + java.util.UUID.randomUUID();
        email = values.getEmail();
        clientName = values.getClientName();
        identityPoolName = values.getIdentityPoolName();
        identityId = values.getIdentityId();
        appId = values.getAppId();
        existingUserPoolId = values.getExistingUserPoolId();
        existingIdentityPoolId = values.getExistingIdentityPoolId();
        providerName = values.getProviderName();
        existingPoolName = values.getExistingPoolName();
        clientId = values.getClientId();
        secretkey = values.getSecretkey();
        password = values.getPassword();
        poolIdMVP = values.getPoolIdMVP();
        clientIdMVP = values.getClientIdMVP();
        userNameMVP = values.getUserNameMVP();
        passwordMVP = values.getPasswordMVP();
        emailMVP = values.getEmailMVP();
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testCreateUserPool() {
        userPoolId = CreateUserPool.createPool(cognitoclient, userPoolName);
        assertFalse(userPoolId.isEmpty());
        logger.info("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testCreateUser() {
        assertDoesNotThrow(() -> CreateUser.createNewUser(cognitoclient, userPoolId, username, email, password));
        logger.info("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testCreateUserPoolClient() {
        assertDoesNotThrow(() -> CreateUserPoolClient.createPoolClient(cognitoclient, clientName, userPoolId));
        logger.info("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void testCreateIdentityPool() {
        identityPoolId = CreateIdentityPool.createIdPool(cognitoIdclient, identityPoolName);
        assertFalse(identityPoolId.isEmpty());
        logger.info("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void testListUserPools() {
        assertDoesNotThrow(() -> ListUserPools.listAllUserPools(cognitoclient));
        logger.info("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void testListIdentityPools() {
        assertDoesNotThrow(() -> ListIdentityPools.listIdPools(cognitoIdclient));
        logger.info("Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void testListUserPoolClients() {
        assertDoesNotThrow(
                () -> ListUserPoolClients.listAllUserPoolClients(cognitoIdentityProviderClient, existingUserPoolId));
        logger.info("Test 7 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
    public void testListUsers() {
        assertDoesNotThrow(() -> ListUsers.listAllUsers(cognitoclient, existingUserPoolId));
        logger.info("Test 8 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(9)
    public void testListIdentities() {
        assertDoesNotThrow(() -> ListIdentities.listPoolIdentities(cognitoIdclient, existingIdentityPoolId));
        logger.info("Test 9 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(10)
    public void testAddLoginProvider() {
        assertDoesNotThrow(() -> AddLoginProvider.setLoginProvider(cognitoIdclient, appId, existingPoolName,
                existingIdentityPoolId, providerName));
        logger.info("Test 10 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(11)
    public void testGetIdentityCredentials() {
        assertDoesNotThrow(() -> GetIdentityCredentials.getCredsForIdentity(cognitoIdclient, identityId));
        logger.info("Test 11 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(12)
    public void testGetId() {
        assertDoesNotThrow(() -> GetId.getClientID(cognitoIdclient, existingIdentityPoolId));
        logger.info("Test 12 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(13)
    public void testDeleteUserPool() {
        assertDoesNotThrow(() -> DeleteUserPool.deletePool(cognitoclient, userPoolId));
        logger.info("Test 13 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(14)
    public void testDeleteIdentityPool() {
        assertDoesNotThrow(() -> DeleteIdentityPool.deleteIdPool(cognitoIdclient, identityPoolId));
        logger.info("Test 15 passed");
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
                .region(Region.US_EAST_1)
                .build();
        String secretName = "test/cognito";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/cognito (an AWS Secrets Manager secret)")
    class SecretValues {
        private String username;
        private String userPoolName;
        private String identityId;

        private String email;

        private String clientName;

        private String identityPoolName;

        private String existingPoolName;

        private String existingIdentityPoolId;
        private String existingUserPoolId;

        private String providerName;

        private String clientId;

        private String appId;
        private String secretkey;

        private String password;

        private String poolIdMVP;

        private String clientIdMVP;

        private String userNameMVP;

        private String passwordMVP;

        private String emailMVP;

        public String getUsername() {
            return username;
        }

        public String getUserPoolName() {
            return userPoolName;
        }

        public String getIdentityId() {
            return identityId;
        }

        public String getEmail() {
            return email;
        }

        public String getClientName() {
            return clientName;
        }

        public String getIdentityPoolName() {
            return identityPoolName;
        }

        public String getExistingPoolName() {
            return existingPoolName;
        }

        public String getExistingIdentityPoolId() {
            return existingIdentityPoolId;
        }

        public String getExistingUserPoolId() {
            return existingUserPoolId;
        }

        public String getAppId() {
            return appId;
        }

        public String getProviderName() {
            return providerName;
        }

        public String getClientId() {
            return clientId;
        }

        public String getSecretkey() {
            return secretkey;
        }

        public String getPassword() {
            return password;
        }

        public String getPoolIdMVP() {
            return poolIdMVP;
        }

        public String getClientIdMVP() {
            return clientIdMVP;
        }

        public String getUserNameMVP() {
            return userNameMVP;
        }

        public String getPasswordMVP() {
            return passwordMVP;
        }

        public String getEmailMVP() {
            return emailMVP;
        }
    }
}
