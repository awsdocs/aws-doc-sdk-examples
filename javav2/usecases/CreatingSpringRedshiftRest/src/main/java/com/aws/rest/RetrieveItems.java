/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.aws.rest;

import java.util.List;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.redshiftdata.model.ExecuteStatementResponse;
import software.amazon.awssdk.services.redshiftdata.model.SqlParameter;

@Component
public class RetrieveItems {

    // Specify the database name, the database user, and the cluster Id value.
    static final String username = "user";

    SqlParameter param(String name, String value) {
        return SqlParameter.builder().name(name).value(value).build();
    }

    // Update the work table.
    public void flipItemArchive(String id ) {
        String arc = "1";
        String sqlStatement = "update work set archive = :arc where idwork =:id ";
        List<SqlParameter> parameters = List.of(
                param("arc", arc),
                param("id", id)
        );

        App.flipItemArchive(sqlStatement,parameters);
    }

    // Return items from the work table.
    public List<WorkItem> getData(String arch) {
        String sqlStatement = "SELECT idwork, date, description, guide, status, username "+
            "FROM work WHERE username = :username and archive = :arch ;";

        List<SqlParameter> parameters = List.of(
            param("username", username),
            param("arch", arch)
        );

        ExecuteStatementResponse response = App.execute(sqlStatement,parameters);
        String id = response.id();
        System.out.println("The identifier of the statement is "+id);
        App.checkStatement(id);
        return App.getResults(id);
    }
}

