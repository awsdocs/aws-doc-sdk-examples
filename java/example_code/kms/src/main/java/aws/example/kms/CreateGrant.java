//snippet-sourcedescription:[CreateGrant.java demonstrates how to add a grant to a CMK that specifies the CMK's use.]
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
import com.amazonaws.services.kms.model.CreateGrantRequest;
import com.amazonaws.services.kms.model.CreateGrantResult;
import com.amazonaws.services.kms.model.GrantOperation;

public class CreateGrant {
    public static void main(String[] args) {
        final String USAGE =
            "To run this example, supply a key id or ARN, a grantee principal" +
            ", and an operation\n" +
            "Usage: CreateGrant <key-id> <grantee-principal> <operation>\n" +
            "Example: CreateGrant 1234abcd-12ab-34cd-56ef-1234567890ab " +
            "arn:aws:iam::111122223333:user/Alice Encrypt\n";

        if (args.length != 3) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String keyId = args[0];
        String granteePrincipal = args[1];
        String operation = args[2];

        AWSKMS kmsClient = AWSKMSClientBuilder.standard().build();

        // Create a grant

        CreateGrantRequest request = new CreateGrantRequest()
                .withKeyId(keyId)
                .withGranteePrincipal(granteePrincipal)
                .withOperations(operation);

        CreateGrantResult result = kmsClient.createGrant(request);
        String grantId = result.getGrantId();

        System.out.printf("Successfully created a grant with ID %s%n", grantId);
    }
}
