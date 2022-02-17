import com.example.commit.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codecommit.CodeCommitClient;
import org.junit.jupiter.api.*;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CodeCommitTest {


    private static String branchCommitId =""; // needs to be updated to use latest for each test - required for PutFile test
    private static CodeCommitClient codeCommitClient ;
    private static String newRepoName ="";
    private static String existingRepoName ="";
    private static String newBranchName="";
    private static String existingBranchName="";
    private static String commitId ="" ;
    private static String filePath ="";
    private static String email ="";
    private static String name ="";
    private static String repoPath =""; // change for each test
    private static String targetBranch ="";
    private static String prID = "";

    @BeforeAll
    public static void setUp() throws IOException, URISyntaxException {

        Region region = Region.US_EAST_1;
        codeCommitClient = CodeCommitClient.builder()
                .region(region)
                .build();

        try (InputStream input = CodeCommitClient.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            newRepoName = prop.getProperty("newRepoName");
            existingRepoName = prop.getProperty("existingRepoName");
            newBranchName = prop.getProperty("newBranchName");
            existingBranchName = prop.getProperty("existingBranchName");
            branchCommitId = prop.getProperty("branchCommitId");
            filePath = prop.getProperty("filePath");
            name = prop.getProperty("name");
            repoPath = prop.getProperty("repoPath");
            email = prop.getProperty("email");
            targetBranch = prop.getProperty("targetBranch");
            prID = prop.getProperty("prID");


        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSService_thenNotNull() {
        assertNotNull(codeCommitClient);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void CreateRepository() {
        CreateRepository.createRepo(codeCommitClient, newRepoName);
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void PutFile() {
        PutFile.uploadFile(codeCommitClient, filePath, existingRepoName, existingBranchName, email, name, repoPath, branchCommitId);
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void CreatePullRequest() {
        prID =  CreatePullRequest.createPR(codeCommitClient, existingRepoName, targetBranch, existingBranchName);
        assertTrue(!prID.isEmpty());
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void GetMergeOptions() {
        commitId = GetMergeOptions.getMergeValues(codeCommitClient, existingRepoName, targetBranch, existingBranchName );
        assertTrue(!commitId.isEmpty());
        System.out.println("Test 5 passed");
    }

    @Test
    @Order(6)
    public void MergeBranches() {
        MergeBranches.merge(codeCommitClient, existingRepoName, targetBranch, existingBranchName, commitId);
        System.out.println("Test 6 passed");
    }

    @Test
    @Order(7)
    public void CreateBranch() {
        CreateBranch.createSpecificBranch(codeCommitClient, existingRepoName, newBranchName, commitId);
        System.out.println("Test 7 passed");
    }

    @Test
    @Order(8)
   public void ListRepositories() {
        ListRepositories.listRepos(codeCommitClient);
        System.out.println("Test 8 passed");
   }


    @Test
    @Order(9)
    public void DeleteRepository() {
        DeleteRepository.deleteRepo(codeCommitClient, newRepoName);
        System.out.println("Test 9 passed");
    }

    @Test
    @Order(10)
    public void DeleteBranch() {
        DeleteBranch.deleteSpecificBranch(codeCommitClient, existingRepoName, newBranchName);
        System.out.println("Test 10 passed");
    }

    @Test
    @Order(11)
    public void DescribePullRequestEvents() {
        DescribePullRequestEvents.describePREvents(codeCommitClient, prID);
        System.out.println("Test 11 passed");
    }

    @Test
    @Order(12)
    public void GetRepository() {
        GetRepository.getRepInformation(codeCommitClient, existingRepoName);
        System.out.println("Test 12 passed");
     }
}
