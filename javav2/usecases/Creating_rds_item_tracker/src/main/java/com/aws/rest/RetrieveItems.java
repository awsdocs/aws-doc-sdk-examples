/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.aws.rest;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList ;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class RetrieveItems {

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

    // Retrieves archive data from the MySQL database
    public List<WorkItem> getArchiveData(String username) {
         Connection c = null;
         List<WorkItem> itemList = new ArrayList<>();
         String query;
         WorkItem item;

         try {
             c = ConnectionHelper.getConnection();
             ResultSet rs;
             PreparedStatement pstmt;
             int arch = 1;

             // Specify the SQL Statement to query data.
             query = "Select idwork,username,date,description,guide,status FROM work where username = '" +username +"' and archive = " +arch +"";
             pstmt = c.prepareStatement(query);
             rs = pstmt.executeQuery();

             while (rs.next()) {
                 // For each record, create a WorkItem object.
                 item = new WorkItem();
                 item.setId(rs.getString(1));
                 item.setName(rs.getString(2));
                 item.setDate(rs.getDate(3).toString().trim());
                 item.setDescription(rs.getString(4));
                 item.setGuide(rs.getString(5));
                 item.setStatus(rs.getString(6));

                 // Push the WorkItem object to the list.
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

    // Get Items data from MySQL.
    public List<WorkItem> getItemsDataSQLReport(String username) {
        Connection c = null;
        List<WorkItem> itemList = new ArrayList<>();
        String query;
        WorkItem item;

        try {
            c = ConnectionHelper.getConnection();
            ResultSet rs = null;
            PreparedStatement pstmt;
            int arch = 0;

            // Specify the SQL Statement to query data.
            query = "Select idwork,username,date,description,guide,status FROM work where username = '" +username +"' and archive = " +arch +"";
            pstmt = c.prepareStatement(query);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                item = new WorkItem();
                item.setId(rs.getString(1));
                item.setName(rs.getString(2));
                item.setDate(rs.getDate(3).toString().trim());
                item.setDescription(rs.getString(4));
                item.setGuide(rs.getString(5));
                item.setStatus(rs.getString(6));

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
}