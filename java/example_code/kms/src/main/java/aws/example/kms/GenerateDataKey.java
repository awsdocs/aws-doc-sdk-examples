//snippet-sourcedescription:[GenerateDataKey.java demonstrates how to generate a data key for KMS. This operation returns plaintext and encrypted copies of the data key that it creates.]
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
import com.amazonaws.services.kms.model.GenerateDataKeyRequest;
import com.amazonaws.services.kms.model.GenerateDataKeyResult;

import java.nio.ByteBuffer;
import java.util.Base64;

public class GenerateDataKey {
    public static void main(String[] args) {
        final String USAGE =
            "To run this example, supply a key id or ARN and a KeySpec\n" +
            "Usage: GenerateDataKey <key-id> <key-spec>\n" +
            "Example: GenerateDataKey 1234abcd-12ab-34cd-56ef-1234567890ab" +
            " AES_256\n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String keyId = args[0];
        String keySpec = args[1];

        AWSKMS kmsClient = AWSKMSClientBuilder.standard().build();

        // Generate a data key

        GenerateDataKeyRequest dataKeyRequest = new GenerateDataKeyRequest();
        dataKeyRequest.setKeyId(keyId);
        dataKeyRequest.setKeySpec(keySpec);

        GenerateDataKeyResult dataKeyResult = kmsClient.generateDataKey(dataKeyRequest);

        ByteBuffer plaintextKey = dataKeyResult.getPlaintext();

        ByteBuffer encryptedKey = dataKeyResult.getCiphertextBlob();

        System.out.printf(
            "Successfully generated an encrypted data key: %s%n",
            Base64.getEncoder().encodeToString(encryptedKey.array())
        );

    }

}

