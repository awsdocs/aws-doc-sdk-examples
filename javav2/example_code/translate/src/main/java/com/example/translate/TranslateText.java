//snippet-sourcedescription:[TranslateText.java demonstrates how to translate text from one language to another.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Translate]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/19/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/


package com.example.translate;

// snippet-start:[translate.java2._text.import]
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.translate.TranslateClient;
import software.amazon.awssdk.services.translate.model.TranslateTextRequest;
import software.amazon.awssdk.services.translate.model.TranslateTextResponse;
import software.amazon.awssdk.services.translate.model.TranslateException;
// snippet-end:[translate.java2._text.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class TranslateText {

    public static void main(String[] args) {

        Region region = Region.US_WEST_2;
        TranslateClient translateClient = TranslateClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        textTranslate( translateClient);
        translateClient.close();
    }

    // snippet-start:[translate.java2._text.main]
    public static void textTranslate(TranslateClient translateClient) {

        try {
            TranslateTextRequest textRequest = TranslateTextRequest.builder()
                .sourceLanguageCode("en")
                .targetLanguageCode("fr")
                .text("Its a sunny day today")
                .build();

            TranslateTextResponse textResponse = translateClient.translateText(textRequest);
            System.out.println(textResponse.translatedText());

        } catch (TranslateException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[translate.java2._text.main]
}
