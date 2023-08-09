package org.example;

import java.util.HashMap;

public class QueuePayload {
    // The payload job token.
    private String token;

    // The Amazon Resource Name (ARN) of the pipeline run.
    private String pipelineExecutionArn;

    // The status of the job.
    private String status;

    // A dictionary of payload arguments.
    private HashMap<String, String> arguments;

    // Constructor
    public QueuePayload() {
    }

    // Getter and Setter methods for token
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    // Getter and Setter methods for pipelineExecutionArn
    public String getPipelineExecutionArn() {
        return pipelineExecutionArn;
    }

    public void setPipelineExecutionArn(String pipelineExecutionArn) {
        this.pipelineExecutionArn = pipelineExecutionArn;
    }

    // Getter and Setter methods for status
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Getter and Setter methods for arguments
    public HashMap<String, String> getArguments() {
        return arguments;
    }

    public void setArguments(HashMap<String, String> arguments) {
        this.arguments = arguments;
    }
}
