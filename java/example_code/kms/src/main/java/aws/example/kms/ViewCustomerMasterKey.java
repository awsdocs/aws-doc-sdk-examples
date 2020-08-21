//snippet-sourcedescription:[ViewCustomerMasterKey.java demonstrates get detailed information about a customer master key (CMK).]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
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
import com.amazonaws.services.kms.model.KeyMetadata;

public class ViewCustomerMasterKey {
    public static void main(String[] args) {
        final String USAGE =
            "To run this example, supply a key id or ARN\n" +
            "Usage: ViewCustomerMasterKey <key-id>\n" +
            "Example: ViewCustomerMasterKey 1234abcd-12ab-34cd-56ef-1234567890ab\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String keyId = args[0];

        AWSKMS kmsClient = AWSKMSClientBuilder.standard().build();

        // Describe a CMK

        DescribeKeyRequest req = new DescribeKeyRequest().withKeyId(keyId);
        DescribeKeyResult result = kmsClient.describeKey(req);

        KeyMetadata metadata = result.getKeyMetadata();

        System.out.printf("%-15s %s%n", "KeyId:", keyId);
        System.out.printf("%-15s %s%n", "Arn:", metadata.getArn());
        System.out.printf("%-15s %s%n", "CreationDate:", metadata.getCreationDate());
        System.out.printf("%-15s %s%n", "Description:", metadata.getDescription());
        System.out.printf("%-15s %s%n", "KeyUsage:", metadata.getKeyUsage());
        System.out.printf("%-15s %s%n", "KeyState:", metadata.getKeyState());
        System.out.printf("%-15s %s%n", "Origin:", metadata.getOrigin());
        System.out.printf("%-15s %s%n", "KeyManager:", metadata.getKeyManager());

    }
}

