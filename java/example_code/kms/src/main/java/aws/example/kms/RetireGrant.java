//snippet-sourcedescription:[RetireGrant.java demonstrates how to retire a grant for an AWS KMS customer master key.]
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
import com.amazonaws.services.kms.model.RetireGrantRequest;
import com.amazonaws.services.kms.model.RetireGrantResult;

public class RetireGrant {
    public static void main(String[] args) {
        AWSKMS kmsClient = AWSKMSClientBuilder.standard().build();

        // Retire a grant
        //
        String grantToken = "Place your grant token here";

        RetireGrantRequest request = new RetireGrantRequest().withGrantToken(grantToken);
        RetireGrantResult response = kmsClient.retireGrant(request);
    }
}