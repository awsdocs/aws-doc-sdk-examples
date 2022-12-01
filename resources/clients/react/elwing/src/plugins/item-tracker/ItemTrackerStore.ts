// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import create from "zustand";
import { WorkItem, workItemService } from "./work-item/WorkItemService";

export type WorkItemsFilter = "archived" | "active" | "";

interface ItemTrackerState {
  items: WorkItem[];
  selectedItems: WorkItem["id"][];
  filter: WorkItemsFilter;
  error: string;
  loading: boolean;
}

interface ItemTrackerActions {
  setFilter: (filter: WorkItemsFilter) => void;
  setError: (error: string) => void;
  clearError: () => void;
  setLoading: (loading: boolean) => void;
  setSelectedItems: (items: WorkItem["id"][]) => void;
  loadItems: () => Promise<void>;
}

export type Store<State, Actions> = State & { actions: Actions };

export const useItemTrackerState = create<
  Store<ItemTrackerState, ItemTrackerActions>
>((set, get) => {
  const actions: ItemTrackerActions = {
    setFilter: (filter) => set({ filter }),
    setError: (error) => set({ error }),
    clearError: () => set({ error: "" }),
    setLoading: (loading) => set({ loading }),
    loadItems: async () => {
      actions.clearError();
      actions.setLoading(true);
      try {
        const filter = get().filter;
        const items = await workItemService.list(
          filter ? { archived: filter === "archived" ? "true" : "false" } : {}
        );
        set({ items });
      } catch (e) {
        actions.setError((e as Error).message);
      } finally {
        actions.setLoading(false);
      }
    },
    setSelectedItems: (selectedItems) => set({ selectedItems }),
  };

  return {
    filter: "" as WorkItemsFilter,
    error: "",
    loading: false,
    items: [],
    selectedItems: [],
    actions,
  };
});

export function useItemTrackerAction(): ItemTrackerActions {
  return useItemTrackerState(({ actions }) => actions);
}
