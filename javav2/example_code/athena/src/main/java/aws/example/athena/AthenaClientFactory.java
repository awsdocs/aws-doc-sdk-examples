//snippet-sourcedescription:[AthenaClientFactory.java demonstrates how to create and configure an Amazon Athena client]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Athena]
//snippet-service:[athena]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2019-04-15]
//snippet-sourceauthor:[jschwarzwalder AWS]
//snippet-start:[athena.java2.AthenaClientFactory.client]
package aws.example.athena;

//snippet-start:[athena.java2.AthenaClientFactory.client.import]
import software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.athena.AthenaClient;
import software.amazon.awssdk.services.athena.AthenaClientBuilder;
//snippet-end:[athena.java2.AthenaClientFactory.client.import]


/**
 * AthenaClientFactory
 * -------------------------------------
 * This code shows how to create and configure an Amazon Athena client.
 */
public class AthenaClientFactory {
    //snippet-start:[athena.java2.AthenaClientFactory.client.main]
    /**
     * AthenaClientClientBuilder to build Athena with the following properties:
     * - Set the region of the client
     * - Use the instance profile from the EC2 instance as the credentials provider
     * - Configure the client to increase the execution timeout.
     */
    private final AthenaClientBuilder builder = AthenaClient.builder()
            .region(Region.US_WEST_2)
            .credentialsProvider(InstanceProfileCredentialsProvider.create());

    public AthenaClient createClient() {
        return builder.build();
    }
    //snippet-end:[athena.java2.AthenaClientFactory.client.main]
}
//snippet-end:[athena.java2.AthenaClientFactory.client]