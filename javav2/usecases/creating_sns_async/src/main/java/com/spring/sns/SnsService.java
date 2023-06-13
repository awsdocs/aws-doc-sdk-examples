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
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sns.model.ListSubscriptionsByTopicResponse;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;
import software.amazon.awssdk.services.sns.model.SubscribeResponse;
import software.amazon.awssdk.services.sns.model.Subscription;
import software.amazon.awssdk.services.sns.model.UnsubscribeRequest;
import software.amazon.awssdk.services.translate.TranslateAsyncClient;
import software.amazon.awssdk.services.translate.model.TranslateTextRequest;
import software.amazon.awssdk.services.translate.model.TranslateTextResponse;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class SnsService {
    String topicArn = "<Enter the topic ARN>";

    private SnsAsyncClient getSnsClient() {
        return SnsAsyncClient.builder()
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .region(Region.US_WEST_2)
            .build();
    }

    public void unSubEmail(String emailEndpoint) {
        try {
            String subscriptionArn = getTopicArnValue(emailEndpoint);
            SnsAsyncClient snsAsyncClient = getSnsClient();
            UnsubscribeRequest request = UnsubscribeRequest.builder()
                .subscriptionArn(subscriptionArn)
                .build();

            snsAsyncClient.unsubscribe(request);

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    // Returns the Sub ARN based on the given endpoint
    private String getTopicArnValue(String endpoint){
        final AtomicReference<String> reference = new AtomicReference<>();
        SnsAsyncClient snsAsyncClient = getSnsClient();
        try {
            ListSubscriptionsByTopicRequest request = ListSubscriptionsByTopicRequest.builder()
                .topicArn(topicArn)
                .build();

            CompletableFuture<ListSubscriptionsByTopicResponse> futureGet  = snsAsyncClient.listSubscriptionsByTopic(request);
            futureGet.whenComplete((resp, err) -> {
                List<Subscription> allSubs  = resp.subscriptions();
                for (Subscription sub: allSubs) {

                    if (sub.endpoint().compareTo(endpoint)==0)
                        reference.set(sub.subscriptionArn());
                }
            });
            futureGet.join();
            return reference.get();

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    // Create a Subscription.
    public String subEmail(String email) {
        final AtomicReference<String> reference = new AtomicReference<>();
        try {
            SnsAsyncClient snsAsyncClient = getSnsClient();
            SubscribeRequest request = SubscribeRequest.builder()
                .protocol("email")
                .endpoint(email)
                .returnSubscriptionArn(true)
                .topicArn(topicArn)
                .build();

            CompletableFuture<SubscribeResponse> futureGet  = snsAsyncClient.subscribe(request);
            futureGet.whenComplete((resp, err) -> {
                String subscriptionArn = resp.subscriptionArn();
                reference.set(subscriptionArn);
            });
            futureGet.join();

            return reference.get();

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    public String getAllSubscriptions() {
        final AtomicReference<List<String>> reference = new AtomicReference<>();
        List<String> subList = new ArrayList<>() ;
        try {
            SnsAsyncClient snsAsyncClient = getSnsClient();
            ListSubscriptionsByTopicRequest request = ListSubscriptionsByTopicRequest.builder()
                .topicArn(topicArn)
                .build();

            CompletableFuture<ListSubscriptionsByTopicResponse> futureGet  = snsAsyncClient.listSubscriptionsByTopic(request);
            futureGet.whenComplete((resp, err) -> {

                List<Subscription> allSubs  = resp.subscriptions();
                for (Subscription sub: allSubs) {
                    subList.add(sub.endpoint());
                }
                reference.set(subList);
            });
            futureGet.join();

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return convertToString(toXml(reference.get()));
    }



      public String pubTopic(String message, String lang) {

        final AtomicReference<Integer> reference = new AtomicReference<>();
        String body;

        if (lang.compareTo("English")==0)
            body = message;
        else if(lang.compareTo("French")==0)
            body = translateBody(message, "fr");
        else
            body = translateBody(message, "es");

        try {
            SnsAsyncClient snsAsyncClient = getSnsClient();
            PublishRequest request = PublishRequest.builder()
                .message(body)
                .topicArn(topicArn)
                .build();

            CompletableFuture<PublishResponse> futureGet  = snsAsyncClient.publish(request);
            futureGet.whenComplete((resp, err) -> {
                reference.set(resp.sdkHttpResponse().statusCode());
            });
            futureGet.join();
            return " Message sent in " +lang +". Status was " + reference.get();

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "Error - msg not sent";
    }

    private String translateBody(String message, String lan)
    {
        final AtomicReference<String> reference = new AtomicReference<>();
        Region region = Region.US_WEST_2;
        TranslateAsyncClient translateClient = TranslateAsyncClient.builder()
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .region(region)
            .build();

        TranslateTextRequest textRequest = TranslateTextRequest.builder()
            .sourceLanguageCode("en")
            .targetLanguageCode(lan)
            .text(message)
            .build();

        CompletableFuture<TranslateTextResponse> futureGet = translateClient.translateText(textRequest);;
        futureGet.whenComplete((resp, err) -> {
            reference.set(resp.translatedText());
        });
        futureGet.join();
        return reference.get();
    }

    // Convert the list to XML to pass back to the view.
    private Document toXml(List<String> subsList) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
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
            TransformerFactory transformerFactory = getSecureTransformerFactory();
            Transformer transformer = transformerFactory.newTransformer();
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(xml);
            transformer.transform(source, result);
            return result.getWriter().toString();

        } catch(TransformerException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private static TransformerFactory getSecureTransformerFactory() {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {
            transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        return transformerFactory;
    }
}