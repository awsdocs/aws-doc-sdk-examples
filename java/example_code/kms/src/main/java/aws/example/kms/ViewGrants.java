//snippet-sourcedescription:[ViewGrants.java demonstrates how to get detailed information about the grants on an AWS KMS customer master key .]
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
import com.amazonaws.services.kms.model.ListGrantsRequest;
import com.amazonaws.services.kms.model.ListGrantsResult;

public class ViewGrants {
    public static void main(String[] args) {
        AWSKMS kmsClient = AWSKMSClientBuilder.standard().build();

        // Listing grants on a CMK
        //
        // Replace the following fictitious CMK ARN with a valid CMK ID or ARN
        String keyId = "arn:aws:kms:us-west-2:111122223333:key/1234abcd-12ab-34cd-56ef-1234567890ab";
        Integer limit = 10;

        ListGrantsRequest request = new ListGrantsRequest()
                .withKeyId(keyId)
                .withLimit(limit);

        ListGrantsResult result = kmsClient.listGrants(request);
    }
}
