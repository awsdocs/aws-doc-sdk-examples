//snippet-sourcedescription:[CreateNamedQueryExample.java demonstrates how to create a named query]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Athena]
//snippet-service:[athena]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-06-25]
//snippet-sourceauthor:[soo-aws]
package aws.example.athena;

import software.amazon.awssdk.services.athena.AthenaClient;
import software.amazon.awssdk.services.athena.model.CreateNamedQueryRequest;
import software.amazon.awssdk.services.athena.model.CreateNamedQueryResponse;

/**
* CreateNamedQueryExample
* -------------------------------------
* This code shows how to create a named query.
*/
public class CreateNamedQueryExample
{
  public static void main(String[] args) throws Exception
  {
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
  }
}
