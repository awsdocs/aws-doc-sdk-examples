// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import create from "zustand";
import {
  WorkItem,
  workItemService,
  WorkItemStatus,
} from "./work-item/WorkItemService";

interface ItemTrackerState {
  items: WorkItem[];
  status: WorkItemStatus;
  error: string;
  loading: boolean;
}

interface ItemTrackerActions {
  setStatus: (status: WorkItemStatus) => void;
  setError: (error: string) => void;
  clearError: () => void;
  setLoading: (loading: boolean) => void;
  loadItems: () => Promise<void>;
}

export type Store<State, Actions> = State & { actions: Actions };

export const useItemTrackerState = create<
  Store<ItemTrackerState, ItemTrackerActions>
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
        const items = await workItemService.list({ status: get().status });
        set({ items });
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
