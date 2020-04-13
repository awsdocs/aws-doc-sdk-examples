package com.example.pinpoint;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.pinpoint.PinpointClient;
import software.amazon.awssdk.services.pinpoint.model.DirectMessageConfiguration;
import software.amazon.awssdk.services.pinpoint.model.*;

import java.util.HashMap;
import java.util.Map;

public class SendMessage {

    // The phone number or short code to send the message from. The phone number
    // or short code that you specify has to be associated with your Amazon Pinpoint
    // account. For best results, specify long codes in E.164 format.
    public static String originationNumber = "+1-613-839-7418";

    // The recipient's phone number.  For best results, you should specify the
    // phone number in E.164 format.
    public static String destinationNumber = "+1-819-576-5654";

    // The content of the SMS message.
    public static String message = "This message was sent through Amazon Pinpoint "
            + "using the AWS SDK for Java." ;

    // The Pinpoint project/application ID to use when you send this message.
    // Make sure that the SMS channel is enabled for the project or application
    // that you choose.
    public static String appId = "2fdc4442c6a2483f85eaf7a943054815";

    // The type of SMS message that you want to send. If you plan to send
    // time-sensitive content, specify TRANSACTIONAL. If you plan to send
    // marketing-related content, specify PROMOTIONAL.
    public static String messageType = "TRANSACTIONAL";

    // The registered keyword associated with the originating short code.
    public static String registeredKeyword = "myKeyword";

    // The sender ID to use when sending the message. Support for sender ID
    // varies by country or region. For more information, see
    // https://docs.aws.amazon.com/pinpoint/latest/userguide/channels-sms-countries.html
    public static String senderId = "MySenderID";

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "SendMessage -sends a message\n\n" +
                "Usage: CreateApp <appName>\n\n" +
                "Where:\n" +
                "  appId - the id of the application to delete.\n\n";

       // if (args.length < 1) {
        //    System.out.println(USAGE);
       //     System.exit(1);
       // }

 //       String appId = args[0];
        System.out.println("Sending a message" );

        PinpointClient pinpoint = PinpointClient.builder()
                .region(Region.US_EAST_1)
                .build();

        sendSMSMessage(pinpoint) ;
    }

    public static void sendSMSMessage(PinpointClient pinpoint) {

    try {

        Map<String, AddressConfiguration> addressMap =
                new HashMap<String, AddressConfiguration>();

        AddressConfiguration addConfig = AddressConfiguration.builder()
                .channelType(ChannelType.SMS)
                .build();

        addressMap.put(destinationNumber, addConfig);

        SMSMessage smsMessage = SMSMessage.builder()
                .body(message)
                .messageType(messageType)
                .originationNumber(originationNumber)
                .senderId(senderId)
                .keyword(registeredKeyword)
                .build();

        // Create a DirectMessageConfiguration object
        DirectMessageConfiguration direct = DirectMessageConfiguration.builder()
                .smsMessage(smsMessage)
                 .build();

        MessageRequest msgReq = MessageRequest.builder()
                .addresses(addressMap)
                .messageConfiguration(direct)
                .build();

        // create a  SendMessagesRequest object
        SendMessagesRequest request = SendMessagesRequest.builder()
                .applicationId(appId)
                .messageRequest(msgReq)
                .build();

        pinpoint.sendMessages(request);

        System.out.println("Done");

    } catch (PinpointException e) {
        e.getStackTrace();
    }
  }
}
