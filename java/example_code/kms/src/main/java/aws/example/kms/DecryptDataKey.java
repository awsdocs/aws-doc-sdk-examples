//snippet-sourcedescription:[DecryptDataKey.java demonstrates how to decrypt a data key.]
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
import com.amazonaws.services.kms.model.DecryptRequest;

import java.nio.ByteBuffer;

public class DecryptDataKey {
    public static void main(String[] args) {
        AWSKMS kmsClient = AWSKMSClientBuilder.standard().build();
        // Decrypt a data key
        //

        ByteBuffer ciphertextBlob = ByteBuffer.wrap(new byte[]{Byte.parseByte("Place your ciphertext here")});

        DecryptRequest req = new DecryptRequest().withCiphertextBlob(ciphertextBlob);
        ByteBuffer plainText = kmsClient.decrypt(req).getPlaintext();

    }
}
