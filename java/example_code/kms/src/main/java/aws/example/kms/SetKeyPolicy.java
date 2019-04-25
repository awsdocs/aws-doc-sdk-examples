//snippet-sourcedescription:[SetKeyPolicy.java demonstrates how to establish or change a key policy for a CMK.]
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
import com.amazonaws.services.kms.model.PutKeyPolicyRequest;

public class SetKeyPolicy {
    public static void main(String[] args) {
        AWSKMS kmsClient = AWSKMSClientBuilder.standard().build();
        // Set a key policy for a CMK
        //
        // Replace the following fictitious CMK ARN with a valid CMK ID or ARN
        String keyId = "1234abcd-12ab-34cd-56ef-1234567890ab";
        String policyName = "default";
        String policy = "{" +
                "  \"Version\": \"2012-10-17\"," +
                "  \"Statement\": [{" +
                "    \"Sid\": \"Allow access for ExampleUser\"," +
                "    \"Effect\": \"Allow\"," +
                // Replace the following user ARN with one for a real user.
                "    \"Principal\": {\"AWS\": \"arn:aws:iam::111122223333:user/ExampleUser\"}," +
                "    \"Action\": [" +
                "      \"kms:Encrypt\"," +
                "      \"kms:GenerateDataKey*\"," +
                "      \"kms:Decrypt\"," +
                "      \"kms:DescribeKey\"," +
                "      \"kms:ReEncrypt*\"" +
                "    ]," +
                "    \"Resource\": \"*\"" +
                "  }]" +
                "}";

        PutKeyPolicyRequest req = new PutKeyPolicyRequest().withKeyId(keyId).withPolicy(policy).withPolicyName(policyName);
        kmsClient.putKeyPolicy(req);

    }
}

