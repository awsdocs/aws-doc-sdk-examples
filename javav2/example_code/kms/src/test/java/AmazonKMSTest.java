import com.example.kms.*;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kms.KmsClient;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import software.amazon.awssdk.regions.Region;
import java.io.*;
import java.util.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AmazonKMSTest {

    private static KmsClient kmsClient ;
    private static Region region ;
    private static String keyId="" ; // gets set in test 2
    private static String keyDesc="";
    private static SdkBytes EncryptData; // set in test 3
    private static String granteePrincipal="";
    private static String operation="";
    private static String grantId = "";
    private static String aliasName = "";

    @BeforeAll
    public static void setUp() throws IOException {

        region = Region.US_WEST_2;
        kmsClient = KmsClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        try (InputStream input = AmazonKMSTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            keyDesc = prop.getProperty("keyDesc");
            operation=prop.getProperty("operation");
            aliasName=prop.getProperty("aliasName");
            granteePrincipal=prop.getProperty("granteePrincipal");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingKMS_thenNotNull() {
        assertNotNull(kmsClient);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void CreateCustomerKey() {

        keyId = CreateCustomerKey.createKey(kmsClient,keyDesc);
        assertTrue(!keyId.isEmpty());
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void EncryptDataKey() {

        EncryptData = EncryptDataKey.encryptData(kmsClient,keyId);
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void DecryptDataKey() {

        EncryptDataKey.decryptData(kmsClient,EncryptData,keyId);
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void DisableCustomerKey() {
        DisableCustomerKey.disableKey(kmsClient, keyId);
        System.out.println("Test 5 passed");
    }

    @Test
    @Order(6)
    public void EnableCustomerKey() {
        EnableCustomerKey.enableKey(kmsClient, keyId);
        System.out.println("Test 6 passed");
    }

    @Test
    @Order(7)
    public void CreateGrant() {

        grantId = CreateGrant.createGrant(kmsClient, keyId, granteePrincipal, operation);
        assertTrue(!grantId.isEmpty());
        System.out.println("Test 7 passed");
    }

    @Test
    @Order(8)
   public void ViewGrants() {
       ListGrants.displayGrantIds(kmsClient, keyId);
       System.out.println("Test 8 passed");
   }

    @Test
    @Order(9)
    public void RevokeGrant() {
        RevokeGrant.revokeKeyGrant(kmsClient, keyId, grantId);
        System.out.println("Test 9 passed");
    }

    @Test
    @Order(10)
    public void DescribeKey() {

        DescribeKey.describeSpecifcKey(kmsClient, keyId);
        System.out.println("Test 10 passed");
    }

    @Test
    @Order(11)
    public void CreateAlias() {

        CreateAlias.createCustomAlias(kmsClient, keyId, aliasName);
        System.out.println("Test 11 passed");
   }

    @Test
    @Order(12)
   public void ListAliases() {
        ListAliases.listAllAliases(kmsClient);
        System.out.println("Test 12 passed");
   }

    @Test
    @Order(13)
   public void DeleteAlias(){
       DeleteAlias.deleteSpecificAlias(kmsClient, aliasName);
        System.out.println("Test 13 passed");
   }

    @Test
    @Order(14)
   public void ListKeys() {

        ListKeys.listAllKeys(kmsClient);
        System.out.println("Test 14 passed");
   }

    @Test
    @Order(15)
   public void SetKeyPolicy() {
       SetKeyPolicy.createPolicy(kmsClient, keyId, "default");
       System.out.println("Test 15 passed");
   }
}
