//snippet-sourcedescription:[DisableCustomerMasterKey.java demonstrates how to disable a different customer master key (CMK).]
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
import com.amazonaws.services.kms.model.DisableKeyRequest;

public class DisableCustomerMasterKey {
    public static void main(String[] args) {
        final String USAGE =
            "To run this example, supply a key id or ARN\n" +
            "Usage: DisableCustomerMasterKey <key-id>\n" +
            "Example: DisableCustomerMasterKey 1234abcd-12ab-34cd-56ef-1234567890ab\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String keyId = args[0];

        AWSKMS kmsClient = AWSKMSClientBuilder.standard().build();

        // Disable a CMK

        DisableKeyRequest req = new DisableKeyRequest().withKeyId(keyId);
        kmsClient.disableKey(req);

    }
}

