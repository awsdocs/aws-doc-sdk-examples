/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.spring.sns;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.sns.model.ListSubscriptionsByTopicRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;
import software.amazon.awssdk.services.translate.TranslateClient;
import software.amazon.awssdk.services.translate.model.TranslateTextRequest;
import software.amazon.awssdk.services.translate.model.TranslateTextResponse;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

@Component
public class SnsService {

    String topicArn = "arn:aws:sns:us-west-2:814548047983:MyMailTopic";

    private SnsClient getSnsClient() {

        Region region = Region.US_WEST_2;
        SnsClient snsClient = SnsClient.builder()
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .region(region)
                .build();

        return snsClient;
    }

    public String pubTopic(String message, String lang) {

        try {
            String body;
            Region region = Region.US_WEST_2;
            TranslateClient translateClient = TranslateClient.builder()
                    .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                    .region(region)
                    .build();


            if (lang.compareTo("English")==0) {
                    body = message;

            } else if(lang.compareTo("French")==0) {

                    TranslateTextRequest textRequest = TranslateTextRequest.builder()
                            .sourceLanguageCode("en")
                            .targetLanguageCode("fr")
                            .text(message)
                            .build();

                    TranslateTextResponse textResponse = translateClient.translateText(textRequest);
                    body = textResponse.translatedText();

            } else  {

                TranslateTextRequest textRequest = TranslateTextRequest.builder()
                        .sourceLanguageCode("en")
                        .targetLanguageCode("es")
                        .text(message)
                        .build();

                TranslateTextResponse textResponse = translateClient.translateText(textRequest);
                body = textResponse.translatedText();
            }

            SnsClient snsClient =  getSnsClient();
            PublishRequest request = PublishRequest.builder()
                    .message(body)
                    .topicArn(topicArn)
                    .build();

            PublishResponse result = snsClient.publish(request);
            return " Message sent in " +lang +". Status was " + result.sdkHttpResponse().statusCode();

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "Error - msg not sent";
    }

    public void unSubEmail(String emailEndpoint) {

     try {

         String subscriptionArn = getTopicArnValue(emailEndpoint);
         SnsClient snsClient =  getSnsClient();

         UnsubscribeRequest request = UnsubscribeRequest.builder()
                 .subscriptionArn(subscriptionArn)
                 .build();

         snsClient.unsubscribe(request);

     } catch (SnsException e) {
        System.err.println(e.awsErrorDetails().errorMessage());
        System.exit(1);
    }
  }

  // Returns the Sub ARN based on the given endpoint
  private String getTopicArnValue(String endpoint){

        SnsClient snsClient =  getSnsClient();
        try {
            String subArn = "";
            ListSubscriptionsByTopicRequest request = ListSubscriptionsByTopicRequest.builder()
                    .topicArn(topicArn)
                    .build();


            ListSubscriptionsByTopicResponse result = snsClient.listSubscriptionsByTopic(request);
            List<Subscription> allSubs  = result.subscriptions();

            for (Subscription sub: allSubs) {

            if (sub.endpoint().compareTo(endpoint)==0) {

                subArn = sub.subscriptionArn();
                return subArn;
            }
         }
        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
      return "";
  }




    // Create a Subscription.
    public String subEmail(String email) {

       try {
            SnsClient snsClient =  getSnsClient();
            SubscribeRequest request = SubscribeRequest.builder()
                    .protocol("email")
                    .endpoint(email)
                    .returnSubscriptionArn(true)
                    .topicArn(topicArn)
                    .build();

            SubscribeResponse result = snsClient.subscribe(request);
            return result.subscriptionArn() ;

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }


    public String getAllSubscriptions() {


        List subList = new ArrayList<String>() ;

        try {
            SnsClient snsClient =  getSnsClient();
            ListSubscriptionsByTopicRequest request = ListSubscriptionsByTopicRequest.builder()
                    .topicArn(topicArn)
                    .build();

            ListSubscriptionsByTopicResponse result = snsClient.listSubscriptionsByTopic(request);
            List<Subscription> allSubs  = result.subscriptions();

            for (Subscription sub: allSubs) {
                subList.add(sub.endpoint());
            }

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return convertToString(toXml(subList));
    }

   // Convert the list to XML to pass back to the view.
    private Document toXml(List<String> subsList) {

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            // Start building the XML.
            Element root = doc.createElement("Subs");
            doc.appendChild(root);

            // Iterate through the collection.
            for (String sub : subsList) {

                Element item = doc.createElement("Sub");
                root.appendChild(item);

                // Set email
                Element email = doc.createElement("email");
                email.appendChild(doc.createTextNode(sub));
                item.appendChild(email);
            }

            return doc;

        }catch(ParserConfigurationException e){
            e.printStackTrace();
        }
        return null;
    }


    private String convertToString(Document xml) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(xml);
            transformer.transform(source, result);
            return result.getWriter().toString();

        } catch(TransformerException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}



