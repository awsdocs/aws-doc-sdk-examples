//snippet-sourcedescription:[CreateNamedQueryExample.java demonstrates how to create a named query]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Athena]
//snippet-service:[athena]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2019-04-15]
//snippet-sourceauthor:[jschwarzwalder]
//snippet-start:[athena.java2.CreateNamedQueryExample.complete]
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

//snippet-end:[athena.java2.CreateNamedQueryExample.complete]