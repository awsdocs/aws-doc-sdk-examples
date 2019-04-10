//snippet-sourcedescription:[ViewCustomerMasterKey.java demonstrates get detailed information about a customer master key (CMK).]
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
import com.amazonaws.services.kms.model.DescribeKeyRequest;
import com.amazonaws.services.kms.model.DescribeKeyResult;

public class ViewCustomerMasterKey {
    public static void main(String[] args) {
        AWSKMS kmsClient = AWSKMSClientBuilder.standard().build();
        // Describe a CMK
        //
        // Replace the following fictitious CMK ARN with a valid CMK ID or ARN
        String keyId = "arn:aws:kms:us-west-2:111122223333:key/1234abcd-12ab-34cd-56ef-1234567890ab";

        DescribeKeyRequest req = new DescribeKeyRequest().withKeyId(keyId);
        DescribeKeyResult result = kmsClient.describeKey(req);

    }
}

