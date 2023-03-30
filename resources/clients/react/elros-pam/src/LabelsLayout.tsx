import {
  Button,
  Cards,
  Flashbar,
  FlashbarProps,
  Header,
  SpaceBetween,
  TextContent,
} from "@cloudscape-design/components";
import { useEffect, useState } from "react";
import FileUpload from "./FileUpload";
import { Label } from "./pam-api";
import { useStore } from "./store";

function LabelsLayout() {
  const [selectedLabels, setSelectedTags] = useState<Label[]>([]);
  const [selectedLabelsCount, setSelectedLabelsCount] = useState<number>(0);
  const [isDownloading, setIsDownloading] = useState(false);
  const [flashbarItems, setFlashbarItems] = useState<FlashbarProps["items"]>(
    []
  );

  const {
    labels,
    token,
    authStatus,
    currentUser,
    getLabels,
    uploadFile,
    initializeDownload,
  } = useStore();

  useEffect(() => {
    if (token) {
      getLabels();
    }
  }, [token]);

  useEffect(() => {
    setSelectedLabelsCount(selectedLabels.length);
  }, [selectedLabels]);

  const setMessage = ({
    id,
    content,
    type,
  }: {
    id: string;
    content: string;
    type: FlashbarProps.Type;
  }) => {
    setFlashbarItems([
      {
        dismissible: true,
        dismissLabel: "Dismiss message",
        onDismiss: () => setFlashbarItems([]),
        content,
        id,
        type,
      },
    ]);
  };

  const handleUpload = async (file: File) => {
    await uploadFile(file);
  };

  const handleDownload = async () => {
    if (!currentUser?.username) {
      setMessage({
        id: "download-failure-params",
        content: "A email is required. Are you signed in?",
        type: "error",
      });
      return;
    }

    setIsDownloading(true);

    try {
      await initializeDownload(selectedLabels.map((t) => t.name));
      setMessage({
        id: "download-success",
        content:
          "Your photos are being compressed. You will receive an email " +
          "with a link to download a zip file.",
        type: "info",
      });
    } catch (err) {
      console.log("UI Failed downloading", err);
      setMessage({
        id: "download-failure-server",
        content: "Your photos failed to download.",
        type: "error",
      });
    } finally {
      setIsDownloading(false);
    }
  };

  return (
    <>
      <Cards
        trackBy={(tag) => tag.name}
        empty={<TextContent>There are no tags to display.</TextContent>}
        items={labels}
        stickyHeader={true}
        variant="full-page"
        header={
          <>
            <Flashbar items={flashbarItems} />
            <Header
              variant="awsui-h1-sticky"
              actions={
                <SpaceBetween size="s" direction="horizontal">
                  <Button iconName="refresh" onClick={getLabels} />
                  <FileUpload
                    disabled={authStatus !== "signed_in"}
                    accept={[".jpg", ".jpeg"]}
                    onSubmit={handleUpload}
                  />
                  <Button
                    disabled={
                      authStatus !== "signed_in" ||
                      !selectedLabelsCount ||
                      isDownloading
                    }
                    onClick={handleDownload}
                  >
                    Download
                  </Button>
                </SpaceBetween>
              }
            >
              Image Labels
            </Header>
          </>
        }
        selectedItems={selectedLabels}
        onSelectionChange={({ detail }) => {
          setSelectedTags(detail.selectedItems);
        }}
        selectionType="multi"
        cardDefinition={{
          header: (tag) => `${tag.name}  (${tag.count})`,
        }}
      />
    </>
  );
}

export default LabelsLayout;
