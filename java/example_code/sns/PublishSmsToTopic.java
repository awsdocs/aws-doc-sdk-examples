// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0


// snippet-start:[sns.java.publish_sms_to_topic.main]
public static void main(String[] args) {
    AmazonSNSClient snsClient = new AmazonSNSClient();

    String topicArn = createSNSTopic(snsClient);
    String phoneNumber = "+1XXX5550100";
    // Specify a protocol of "sms" when subscribing a phone number.
    subscribeToTopic(snsClient, topicArn, "sms", phoneNumber);

    String message = "My SMS message";
    Map<String, MessageAttributeValue> smsAttributes =
        new HashMap<String, MessageAttributeValue>();
    addMessageAttributes(smsAttributes)
    sendSMSMessageToTopic(snsClient, topicArn, message, smsAttributes);
}
// snippet-end:[sns.java.publish_sms_to_topic.main]

// snippet-start:[sns.java.publish_sms_to_topic.create_topic]
public static String createSNSTopic(AmazonSNSClient snsClient) {
    CreateTopicRequest createTopic = new CreateTopicRequest("mySNSTopic");
    CreateTopicResult result = snsClient.createTopic(createTopic);
    System.out.println("Create topic request: " +
        snsClient.getCachedResponseMetadata(createTopic));
    System.out.println("Create topic result: " + result);
    return result.getTopicArn();
}
// snippet-end:[sns.java.publish_sms_to_topic.create_topic]

// snippet-start:[sns.java.publish_sms_to_topic.subscribe]
public static void subscribeToTopic(AmazonSNSClient snsClient, String topicArn,
        String protocol, String endpoint) {
    SubscribeRequest subscribe = new SubscribeRequest(topicArn, protocol, endpoint);
    SubscribeResult subscribeResult = snsClient.subscribe(subscribe);
    System.out.println("Subscribe request: " +
        snsClient.getCachedResponseMetadata(subscribe));
    System.out.println("Subscribe result: " + subscribeResult);
}
// snippet-end:[sns.java.publish_sms_to_topic.subscribe]

// snippet-start:[sns.java.publish_sms_to_topic.set_message_attributes]
public static void addMessageAttributes(Map<String, MessageAttributeValue> smsAttributes) {
    smsAttributes.put("AWS.SNS.SMS.SenderID", new MessageAttributeValue()
        .withStringValue("mySenderID") //The sender ID shown on the device.
        .withDataType("String"));
    smsAttributes.put("AWS.SNS.SMS.MaxPrice", new MessageAttributeValue()
        .withStringValue("0.50") //Sets the max price to 0.50 USD.
        .withDataType("Number"));
    smsAttributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue()
        .withStringValue("Promotional") //Sets the type to promotional.
        .withDataType("String"));
}
// snippet-end:[sns.java.publish_sms_to_topic.set_message_attributes]

// snippet-start:[sns.java.publish_sms_to_topic.publish]
public static void sendSMSMessageToTopic(AmazonSNSClient snsClient, String topicArn,
        String message, Map<String, MessageAttributeValue> smsAttributes) {
    PublishResult result = snsClient.publish(new PublishRequest()
        .withTopicArn(topicArn)
        .withMessage(message)
        .withMessageAttributes(smsAttributes));
    System.out.println(result);
}
// snippet-end:[sns.java.publish_sms_to_topic.publish]
