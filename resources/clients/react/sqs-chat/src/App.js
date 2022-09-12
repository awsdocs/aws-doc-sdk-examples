// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import "@cloudscape-design/global-styles/index.css";
import {
  AppLayout,
  Box,
  Container,
  Header,
  SideNavigation,
  SpaceBetween,
} from "@cloudscape-design/components";
import React, { useEffect, useState } from "react";
import { eqBy, prop, unionWith } from "ramda";
import { ChatItems } from "./ChatItems";
import { MessageForm } from "./MessageForm";
import { getMessages, purgeMessages } from "./AwsService";

function App() {
  const [chats, setChats] = useState([]);

  const purgeChats = async () => {
    await purgeMessages();
    setChats([]);
  };

  useEffect(() => {
    const getChats = async () => {
      const { data: messages } = await getMessages();
      const uniqueChats = unionWith(eqBy(prop("id")), messages);
      setChats((c) => uniqueChats(c));
    };

    const tick = setInterval(() => {
      getChats();
    }, 5000);

    return () => clearInterval(tick);
  }, []);

  return (
    <AppLayout
      toolsHide={true}
      content={
        <Container
          header={
            <Header variant="h1">
              Amazon Simple Queue Service (Amazon SQS)
            </Header>
          }
        >
          <SpaceBetween size="l">
            <Box variant="p">
              A sample application that shows you how to work with Amazon SQS
              messages. Enter a message and a user and press{" "}
              <em>Send Message</em>
              to add to see an example.
              <br />
              Sending a message here posts a message to your SQS queue. This app
              is polling the SQS queue for new messages and displaying them
              below.
            </Box>
            <Header variant="h2">Messages</Header>
            <ChatItems chatItems={chats} />
            <MessageForm purgeHandler={purgeChats} />
          </SpaceBetween>
        </Container>
      }
      navigation={
        <SideNavigation
          header={{ text: "Resources" }}
          items={[
            {
              type: "link",
              text: "Amazon SQS Developer Guide",
              href: "https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/welcome.html",
              external: true,
            },
            {
              type: "link",
              text: "Sending messages",
              href: "https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-using-send-messages.html",
              external: true,
            },
            {
              type: "link",
              text: "Purging a queue",
              href: "https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-using-purge-queue.html",
              external: true,
            },
          ]}
        ></SideNavigation>
      }
    ></AppLayout>
  );
}

export default App;
