//snippet-sourcedescription:[StopDatabase.java demonstrates how to stop an Amazon Relational Database Service (RDS) instance.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon Relational Database Service]

package com.example.rds;

// snippet-start:[rds.java2.stop_instance.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.model.RdsException;
import software.amazon.awssdk.services.rds.model.StopDbInstanceRequest;
// snippet-end:[rds.java2.stop_instance.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class StopDatabase {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "    <dbInstanceIdentifier> \n\n" +
            "Where:\n" +
            "    dbInstanceIdentifier - The database instance identifier \n" ;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String dbInstanceIdentifier = args[0];
        Region region = Region.US_WEST_2;
        RdsClient rdsClient = RdsClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        stopInstance(rdsClient, dbInstanceIdentifier) ;
        rdsClient.close();
    }

    // snippet-start:[rds.java2.stop_instance.main]
    public static void stopInstance(RdsClient rdsClient, String dbInstanceIdentifier ) {

        try {
            StopDbInstanceRequest stopDbInstanceRequest = StopDbInstanceRequest.builder()
                .dbInstanceIdentifier(dbInstanceIdentifier)
                .build();

            rdsClient.stopDBInstance(stopDbInstanceRequest);
            System.out.print("The database was stopped!");

        } catch (RdsException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }
    // snippet-end:[rds.java2.stop_instance.main]
}

