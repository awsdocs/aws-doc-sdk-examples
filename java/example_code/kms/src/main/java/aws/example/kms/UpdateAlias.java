//snippet-sourcedescription:[UpdateAlias.java demonstrates how to associate an existing alias with a different CMK.]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon KMS]
//snippet-service:[kms]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2019-04-09]
//snippet-sourceauthor:[AWS]

package aws.example.kms;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.UpdateAliasRequest;

public class UpdateAlias {
    public static void main(String[] args) {
        final String USAGE =
            "To run this example, supply a key id or ARN and an alias name\n" +
            "Usage: UpdateAlias <target-key-id> <alias-name>\n" +
            "Example: UpdateAlias 1234abcd-12ab-34cd-56ef-1234567890ab " +
            "alias/projectKey1\n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String targetKeyId = args[0];
        String aliasName = args[1];

        AWSKMS kmsClient = AWSKMSClientBuilder.standard().build();

        // Updating an alias

        UpdateAliasRequest req = new UpdateAliasRequest()
                .withAliasName(aliasName)
                .withTargetKeyId(targetKeyId);

        kmsClient.updateAlias(req);
    }
}
