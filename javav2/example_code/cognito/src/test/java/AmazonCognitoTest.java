/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.cognito.*;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentity.CognitoIdentityClient;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthResponse;

import java.io.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AmazonCognitoTest {

    private static CognitoIdentityProviderClient cognitoclient;
    private static  CognitoIdentityProviderClient cognitoIdentityProviderClient ;
    private static CognitoIdentityClient cognitoIdclient ;
    private static String userPoolName="";
    private static String identityId="";
    private static String userPoolId="" ; //set in test 2
    private static String identityPoolId =""; //set in test 5
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


    @BeforeAll
    public static void setUp() throws IOException {

        // Run tests on Real AWS Resources
        Region region = Region.US_EAST_1;
        cognitoclient = CognitoIdentityProviderClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        cognitoIdclient  = CognitoIdentityClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        cognitoIdentityProviderClient  = CognitoIdentityProviderClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();


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
            username= prop.getProperty("username");
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
    }

    @Test
    @Order(1)
    public void whenInitializingAWSS3Service_thenNotNull() {
        assertNotNull(cognitoclient);
        assertNotNull(cognitoIdclient);
        assertNotNull(cognitoIdentityProviderClient);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void CreateUserPool() {
         userPoolId = CreateUserPool.createPool(cognitoclient, userPoolName);
         assertTrue(!userPoolId.isEmpty());
         System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void CreateUser() {
        CreateUser.createNewUser(cognitoclient,userPoolId ,username, email, password);
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(4)
    public void CreateUserPoolClient() {
        CreateUserPoolClient.createPoolClient(cognitoclient,clientName, userPoolId);
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void CreateIdentityPool() {
        identityPoolId = CreateIdentityPool.createIdPool(cognitoIdclient, identityPoolName);
        assertTrue(!identityPoolId.isEmpty());
        System.out.println("Test 5 passed");
    }

    @Test
    @Order(6)
    public void ListUserPools() {
        ListUserPools.listAllUserPools(cognitoclient);
        System.out.println("Test 6 passed");
    }

    @Test
    @Order(7)
    public void ListIdentityPools() {
        ListIdentityPools.listIdPools(cognitoIdclient);
        System.out.println("Test 7 passed");
    }

    @Test
    @Order(8)
    public void ListUserPoolClients() {

       ListUserPoolClients.listAllUserPoolClients(cognitoIdentityProviderClient, existingUserPoolId);
       System.out.println("Test 8 passed");
    }

    @Test
    @Order(9)
    public void ListUsers() {
      ListUsers.listAllUsers(cognitoclient, existingUserPoolId);
      System.out.println("Test 9 passed");
    }

    @Test
    @Order(10)
    public void ListIdentities() {
        ListIdentities.listPoolIdentities(cognitoIdclient, existingIdentityPoolId);
        System.out.println("Test 9 passed");
    }

    @Test
    @Order(11)
    public void AddLoginProvider() {
       AddLoginProvider.setLoginProvider(cognitoIdclient, appId, existingPoolName, existingIdentityPoolId, providerName);
       System.out.println("Test 11 passed");
    }

    @Test
    @Order(12)
    public void GetIdentityCredentials() {
        GetIdentityCredentials.getCredsForIdentity(cognitoIdclient, identityId);
        System.out.println("Test 12 passed");
    }

    @Test
    @Order(13)
    public void GetId() {
        GetId.getClientID(cognitoIdclient, existingIdentityPoolId);
        System.out.println("Test 13 passed");
    }

    @Test
    @Order(14)
    public void DeleteUserPool() {
      DeleteUserPool.deletePool(cognitoclient, userPoolId);
     System.out.println("Test 14 passed");
    }

    @Test
    @Order(15)
   public void SignUp() {

       SignUpUser.signUp(cognitoIdentityProviderClient, clientId, secretkey, username, password, email);
        System.out.println("Test 15 passed");
   }

    @Test
    @Order(16)
    public void DeleteIdentityPool() {

        DeleteIdentityPool.deleteIdPool(cognitoIdclient, identityPoolId);
        System.out.println("Test 16 passed");
    }

    @Test
    @Order(17)
    public void testMVP() {

        Scanner in = new Scanner(System.in);
        System.out.println("*** Signing up " + userNameMVP);
        CognitoMVP.signUp(cognitoIdentityProviderClient, clientIdMVP, userNameMVP, passwordMVP, emailMVP);

        System.out.println("*** Getting " + userNameMVP + " in the user pool");
        CognitoMVP.getAdminUser(cognitoIdentityProviderClient, userNameMVP, poolIdMVP);

        System.out.println("*** Enter confirmation code that was emailed");
        String code = in.nextLine();
        CognitoMVP.confirmSignUp(cognitoIdentityProviderClient, clientIdMVP, code, userNameMVP);

        System.out.println("*** Rechecking the status of " + userNameMVP + " in the user pool");
        CognitoMVP.getAdminUser(cognitoIdentityProviderClient, userNameMVP, poolIdMVP);

        InitiateAuthResponse authResponse = CognitoMVP.initiateAuth(cognitoIdentityProviderClient, clientIdMVP, userNameMVP, passwordMVP) ;
        String mySession = authResponse.session() ;
        assertTrue(!mySession.isEmpty());

        String newSession = CognitoMVP.getSecretForAppMFA(cognitoIdentityProviderClient, mySession);
        assertTrue(!newSession.isEmpty());

        System.out.println("*** Enter the 6-digit code displayed in Google Authenticator");
        String myCode = in.nextLine();

        // Verify the TOTP and register for MFA.
        CognitoMVP.verifyTOTP(cognitoIdentityProviderClient, newSession, myCode);
        System.out.println("*** Re-enter a 6-digit code displayed in Google Authenticator");
        String mfaCode = in.nextLine();
        InitiateAuthResponse authResponse1 =  CognitoMVP.initiateAuth(cognitoIdentityProviderClient, clientIdMVP, userNameMVP, passwordMVP);
        String session2 = authResponse1.session();
        CognitoMVP.adminRespondToAuthChallenge(cognitoIdentityProviderClient, userNameMVP, clientIdMVP, mfaCode, session2);
    }
}
