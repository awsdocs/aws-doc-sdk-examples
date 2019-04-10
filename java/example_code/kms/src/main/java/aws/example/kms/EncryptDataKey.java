//snippet-sourcedescription:[EncryptDataKey.java demonstrates how to encrypt data keys]
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
import com.amazonaws.services.kms.model.EncryptRequest;

import java.nio.ByteBuffer;

public class EncryptDataKey {
    public static void main(String[] args) {
        AWSKMS kmsClient = AWSKMSClientBuilder.standard().build();

        // Encrypt a data key
        //
        // Replace the following fictitious CMK ARN with a valid CMK ID or ARN
        String keyId = "arn:aws:kms:us-west-2:111122223333:key/1234abcd-12ab-34cd-56ef-1234567890ab";
        ByteBuffer plaintext = ByteBuffer.wrap(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0});

        EncryptRequest req = new EncryptRequest().withKeyId(keyId).withPlaintext(plaintext);
        ByteBuffer ciphertext = kmsClient.encrypt(req).getCiphertextBlob();
    }
}
