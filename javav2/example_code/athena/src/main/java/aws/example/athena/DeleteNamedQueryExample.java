//snippet-sourcedescription:[DeleteNamedQueryExample.java demonstrates how to delete a named query by using the named query ID.]
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
//snippet-start:[athena.java2.DeleteNamedQueryExample.complete]
//snippet-start:[athena.java.DeleteNamedQueryExample.complete]
package aws.example.athena;

//snippet-start:[athena.java2.DeleteNamedQueryExample.import]
import software.amazon.awssdk.services.athena.AthenaClient;
import software.amazon.awssdk.services.athena.model.CreateNamedQueryRequest;
import software.amazon.awssdk.services.athena.model.CreateNamedQueryResponse;
import software.amazon.awssdk.services.athena.model.DeleteNamedQueryRequest;
import software.amazon.awssdk.services.athena.model.DeleteNamedQueryResponse;
//snippet-end:[athena.java2.DeleteNamedQueryExample.import]

/**
 * DeleteNamedQueryExample
 * -------------------------------------
 * This code shows how to delete a named query by using the named query ID.
 */
public class DeleteNamedQueryExample {
    //snippet-start:[athena.java2.DeleteNamedQueryExample.main]
    private static String getNamedQueryId(AthenaClient athenaClient) {
        // Create the NameQuery Request.
        CreateNamedQueryRequest createNamedQueryRequest = CreateNamedQueryRequest.builder()
                .database(ExampleConstants.ATHENA_DEFAULT_DATABASE)
                .queryString(ExampleConstants.ATHENA_SAMPLE_QUERY)
                .name("SampleQueryName")
                .description("Sample Description").build();

        // Create the named query. If it fails, an exception is thrown.
        CreateNamedQueryResponse createNamedQueryResponse = athenaClient.createNamedQuery(createNamedQueryRequest);
        return createNamedQueryResponse.namedQueryId();
    }

    public static void main(String[] args) throws Exception {
        // Build an Athena client
        AthenaClientFactory factory = new AthenaClientFactory();
        AthenaClient athenaClient = factory.createClient();

        String sampleNamedQueryId = getNamedQueryId(athenaClient);

        // Create the delete named query request
        DeleteNamedQueryRequest deleteNamedQueryRequest = DeleteNamedQueryRequest.builder()
                .namedQueryId(sampleNamedQueryId).build();

        // Delete the named query
        DeleteNamedQueryResponse deleteNamedQueryResponse = athenaClient.deleteNamedQuery(deleteNamedQueryRequest);
    }
    //snippet-end:[athena.java2.DeleteNamedQueryExample.main]
}

//snippet-end:[athena.java.DeleteNamedQueryExample.complete]
//snippet-end:[athena.java2.DeleteNamedQueryExample.complete]
