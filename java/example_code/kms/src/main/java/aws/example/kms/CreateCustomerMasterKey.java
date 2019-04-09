//snippet-sourcedescription:[CreateCustomerMasterKey.java demonstrates how to create a customer master key (CMK).]
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
import com.amazonaws.services.kms.model.CreateKeyRequest;
import com.amazonaws.services.kms.model.CreateKeyResult;

public class CreateCustomerMasterKey {
    public static void main(String[] args) {
        AWSKMS kmsClient = AWSKMSClientBuilder.standard().build();

        // Create a CMK
//
        String desc = "Key for protecting critical data";

        CreateKeyRequest req = new CreateKeyRequest().withDescription(desc);
        CreateKeyResult result = kmsClient.createKey(req);

    }

}

