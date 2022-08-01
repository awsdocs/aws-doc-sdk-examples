//snippet-sourcedescription:[StartDatabase.java demonstrates how to start an Amazon Relational Database Service (RDS) instance.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon Relational Database Service]


package com.example.rds;

// snippet-start:[rds.java2.start_instance.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.model.RdsException;
import software.amazon.awssdk.services.rds.model.StartDbInstanceRequest;
// snippet-end:[rds.java2.start_instance.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class StartDatabase {

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

            startInstance(rdsClient, dbInstanceIdentifier) ;
            rdsClient.close();
        }

    // snippet-start:[rds.java2.start_instance.main]
    public static void startInstance(RdsClient rdsClient, String dbInstanceIdentifier ) {

        try {
            StartDbInstanceRequest startDbInstanceRequest = StartDbInstanceRequest.builder()
                .dbInstanceIdentifier(dbInstanceIdentifier)
                .build();

            rdsClient.startDBInstance(startDbInstanceRequest);
            System.out.print("The database was started!");

        } catch (RdsException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }
    // snippet-end:[rds.java2.start_instance.main]
}

