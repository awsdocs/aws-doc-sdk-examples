// snippet-sourcedescription:[DetectKeyPhrases demonstrates how to detect key phrases.]
// snippet-service:[Amazon Comprehend]
// snippet-keyword:[Java]
// snippet-sourcesyntax:[java]
// snippet-keyword:[Amazon Comprehend]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[6/3/2020]
// snippet-sourceauthor:[scmacdon AWS]

/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.example.comprehend;

//snippet-start:[comprehend.java2.detect_keyphrases.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.comprehend.ComprehendClient;
import software.amazon.awssdk.services.comprehend.model.DetectKeyPhrasesRequest;
import software.amazon.awssdk.services.comprehend.model.DetectKeyPhrasesResponse;
import software.amazon.awssdk.services.comprehend.model.KeyPhrase;
import software.amazon.awssdk.services.comprehend.model.ComprehendException;
import java.util.Iterator;
import java.util.List;
//snippet-end:[comprehend.java2.detect_keyphrases.import]

public class DetectKeyPhrases {

    public static void main(String[] args) {

        String text = "Amazon.com, Inc. is located in Seattle, WA and was founded July 5, 1994 by Jeff Bezos, enabling customers to buy everything from books to blenders. Seattle is north of Portland and south of Vancouver, BC. Other notable Seattle-based companies are Starbucks and Boeing.";
        Region region = Region.US_EAST_1;
        ComprehendClient comClient = ComprehendClient.builder()
                .region(region)
                .build();

        System.out.println("Calling DetectKeyPhrases");
        detectAllKeyPhrases(comClient, text);
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
        //snippet-end:[comprehend.java2.detect_keyphrases.main]
    }
}
