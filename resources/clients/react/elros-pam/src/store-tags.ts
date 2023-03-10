import { create } from "zustand";
import { Tag } from "./pam-api";


export interface TagsStore {
  tagCollection: Tag[];
  clearTags: () => void;
  setTags: (tags: Tag[]) => void;
}

export const useTagsStore = create<TagsStore>((set, get) => ({
  tagCollection: [],
  setTags: (tags: Tag[]) =>
    set({
      tagCollection: tags,
    }),
  clearTags: () => {
    set({ tagCollection: [] });
  },
}));
