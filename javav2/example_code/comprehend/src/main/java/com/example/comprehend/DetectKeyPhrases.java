// snippet-sourcedescription:[DetectKeyPhrases demonstrates how to detect key phrases.]
//snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[Amazon Comprehend]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[05/18/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.comprehend;

//snippet-start:[comprehend.java2.detect_keyphrases.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.comprehend.ComprehendClient;
import software.amazon.awssdk.services.comprehend.model.DetectKeyPhrasesRequest;
import software.amazon.awssdk.services.comprehend.model.DetectKeyPhrasesResponse;
import software.amazon.awssdk.services.comprehend.model.KeyPhrase;
import software.amazon.awssdk.services.comprehend.model.ComprehendException;
import java.util.Iterator;
import java.util.List;
//snippet-end:[comprehend.java2.detect_keyphrases.import]
/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DetectKeyPhrases {

    public static void main(String[] args) {

        String text = "Amazon.com, Inc. is located in Seattle, WA and was founded July 5th, 1994 by Jeff Bezos, allowing customers to buy everything from books to blenders. Seattle is north of Portland and south of Vancouver, BC. Other notable Seattle - based companies are Starbucks and Boeing.";
        Region region = Region.US_EAST_1;
        ComprehendClient comClient = ComprehendClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        System.out.println("Calling DetectKeyPhrases");
        detectAllKeyPhrases(comClient, text);
        comClient.close();
    }

    //snippet-start:[comprehend.java2.detect_keyphrases.main]
    public static void detectAllKeyPhrases(ComprehendClient comClient, String text) {

        try {
            DetectKeyPhrasesRequest detectKeyPhrasesRequest = DetectKeyPhrasesRequest.builder()
                    .text(text)
                    .languageCode("en")
                    .build();

            DetectKeyPhrasesResponse detectKeyPhrasesResult = comClient.detectKeyPhrases(detectKeyPhrasesRequest);

            List<KeyPhrase> phraseList = detectKeyPhrasesResult.keyPhrases();
            Iterator<KeyPhrase> keyIterator = phraseList.iterator();

            while (keyIterator.hasNext()) {
                KeyPhrase keyPhrase = keyIterator.next();
                System.out.println("Key phrase text is " + keyPhrase.text());
            }

        } catch (ComprehendException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    //snippet-end:[comprehend.java2.detect_keyphrases.main]
}

