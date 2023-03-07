import { create } from "zustand";
import { getTags, PamApiConfig } from "./pam-api";

export interface Tag {
  name: string;
  count: number;
}

export interface Image {
  fileName: string;
}

export interface TagsStore {
  tagCollection: Tag[];
  clearTags: () => void;
  fetchTags: (apiConfig: PamApiConfig) => void;
}

export const useTagsStore = create<TagsStore>((set, get) => ({
  tagCollection: [],
  fetchTags: async (apiConfig: PamApiConfig) => {
    const tags = await getTags(apiConfig);
    set({
      tagCollection: Object.entries(tags).map(([name, { count }]) => ({
        name,
        count,
      })),
    });
  },
  clearTags: () => {
    set({ tagCollection: [] });
  },
}));
