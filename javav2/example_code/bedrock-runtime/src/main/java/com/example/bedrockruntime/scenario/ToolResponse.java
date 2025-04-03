package com.example.bedrockruntime.scenario;

import software.amazon.awssdk.core.document.Document;

public class ToolResponse {
    private String toolUseId;
    private Document content;

    public String getToolUseId() {
        return toolUseId;
    }

    public void setToolUseId(String toolUseId) {
        this.toolUseId = toolUseId;
    }

    public Document getContent() {
        return content;
    }

    public void setContent(Document content) {
        this.content = content;
    }
}