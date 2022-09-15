import {
  Box,
  Button,
  Form,
  FormField,
  Header,
  Input,
  Modal,
  SpaceBetween,
} from "@cloudscape-design/components";
import React, { useState } from "react";
import * as service from "./AwsService";

export const MessageForm = ({ purgeHandler }) => {
  const [message, setMessage] = useState("");
  const [username, setUser] = useState("");
  const [purgeAlertVisible, setPurgeAlertVisible] = useState(false);

  const handleSubmit = (event) => {
    event.preventDefault();
    service.postMessage({ message, username });
    setMessage("");
    setUser("");
  };

  const handlePurgeConfirmation = () => {
    purgeHandler();
    setPurgeAlertVisible(false);
  };

  const handlePurgeCancellation = () => {
    setPurgeAlertVisible(false);
  };

  return (
    <>
      <form onSubmit={handleSubmit}>
        <Form
          header={<Header variant="h2">Send Message</Header>}
          actions={
            <SpaceBetween direction="horizontal" size="xs">
              <Button variant="primary">Send Message</Button>
              <Button
                variant="normal"
                formAction="none"
                onClick={() => setPurgeAlertVisible(true)}
              >
                Clear Messages
              </Button>
            </SpaceBetween>
          }
        >
          <FormField label="Message">
            <Input
              value={message}
              onChange={({ detail }) => setMessage(detail.value)}
            />
          </FormField>

          <FormField label="User">
            <Input
              value={username}
              onChange={({ detail }) => setUser(detail.value)}
            />
          </FormField>
        </Form>
      </form>
      <Modal
        onDismiss={handlePurgeCancellation}
        visible={purgeAlertVisible}
        header="Purge queue"
        footer={
          <Box float="right">
            <SpaceBetween direction="horizontal" size="xs">
              <Button variant="link" onClick={handlePurgeCancellation}>
                Cancel
              </Button>
              <Button variant="primary" onClick={handlePurgeConfirmation}>
                Purge
              </Button>
            </SpaceBetween>
          </Box>
        }
      >
        <Box variant="p">
          The message deletion process takes up to 60 seconds. We recommend
          waiting for 60 seconds regardless of your queue's size.
        </Box>
      </Modal>
    </>
  );
};
