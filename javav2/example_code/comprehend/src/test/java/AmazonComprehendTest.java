import com.example.comprehend.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.comprehend.ComprehendClient;
import java.io.*;
import java.util.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AmazonComprehendTest {

    private static  ComprehendClient comClient;
    private static String text = "Amazon.com, Inc. is located in Seattle, WA and was founded July 5th, 1994 by Jeff Bezos, allowing customers to buy everything from books to blenders. Seattle is north of Portland and south of Vancouver, BC. Other notable Seattle - based companies are Starbucks and Boeing" ;
    private static String frText = "Il pleut aujourd'hui à Seattle" ;
    private static String dataAccessRoleArn;
    private static String s3Uri;
    private static String documentClassifierName;

    @BeforeAll
    public static void setUp() throws IOException {

        // Run tests on Real AWS Resources
        Region region = Region.US_EAST_1;
        comClient = ComprehendClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        try (InputStream input = AmazonComprehendTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
             dataAccessRoleArn = prop.getProperty("dataAccessRoleArn");
            s3Uri = prop.getProperty("s3Uri");
            documentClassifierName = prop.getProperty("documentClassifier");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSService_thenNotNull() {
        assertNotNull(comClient);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void DetectEntities() {

        DetectEntities.detectAllEntities(comClient, text);
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void DetectKeyPhrases() {

        DetectKeyPhrases.detectAllKeyPhrases(comClient, text);
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void DetectLanguage() {

        DetectLanguage.detectTheDominantLanguage(comClient, frText);
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void DetectSentiment() {

        DetectSentiment.detectSentiments(comClient, text);
        System.out.println("Test 5 passed");
    }

    @Test
    @Order(6)
    public void DetectSyntax() {

        DetectSyntax.detectAllSyntax(comClient, text);
        System.out.println("Test 6 passed");
    }

    @Test
    @Order(7)
    public void DocumentClassifierDemo() {

      DocumentClassifierDemo.createDocumentClassifier(comClient, dataAccessRoleArn, s3Uri, documentClassifierName);
     System.out.println("Test 7 passed");
    }
}
