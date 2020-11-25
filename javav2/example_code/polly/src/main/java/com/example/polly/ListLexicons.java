// snippet-sourcedescription:[ListLexicons demonstrates how to produce a list of pronunciation lexicons stored in an AWS Region.]
//snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[Amazon Polly]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[11/05/2020]
// snippet-sourceauthor:[scmacdon AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
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
        polly.close();
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
