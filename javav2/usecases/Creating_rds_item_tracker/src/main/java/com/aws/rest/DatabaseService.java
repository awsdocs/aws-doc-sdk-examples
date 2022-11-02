/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.aws.rest;

import org.springframework.stereotype.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class DatabaseService {

    // Set the specified item to archive.
    public void flipItemArchive(String id) {
        Connection c = null;
        String query;
        try {
            c = ConnectionHelper.getConnection();
            query = "update work set archive = ? where idwork ='" +id + "' ";
            PreparedStatement updateForm = c.prepareStatement(query);
            updateForm.setBoolean(1, true);
            updateForm.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionHelper.close(c);
        }
    }

    // Get Items data from MySQL.
    public List<WorkItem> getItemsDataSQLReport(int flag) {
        Connection c = null;
        List<WorkItem> itemList = new ArrayList<>();
        String query;
        String username = "user";
        WorkItem item;

        try {
            c = ConnectionHelper.getConnection();
            ResultSet rs = null;
            PreparedStatement pstmt = null;
            if (flag == 0) {
                // Retrieves active data from the MySQL database
                int arch = 0;
                query = "Select idwork,username,date,description,guide,status,archive FROM work where username=? and archive=?;";
                pstmt = c.prepareStatement(query);
                pstmt.setString(1, username);
                pstmt.setInt(2, arch);
                rs = pstmt.executeQuery();
            }else if (flag == 1)  {
                // Retrieves archive data from the MySQL database
                int arch = 1;
                query = "Select idwork,username,date,description,guide,status, archive  FROM work where username=? and archive=?;";
                pstmt = c.prepareStatement(query);
                pstmt.setString(1, username);
                pstmt.setInt(2, arch);
                rs = pstmt.executeQuery();
            } else {
                // Retrieves all data from the MySQL database
                query = "Select idwork,username,date,description,guide,status, archive FROM work";
                pstmt = c.prepareStatement(query);
                rs = pstmt.executeQuery();
            }

            while (rs.next()) {
                item = new WorkItem();
                item.setId(rs.getString(1));
                item.setName(rs.getString(2));
                item.setDate(rs.getDate(3).toString().trim());
                item.setDescription(rs.getString(4));
                item.setGuide(rs.getString(5));
                item.setStatus(rs.getString(6));
                item.setArchived(rs.getBoolean(7));

                // Push the WorkItem Object to the list.
                itemList.add(item);
            }
            return itemList;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionHelper.close(c);
        }
        return null;
    }

    // Inject a new submission.
    public void injestNewSubmission(WorkItem item) {
        Connection c = null;
        try {
            c = ConnectionHelper.getConnection();
            PreparedStatement ps;

            // Convert rev to int.
            String name = item.getName();
            String guide = item.getGuide();
            String description = item.getDescription();
            String status = item.getStatus();

            // Generate the work item ID.
            UUID uuid = UUID.randomUUID();
            String workId = uuid.toString();

            // Date conversion.
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String sDate1 = dtf.format(now);
            Date date1 = new SimpleDateFormat("yyyy/MM/dd").parse(sDate1);
            java.sql.Date sqlDate = new java.sql.Date( date1.getTime());

            // Inject an item into the system.
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

        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        } finally {
            ConnectionHelper.close(c);
        }
    }
}
