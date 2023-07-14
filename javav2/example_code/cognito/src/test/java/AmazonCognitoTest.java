/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
import com.example.cognito.*;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
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
    private static CognitoIdentityProviderClient cognitoclient;
    private static  CognitoIdentityProviderClient cognitoIdentityProviderClient ;
    private static CognitoIdentityClient cognitoIdclient ;
    private static String userPoolName="";
    private static String identityId="";
    private static String userPoolId="" ;
    private static String identityPoolId ="";
    private static String username="";
    private static String email="";
    private static String clientName="";
    private static String identityPoolName="";
    private static String appId="";
    private static String existingUserPoolId="";
    private static String existingIdentityPoolId = "";
    private static String providerName="";
    private static String existingPoolName="";
    private static String clientId="";
    private static String secretkey="";
    private static String password="";
    private static String poolIdMVP="";
    private static String clientIdMVP="";
    private static String userNameMVP="";
    private static String passwordMVP="";
    private static String emailMVP="";
    private static String confirmationCode="";
    private static String authFlow="";
    @BeforeAll
    public static void setUp() throws IOException {
        // Run tests on Real AWS Resources
        cognitoclient = CognitoIdentityProviderClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        cognitoIdclient  = CognitoIdentityClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        cognitoIdentityProviderClient  = CognitoIdentityProviderClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        userPoolName = values.getUserPoolName();
        username= values.getUsername()+"_"+ java.util.UUID.randomUUID();
        email= values.getEmail();
        clientName = values.getClientName();
        identityPoolName =  values.getIdentityPoolName();
        identityId = values.getIdentityId();
        appId = values.getAppId();
        existingUserPoolId = values.getExistingUserPoolId();
        existingIdentityPoolId = values.getExistingIdentityPoolId();
        providerName = values.getProviderName();
        existingPoolName =  values.getExistingPoolName();
        clientId =  values.getClientId();
        secretkey =  values.getSecretkey();
        password = values.getPassword();
        poolIdMVP = values.getPoolIdMVP();
        clientIdMVP = values.getClientIdMVP();
        userNameMVP = values.getUserNameMVP();
        passwordMVP = values.getPasswordMVP();
        emailMVP = values.getEmailMVP();

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
       /*
        try (InputStream input = AmazonCognitoTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            userPoolName = prop.getProperty("userPoolName");
            username= prop.getProperty("username")+"_"+ java.util.UUID.randomUUID();
            email= prop.getProperty("email");
            clientName = prop.getProperty("clientName");
            identityPoolName =  prop.getProperty("identityPoolName");
            identityId = prop.getProperty("identityId"); // used in the GetIdentityCredentials test
            appId = prop.getProperty("appId");
            existingUserPoolId = prop.getProperty("existingUserPoolId");
            existingIdentityPoolId = prop.getProperty("existingIdentityPoolId");
            providerName = prop.getProperty("providerName");
            existingPoolName =  prop.getProperty("existingPoolName");
            clientId =  prop.getProperty("clientId");
            secretkey =  prop.getProperty("secretkey");
            password = prop.getProperty("password");
            poolIdMVP = prop.getProperty("poolIdMVP");
            clientIdMVP = prop.getProperty("clientIdMVP");
            userNameMVP = prop.getProperty("userNameMVP");
            passwordMVP = prop.getProperty("passwordMVP");
            emailMVP = prop.getProperty("emailMVP");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        */
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void CreateUserPool() {
        userPoolId = CreateUserPool.createPool(cognitoclient, userPoolName);
        assertFalse(userPoolId.isEmpty());
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void CreateUser() {
        assertDoesNotThrow(() -> CreateUser.createNewUser(cognitoclient,userPoolId ,username, email, password));
        System.out.println("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void CreateUserPoolClient() {
        assertDoesNotThrow(() ->CreateUserPoolClient.createPoolClient(cognitoclient,clientName, userPoolId));
        System.out.println("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void CreateIdentityPool() {
        identityPoolId = CreateIdentityPool.createIdPool(cognitoIdclient, identityPoolName);
        assertFalse(identityPoolId.isEmpty());
        System.out.println("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void ListUserPools() {
        assertDoesNotThrow(() -> ListUserPools.listAllUserPools(cognitoclient));
        System.out.println("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void ListIdentityPools() {
        assertDoesNotThrow(() -> ListIdentityPools.listIdPools(cognitoIdclient));
        System.out.println("Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void ListUserPoolClients() {
       assertDoesNotThrow(() ->ListUserPoolClients.listAllUserPoolClients(cognitoIdentityProviderClient, existingUserPoolId));
       System.out.println("Test 7 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
    public void ListUsers() {
       assertDoesNotThrow(() ->ListUsers.listAllUsers(cognitoclient, existingUserPoolId));
       System.out.println("Test 8 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(9)
    public void ListIdentities() {
        assertDoesNotThrow(() ->  ListIdentities.listPoolIdentities(cognitoIdclient, existingIdentityPoolId));
        System.out.println("Test 9 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(10)
    public void AddLoginProvider() {
       assertDoesNotThrow(() ->AddLoginProvider.setLoginProvider(cognitoIdclient, appId, existingPoolName, existingIdentityPoolId, providerName));
       System.out.println("Test 10 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(11)
    public void GetIdentityCredentials() {
        assertDoesNotThrow(() -> GetIdentityCredentials.getCredsForIdentity(cognitoIdclient, identityId));
        System.out.println("Test 11 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(12)
    public void GetId() {
        assertDoesNotThrow(() -> GetId.getClientID(cognitoIdclient, existingIdentityPoolId));
        System.out.println("Test 12 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(13)
    public void DeleteUserPool() {
        assertDoesNotThrow(() ->DeleteUserPool.deletePool(cognitoclient, userPoolId));
        System.out.println("Test 13 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(14)
   public void SignUp() {
        assertDoesNotThrow(() ->SignUpUser.signUp(cognitoIdentityProviderClient, clientId, secretkey, username, password, email));
        System.out.println("Test 14 passed");
   }

    @Test
    @Tag("IntegrationTest")
    @Order(15)
    public void DeleteIdentityPool() {
        assertDoesNotThrow(() ->DeleteIdentityPool.deleteIdPool(cognitoIdclient, identityPoolId));
        System.out.println("Test 15 passed");
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
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
