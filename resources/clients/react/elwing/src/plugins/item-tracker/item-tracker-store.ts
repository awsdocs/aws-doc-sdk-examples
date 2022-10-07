import create from "zustand";
import { WorkItemStatus } from "./work-item/work-item-service";

export const useItemTrackerState = create<{
  status: WorkItemStatus;
  setStatus: (status: WorkItemStatus) => void;
  error: string;
  setError: (error: string) => void;
  clearError: () => void;
}>((set) => ({
  status: "" as WorkItemStatus,
  setStatus: (status: WorkItemStatus) => set({ status }),
  error: "",
  setError: (error: string) => set({ error }),
  clearError: () => set({ error: "" }),
}));
