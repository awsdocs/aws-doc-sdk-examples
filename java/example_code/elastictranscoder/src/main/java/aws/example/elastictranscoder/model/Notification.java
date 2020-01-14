//snippet-sourcedescription:[Notification.java provides a model for job notifications.]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Elastic Transcoder]
//snippet-service:[elastictranscoder]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]
/*
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
// snippet-start:[elastictranscoder.java.notification.import]
package com.amazonaws.services.elastictranscoder.samples.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Notification<MESSAGE_TYPE> {

    private String type;
    private String messageId;
    private String topicArn;
    private String subject;
    private MESSAGE_TYPE message;
    private String timestamp;
    private String signatureVersion;
    private String signature;
    private String signingCertURL;
    private String unsubscribeURL;
    
    @JsonProperty(value="Type")
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    @JsonProperty(value="MessageId")
    public String getMessageId() {
        return messageId;
    }
    
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    
    @JsonProperty(value="TopicArn")
    public String getTopicArn() {
        return topicArn;
    }
    
    public void setTopicArn(String topicArn) {
        this.topicArn = topicArn;
    }
    
    @JsonProperty(value="Subject")
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    @JsonProperty(value="Message")
    public MESSAGE_TYPE getMessage() {
        return message;
    }
    
    public void setNotification(MESSAGE_TYPE message) {
        this.message = message;
    }
    
    @JsonProperty(value="Timestamp")
    public String getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
    @JsonProperty(value="SignatureVersion")
    public String getSignatureVersion() {
        return signatureVersion;
    }
    
    public void setSignatureVersion(String signatureVersion) {
        this.signatureVersion = signatureVersion;
    }
    
    @JsonProperty(value="Signature")
    public String getSignature() {
        return signature;
    }
    
    public void setSignature(String signature) {
        this.signature = signature;
    }
    
    @JsonProperty(value="SigningCertURL")
    public String getSigningCertURL() {
        return signingCertURL;
    }
    
    public void setSigningCertURL(String signingCertURL) {
        this.signingCertURL = signingCertURL;
    }
    
    @JsonProperty(value="UnsubscribeURL")
    public String getUnsubscribeURL() {
        return unsubscribeURL;
    }
    
    public void setUnsubscribeURL(String unsubscribeURL) {
        this.unsubscribeURL = unsubscribeURL;
    }
    
    @Override
    public String toString() {
        return "Notification [type=" + type + ", messageId=" + messageId
                + ", topicArn=" + topicArn + ", subject=" + subject
                + ", message=" + message + ", timestamp=" + timestamp
                + ", signatureVersion=" + signatureVersion + ", signature="
                + signature + ", signingCertURL=" + signingCertURL
                + ", unsubscribeURL=" + unsubscribeURL + "]";
    }
}
// snippet-end:[elastictranscoder.java.notification.import]
