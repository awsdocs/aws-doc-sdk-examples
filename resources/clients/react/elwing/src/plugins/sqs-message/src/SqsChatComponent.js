/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import "@cloudscape-design/global-styles/index.css";
import {
  Box,
  Container,
  Header,
  Link,
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
    <SpaceBetween size="l">
      <Header variant="h1">Message</Header>
      <Box variant="p">
        A sample application that shows you how to work with Amazon SQS
        messages. Enter a message and a user and press <em>Send Message </em>
        to see an example.
        <br />
        Sending a message here posts a message to your SQS queue. This app is
        polling the SQS queue for new messages and displaying them below.
      </Box>
      <Header variant="h2">Services Used</Header>
      <ul>
        <li>
          <Link external href="https://aws.amazon.com/sqs/">
            Amazon Simple Queue Service (Amazon SQS)
          </Link>
        </li>
      </ul>
      <Header variant="h2">Available Backends</Header>
      <ul>
        <li>
          <Link
            external
            href="https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/usecases/creating_message_application"
          >
            Java
          </Link>
        </li>
      </ul>
      <Container header={<Header variant="h2">Messages</Header>}>
        <ChatItems chatItems={chats} />
        <MessageForm purgeHandler={purgeChats} />
      </Container>
    </SpaceBetween>
  );
}

export default App;
