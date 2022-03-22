/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/


package redshift;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.redshiftdata.model.*;
import software.amazon.awssdk.services.redshiftdata.RedshiftDataClient;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class RedshiftHandler implements RequestHandler<String, String> {

    String clusterId = "redshift-cluster-1";
    String database = "dev";
    String dbUser = "awsuser";

@Override
public String handleRequest(String event, Context context) {
        LambdaLogger logger = context.getLogger();
        String val = event ;
        delPost(val);
        logger.log("The Amazon Redshift record that was deleted is " +val);
        return val;
        }

    private RedshiftDataClient getClient() {

        Region region = Region.US_WEST_2;
        RedshiftDataClient redshiftDataClient = RedshiftDataClient.builder()
                .region(region)
                .build();

        return redshiftDataClient;
    }

    public void delPost(String id) {

        try {

            RedshiftDataClient redshiftDataClient = getClient();
            String sqlStatement = "DELETE FROM blog WHERE idblog = '" + id + "'";

            ExecuteStatementRequest statementRequest = ExecuteStatementRequest.builder()
                    .clusterIdentifier(clusterId)
                    .database(database)
                    .dbUser(dbUser)
                    .sql(sqlStatement)
                    .build();

            redshiftDataClient.executeStatement(statementRequest);

        } catch (RedshiftDataException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
