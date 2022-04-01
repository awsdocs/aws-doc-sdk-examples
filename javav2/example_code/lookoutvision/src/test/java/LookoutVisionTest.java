/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
import com.example.lookoutvision.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import software.amazon.awssdk.services.lookoutvision.LookoutVisionClient;
import software.amazon.awssdk.services.lookoutvision.model.LookoutVisionException;

import java.io.*;
import java.util.Properties;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LookoutVisionTest {

    private static LookoutVisionClient lfvClient;

    private static String projectName="";
    private static String modelVersion ="";
    private static String photo="";

    @BeforeAll
    public static void setUp() throws IOException {

        try (InputStream input = LookoutVisionTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //Load a properties file.
            prop.load(input);
            projectName = prop.getProperty("projectName");
            modelVersion = prop.getProperty("modelVersion");
            photo = prop.getProperty("photo");

            // Get the lookoutvision client.
            lfvClient = LookoutVisionClient.builder()
            .build();

   
        } catch (IOException ex) {
            ex.printStackTrace();
        }
  }

    @Test
    @Order(1)
    public void whenInitializingAWSRekognitionService_thenNotNull() {
        assertNotNull(lfvClient);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void detectAnomaliesPanel_thenNotNull() throws IOException, LookoutVisionException{

        DetectAnomalies panel= new DetectAnomalies(lfvClient, projectName, modelVersion, photo);
        assertNotNull(panel);
        System.out.println("Test 2 passed");
 
    }
   
}
