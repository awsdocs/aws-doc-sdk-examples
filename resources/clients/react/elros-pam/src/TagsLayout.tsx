import { Header } from "@cloudscape-design/components";
import Cards from "@cloudscape-design/components/cards";
import { lazy, Suspense, useEffect, useState } from "react";
import { useAuthStore } from "./store-auth";

const LazyLoginModal = lazy(() => import("./LoginModal"));
const LazyWelcomeUser = lazy(() => import("./WelcomeUser"));

import { TagImages, useTagsStore } from "./store-tags";
import { useUiStore } from "./store-ui";

function TagsLayout() {
  const { tagImagesList, fetchTags } = useTagsStore();
  const [selectedTagImages, setSelectedTagImages] = useState<TagImages[]>([]);
  const {
    login: { enabled: loginEnabled },
  } = useUiStore();

  const { token } = useAuthStore();

  useEffect(() => {
    if (token) {
      fetchTags({ token });
    }
  }, [token]);

  return (
    <>
      <Cards
        trackBy={(tagImages) => tagImages.tag.name}
        variant="full-page"
        items={tagImagesList}
        selectedItems={selectedTagImages}
        onSelectionChange={({ detail }) => {
          setSelectedTagImages(detail.selectedItems);
        }}
        selectionType="multi"
        header={
          <Header
            variant="h1"
            description="Image storage and tagging."
            actions={
              // This is potentially a very common pattern that we could abstract into an HOC.
              // <Feature enabled={loginEnabled}><LazyComponent /></Feature> maybe?
              loginEnabled && (
                <Suspense>
                  <LazyWelcomeUser />
                </Suspense>
              )
            }
          >
            Photo Archive
          </Header>
        }
        cardDefinition={{
          header: (tagImages) =>
            `${tagImages.tag.name}  (${tagImages.images.length})`,
          sections: [
            {
              id: "image-names",
              header: "Image names",
              content: (tagImages) => (
                <ul>
                  {tagImages.images.map((image) => (
                    <li key={image.fileName}>{image.fileName}</li>
                  ))}
                </ul>
              ),
            },
          ],
        }}
      />
      {loginEnabled && (
        <Suspense>
          <LazyLoginModal />
        </Suspense>
      )}
    </>
  );
}

export default TagsLayout;
