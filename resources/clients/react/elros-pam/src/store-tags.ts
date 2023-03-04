import { create } from "zustand";
import { getTags, PamApiConfig } from "./pam-api";

export interface Tag {
  name: string;
}

export interface Image {
  fileName: string;
}

export interface TagImages {
  tag: Tag;
  images: Image[];
}

export interface TagsStore {
  tagImagesList: TagImages[];
  fetchTags: (apiConfig: PamApiConfig) => void;
}

export const useTagsStore = create<TagsStore>((set, get) => ({
  tagImagesList: [
    {
      tag: { name: "Landscape" },
      images: [
        { fileName: "mount_rainier.jpg" },
        { fileName: "landscape.jpg" },
        { fileName: "landscape2.jpg" },
        { fileName: "landscape3.jpg" },
        { fileName: "landscape4.jpg" },
      ],
    },
    {
      tag: { name: "Portrait" },
      images: [
        { fileName: "portrait.jpg" },
        { fileName: "portrait2.jpg" },
        { fileName: "portrait3.jpg" },
      ],
    },
    { tag: { name: "Nature" }, images: [{ fileName: "nature.jpg" }] },
    {
      tag: { name: "People" },
      images: [
        { fileName: "people.jpg" },
        { fileName: "people2.jpg" },
        { fileName: "people3.jpg" },
      ],
    },
    { tag: { name: "City" }, images: [{ fileName: "city.jpg" }] },
    { tag: { name: "Food" }, images: [{ fileName: "food.jpg" }] },
    { tag: { name: "Night" }, images: [{ fileName: "night.jpg" }] },
    { tag: { name: "Sport" }, images: [{ fileName: "sport.jpg" }] },
    { tag: { name: "Travel" }, images: [{ fileName: "travel.jpg" }] },
    { tag: { name: "Other" }, images: [{ fileName: "other.jpg" }] },
  ],

  fetchTags: async (apiConfig: PamApiConfig) => {
    const tags = await getTags(apiConfig);
  },
}));
