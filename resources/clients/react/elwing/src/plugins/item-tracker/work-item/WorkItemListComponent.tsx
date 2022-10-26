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
import { useItemTrackerAction, useItemTrackerState, WorkItemsFilter } from "../ItemTrackerStore";

export const FILTER_OPTIONS: OptionDefinition[] = [
  { value: "active", label: "Active" },
  { value: "archived", label: "Archived" },
  { value: "", label: "All" },
];

export const WorkItemControls = () => {
  const [email, setEmail] = useState("");
  const [filter, setFilter] = useState(FILTER_OPTIONS[0]);
  const { setFilter: handleFilterChange, setError } = useItemTrackerAction();

  const sendReport = useCallback(
    async (email: string) => {
      try {
        service.mailItem(email);
      } catch (e) {
        setError((e as Error).message);
      }
    },
    [setError]
  );

  useEffect(() => {
    handleFilterChange(filter.value as WorkItemsFilter);
  }, [filter, handleFilterChange]);

  return (
    <SpaceBetween size="s">
      <FormField>
        <Select
          selectedOption={filter}
          onChange={(event) => setFilter(event.detail.selectedOption)}
          options={FILTER_OPTIONS}
        />
      </FormField>
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
  const { loadItems, setError } = useItemTrackerAction();
  const items = useItemTrackerState(({ items }) => items);
  const filter = useItemTrackerState(({ filter }) => filter);

  useEffect(() => {
    loadItems();
  }, [loadItems, filter]);

  const archiveItem = async (itemId: string) => {
    try {
      await service.archiveItem(itemId);
      await loadItems();
    } catch (e) {
      setError((e as Error).message);
    }
  };

  return (
    <Table
      variant="embedded"
      selectedItems={selected}
      sortingDisabled
      onSelectionChange={({ detail }) =>
        setSelected(detail.selectedItems as WorkItem[])
      }
      columnDefinitions={[
        {
          id: "id",
          header: "ID",
          cell: (e) => e.id,
          sortingField: "id",
        },
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
        {
          id: "archive",
          header: "",
          cell: (e) =>
            e.archived ? (
              <Button variant="normal" disabled={true}>
                Archived
              </Button>
            ) : (
              <Button variant="normal" onClick={() => archiveItem(e.id)}>
                Archive
              </Button>
            ),
        },
      ]}
      empty={<Alert>No work items found.</Alert>}
      items={items}
    />
  );
};
