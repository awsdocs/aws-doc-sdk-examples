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
import { initializeDownload, Tag, uploadFile } from "./pam-api";
import S3Transfer from "./S3Transfer";
import { useAuthStore } from "./store-auth";

import { useTagsStore } from "./store-tags";
import { getTags } from "./pam-api";

function TagsLayout() {
  const { tagCollection, setTags } = useTagsStore();
  const [selectedTags, setSelectedTags] = useState<Tag[]>([]);
  const [selectedTagsCount, setSelectedTagsCount] = useState<number>(0);
  const [isDownloading, setIsDownloading] = useState(false);
  const [flashbarItems, setFlashbarItems] = useState<FlashbarProps["items"]>(
    []
  );

  const { token, authStatus, currentUser } = useAuthStore();

  useEffect(() => {
    if (token) {
      refreshTags();
    }
  }, [token]);

  useEffect(() => {
    setSelectedTagsCount(selectedTags.length);
  }, [selectedTags]);

  const refreshTags = async () => {
    const tagCollection = await getTags({ token });
    setTags(tagCollection);
  };

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
    await uploadFile(file, { token });
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
      await initializeDownload(
        selectedTags.map((t) => t.name),
        { token }
      );
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
        items={tagCollection}
        stickyHeader={true}
        variant="full-page"
        header={
          <>
            <Flashbar items={flashbarItems} />
            <Header
              variant="awsui-h1-sticky"
              actions={
                <SpaceBetween size="s" direction="horizontal">
                  <Button iconName="refresh" onClick={refreshTags} />
                  <S3Transfer />
                  <FileUpload
                    disabled={authStatus !== "signed_in"}
                    accept={[".jpg", ".jpeg"]}
                    onSubmit={handleUpload}
                  />
                  <Button
                    disabled={
                      authStatus !== "signed_in" ||
                      !selectedTagsCount ||
                      isDownloading
                    }
                    onClick={handleDownload}
                  >
                    Download
                  </Button>
                </SpaceBetween>
              }
            >
              Download Tagged Images
            </Header>
          </>
        }
        selectedItems={selectedTags}
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

export default TagsLayout;
