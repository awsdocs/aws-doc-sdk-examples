/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.aws.rest;

import software.amazon.awssdk.services.redshiftdata.model.Field;

import java.util.List;

public class WorkItem {
    private String id;
    private String name;
    private String guide;
    private String date;
    private String description;
    private String status;

    public static WorkItem from(List<Field> fields) {
        var item = new WorkItem();
        for (int i = 0; i <= 5; i++) {
            String value = fields.get(i).stringValue();
            switch (i) {
                case 0:
                    item.setId(value);
                    break;
                case 1:
                    item.setDate(value);
                    break;
                case 2:
                    item.setDescription(value);
                    break;
                case 3:
                    item.setGuide(value);
                    break;
                case 4:
                    item.setStatus(value);
                    break;
                case 5:
                    item.setName(value);
                    break;
            }
        }
        return item;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }


    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return this.date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setGuide(String guide) {
        this.guide = guide;
    }

    public String getGuide() {
        return this.guide;
    }
}