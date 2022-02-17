// snippet-sourcedescription:[DetectPHI demonstrates how to retrieve protected health information (PHI).]
//snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[Amazon Comprehend Medical]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[09/28/2021]
// snippet-sourceauthor:[scmacdon - AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.comprehend.medical;

//snippet-start:[comprehendmed.java2.detect_phi.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.comprehendmedical.ComprehendMedicalClient;
import software.amazon.awssdk.services.comprehendmedical.model.ComprehendMedicalException;
import software.amazon.awssdk.services.comprehendmedical.model.DetectPhiRequest;
import software.amazon.awssdk.services.comprehendmedical.model.DetectPhiResponse;
import software.amazon.awssdk.services.comprehendmedical.model.Entity;
import java.util.Iterator;
import java.util.List;
//snippet-end:[comprehendmed.java2.detect_phi.import]
/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DetectPHI {

    public static void main(String[] args) {

        String text = "Pt is 87 yo woman, highschool teacher with past medical history that includes\n" +
                "   - status post cardiac catheterization in April 2019.\n" +
                "She presents today with palpitations and chest pressure.\n" +
                "HPI : Sleeping trouble on present dosage of Clonidine. Severe Rash  on face and leg, slightly itchy  \n" +
                "Meds : Vyvanse 50 mgs po at breakfast daily, \n" +
                "            Clonidine 0.2 mgs -- 1 and 1 / 2 tabs po qhs \n" +
                "HEENT : Boggy inferior turbinates, No oropharyngeal lesion \n" +
                "Lungs : clear \n" +
                "Heart : Regular rhythm \n" +
                "Skin :  Mild erythematous eruption to hairline \n" +
                "\n" +
                "Follow-up as scheduled";
        Region region = Region.US_EAST_1;
        ComprehendMedicalClient medClient = ComprehendMedicalClient.builder()
                .region(region)
                .build();

        System.out.println("Calling Detect Medical Entities");
        detectPHIValues(medClient, text) ;
        medClient.close();

    }
    //snippet-start:[comprehendmed.java2.detect_phi.main]
    public static void detectPHIValues(ComprehendMedicalClient medClient, String text ) {

        try {
            DetectPhiRequest detectRequest = DetectPhiRequest.builder()
                    .text(text)
                    .build();

            DetectPhiResponse detectResult = medClient.detectPHI(detectRequest);
            List<Entity> entList = detectResult.entities();
            Iterator<Entity> lanIterator = entList.iterator();

            while (lanIterator.hasNext()) {
                Entity entity = lanIterator.next();
                System.out.println("Entity text is " + entity.text());
            }

        } catch (ComprehendMedicalException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    //snippet-end:[comprehendmed.java2.detect_phi.main]
}
