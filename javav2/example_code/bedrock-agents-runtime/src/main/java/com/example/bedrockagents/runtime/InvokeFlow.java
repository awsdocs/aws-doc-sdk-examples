// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package com.example.bedrockagents.runtime;

// snippet-start:[bedrock-agent.java2.InvokeFlow]
import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.document.Document;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockagentruntime.BedrockAgentRuntimeAsyncClient;
import software.amazon.awssdk.services.bedrockagentruntime.model.InvokeFlowRequest;
import software.amazon.awssdk.services.bedrockagentruntime.model.InvokeFlowResponse;
import software.amazon.awssdk.services.bedrockagentruntime.model.InvokeFlowResponseHandler;
import software.amazon.awssdk.services.bedrockagentruntime.model.FlowInput;
import software.amazon.awssdk.services.bedrockagentruntime.model.FlowInputContent;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.async.SdkPublisher;


/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */


public class InvokeFlow {

    public static String invokeFlowString(String[] args) {
        final String usage = """

        Usage:
           <flowId> <flowAliasId> <inputText>

        Where:
           flowId - An instance id value that you can obtain from the AWS Management Console.\s
           flowAliasId - An instance id value that you can obtain from the AWS Management Console.\s
           inputText - A monitoring status (true|false)""";


        if (args.length != 3) {
            System.out.println(usage);
            System.exit(1);
        }

        String flowId = args[0];
        String flowAliasId = args[1];
        String inputText = args[2];

        //Create Agent Runtime Async Client
        BedrockAgentRuntimeAsyncClient AgentClient = BedrockAgentRuntimeAsyncClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(Region.US_EAST_1)
                .build();
        

        //Create input prompt
        Document doc = Document.fromString(inputText);
        FlowInputContent flowInputContent = FlowInputContent.builder().document(doc).build();
        FlowInput flowInput = FlowInput.builder().nodeName("FlowInputNode").nodeOutputName("document").content(flowInputContent).build();

        //Create Invoke Flow Request
        InvokeFlowRequest invokeFlowRequest = InvokeFlowRequest.builder().flowAliasIdentifier(flowAliasId).flowIdentifier(flowId).inputs(flowInput).build();
        
        //Build a string buffer to contain all events 
        var completeResponseTextBuffer = new StringBuilder();

        //Invoke the Invoke Flow endpoint
        CompletableFuture<Void> future = AgentClient.invokeFlow(invokeFlowRequest,
        new InvokeFlowResponseHandler() {
                @Override
                public void responseReceived(InvokeFlowResponse response) {
                    System.out.println("Flow response received: " + response +"\n");
                    completeResponseTextBuffer.append(response.toString());
                }

                @Override
                @SuppressWarnings("unchecked")
                public void onEventStream(SdkPublisher publisher) {
                publisher.subscribe(event -> {
                        if (event instanceof SdkBytes) {
                            SdkBytes bytes = (SdkBytes) event;
                            System.out.println("Message: " + bytes.asUtf8String()+"\n");
                            completeResponseTextBuffer.append(bytes.asUtf8String());
                        } 
                        else {
                            System.out.println("Received event: " + event + "\n");
                            completeResponseTextBuffer.append(event.toString());
                        }
                });
                }

                @Override
                public void exceptionOccurred(Throwable throwable) {
                    System.err.println("Error occurred: " + throwable.getMessage());
                }

                @Override
                public void complete() {
                    System.out.println("Flow invocation completed");
                }
            });

        future.join();
        // Return the complete response text.
        return completeResponseTextBuffer.toString();

    }
}
// snippet-end:[bedrock-agent.java2.InvokeFlow]