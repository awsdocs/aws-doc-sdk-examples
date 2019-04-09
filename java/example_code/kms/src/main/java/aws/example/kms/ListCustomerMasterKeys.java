//snippet-sourcedescription:[ListCustomerMasterKeys.java demonstrates how to get the IDs and ARNs of the customer master keys (CMK).]
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
import com.amazonaws.services.kms.model.ListKeysRequest;
import com.amazonaws.services.kms.model.ListKeysResult;

public class ListCustomerMasterKeys {
    public static void main(String[] args) {
        AWSKMS kmsClient = AWSKMSClientBuilder.standard().build();

        // List CMKs in this account

        Integer limit = 10;

        ListKeysRequest req = new ListKeysRequest().withLimit(limit);
        ListKeysResult result = kmsClient.listKeys(req);

    }
}

