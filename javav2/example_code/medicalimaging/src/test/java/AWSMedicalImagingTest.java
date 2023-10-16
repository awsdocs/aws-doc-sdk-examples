/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.medicalimaging.*;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.medicalimaging.MedicalImagingClient;
import software.amazon.awssdk.services.medicalimaging.model.*;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.utils.ImmutableMap;

import java.io.File;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AWSMedicalImagingTest {
    private static MedicalImagingClient medicalImagingClient;

    private static String datastoreName = "";

    private static String workingDatastoreId = "";

    private static String imageSetId = "";
    private static String imageFrameId = "";

    private static String dataResourceArn = "";
    private static String createdDatastoreId = "";

    private static String importJobId = "";



    @BeforeAll
    public static void setUp() {

        medicalImagingClient = MedicalImagingClient.builder()
                .region(Region.US_WEST_2) // TODO: change back to US-EAST-1
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();

        Random random = new Random();
        int randomNum = random.nextInt((10000 - 1) + 1) + 1;

        datastoreName = "java_test_" + randomNum;
        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        workingDatastoreId = values.getDatastoreId();
        imageSetId = values.getImageSetId();
        imageFrameId = values.getImageFrameId();
        importJobId = values.getImportJobId();
        dataResourceArn = values.getDataResourceArn();

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
    /*

        try (InputStream input = AWSMedicalImagingTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            dataAccessRoleArn = prop.getProperty("dataAccessRoleArn");
            inputS3Uri= prop.getProperty("inputS3Uri");
            outputS3Uri= prop.getProperty("outputS3Uri");

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
       */
    }


    @SuppressWarnings("resource")
    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
                .region(Region.US_WEST_2)  // TODO: change back to US-EAST-1
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();
        String secretName = "test/medicalimaging";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void createDatastoreTest() {
        assertDoesNotThrow(() -> createdDatastoreId = CreateDatastore.createMedicalImageDatastore(medicalImagingClient, datastoreName));
        assert (!createdDatastoreId.isEmpty());

    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void getDatastoreTest() {
        final DatastoreProperties[] datastoreProperties = {null};
        assertDoesNotThrow(() -> datastoreProperties[0] = GetDatastore.getMedicalImageDatastore(medicalImagingClient, workingDatastoreId));
        assertNotNull(datastoreProperties[0]);

        System.out.println("Test 2 passed");

    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void listDatastoresTest() {
        @SuppressWarnings("rawtypes") final List[] dataStoreSummaries = {null};
        assertDoesNotThrow(() -> dataStoreSummaries[0] = ListDatastores.listMedicalImagingDatastores(medicalImagingClient));
        assertNotNull(dataStoreSummaries[0]);

        System.out.println("Test 3 passed");

    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void getDicomImportJobTest() {
        final DICOMImportJobProperties[] dicomImportJobSummaries = {null};
        assertDoesNotThrow(() -> dicomImportJobSummaries[0] = GetDicomImportJob.getDicomImportJob(medicalImagingClient,
                workingDatastoreId, importJobId));
        assertNotNull(dicomImportJobSummaries[0]);

        System.out.println("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void listDicomImportJobsTest() {
        @SuppressWarnings("rawtypes") final List[] dicomImportJobSummaries = {null};
        assertDoesNotThrow(() -> dicomImportJobSummaries[0] = ListDicomImportJobs.listDicomImportJobs(medicalImagingClient,
                workingDatastoreId));
        assertNotNull(dicomImportJobSummaries[0]);

        System.out.println("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void searchImageSetsTest() {
        List<SearchFilter> searchFilters = Collections.singletonList(SearchFilter.builder()
                .operator(Operator.BETWEEN)
                .values(SearchByAttributeValue.builder()
                                .createdAt(Instant.parse("1985-04-12T23:20:50.52Z"))
                                .build(),
                        SearchByAttributeValue.builder()
                                .createdAt(Instant.now())
                                .build())
                .build());

        @SuppressWarnings("rawtypes") final List[] searchResults = {null};
        assertDoesNotThrow(() -> searchResults[0] = SearchImageSets.searchMedicalImagingImageSets(medicalImagingClient,
                workingDatastoreId, searchFilters));
        assertNotNull(searchResults[0]);

        System.out.println("Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void getImageSetTest() {
        final GetImageSetResponse[] imageSetResponses = {null};
        assertDoesNotThrow(() -> imageSetResponses[0] = GetImageSet.getMedicalImageSet(medicalImagingClient,
                workingDatastoreId, imageSetId, "1"));
        assertNotNull(imageSetResponses[0]);

        System.out.println("Test 7 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
    public void getImageSetMetadataTest() {
        final String metadataFileName = "java_metadatata.json.gzip";
        assertDoesNotThrow(() -> GetImageSetMetadata.getMedicalImageSetMetadata(medicalImagingClient, metadataFileName,
                workingDatastoreId, imageSetId, "1"));

        File metadataFile = new File(metadataFileName);
        assert (metadataFile.exists());
        //noinspection ResultOfMethodCallIgnored
        metadataFile.delete();

        System.out.println("Test 8 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(9)
    public void getImageFrameTest() {
        final String imageFileName = "java_impage.jph";
        assertDoesNotThrow(() -> GetImageFrame.getMedicalImageSetFrame(medicalImagingClient, imageFileName,
                workingDatastoreId, imageSetId, imageFrameId));

        File imageFile = new File(imageFileName);
        assert (imageFile.exists());
        //noinspection ResultOfMethodCallIgnored
        imageFile.delete();

        System.out.println("Test 9 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(10)
    public void listImageSetVersionsTest() {
        @SuppressWarnings("rawtypes") List[] imageSetVersions = new List[1];
        assertDoesNotThrow(() -> imageSetVersions[0] = ListImageSetVersions.listMedicalImageSetVersions(medicalImagingClient, workingDatastoreId, imageSetId));
        assertNotNull(imageSetVersions[0]);

        System.out.println("Test 10 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(11)
    public void tagResourceTest() {
        assertDoesNotThrow(() -> TagResource.tagMedicalImagingResource(medicalImagingClient, dataResourceArn, ImmutableMap.of("Deployment", "Development")));


        System.out.println("Test 11 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(12)
    public void listTagsForResourceTest() {
        ListTagsForResourceResponse[] listTagsForResourceResponses = {null};
        assertDoesNotThrow(() -> listTagsForResourceResponses[0] = ListTagsForResource.listMedicalImagingResourceTags(medicalImagingClient, dataResourceArn));
        assertNotNull(listTagsForResourceResponses[0]);

        System.out.println("Test 12 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(13)
    public void untagResourceTest() {
        assertDoesNotThrow(() -> UntagResource.untagMedicalImagingResource(medicalImagingClient, dataResourceArn, Collections.singletonList("Deployment")));

        System.out.println("Test 13 passed");
    }


    @Test
    @Tag("IntegrationTest")
    @Order(14)
    public void deleteDatastoreTest() {
        assert (!createdDatastoreId.isEmpty());
        int count = 0;
        while (count < 20) {
            final DatastoreProperties[] datastoreProperties = {null};
            assertDoesNotThrow(() -> datastoreProperties[0] = GetDatastore.getMedicalImageDatastore(medicalImagingClient, workingDatastoreId));
            if (datastoreProperties[0].datastoreStatus().toString().equals("ACTIVE")) {
                break;
            }
            assertDoesNotThrow(() -> Thread.sleep(1000));
            count++;
        }
        assertDoesNotThrow(() -> DeleteDatastore.deleteMedicalImagingDatastore(medicalImagingClient, createdDatastoreId));
        System.out.println("Test 14 passed");
    }

    @SuppressWarnings("unused")
    @Nested
    @DisplayName("A class used to get test values from test/medicalimaging (an AWS Secrets Manager secret)")
    class SecretValues {

        private String datastoreId;

        private String imageSetId;

        private String imageFrameId;

        private String importJobId;

        private String dataResourceArn;

        public String getDatastoreId() {
            return datastoreId;
        }

        public String getImageSetId() {
            return imageSetId;
        }

        public String getImageFrameId() {
            return imageFrameId;
        }

        public String getImportJobId() {
            return importJobId;
        }

        public String getDataResourceArn() {
            return dataResourceArn;
        }
    }
}