// snippet-sourcedescription:[ListLexicons demonstrates how to produce a list of pronunciation lexicons stored in an AWS Region.]
// snippet-service:[Amazon Polly]
// snippet-keyword:[Java]
// snippet-sourcesyntax:[java]
// snippet-keyword:[Amazon Polly]
// snippet-keyword:[Code Sample]
// snippet-keyword:[GetLexicon]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[5/7/2020]
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

package com.example.polly;

// snippet-start:[polly.java2.list_icons.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.polly.PollyClient;
import software.amazon.awssdk.services.polly.model.ListLexiconsResponse;
import software.amazon.awssdk.services.polly.model.ListLexiconsRequest;
import software.amazon.awssdk.services.polly.model.LexiconDescription;
import software.amazon.awssdk.services.polly.model.PollyException ;
import java.util.Iterator;
import java.util.List;
// snippet-end:[polly.java2.list_icons.import]

public class ListLexicons {

    public static void main(String args[]) {

        PollyClient polly = PollyClient.builder()
                .region(Region.US_WEST_2)
                .build();

        listLexicons(polly) ;
    }

    // snippet-start:[polly.java2.list_icons.main]
    public static void listLexicons(PollyClient client) {

        try {
        ListLexiconsRequest listLexiconsRequest = ListLexiconsRequest.builder()
                .build();

        ListLexiconsResponse listLexiconsResult = client.listLexicons(listLexiconsRequest);
        List<LexiconDescription> lexiconDescription = listLexiconsResult.lexicons();
        Iterator<LexiconDescription> iterator = lexiconDescription.iterator();

        // Get each voice
        while (iterator.hasNext()) {
            LexiconDescription lexDescription = iterator.next();
            System.out.println("The name of the Lexicon is " +lexDescription.name());
        }

        } catch (PollyException e) {
            System.err.println("Exception caught: " + e);
            System.exit(1);
        }
    }
    // snippet-end:[polly.java2.list_icons.main]
}
