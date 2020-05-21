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
package com.aws.entities;

public class WorkItem {

    private String id;
    private String name;
    private String guide ;
    private String date;
    private String description;
    private String status;

    public void SetId (String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return this.id;
    }

    public void SetStatus (String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return this.status;
    }

    public void SetDescription (String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return this.description;
    }

    public void SetDate (String date)
    {
        this.date = date;
    }

    public String getDate()
    {
        return this.date;
    }


    public void SetName (String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public void SetGuide (String guide)
    {
        this.guide = guide;
    }

    public String getGuide()
    {
        return this.guide;
    }
}
