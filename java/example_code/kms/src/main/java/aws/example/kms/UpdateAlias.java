//snippet-sourcedescription:[UpdateAlias.java demonstrates how to associate an existing alias with a different CMK.]
//snippet-keyword:[Java]
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
        AWSKMS kmsClient = AWSKMSClientBuilder.standard().build();

        // Updating an alias
        //
        String aliasName = "alias/projectKey1";
        // Replace the following fictitious CMK ARN with a valid CMK ID or ARN
        String targetKeyId = "arn:aws:kms:us-west-2:111122223333:key/0987dcba-09fe-87dc-65ba-ab0987654321";

        UpdateAliasRequest req = new UpdateAliasRequest()
                .withAliasName(aliasName)
                .withTargetKeyId(targetKeyId);

        kmsClient.updateAlias(req);
    }
}
