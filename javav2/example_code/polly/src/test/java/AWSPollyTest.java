import com.example.polly.DescribeVoicesSample;
import com.example.polly.ListLexicons;
import com.example.polly.PollyDemo;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.polly.PollyClient;
import java.io.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class AWSPollyTest {

    private static PollyClient polly;

    @BeforeAll
    public static void setUp() throws IOException {

        polly = PollyClient.builder()
                .region(Region.US_WEST_2)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
    }

    @Test
    @Order(1)
    public void whenInitializingAWSService_thenNotNull() {
        assertNotNull(polly);
        System.out.println("Running SNS Test 1");
    }

    @Test
    @Order(2)
    public void pollyDemo() {

        PollyDemo.talkPolly(polly);
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void describeVoicesSample() {

        DescribeVoicesSample.describeVoice(polly);
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void listLexicons() {

        ListLexicons.listLexicons(polly);
        System.out.println("Test 4 passed");
    }
}
