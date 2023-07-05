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

function FeedbackLayout() {
  const { feedback, authStatus, getFeedback, uploadFile } = useStore();

  useEffect(() => {
    getFeedback();
  }, []);

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
      empty={<TextContent>Il n'y a aucun commentaire</TextContent>}
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
                    accept={[".jpg", ".jpeg"]}
                    onSubmit={handleUpload}
                  />
                )}
              </SpaceBetween>
            }
          >
            Commentaires des clients
          </Header>
        </>
      }
      cardDefinition={{
        sections: [
          {
            content: (feedbackItem: Feedback) => (
              <p>
                <img
                  src={`/upload/${feedbackItem.id}`}
                  alt={feedbackItem.text}
                />
              </p>
            ),
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
    <TextContent>Veuillez vous connecter</TextContent>
  );
}

export default FeedbackLayout;
