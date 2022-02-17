import com.example.textract.AnalyzeDocument;
import com.example.textract.DetectDocumentText;
import com.example.textract.DetectDocumentTextS3;
import com.example.textract.StartDocumentAnalysis;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.textract.TextractClient;
import java.io.*;
import java.util.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AmazonTextractServiceIntegrationTest {

    private static TextractClient textractClient;
    private static Region region;
    private static String sourceDoc = "";
    private static String bucketName = "";
    private static String docName = "";

    @BeforeAll
    public static void setUp() throws IOException {

        // Run tests on Real AWS Resources
        region = Region.US_WEST_2;
        textractClient = TextractClient.builder()
                .region(region)
                .build();

        try (InputStream input = AmazonTextractServiceIntegrationTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            sourceDoc = prop.getProperty("sourceDoc");
            bucketName = prop.getProperty("bucketName");
            docName = prop.getProperty("docName");


        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingTextractService_thenNotNull() {
        assertNotNull(textractClient);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void AnalyzeDocument() {

        AnalyzeDocument.analyzeDoc(textractClient, sourceDoc);
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void DetectDocumentText() {

        DetectDocumentText.detectDocText(textractClient, sourceDoc);
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void DetectDocumentTextS3() {

        DetectDocumentTextS3.detectDocTextS3(textractClient, bucketName, docName);
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void StartDocumentAnalysis() {

        StartDocumentAnalysis.startDocAnalysisS3(textractClient, bucketName, docName);
        System.out.println("Test 5 passed");
    }
}
