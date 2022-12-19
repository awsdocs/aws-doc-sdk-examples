/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import java.io.*;
import java.util.*;
import com.example.support.SupportScenario;
import software.amazon.awssdk.services.support.SupportClient;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SupportTest {

    private static SupportClient supportClient;
    private static String fileAttachment = "";

    @BeforeAll
    public static void setUp() throws IOException {

        Region region = Region.US_WEST_2;
        supportClient = SupportClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        try (InputStream input = SupportTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();
            prop.load(input);

            // Populate the data members required for all tests.
            fileAttachment = prop.getProperty("fileAttachment");

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
    public void whenInitializingAWSService_thenNotNull() {
        assertNotNull(supportClient);
        System.out.printf("\n Test 1 passed");
    }

    @Test
    @Order(2)
    public void supportScenario() {
        List<String> sevCatList = SupportScenario.displayServices(supportClient);
        String sevLevel = SupportScenario.displaySevLevels(supportClient);
        assertFalse(sevLevel.isEmpty());

        String caseId = SupportScenario.createSupportCase(supportClient, sevCatList, sevLevel);
        if (caseId.compareTo("")==0) {
            System.exit(1);
        }

        SupportScenario.getOpenCase(supportClient);
        String attachmentSetId = SupportScenario.addAttachment(supportClient, fileAttachment);
        assertFalse(attachmentSetId.isEmpty());

        SupportScenario.addAttachSupportCase(supportClient, caseId, attachmentSetId);

        String attachId = SupportScenario.listCommunications(supportClient, caseId);
        assertFalse(attachId.isEmpty());
        SupportScenario.describeAttachment(supportClient, attachId);

        SupportScenario.resolveSupportCase(supportClient, caseId);
        SupportScenario.getResolvedCase(supportClient);
        System.out.println("\n Test 2 passed");
    }
}
