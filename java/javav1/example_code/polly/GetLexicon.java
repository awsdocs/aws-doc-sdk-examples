/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

// snippet-sourcedescription:[GetLexicon demonstrates how to produce the content of a specific pronunciation lexicon stored in an AWS region.]
// snippet-service:[polly]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon Polly]
// snippet-keyword:[Code Sample]
// snippet-keyword:[GetLexicon]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-01-31]
// snippet-sourceauthor:[AWS]
// snippet-start:[polly.java.GetLexicon.complete]


package com.amazonaws.polly.samples;
 
import com.amazonaws.services.polly.AmazonPolly;
import com.amazonaws.services.polly.AmazonPollyClientBuilder;
import com.amazonaws.services.polly.model.GetLexiconRequest;
import com.amazonaws.services.polly.model.GetLexiconResult;
 
public class GetLexiconSample {
    private String LEXICON_NAME = "SampleLexicon";
 
    AmazonPolly client = AmazonPollyClientBuilder.defaultClient();
 
    public void getLexicon() {
        GetLexiconRequest getLexiconRequest = new GetLexiconRequest().withName(LEXICON_NAME);
 
        try {
            GetLexiconResult getLexiconResult = client.getLexicon(getLexiconRequest);
            System.out.println("Lexicon: " + getLexiconResult.getLexicon());
        } catch (Exception e) {
            System.err.println("Exception caught: " + e);
        }
    }
}




// snippet-end:[polly.java.GetLexicon.complete]