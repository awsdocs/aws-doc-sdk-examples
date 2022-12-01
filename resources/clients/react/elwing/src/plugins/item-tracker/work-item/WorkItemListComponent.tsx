// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { useCallback, useEffect, useState } from "react";
import { WorkItem } from "./WorkItemService";
import { workItemService as service } from "./WorkItemService";
import {
  Alert,
  Button,
  FormField,
  Input,
  Select,
  SpaceBetween,
  Table,
} from "@cloudscape-design/components";
import { OptionDefinition } from "@cloudscape-design/components/internal/components/option/interfaces";
import {
  useItemTrackerAction,
  useItemTrackerState,
  WorkItemsFilter,
} from "../ItemTrackerStore";
import { AddWorkItem } from "./AddWorkItemComponent";

export const FILTER_OPTIONS: OptionDefinition[] = [
  { value: "active", label: "Active" },
  { value: "archived", label: "Archived" },
  { value: "", label: "All" },
];

export const WorkItemControls = () => {
  const [email, setEmail] = useState("");
  const [filter, setFilter] = useState(FILTER_OPTIONS[0]);
  const {
    setFilter: handleFilterChange,
    setError,
    loadItems,
    setSelectedItems,
  } = useItemTrackerAction();
  const { selectedItems } = useItemTrackerState();

  const sendReport = useCallback(
    async (email: string) => {
      try {
        setError("");
        await service.mailItem(email);
      } catch (e) {
        console.error(e);
        setError(
          "There was an error sending the report. Check the console for more information."
        );
      }
    },
    [setError]
  );

  useEffect(() => {
    handleFilterChange(filter.value as WorkItemsFilter);
  }, [filter, handleFilterChange]);

  const archiveItems = async (itemIds: WorkItem["id"][]) => {
    try {
      setSelectedItems([]);
      const archiveRequests = itemIds.map((id) => service.archiveItem(id));
      await Promise.all(archiveRequests);
      await loadItems();
    } catch (e) {
      setError((e as Error).message);
    }
  };

  return (
    <SpaceBetween size="s">
      <FormField>
        <Select
          selectedOption={filter}
          onChange={(event) => setFilter(event.detail.selectedOption)}
          options={FILTER_OPTIONS}
        />
      </FormField>

      <AddWorkItem />
      <Button
        disabled={selectedItems.length === 0}
        onClick={() => archiveItems(selectedItems)}
      >
        Archive item(s)
      </Button>
      <FormField
        label="Email Report"
        description="Register the recipient's email with Amazon SES."
      >
        <Input
          value={email}
          onChange={(event) => setEmail(event.detail.value)}
          placeholder="Recipient's email"
        />
      </FormField>
      <Button disabled={email === ""} onClick={() => sendReport(email)}>
        Send report
      </Button>
    </SpaceBetween>
  );
};

export const WorkItems = () => {
  const [selected, setSelected] = useState<WorkItem[]>([]);
  const { loadItems, setSelectedItems } = useItemTrackerAction();
  const items = useItemTrackerState(({ items }) => items);
  const filter = useItemTrackerState(({ filter }) => filter);

  useEffect(() => {
    loadItems();
  }, [loadItems, filter]);

  useEffect(() => {
    setSelectedItems(selected.map(({ id }) => id));
  }, [selected, setSelectedItems]);

  return (
    <Table
      variant="embedded"
      selectedItems={selected}
      selectionType="multi"
      sortingDisabled
      wrapLines
      onSelectionChange={({ detail }) =>
        setSelected(detail.selectedItems as WorkItem[])
      }
      columnDefinitions={[
        {
          id: "name",
          header: "Name",
          cell: (e) => e.name,
          sortingField: "name",
        },
        {
          id: "guide",
          header: "Guide",
          cell: (e) => e.guide,
          sortingField: "guide",
        },
        {
          id: "description",
          header: "Description",
          cell: (e) => e.description,
          sortingField: "description",
        },
        {
          id: "status",
          header: "Status",
          cell: (e) => e.status,
          sortingField: "status",
        },
      ]}
      empty={<Alert>No work items found.</Alert>}
      items={items}
    />
  );
};
