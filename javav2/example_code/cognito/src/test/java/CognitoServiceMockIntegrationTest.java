import com.example.cognito.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentity.CognitoIdentityClient;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CreateUserPoolRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CreateUserPoolResponse;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CognitoServiceMockIntegrationTest {

    @Mock
    private static CognitoIdentityProviderClient cognitoclient;


    private static String userPoolName="";
    private static String userPoolId="" ; //set in test 2
    private static String identityPoolId =""; //set in test 5
    private static String username="";
    private static String email="";
    private static String clientName="";
    private static String identityPoolName="";

    @BeforeAll
    public static void setUp() throws IOException {

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
         } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void CreateUserPool() {

        try{
             cognitoclient.createUserPool(
                    CreateUserPoolRequest.builder()
                           .poolName(userPoolName)
                           .build()
                );

        } catch (CognitoIdentityProviderException e){
            System.err.println(e.getMessage());
            System.exit(1);
        }

        System.out.println("Test 1 passed");
    }

}
