//snippet-sourcedescription:[ListKeyPolicies.java demonstrates how to dget the names of key policies for a customer master key.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon KMS]
//snippet-service:[kms]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2019-04-08]
//snippet-sourceauthor:[AWS]

package aws.example.kms;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.ListKeyPoliciesRequest;
import com.amazonaws.services.kms.model.ListKeyPoliciesResult;

public class ListKeyPolicies {
    public static void main(String[] args) {
        final String USAGE =
            "To run this example, supply a key id or ARN\n" +
            "Usage: ListKeyPolicies <key-id>\n" +
            "Example: ListKeyPolicies 1234abcd-12ab-34cd-56ef-1234567890ab\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String keyId = args[0];

        AWSKMS kmsClient = AWSKMSClientBuilder.standard().build();

        // List key policies

        String nextMarker = null;
        do {
            ListKeyPoliciesRequest req = new ListKeyPoliciesRequest()
                .withMarker(nextMarker).withKeyId(keyId);
            ListKeyPoliciesResult result = kmsClient.listKeyPolicies(req);
            for (String policyName : result.getPolicyNames()) {
                System.out.printf("Found a policy named \"%s\".%n", policyName);
            }
            nextMarker = result.getNextMarker();
        } while (nextMarker != null);

    }
}
