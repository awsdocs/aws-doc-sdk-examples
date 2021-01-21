package com.example.gateway;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.apigateway.ApiGatewayClient;
import software.amazon.awssdk.services.apigateway.model.*;

import java.util.List;

public class GetAPIKeys {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage:\n" +
                "  SetAcl <bucketName> <objectKey> <id> \n\n" +
                "Where:\n" +
                " bucketName - the Amazon S3 bucket to grant permissions on. \n" +
                " objectKey - the object to grant permissions on. \n" +
                " id - the ID of the owner of this bucket (you can get this value from the AWS Management Console).\n"  ;

       // if (args.length != 3) {
       //     System.out.println(USAGE);
       //     System.exit(1);
       // }

        //String objectKey = args[1];
       // String id = args[2];

        System.out.format("Setting access \n");


        Region region = Region.US_EAST_1 ;
        ApiGatewayClient apiGateway = ApiGatewayClient.builder()
                .region(region)
                .build();


        try {
            GetApiKeysResponse response = apiGateway.getApiKeys();
            List<ApiKey> keys = response.items();
            for (ApiKey key: keys) {
                System.out.println("key id is: "+key.id());
            }

        int yt = 0;
        } catch (ApiGatewayException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }



        System.out.println("Done!");
        apiGateway.close();
    }

}
