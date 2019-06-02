//snippet-sourcedescription:[CreateNamedQueryExample.java demonstrates how to create a named query]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Athena]
//snippet-service:[athena]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2019-04-15]
//snippet-sourceauthor:[jschwarzwalder AWS]
/*
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */ 
//snippet-start:[athena.java2.CreateNamedQueryExample.complete]
//snippet-start:[athena.java.CreateNamedQueryExample.complete]
package aws.example.athena;

//snippet-start:[athena.java2.CreateNamedQueryExample.import]
import software.amazon.awssdk.services.athena.AthenaClient;
import software.amazon.awssdk.services.athena.model.CreateNamedQueryRequest;
import software.amazon.awssdk.services.athena.model.CreateNamedQueryResponse;
//snippet-end:[athena.java2.CreateNamedQueryExample.import]

/**
 * CreateNamedQueryExample
 * -------------------------------------
 * This code shows how to create a named query.
 */
public class CreateNamedQueryExample {
    public static void main(String[] args) throws Exception {
        //snippet-start:[athena.java2.CreateNamedQueryExample.main]
        // Build an Athena client
        AthenaClientFactory factory = new AthenaClientFactory();
        AthenaClient athenaClient = factory.createClient();

        // Create the named query request.
        CreateNamedQueryRequest createNamedQueryRequest = CreateNamedQueryRequest.builder()
                .database(ExampleConstants.ATHENA_DEFAULT_DATABASE)
                .queryString(ExampleConstants.ATHENA_SAMPLE_QUERY)
                .description("Sample Description")
                .name("SampleQuery2").build();

        // Call Athena to create the named query. If it fails, an exception is thrown.
        CreateNamedQueryResponse createNamedQueryResult = athenaClient.createNamedQuery(createNamedQueryRequest);
        //snippet-end:[athena.java2.CreateNamedQueryExample.main]
    }
}
//snippet-end:[athena.java.CreateNamedQueryExample.complete]
//snippet-end:[athena.java2.CreateNamedQueryExample.complete]

