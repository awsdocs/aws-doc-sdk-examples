/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.aws.rest;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementResponse;
import software.amazon.awssdk.services.rdsdata.model.RdsDataException;
import software.amazon.awssdk.services.rdsdata.model.SqlParameter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class InjectWorkService {
    public String injectNewSubmission(WorkItem item) {
        try {
            String name = item.getName();
            String guide = item.getGuide();
            String description = item.getDescription();
            String status = item.getStatus();
            String archived = "0";

            UUID uuid = UUID.randomUUID();
            String workId = uuid.toString();

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String sDate1 = dtf.format(now);
            Date date1 = new SimpleDateFormat("yyyy/MM/dd").parse(sDate1);
            java.sql.Date sqlDate = new java.sql.Date(date1.getTime());

            String sql = "INSERT INTO work (idwork, username, date, description, guide, status, archive) VALUES" +
                "(:idwork, :username, :date, :description, :guide, :status, :archive);";
            List<SqlParameter> paremeters = List.of(
                App.param("idwork", workId),
                App.param("username", name),
                App.param("date", sqlDate.toString()),
                App.param("description", description),
                App.param("guide", guide),
                App.param("status", status),
                App.param("archive", archived)
            );

            ExecuteStatementResponse result = App.execute(sql, paremeters);
            System.out.println(result.toString());
            return workId;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String modifySubmission(String id, String desc, String status) {
        try {
            App.execute(
                "UPDATE Work SET description = :description, status = :status WHERE idwork = :idwork;",
                List.of(
                    App.param("idwork", id),
                    App.param("description", desc),
                    App.param("status", status)
                )
            );
            return id;
        } catch (RdsDataException e) {
            e.printStackTrace();
        }
        return "";
    }
}