import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import software.amazon.awssdk.services.iam.model.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IAMServiceIntegrationTest {

    private static IamClient iam;
    private static String userName="";
    private static String policyName="";
    private static String roleName="";
    private static String policyARN=""; // Set in test 3
    private static String accessKey="" ;
    private static String accountAlias="";

    @BeforeAll
    public static void setUp() throws IOException {

        Region region = Region.AWS_GLOBAL;
        iam =  IamClient.builder().region(region).build();

        try (InputStream input = IAMServiceIntegrationTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();
            prop.load(input);
            // Populate the data members required for all tests
            userName = prop.getProperty("userName");
            policyName= prop.getProperty("policyName");
            policyARN= prop.getProperty("policyARN");
            roleName=prop.getProperty("roleName");
            accountAlias=prop.getProperty("accountAlias");

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
    public void whenInitializingAWSIAMService_thenNotNull() {
        assertNotNull(iam);
        System.out.printf("\n Test 1 passed");
    }

    @Test
    @Order(2)
    public void CreatUser() {

        try {
            CreateUserRequest request = CreateUserRequest.builder()
                    .userName(userName).build();

            CreateUserResponse response = iam.createUser(request);

            System.out.println("Successfully created user: " +
                    response.user().userName());

        } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.println("\n Test 2 passed");
    }

    @Test
    @Order(3)
    public void CreatePolicy() {

        String polDocument =
                "{" +
                        "  \"Version\": \"2012-10-17\"," +
                        "  \"Statement\": [" +
                        "    {" +
                        "        \"Effect\": \"Allow\"," +
                        "        \"Action\": [" +
                        "            \"dynamodb:DeleteItem\"," +
                        "            \"dynamodb:GetItem\"," +
                        "            \"dynamodb:PutItem\"," +
                        "            \"dynamodb:Scan\"," +
                        "            \"dynamodb:UpdateItem\"" +
                        "       ]," +
                        "       \"Resource\": \"*\"" +
                        "    }" +
                        "   ]" +
                        "}";


        try {
            CreatePolicyRequest request = CreatePolicyRequest.builder()
                    .policyName(policyName)
                    .policyDocument(polDocument).build();

            CreatePolicyResponse response = iam.createPolicy(request);
            policyARN=  response.policy().arn();
            System.out.println("Successfully created policy: " +
                    response.policy().policyName());

        } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.println("\n Test 3 passed");
    }

    @Test
    @Order(4)
    public void CreateAccessKey() {

        try {
                CreateAccessKeyRequest request = CreateAccessKeyRequest.builder()
                    .userName(userName).build();
                CreateAccessKeyResponse response = iam.createAccessKey(request);
                accessKey = response.accessKey().accessKeyId();
                System.out.println("Created access key: " + response.accessKey());

           } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        System.out.println("\n Test 4 passed");
    }

    @Test
    @Order(5)
    public void AttachRolePolicy() {

        try {
            List<AttachedPolicy> matching_policies = new ArrayList<>();

            boolean done = false;
            String new_marker = null;

            while(!done) {

                ListAttachedRolePoliciesResponse response;

                if (new_marker == null) {
                    ListAttachedRolePoliciesRequest request =
                            ListAttachedRolePoliciesRequest.builder()
                                    .roleName(roleName).build();
                    response = iam.listAttachedRolePolicies(request);
                } else {
                    ListAttachedRolePoliciesRequest request =
                            ListAttachedRolePoliciesRequest.builder()
                                    .roleName(roleName)
                                    .marker(new_marker).build();
                    response = iam.listAttachedRolePolicies(request);
                }

                matching_policies.addAll(
                        response.attachedPolicies()
                                .stream()
                                .filter(p -> p.policyName().equals(roleName))
                                .collect(Collectors.toList()));

                if(!response.isTruncated()) {
                    done = true;

                } else {
                    new_marker = response.marker();
                }
            }

            if (matching_policies.size() > 0) {
                System.out.println(roleName +
                        " policy is already attached to this role.");
                return;
            }

            AttachRolePolicyRequest attach_request =
                    AttachRolePolicyRequest.builder()
                            .roleName(roleName)
                            .policyArn(policyARN).build();

            iam.attachRolePolicy(attach_request);

            System.out.println("Successfully attached policy " + policyARN +
                    " to role " + roleName);

        } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        System.out.println("\n Test 5 passed");
    }

    @Test
    @Order(6)
    public void DetachRolePolicy() {

        try {
            DetachRolePolicyRequest request = DetachRolePolicyRequest.builder()
                    .roleName(roleName)
                    .policyArn(policyARN).build();

            DetachRolePolicyResponse response = iam.detachRolePolicy(request);
            System.out.println("Successfully detached policy " + policyARN +
                    " from role " + roleName);

        } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.println("\n Test 6 passed");
    }

    @Test
    @Order(7)
    public void GetPolicy() {

        try {
            GetPolicyRequest request = GetPolicyRequest.builder()
                    .policyArn(policyARN).build();

            GetPolicyResponse response = iam.getPolicy(request);

            System.out.format("Successfully retrieved policy %s",
                    response.policy().policyName());

        } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.println("\n Test 7 passed");
    }

    @Test
    @Order(8)
    public void ListAccessKeys() {

        try {
            boolean done = false;
            String new_marker = null;

            while (!done) {
                ListAccessKeysResponse response;

                if(new_marker == null) {
                    ListAccessKeysRequest request = ListAccessKeysRequest.builder()
                            .userName(userName).build();
                    response = iam.listAccessKeys(request);
                }
                else {
                    ListAccessKeysRequest request = ListAccessKeysRequest.builder()
                            .userName(userName)
                            .marker(new_marker).build();
                    response = iam.listAccessKeys(request);
                }

                for (AccessKeyMetadata metadata :
                        response.accessKeyMetadata()) {
                    System.out.format("Retrieved access key %s",
                            metadata.accessKeyId());
                }

                if (!response.isTruncated()) {
                    done = true;
                }
                else {
                    new_marker = response.marker();
                }
            }
        } catch (  IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.println("\n Test 8 passed");
    }

    @Test
    @Order(9)
    public void ListUsers() {

       try {

           boolean done = false;
           String new_marker = null;

           while(!done) {
               ListUsersResponse response;

               if (new_marker == null) {
                   ListUsersRequest request = ListUsersRequest.builder().build();
                   response = iam.listUsers(request);
               } else {
                   ListUsersRequest request = ListUsersRequest.builder()
                           .marker(new_marker).build();
                   response = iam.listUsers(request);
               }

               for(User user : response.users()) {
                   System.out.format("\n Retrieved user %s", user.userName());
               }

               if(!response.isTruncated()) {
                   done = true;
               }
               else {
                   new_marker = response.marker();
               }
           }

       } catch (IamException e) {
           System.err.println(e.awsErrorDetails().errorMessage());
           System.exit(1);
       }
       System.out.println("\n Test 9 passed");
   }

    @Test
    @Order(10)
   public void CreateAccountAlias() {

        try {

            CreateAccountAliasRequest request = CreateAccountAliasRequest.builder()
                    .accountAlias("myawsaccount2").build();
            iam.createAccountAlias(request);
            System.out.println("Successfully created account alias: " + accountAlias);

        } catch (
                IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        System.out.println("\n Test 10 passed");
    }

    @Test
    @Order(11)
    public void DeleteAccountAlias() {

        try {
           // DeleteAccountAliasRequest request = DeleteAccountAliasRequest.builder()
           //         .accountAlias(accountAlias).build();

             DeleteAccountAliasRequest request = DeleteAccountAliasRequest.builder()
                     .accountAlias("myawsaccount2").build();

            DeleteAccountAliasResponse response = iam.deleteAccountAlias(request);
            System.out.println("Successfully deleted account alias " + accountAlias);

        } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.println("\n Test 11 passed");
    }

    @Test
    @Order(12)
    public void DeletePolicy() {

        try {
            DeletePolicyRequest request = DeletePolicyRequest.builder()
                    .policyArn(policyARN)
                    .build();

            iam.deletePolicy(request);

        } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.println("\n Test 12 passed");
    }

    @Test
    @Order(13)
   public void DeleteAccessKey() {

       try {

           DeleteAccessKeyRequest request = DeleteAccessKeyRequest.builder()
                   .accessKeyId(accessKey)
                   .userName(userName).build();

           iam.deleteAccessKey(request);
           System.out.println("Successfully deleted access key " + accessKey +
                   " from user " + userName);

       } catch (IamException e) {
           System.err.println(e.awsErrorDetails().errorMessage());
           System.exit(1);
       }
       System.out.println("\n Test 13 passed");
   }

    @Test
    @Order(14)
    public void DeleteUser() {

        try {
            DeleteUserRequest request = DeleteUserRequest.builder()
                    .userName(userName).build();

            iam.deleteUser(request);
            System.out.println("Successfully deleted IAM user " + userName);

        } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.println("\n Test 14 passed");
    }
}
