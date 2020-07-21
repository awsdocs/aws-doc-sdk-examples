//snippet-sourcedescription:[TranslateText.java demonstrates how to translate text from one language to another.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Translate]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[7/20/2020]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at
    http://aws.amazon.com/apache2.0/
   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

package com.example.translate;

// snippet-start:[translate.java2._text.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.translate.TranslateClient;
import software.amazon.awssdk.services.translate.model.TranslateTextRequest;
import software.amazon.awssdk.services.translate.model.TranslateTextResponse;
import software.amazon.awssdk.services.translate.model.TranslateException;
// snippet-end:[translate.java2._text.import]

public class TranslateText {

    public static void main(String[] args) {

        Region region = Region.US_WEST_2;
        TranslateClient translateClient = TranslateClient.builder()
                .region(region)
                .build();

        textTranslate( translateClient);
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
       // snippet-end:[translate.java2._text.main]
    }
}
