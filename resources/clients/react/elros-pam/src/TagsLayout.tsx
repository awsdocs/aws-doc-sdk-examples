import {
  Button,
  Cards,
  Header,
  SpaceBetween,
  TextContent,
} from "@cloudscape-design/components";
import { useEffect, useState } from "react";
import FileUpload from "./FileUpload";
import { uploadFile } from "./pam-api";
import S3Transfer from "./S3Transfer";
import { useAuthStore } from "./store-auth";

import { Tag, useTagsStore } from "./store-tags";

function TagsLayout() {
  const { tagCollection, fetchTags } = useTagsStore();
  const [selectedTags, setSelectedTags] = useState<Tag[]>([]);
  const [selectedImageCount, setSelectedImageCount] = useState<number>(0);

  const { token, authStatus } = useAuthStore();

  useEffect(() => {
    if (token) {
      fetchTags({ token });
    }
  }, [token]);

  useEffect(() => {
    const imageCount = selectedTags.reduce(
      (count, nextTag) => count + nextTag.count,
      0
    );

    setSelectedImageCount(imageCount);
  }, [selectedTags]);

  const handleUpload = async (file: File) => {
    const response = await uploadFile(file, { token });
    console.log(response);
  }

  return (
    <>
      <Cards
        trackBy={(tag) => tag.name}
        empty={<TextContent>There are no tags to display.</TextContent>}
        items={tagCollection}
        stickyHeader={true}
        variant="full-page"
        header={
          <Header
            variant="awsui-h1-sticky"
            counter={`${selectedImageCount}`}
            actions={
              <SpaceBetween size="s" direction="horizontal">
                <S3Transfer />
                <FileUpload disabled={authStatus !== "signed_in"} accept={[".jpg", ".jpeg"]} onSubmit={handleUpload}/>
                <Button
                  disabled={authStatus !== "signed_in" || !selectedImageCount}
                >
                  Download
                </Button>
              </SpaceBetween>
            }
          >
            Download Images
          </Header>
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
