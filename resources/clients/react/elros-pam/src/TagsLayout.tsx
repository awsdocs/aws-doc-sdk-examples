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
  const [selectedImageCount, setSelectedImageCount] = useState<number>(0);
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
    const imageCount = selectedTags.reduce(
      (count, nextTag) => count + nextTag.count,
      0
    );

    setSelectedImageCount(imageCount);
  }, [selectedTags]);

  const refreshTags = async () => {
    const tagCollection = await getTags({ token });
    setTags(tagCollection);
  };

  const handleUpload = async (file: File) => {
    await uploadFile(file, { token });
  };

  const handleDownload = async () => {
    if (!currentUser?.username) {
      setFlashbarItems([
        {
          dismissible: true,
          dismissLabel: "Dismiss message",
          onDismiss: () => setFlashbarItems([]),
          content: "A email is required. Are you signed in?",
          id: "download-failure",
          type: "error",
        },
      ]);
      return;
    }
    
    await initializeDownload(
      selectedTags.map((t) => t.name),
      currentUser?.username
    );
    setFlashbarItems([
      {
        dismissible: true,
        dismissLabel: "Dismiss message",
        onDismiss: () => setFlashbarItems([]),
        content:
          "Your photos are being moved out of glacier storage. You will receive an email " +
          "with a link to download a zip file.",
        id: "download",
      },
    ]);
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
              counter={`${selectedImageCount}`}
              actions={
                <SpaceBetween size="s" direction="horizontal">
                  <Button iconName="refresh" onClick={() => refreshTags()} />
                  <S3Transfer />
                  <FileUpload
                    disabled={authStatus !== "signed_in"}
                    accept={[".jpg", ".jpeg"]}
                    onSubmit={handleUpload}
                  />
                  <Button
                    disabled={authStatus !== "signed_in" || !selectedImageCount}
                    onClick={handleDownload}
                  >
                    Download
                  </Button>
                </SpaceBetween>
              }
            >
              Download Images
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
