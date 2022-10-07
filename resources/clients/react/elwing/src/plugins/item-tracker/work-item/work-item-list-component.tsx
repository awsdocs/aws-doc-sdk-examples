/**
 * An element that displays a list of work items that are retrieved from a REST service.
 *
 * - Select Active or Archived to display work items with the specified state.
 * - Select the wastebasket icon to archive an active item.
 * - Select 'Add item' to add a new item.
 * - Enter a recipient email and select 'Send report' to send a report of work items.
 */

import { useCallback, useEffect, useState } from "react";
import { WorkItem as IWorkItem, WorkItemStatus } from "./work-item-service";
import { workItemService as service } from "./work-item-service";
import {
  Alert,
  Button,
  FormField,
  Input,
  Select,
  SpaceBetween,
} from "@cloudscape-design/components";
import { OptionDefinition } from "@cloudscape-design/components/internal/components/option/interfaces";
import { useItemTrackerState } from "../item-tracker-store";

export const WorkItem = ({
  item,
  status,
  archiveItem,
}: {
  item: IWorkItem;
  status: WorkItemStatus;
  archiveItem: (id: string) => Promise<void>;
}) => {
  return (
    <tr>
      <td>{item.id}</td>
      <td>{item.name}</td>
      <td>{item.guide}</td>
      <td>{item.description}</td>
      <td>{item.status}</td>
      <td>
        {status === "active" ? (
          <Button onClick={() => archiveItem(item.id)}>🗑</Button>
        ) : null}
      </td>
    </tr>
  );
};

export const WorkItemsLoading = () => {
  return (
    <>
      {[1, 2, 3].map((item) => (
        <tr key={item}>
          <td></td>
          <td></td>
          <td></td>
          <td></td>
          <td></td>
        </tr>
      ))}
    </>
  );
};

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
  const { setStatus: handleStatusChange } = useItemTrackerState();

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
  const [items, setItems] = useState<IWorkItem[]>([]);
  const [loading, setLoading] = useState(false);
  const { status, setError, clearError } = useItemTrackerState();

  const getItems = useCallback(async () => {
    clearError();
    setLoading(true);
    try {
      setItems(await service.list({ status }));
    } catch (e) {
      setError((e as Error).message);
    } finally {
      setLoading(false);
    }
  }, [setError, clearError, setLoading, setItems, status]);

  useEffect(() => {
    getItems();
  }, [getItems]);

  const archiveItem = async (itemId: string) => {
    try {
      await service.archiveItem(itemId);
      await getItems();
    } catch (e) {
      setError((e as Error).message);
    }
  };

  return (
    <>
      {!loading && items.length === 0 ? (
        <Alert>No work items found.</Alert>
      ) : (
        <table>
          <thead>
            <tr>
              <th>Item Id</th>
              <th>User</th>
              <th>Guide</th>
              <th>Description</th>
              <th>Status</th>
              <th />
            </tr>
          </thead>
          {loading ? (
            <tbody>
              <WorkItemsLoading />
            </tbody>
          ) : (
            <tbody>
              {items.map((item) => (
                <WorkItem
                  item={item}
                  status={status}
                  archiveItem={archiveItem}
                />
              ))}
            </tbody>
          )}
        </table>
      )}
    </>
  );
};
