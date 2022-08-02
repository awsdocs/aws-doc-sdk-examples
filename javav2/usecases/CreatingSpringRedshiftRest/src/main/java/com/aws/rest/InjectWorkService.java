package com.aws.rest;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.redshiftdata.RedshiftDataClient;
import software.amazon.awssdk.services.redshiftdata.model.ExecuteStatementRequest;
import software.amazon.awssdk.services.redshiftdata.model.RedshiftDataException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

@Component
public class InjectWorkService {

    private String database = "dev";
    private String dbUser ="awsuser";
    private String clusterId = "redshift-cluster-1";

    // Inject a new submission.
    public String injestNewSubmission(WorkItem item) {

        Region region = Region.US_WEST_2;
        RedshiftDataClient redshiftDataClient = RedshiftDataClient.builder()
                        .region(region)
                        .credentialsProvider(ProfileCredentialsProvider.create())
                        .build();
        try {

            String name = item.getName();
            String guide = item.getGuide();
            String description = item.getDescription();
            String status = item.getStatus();
            int arc = 0;

            // Generate the work item ID.
            UUID uuid = UUID.randomUUID();
            String workId = uuid.toString();

            // Date conversion.
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String sDate1 = dtf.format(now);
            Date date1 = new SimpleDateFormat("yyyy/MM/dd").parse(sDate1);
            java.sql.Date sqlDate = new java.sql.Date(date1.getTime());

            // Inject an item into the system.
            String sqlStatement = "INSERT INTO work (idwork, username,date,description, guide, status, archive) VALUES('" + workId + "', '" + name + "', '" + sqlDate + "','" + description + "','" + guide + "','" + status + "','" + arc + "');";
            ExecuteStatementRequest statementRequest = ExecuteStatementRequest.builder()
                          .clusterIdentifier(clusterId)
                          .database(database)
                          .dbUser(dbUser)
                          .sql(sqlStatement)
                          .build();

            redshiftDataClient.executeStatement(statementRequest);
            return workId;

        } catch (RedshiftDataException | ParseException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }
}