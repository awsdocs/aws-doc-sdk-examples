//snippet-sourcedescription:[ ListAliases.java demonstrates how to list aliases in the account and region.]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon KMS]
//snippet-service:[kms]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2019-04-09]
//snippet-sourceauthor:[AWS]

package aws.example.kms;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.AliasListEntry;
import com.amazonaws.services.kms.model.ListAliasesRequest;
import com.amazonaws.services.kms.model.ListAliasesResult;

public class ListAliases {
    public static void main(String[] args) {
        AWSKMS kmsClient = AWSKMSClientBuilder.standard().build();

        // List the aliases in this AWS account
        //
        Integer limit = 10;

        String nextMarker = null;
        do {
            ListAliasesRequest req = new ListAliasesRequest()
                .withMarker(nextMarker).withLimit(limit);
            ListAliasesResult result = kmsClient.listAliases(req);
            for (AliasListEntry alias : result.getAliases()) {
                System.out.printf("Found an alias named \"%s\".%n", alias.getAliasName());
            }
            nextMarker = result.getNextMarker();
        } while (nextMarker != null);

    }
}
