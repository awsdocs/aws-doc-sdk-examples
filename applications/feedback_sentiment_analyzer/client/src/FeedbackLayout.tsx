import {
  Button,
  Cards,
  Header,
  SpaceBetween,
  TextContent,
} from "@cloudscape-design/components";
import { Ref, useEffect, useRef } from "react";
import FileUpload from "./FileUpload";
import { useStore, Feedback } from "./store";
import Audio from "./Audio";

function FeedbackLayout() {
  const { feedback, authStatus, getFeedback, uploadFile } = useStore();
  const audioRef: Ref<HTMLAudioElement> = useRef(null);

  useEffect(() => {
    getFeedback();
  }, []);

  const handleUpload = async (file: File) => {
    await uploadFile(file);
  };

  return (
    <>
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
    </>
  );
}

export default FeedbackLayout;
