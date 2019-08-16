//snippet-sourcedescription:[GetKeyPolicy.java demonstrates how to get the key policy for a customer master key (CMK).]
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
import com.amazonaws.services.kms.model.GetKeyPolicyRequest;
import com.amazonaws.services.kms.model.GetKeyPolicyResult;

public class GetKeyPolicy {
    public static void main(String[] args) {
        final String USAGE =
            "To run this example, supply a key id or ARN\n" +
            "Usage: GetKeyPolicy <key-id>\n" +
            "Example: GetKeyPolicy 1234abcd-12ab-34cd-56ef-1234567890ab\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String keyId = args[0];

        AWSKMS kmsClient = AWSKMSClientBuilder.standard().build();

        // Get the policy for a CMK
        String policyName = "default";

        GetKeyPolicyRequest req = new GetKeyPolicyRequest().withKeyId(keyId).withPolicyName(policyName);
        GetKeyPolicyResult result = kmsClient.getKeyPolicy(req);

        System.out.printf("Found key policy for %s:%n%s%n", keyId, result.getPolicy());

    }
}

