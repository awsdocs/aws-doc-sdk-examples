//snippet-sourcedescription:[ ListAliases.java demonstrates how to create an alias. The alias must be unique in the account and region. If you create an alias for a CMK that already has an alias, CreateAlias creates another alias to the same CMK. It does not replace the existing alias.]
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
import com.amazonaws.services.kms.model.CreateAliasRequest;

public class CreateAlias {
    public static void main(String[] args) {
        final String USAGE =
            "To run this example, supply a key id or ARN and an alias name\n" +
            "Usage: CreateAlias <key-id> <alias-name>\n" +
            "Example: CreateAlias 1234abcd-12ab-34cd-56ef-1234567890ab " +
            "alias/projectKey1\n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String targetKeyId = args[0];
        String aliasName = args[1];

        AWSKMS kmsClient = AWSKMSClientBuilder.standard().build();

        // Create an alias for a CMK

        CreateAliasRequest req = new CreateAliasRequest().withAliasName(aliasName).withTargetKeyId(targetKeyId);
        kmsClient.createAlias(req);
    }

}
