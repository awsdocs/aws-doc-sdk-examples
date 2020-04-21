import com.example.cognito.*;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentity.CognitoIdentityClient;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CognitoServiceIntegrationTest {

    private static CognitoIdentityProviderClient cognitoclient;
    private static  CognitoIdentityProviderClient cognitoIdentityProviderClient ;
    private static CognitoIdentityClient cognitoIdclient ;
    private static String userPoolName="";
    private static String userPoolId="" ; //set in test 2
    private static String identityPoolId =""; //set in test 5
    private static String username="";
    private static String email="";
    private static String clientName="";
    private static String identityPoolName="";

    @BeforeAll
    public static void setUp() throws IOException {

        // Run tests on Real AWS Resources
        Region region = Region.US_EAST_1;
        cognitoclient = CognitoIdentityProviderClient.builder()
                .region(Region.US_EAST_1)
                .build();

        cognitoIdclient  = CognitoIdentityClient.builder()
                .region(Region.US_EAST_1)
                .build();

        cognitoIdentityProviderClient  = CognitoIdentityProviderClient.builder()
                .region(Region.US_EAST_1)
                .build();


        try (InputStream input = CognitoServiceIntegrationTest.class.getClassLoader().getResourceAsStream("config.properties")) {

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
        try{
            userPoolId = CreateUserPool.createPool(cognitoclient, userPoolName);
            assertTrue(!userPoolId.isEmpty());
        } catch (CognitoIdentityProviderException e){
            System.err.println(e.getMessage());
            System.exit(1);
        }

         System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void CreateAdminUser() {
        try{
            CreateAdminUser.createAdmin(cognitoclient,userPoolId ,username, email);

        } catch (CognitoIdentityProviderException e){
            System.err.println(e.getMessage());
            System.exit(1);
        }

        System.out.println("Test 2 passed");
    }

    @Test
    @Order(4)
    public void CreateUserPoolClient() {

        try{
            CreateUserPoolClient.createPoolClient(cognitoclient,clientName, userPoolId);
        } catch (CognitoIdentityProviderException e){
            System.err.println(e.getMessage());
            System.exit(1);
        }

        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void CreateIdentityPool() {

        try{
            identityPoolId = CreateIdentityPool.createIdPool(cognitoIdclient, identityPoolName);
            assertTrue(!identityPoolId.isEmpty());
        } catch (CognitoIdentityProviderException e){
            System.err.println(e.getMessage());
            System.exit(1);
        }

        System.out.println("Test 5 passed");
    }

    @Test
    @Order(6)
    public void ListUserPools() {

        try{
            ListUserPools.listAllUserPools(cognitoclient);
        } catch (CognitoIdentityProviderException e){
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("Test 6 passed");
    }

    @Test
    @Order(7)
    public void ListUserPoolClients() {

        try{
            ListUserPoolClients.listAllUserPoolClients(cognitoIdentityProviderClient, userPoolId);
        } catch (CognitoIdentityProviderException e){
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("Test 7 passed");
    }

    @Test
    @Order(8)
    public void ListUsers() {

        try{
            ListUsers.listAllUsers(cognitoclient, userPoolId);
        } catch (CognitoIdentityProviderException e){
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("Test 8 passed");
    }

    @Test
    @Order(9)
    public void AddLoginProvider() {

        try{
            AddLoginProvider.setLoginProvider(cognitoIdclient, userPoolId, identityPoolName, identityPoolId);
        } catch (CognitoIdentityProviderException e){
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("Test 9 passed");
    }



    @Test
    @Order(10)
    public void DeleteUserPool() {

        try{
            DeleteUserPool.deletePool(cognitoclient, userPoolId);
        } catch (CognitoIdentityProviderException e){
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("Test 10 passed");
    }

}
