import create from "zustand";
import {
  WorkItem,
  workItemService,
  WorkItemStatus,
} from "./work-item/WorkItemService";

interface ItemTrackerActions {
  setStatus: (status: WorkItemStatus) => void;
  setError: (error: string) => void;
  clearError: () => void;
  setLoading: (loading: boolean) => void;
  loadItems: () => Promise<void>;
}

interface ItemTrackerState {
  items: WorkItem[];
  status: WorkItemStatus;
  error: string;
  loading: boolean;
}

export const useItemTrackerState = create<
  ItemTrackerState & { actions: ItemTrackerActions }
>((set, get) => {
  const actions: ItemTrackerActions = {
    setStatus: (status: WorkItemStatus) => set({ status }),
    setError: (error: string) => set({ error }),
    clearError: () => set({ error: "" }),
    setLoading: (loading: boolean) => set({ loading }),
    loadItems: async () => {
      actions.clearError();
      actions.setLoading(true);
      try {
        set({ items: await workItemService.list({ status: get().status }) });
      } catch (e) {
        actions.setError((e as Error).message);
      } finally {
        actions.setLoading(false);
      }
    },
  };

  return {
    status: "" as WorkItemStatus,
    error: "",
    loading: false,
    items: [],
    actions,
  };
});

export function useItemTrackerAction(): ItemTrackerActions {
  return useItemTrackerState(({ actions }) => actions);
}
