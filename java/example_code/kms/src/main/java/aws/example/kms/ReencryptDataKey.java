//snippet-sourcedescription:[ReencryptDataKey.java demonstrates how to decrypt an encrypted data key, and then immediately re-encrypt the data key under a different customer master key (CMK).]
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
import com.amazonaws.services.kms.model.ReEncryptRequest;

import java.nio.ByteBuffer;

public class ReencryptDataKey {
    public static void main(String[] args) {
        AWSKMS kmsClient = AWSKMSClientBuilder.standard().build();
        // Re-encrypt a data key

        ByteBuffer sourceCiphertextBlob = ByteBuffer.wrap(new byte[]{Byte.parseByte("Place your ciphertext here")});


        // Replace the following fictitious CMK ARN with a valid CMK ID or ARN
        String destinationKeyId = "arn:aws:kms:us-west-2:111122223333:key/0987dcba-09fe-87dc-65ba-ab0987654321";

        ReEncryptRequest req = new ReEncryptRequest();
        req.setCiphertextBlob(sourceCiphertextBlob);
        req.setDestinationKeyId(destinationKeyId);
        ByteBuffer destinationCipherTextBlob = kmsClient.reEncrypt(req).getCiphertextBlob();
    }
}

