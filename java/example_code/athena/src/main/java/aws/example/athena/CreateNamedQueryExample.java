//snippet-sourceauthor: [soo-aws]

//snippet-sourcedescription:[Description]

//snippet-service:[AWSService]

//snippet-sourcetype:[full example]

//snippet-sourcedate:[N/A]

package aws.example.athena;

import com.amazonaws.services.athena.AmazonAthena;
import com.amazonaws.services.athena.model.CreateNamedQueryRequest;
import com.amazonaws.services.athena.model.CreateNamedQueryResult;

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
      AmazonAthena athenaClient = factory.createClient();

      // Create the named query request.
      CreateNamedQueryRequest createNamedQueryRequest = new CreateNamedQueryRequest()
              .withDatabase(ExampleConstants.ATHENA_DEFAULT_DATABASE)
              .withQueryString(ExampleConstants.ATHENA_SAMPLE_QUERY)
              .withDescription("Sample Description")
              .withName("SampleQuery2");

      // Call Athena to create the named query. If it fails, an exception is thrown.
      CreateNamedQueryResult createNamedQueryResult = athenaClient.createNamedQuery(createNamedQueryRequest);
  }
}
