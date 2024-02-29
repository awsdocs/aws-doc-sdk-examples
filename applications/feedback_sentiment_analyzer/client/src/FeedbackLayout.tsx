// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import {
  Button,
  Cards,
  Header,
  SpaceBetween,
  TextContent,
} from "@cloudscape-design/components";
import { useEffect } from "react";
import FileUpload from "./FileUpload";
import { useStore } from "./store";
import type { Feedback } from "./api";
import Audio from "./Audio";
import Image from "./Image";

function FeedbackLayout() {
  const { feedback, authStatus, getFeedback, uploadFile } = useStore();

  useEffect(() => {
    if (authStatus !== "signed_out") {
      getFeedback();
    }
  }, [authStatus, getFeedback]);

  const handleUpload = async (file: File) => {
    await uploadFile(file);
  };

  return authStatus === "signed_in" ? (
    <Cards
      cardsPerRow={[
        { cards: 2, minWidth: 950 },
        { cards: 1, minWidth: 0 },
      ]}
      trackBy={(feedbackItem) => feedbackItem.audioUrl}
      empty={<TextContent>No data found</TextContent>}
      items={feedback}
      stickyHeader={true}
      variant="full-page"
      isItemDisabled={() => true}
      header={
        <>
          <Header
            variant="awsui-h1-sticky"
            actions={
              <SpaceBetween size="s" direction="horizontal">
                <Button iconName="refresh" onClick={getFeedback} />
                {authStatus === "signed_in" && (
                  <FileUpload
                    disabled={false}
                    accept={[".jpg", ".jpeg", ".png"]}
                    onSubmit={handleUpload}
                  />
                )}
              </SpaceBetween>
            }
          >
            Customer Feedback
          </Header>
        </>
      }
      cardDefinition={{
        sections: [
          {
            content: (feedbackItem: Feedback) => (
              <Image src={feedbackItem.imageUrl} alt={feedbackItem.text} />
            ),
          },
          {
            content: (feedbackItem: Feedback) => <p>{feedbackItem.text}</p>,
          },
          {
            content: (feedbackItem: Feedback) => (
              <Audio src={feedbackItem.audioUrl}></Audio>
            ),
          },
        ],
      }}
    />
  ) : (
    <TextContent>Login to view customer feedback</TextContent>
  );
}

export default FeedbackLayout;
