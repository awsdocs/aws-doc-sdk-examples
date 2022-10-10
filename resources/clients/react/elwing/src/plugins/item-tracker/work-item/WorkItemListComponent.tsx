/**
 * An element that displays a list of work items that are retrieved from a REST service.
 *
 * - Select Active or Archived to display work items with the specified state.
 * - Select the wastebasket icon to archive an active item.
 * - Select 'Add item' to add a new item.
 * - Enter a recipient email and select 'Send report' to send a report of work items.
 */

import { useEffect, useState } from "react";
import { WorkItem, WorkItemStatus } from "./WorkItemService";
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
import { useItemTrackerAction, useItemTrackerState } from "../ItemTrackerStore";

const STATUS_OPTIONS: OptionDefinition[] = [
  { value: "active", label: "Active" },
  { value: "archived", label: "Archived" },
  { value: "", label: "All" },
];

export const WorkItemControls = ({
  sendReport,
}: {
  sendReport: (email: string) => Promise<void>;
}) => {
  const [email, setEmail] = useState("");
  const [status, setStatus] = useState(STATUS_OPTIONS[0]);
  const { setStatus: handleStatusChange } = useItemTrackerAction();

  useEffect(() => {
    handleStatusChange((status.value as WorkItemStatus) ?? "");
  }, [status, handleStatusChange]);

  return (
    <SpaceBetween size="s">
      <FormField>
        <Select
          selectedOption={status}
          onChange={(event) => setStatus(event.detail.selectedOption)}
          options={STATUS_OPTIONS}
        />
      </FormField>
      <FormField
        label="Email Report"
        description="You must first register the recipient's email with Amazon SES."
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
  // const status = useItemTrackerState(({ status }) => status);
  const items = useItemTrackerState(({ items }) => items);
  // const loading = useItemTrackerState(({ loading }) => loading);

  useEffect(() => {
    loadItems();
  }, []);

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
          cell: (e) => (
            <Button
              iconName="check"
              variant="primary"
              onClick={() => archiveItem(e.id)}
            />
          ),
        },
      ]}
      empty={<Alert>No work items found.</Alert>}
      items={items}
    />
  );
  // return (
  //   <>
  //     {!loading && items.length === 0 ? (
  //       <Alert>No work items found.</Alert>
  //     ) : (
  //       <table>
  //         <thead>
  //           <tr>
  //             <th>Item Id</th>
  //             <th>User</th>
  //             <th>Guide</th>
  //             <th>Description</th>
  //             <th>Status</th>
  //             <th />
  //           </tr>
  //         </thead>
  //         {loading ? (
  //           <tbody>
  //             <WorkItemsLoading />
  //           </tbody>
  //         ) : (
  //           <tbody>
  //             {items.map((item) => (
  //               <WorkItem
  //                 item={item}
  //                 status={status}
  //                 archiveItem={archiveItem}
  //               />
  //             ))}
  //           </tbody>
  //         )}
  //       </table>
  //     )}
  //   </>
  // );
};
