/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at
    http://aws.amazon.com/apache2.0/
   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

package com.aws.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

import com.aws.entities.WorkItem;
import org.springframework.stereotype.Component;

@Component
public class InjectWorkService {

    //Inject a new submission
    public String modifySubmission(String id, String desc, String status)
    {
        Connection c = null;
        int rowCount= 0;
        try {
            // Create a Connection object
            c =  ConnectionHelper.getConnection();

            //Use prepared statements to protected against SQL injection attacks
            //  PreparedStatement pstmt = null;
            PreparedStatement ps = null;


            String query = "update work set description = ?, status = ? where idwork = '" +id +"'";

            ps = c.prepareStatement(query);
            ps.setString(1, desc);
            ps.setString(2, status);
            ps.execute();
            return id;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            ConnectionHelper.close(c);
        }
        return null;
    }

    //Inject a new submission
    public String injestNewSubmission(WorkItem item)
    {
        Connection c = null;
        int rowCount= 0;
        try {

            // Create a Connection object
            c =  ConnectionHelper.getConnection();

            // Use a prepared statement
            PreparedStatement ps = null;

            //Convert rev to int
            String name = item.getName();
            String guide = item.getGuide();
            String description = item.getDescription();
            String status = item.getStatus();

            //generate the work item ID
            UUID uuid = UUID.randomUUID();
            String workId = uuid.toString();

            //Date conversion
            // Date date1 = new SimpleDateFormat("yyyy/mm/dd").parse(date);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String sDate1 =   dtf.format(now);
            Date date1 = new SimpleDateFormat("yyyy/MM/dd").parse(sDate1);
            java.sql.Date sqlDate = new java.sql.Date( date1.getTime());

            //Inject a new Formstr template into the system
            String insert = "INSERT INTO work (idwork, username,date,description, guide, status, archive) VALUES(?,?, ?,?,?,?,?);";
            ps = c.prepareStatement(insert);
            ps.setString(1, workId);
            ps.setString(2, name);
            ps.setDate(3, sqlDate);
            ps.setString(4, description);
            ps.setString(5, guide );
            ps.setString(6, status );
            ps.setBoolean(7, false);
            ps.execute();
            return workId;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            ConnectionHelper.close(c);
        }
        return null;
    }
}
