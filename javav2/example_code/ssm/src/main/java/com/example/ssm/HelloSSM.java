package com.example.ssm;

// snippet-start:[ssm.java2.hello.main]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.DocumentFilter;
import software.amazon.awssdk.services.ssm.model.ListDocumentsRequest;
import software.amazon.awssdk.services.ssm.paginators.ListDocumentsIterable;

public class HelloSSM {

    public static void main(String[] args) {

        String awsAccount = "814548047983" ;
        Region region = Region.US_EAST_1;
        SsmClient ssmClient = SsmClient.builder()
            .region(region)
            .build();

        listDocuments(ssmClient, awsAccount);
    }

    public static void listDocuments(SsmClient ssmClient, String awsAccount) {
        ListDocumentsRequest request = ListDocumentsRequest.builder()
            .documentFilterList(
                DocumentFilter.builder()
                    .key("Owner")
                    .value(awsAccount)
                    .build()
            )
            .maxResults(50)
            .build();

        ListDocumentsIterable response = ssmClient.listDocumentsPaginator(request);
        response.documentIdentifiers().forEach(identifier -> System.out.println("Document Name: " + identifier.name()));
    }
}
// snippet-end:[ssm.java2.hello.main]