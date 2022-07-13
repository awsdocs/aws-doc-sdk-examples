import com.example.glacier.*;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.glacier.GlacierClient;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GlacierTest {

    private static GlacierClient glacier;
    private static String vaultName="";
    private static String strPath ="";
    private static String downloadVault="";
    private static String accountId="";
    private static String archiveId="";
    private static String emptyVault="";

    @BeforeAll
    public static void setUp() throws IOException {

        glacier = GlacierClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        try (InputStream input = GlacierTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            vaultName = prop.getProperty("vaultName");
            strPath = prop.getProperty("strPath");
            downloadVault= prop.getProperty("downloadVault");
            accountId= prop.getProperty("accountId");
            emptyVault= prop.getProperty("emptyVault");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSS3Service_thenNotNull() {
        assertNotNull(glacier);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void CreateVault() {
        CreateVault.createGlacierVault(glacier, vaultName);
    }

    @Test
    @Order(3)
    public void DescribeVault() {

       DescribeVault.describeGlacierVault(glacier, vaultName);
     }

    @Test
    @Order(4)
    public void ListVaults() {

        ListVaults.listAllVault(glacier);
     }

    @Test
    @Order(5)
    public void UploadArchive() {

        File myFile = new File(strPath);
        Path path = Paths.get(strPath);
        archiveId =  UploadArchive.uploadContent(glacier,path,vaultName,myFile);
        assertTrue(!archiveId.isEmpty());

    }

    @Test
    @Order(6)
    public void ArchiveDownload() {

       ArchiveDownload.createJob( glacier, downloadVault, accountId);

    }


    @Test
    @Order(7)
    public void DeleteArchive() {
        DeleteArchive.deleteGlacierArchive(glacier, vaultName, accountId, archiveId);
    }


    @Test
    @Order(8)
    public void DeleteVault() {

       DeleteVault.deleteGlacierVault(glacier, emptyVault);
     }

}
