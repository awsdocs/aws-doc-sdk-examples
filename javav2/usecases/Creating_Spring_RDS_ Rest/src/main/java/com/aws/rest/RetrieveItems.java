/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.aws.rest;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.rdsdata.model.RdsDataException;
import software.amazon.awssdk.services.rdsdata.model.SqlParameter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RetrieveItems {
    public void flipItemArchive(String id) {
        try {
            String sqlStatement = "UPDATE work SET archive = (:arch) WHERE idwork = (:id);";
            List<SqlParameter> parameters = List.of(
                App.param("id", id),
                App.param("arch", App.archived)
            );
            App.execute(sqlStatement, parameters);
        } catch (RdsDataException e) {
            e.printStackTrace();
        }
    }

    public List<WorkItem> getItemsDataSQLReport(String archived) {
        try {
            String sqlStatement = "SELECT idwork, date, description, guide, status, username "+
                "FROM work WHERE username = :username and archive = :arch ;";
            List<SqlParameter> parameters = List.of(
                App.param("username", App.username),
                App.param("arch", archived)
            );
            return App.execute(sqlStatement, parameters)
                .records()
                .stream()
                .map(WorkItem::from)
                .collect(Collectors.toUnmodifiableList());
        } catch (RdsDataException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}