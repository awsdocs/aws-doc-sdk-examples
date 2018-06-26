package aws.example.athena;

import com.amazonaws.services.athena.AmazonAthena;
import com.amazonaws.services.athena.model.CreateNamedQueryRequest;
import com.amazonaws.services.athena.model.CreateNamedQueryResult;
import com.amazonaws.services.athena.model.DeleteNamedQueryRequest;
import com.amazonaws.services.athena.model.DeleteNamedQueryResult;

/**
 * DeleteNamedQueryExample
 * -------------------------------------
 * This code shows how to delete a named query by using the named query ID.
 */
public class DeleteNamedQueryExample
{
    private static String getNamedQueryId(AmazonAthena athenaClient)
    {
        // Create the NameQuery Request.
        CreateNamedQueryRequest createNamedQueryRequest = new CreateNamedQueryRequest()
                .withDatabase(ExampleConstants.ATHENA_DEFAULT_DATABASE)
                .withQueryString(ExampleConstants.ATHENA_SAMPLE_QUERY)
                .withName("SampleQueryName")
                .withDescription("Sample Description");

        // Create the named query. If it fails, an exception is thrown.
        CreateNamedQueryResult createNamedQueryResult = athenaClient.createNamedQuery(createNamedQueryRequest);
        return createNamedQueryResult.getNamedQueryId();
    }

    public static void main(String[] args) throws Exception
    {
        // Build an Athena client
        AthenaClientFactory factory = new AthenaClientFactory();
        AmazonAthena client = factory.createClient();

        String sampleNamedQueryId = getNamedQueryId(client);

        // Create the delete named query request
        DeleteNamedQueryRequest deleteNamedQueryRequest = new DeleteNamedQueryRequest()
                .withNamedQueryId(sampleNamedQueryId);

        // Delete the named query
        DeleteNamedQueryResult deleteNamedQueryResult = client.deleteNamedQuery(deleteNamedQueryRequest);
    }
}
