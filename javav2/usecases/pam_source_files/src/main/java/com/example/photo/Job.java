package com.example.photo;

import java.util.Objects;

public class Job {
    private final String jobId;
    private final String topicArn;

    public Job(String jobId, String topicArn) {
        this.jobId = jobId;
        this.topicArn = topicArn;
    }

    public String getJobId() {
        return jobId;
    }

    public String getTopicArn() {
        return topicArn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Job job = (Job) o;
        return Objects.equals(jobId, job.jobId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobId);
    }

    @Override
    public String toString() {
        return "Job{" +
                "jobId='" + jobId + '\'' +
                '}';
    }
}
