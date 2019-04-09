//snippet-sourcedescription:[CreateGrant.java demonstrates how to add a grant to a CMK that specifies the CMK's use.]
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
import com.amazonaws.services.kms.model.CreateGrantRequest;
import com.amazonaws.services.kms.model.CreateGrantResult;
import com.amazonaws.services.kms.model.GrantOperation;

public class CreateGrant {
    public static void main(String[] args) {
        AWSKMS kmsClient = AWSKMSClientBuilder.standard().build();

        // Create a grant
        //
        // Replace the following fictitious CMK ARN with a valid CMK ID or ARN
        String keyId = "arn:aws:kms:us-west-2:111122223333:key/1234abcd-12ab-34cd-56ef-1234567890ab";
        String granteePrincipal = "arn:aws:iam::111122223333:user/Alice";
        String operation = GrantOperation.Encrypt.toString();

        CreateGrantRequest request = new CreateGrantRequest()
                .withKeyId(keyId)
                .withGranteePrincipal(granteePrincipal)
                .withOperations(operation);

        CreateGrantResult result = kmsClient.createGrant(request);
    }
}