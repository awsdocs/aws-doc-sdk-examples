import com.example.workdocs.DownloadUserDoc;
import com.example.workdocs.ListUserDocs;
import com.example.workdocs.ListUsers;
import org.junit.jupiter.api.*;
import com.example.workdocs.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import software.amazon.awssdk.services.workdocs.WorkDocsClient;
import java.io.*;
import java.util.Properties;
import  software.amazon.awssdk.regions.Region;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WorkdocsServiceIntegrationTest {

    private static WorkDocsClient workDocs;
    private static String orgId = "" ;
    private static String userEmail =  "" ;;
    private static String workdocsName =  "";
    private static String saveDocFullName = "";
    private static String docName= "";
    private static String docPath= "";

    @BeforeAll
    public static void setUp() throws IOException {

        Region region = Region.US_WEST_2;
        workDocs = WorkDocsClient.builder()
                .region(region)
                .build();


        try (InputStream input = WorkdocsServiceIntegrationTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);
            orgId = prop.getProperty("orgId");
            userEmail = prop.getProperty("userEmail");
            workdocsName = prop.getProperty("workdocsName");
            saveDocFullName = prop.getProperty("saveDocFullName");
            docName = prop.getProperty("docName");
            docPath = prop.getProperty("docPath");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSWorkdocsService_thenNotNull() {
        assertNotNull(workDocs);
        System.out.println("Test 1 passed");
    }


    @Test
    @Order(2)
    public void UploadUserDoc() {

        UploadUserDocs.uploadDoc(workDocs, orgId, userEmail, docName, docPath);
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void DownloadUserDoc() {

        DownloadUserDoc.downloadDoc(workDocs, orgId, userEmail, workdocsName, saveDocFullName);
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void ListUserDocs() {

    ListUserDocs.listDocs(workDocs, orgId, userEmail);
    System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void ListUsers() {
        ListUsers.getAllUsers(workDocs, orgId);
        System.out.println("Test 5 passed");
    }
 }

