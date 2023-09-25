/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.iam.*;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.AccessKey;
import software.amazon.awssdk.services.iam.model.User;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import java.util.concurrent.TimeUnit;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IAMServiceTest {

    private static IamClient iam;
    private static String userName="";
    private static String policyName="";
    private static String roleName="";
    private static String policyARN="";
    private static String keyId ="" ;
    private static String accountAlias="";
    private static String usernameSc = "";
    private static String policyNameSc = "";
    private static String roleNameSc = "";
    private static String roleSessionName = "";
    private static String fileLocationSc = "";
    private static String bucketNameSc = "";

    @BeforeAll
    public static void setUp() {
        Region region = Region.AWS_GLOBAL;
        iam = IamClient.builder()
            .region(region)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        userName = values.getUserName();
        policyName= values.getPolicyName();
        roleName= values.getRoleName();
        accountAlias=values.getAccountAlias();
        usernameSc=values.getUsernameSc();
        policyNameSc=values.getPolicyNameSc();
        roleNameSc=values.getRoleNameSc();
        roleSessionName=values.getRoleName();
        fileLocationSc=values.getFileLocationSc();
        bucketNameSc=values.getBucketNameSc();

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
       /*

        try (InputStream input = IAMServiceTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            userName = prop.getProperty("userName");
            policyName= prop.getProperty("policyName");
            policyARN= prop.getProperty("policyARN");
            roleName=prop.getProperty("roleName");
            accountAlias=prop.getProperty("accountAlias");
            usernameSc=prop.getProperty("usernameSc");
            policyNameSc=prop.getProperty("policyNameSc");
            roleNameSc=prop.getProperty("roleNameSc");
            roleSessionName=prop.getProperty("roleSessionName");
            fileLocationSc=prop.getProperty("fileLocationSc");
            bucketNameSc=prop.getProperty("bucketNameSc");

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        */
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void CreatUser() {
        String result = CreateUser.createIAMUser(iam, userName);
        assertFalse(result.isEmpty());
        System.out.println("\n Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void CreatePolicy() {
        policyARN = CreatePolicy.createIAMPolicy(iam, policyName);
        assertFalse(policyARN.isEmpty());
        System.out.println("\n Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void CreateAccessKey() {
        keyId = CreateAccessKey.createIAMAccessKey(iam,userName);
        assertFalse(keyId.isEmpty());
        System.out.println("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void AttachRolePolicy() {
        assertDoesNotThrow(() ->AttachRolePolicy.attachIAMRolePolicy(iam, roleName, policyARN));
        System.out.println("\n Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void DetachRolePolicy() {
        assertDoesNotThrow(() ->DetachRolePolicy.detachPolicy(iam, roleName, policyARN));
        System.out.println("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void GetPolicy() {
        assertDoesNotThrow(() ->GetPolicy.getIAMPolicy(iam, policyARN));
        System.out.println("Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void ListAccessKeys() {
        assertDoesNotThrow(() ->ListAccessKeys.listKeys(iam,userName));
        System.out.println("Test 7 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
    public void ListUsers() {
        assertDoesNotThrow(() ->ListUsers.listAllUsers(iam));
        System.out.println("Test 8 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(9)
   public void CreateAccountAlias() {
        assertDoesNotThrow(() ->CreateAccountAlias.createIAMAccountAlias(iam, accountAlias));
       System.out.println("Test 9 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(10)
    public void DeleteAccountAlias() {
        assertDoesNotThrow(() ->DeleteAccountAlias.deleteIAMAccountAlias(iam, accountAlias));
        System.out.println("Test 10 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(11)
    public void DeletePolicy() {
       assertDoesNotThrow(() ->DeletePolicy.deleteIAMPolicy(iam, policyARN));
       System.out.println("Test 12 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(12)
   public void DeleteAccessKey() {
       assertDoesNotThrow(() ->DeleteAccessKey.deleteKey(iam, userName, keyId));
       System.out.println("Test 12 passed");
   }

    @Test
    @Tag("IntegrationTest")
    @Order(13)
    public void DeleteUser() {
        assertDoesNotThrow(() ->DeleteUser.deleteIAMUser(iam,userName));
        System.out.println("Test 13 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(14)
    public void TestIAMScenario() throws Exception {
        String DASHES = new String(new char[80]).replace("\0", "-");
        System.out.println(DASHES);
        System.out.println(" 1. Create the IAM user.");
        User createUser = IAMScenario.createIAMUser(iam, usernameSc);

        System.out.println(DASHES);
        String userArn = createUser.arn();
        AccessKey myKey = IAMScenario.createIAMAccessKey(iam, usernameSc);
        String accessKey = myKey.accessKeyId();
        String secretKey = myKey.secretAccessKey();
        String assumeRolePolicyDocument = "{" +
            "\"Version\": \"2012-10-17\"," +
            "\"Statement\": [{" +
            "\"Effect\": \"Allow\"," +
            "\"Principal\": {" +
            "	\"AWS\": \"" + userArn + "\"" +
            "}," +
            "\"Action\": \"sts:AssumeRole\"" +
            "}]" +
            "}";

        System.out.println(assumeRolePolicyDocument);
        System.out.println(usernameSc + " was successfully created.");
        System.out.println(DASHES);
        System.out.println("2. Creates a policy.");
        String polArn = IAMScenario.createIAMPolicy(iam, policyNameSc);
        System.out.println("The policy " + polArn + " was successfully created.");
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("3. Creates a role.");
        TimeUnit.SECONDS.sleep(30);
        String roleArn = IAMScenario.createIAMRole(iam, roleNameSc, assumeRolePolicyDocument);
        System.out.println(roleArn + " was successfully created.");
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("4. Grants the user permissions.");
        IAMScenario.attachIAMRolePolicy(iam, roleNameSc, polArn);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("*** Wait for 30 secs so the resource is available");
        TimeUnit.SECONDS.sleep(30);
        System.out.println("5. Gets temporary credentials by assuming the role.");
        System.out.println("Perform an Amazon S3 Service operation using the temporary credentials.");
        IAMScenario.assumeRole(roleArn, roleSessionName, bucketNameSc, accessKey, secretKey);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("6 Getting ready to delete the AWS resources");
        IAMScenario.deleteKey(iam, usernameSc, accessKey );
        IAMScenario.deleteRole(iam, roleNameSc, polArn);
        IAMScenario.deleteIAMUser(iam, usernameSc);
        System.out.println(DASHES);
    }

    private static String getSecretValues() {
       SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
        String secretName = "test/iam";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/iam (an AWS Secrets Manager secret)")
    class SecretValues {
        private String userName;
        private String policyName;
        private String roleName;

        private String accountAlias;

        private String usernameSc;

        private String policyNameSc;

        private String roleNameSc;

        private String roleSessionName;

        private String fileLocationSc;

        private String bucketNameSc;
        public String getUserName() {
            return userName;
        }

        public String getPolicyName() {
            return policyName;
        }

        public String getRoleName() {
            return roleName;
        }

        public String getAccountAlias() {
            return accountAlias;
        }

        public String getUsernameSc() {
            return usernameSc;
        }

        public String getPolicyNameSc() {
            return policyNameSc;
        }

        public String getRoleNameSc() {
            return roleSessionName;
        }

        public String getFileLocationSc() {
            return fileLocationSc;
        }

        public String getBucketNameSc() {
            return bucketNameSc;
        }
    }
}

