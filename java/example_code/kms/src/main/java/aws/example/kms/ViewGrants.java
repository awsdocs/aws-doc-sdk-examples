//snippet-sourcedescription:[ViewGrants.java demonstrates how to get detailed information about the grants on an AWS KMS customer master key .]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon KMS]
//snippet-service:[kms]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2019-04-08]
//snippet-sourceauthor:[AWS]

package aws.example.kms;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.GrantListEntry;
import com.amazonaws.services.kms.model.ListGrantsRequest;
import com.amazonaws.services.kms.model.ListGrantsResult;

public class ViewGrants {
    public static void main(String[] args) {
        final String USAGE =
            "To run this example, supply a key id or ARN\n" +
            "Usage: ViewGrants <key-id>\n" +
            "Example: ViewGrants 1234abcd-12ab-34cd-56ef-1234567890ab\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String keyId = args[0];

        AWSKMS kmsClient = AWSKMSClientBuilder.standard().build();

        // Listing grants on a CMK
        String nextMarker = null;
        do {
            ListGrantsRequest request = new ListGrantsRequest()
                    .withKeyId(keyId)
                    .withMarker(nextMarker);

            ListGrantsResult result = kmsClient.listGrants(request);
            for (GrantListEntry grant : result.getGrants()) {
                System.out.printf("Found grant \"%s\" with grantee principal " +
                "%s, operations %s, and constraints %s.%n", grant.getGrantId(),
                grant.getGranteePrincipal(), grant.getOperations(), grant.getConstraints());
            }
            nextMarker = result.getNextMarker();
        } while (nextMarker != null);

    }
}
