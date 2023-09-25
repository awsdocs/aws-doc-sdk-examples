/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.forecast.*;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import java.util.*;
import java.util.concurrent.TimeUnit;
import software.amazon.awssdk.services.forecast.ForecastClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ForecastTest {
    private static ForecastClient forecast;
    private static String predARN ="" ;
    private static String forecastArn=""; // set in test 3
    private static String forecastName="" ;
    private static String dataSet="";
    private static String myDataSetARN =""; // set in test 2

    @BeforeAll
    public static void setUp() {
        Random rand = new Random();
        int randomNum = rand.nextInt((10000 - 1) + 1) + 1;
        forecast = ForecastClient.builder()
            .region(Region.US_WEST_2)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        predARN = values.getPredARN();
        forecastName =  values.getForecastName()+randomNum;
        dataSet = values.getDataSet()+randomNum;

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
       /*

        try (InputStream input = ForecastTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            // Populate the data members required for all tests.
            predARN = "arn:aws:forecast:us-west-2:814548047983:predictor/ScottPredictor";
            forecastName = "forecast"+randomNum;
            dataSet = "dataSet"+randomNum;

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        */
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void CreateDataSet() {
        myDataSetARN = CreateDataSet.createForecastDataSet(forecast, dataSet);
        assertFalse(myDataSetARN.isEmpty());
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void CreateForecast() {
        forecastArn =CreateForecast.createNewForecast(forecast, forecastName, predARN);
        assertFalse(forecastArn.isEmpty());
        System.out.println("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void ListDataSets() {
        assertDoesNotThrow(() ->ListDataSets.listForecastDataSets(forecast));
        System.out.println("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void ListDataSetGroups(){
        assertDoesNotThrow(() ->ListDataSetGroups.listDataGroups(forecast));
        System.out.println("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void ListForecasts() {
        assertDoesNotThrow(() ->ListForecasts.listAllForeCasts(forecast));
        System.out.println("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void DeleteDataSet() {
        assertDoesNotThrow(() ->DeleteDataset.deleteForecastDataSet(forecast, myDataSetARN));
        System.out.println("Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void DeleteForecast() throws InterruptedException {
        System.out.println("Wait 40 mins for resource to become available.");
        TimeUnit.MINUTES.sleep(40);
        assertDoesNotThrow(() ->DeleteForecast.delForecast(forecast, forecastArn));
        System.out.println("Test 7 passed");
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
        String secretName = "test/forecast";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/forecast (an AWS Secrets Manager secret)")
    class SecretValues {
        private String predARN;
        private String forecastName;
        private String dataSet;

        public String getPredARN() {
            return predARN;
        }

        public String getForecastName() {
            return forecastName;
        }

        public String getDataSet() {
            return dataSet;
        }
    }
}
