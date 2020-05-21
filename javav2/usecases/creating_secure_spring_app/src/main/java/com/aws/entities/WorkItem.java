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
