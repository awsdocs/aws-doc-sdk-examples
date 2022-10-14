// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { useCallback, useEffect, useState } from "react";
import { WorkItemStatus } from "./WorkItemService";
import { workItemService as service } from "./WorkItemService";
import {
  Button,
  FormField,
  Input,
  Select,
  SpaceBetween,
} from "@cloudscape-design/components";
import { OptionDefinition } from "@cloudscape-design/components/internal/components/option/interfaces";
import { useItemTrackerAction } from "../ItemTrackerStore";

const STATUS_OPTIONS: OptionDefinition[] = [
  { value: "active", label: "Active" },
  { value: "archived", label: "Archived" },
  { value: "", label: "All" },
];

export const WorkItemControls = () => {
  const [email, setEmail] = useState("");
  const [status, setStatus] = useState(STATUS_OPTIONS[0]);
  const { setStatus: handleStatusChange, setError } = useItemTrackerAction();

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
        description="Register the recipient's email with Amazon SES."
      >
        <Input
          value={email}
          onChange={(event) => setEmail(event.detail.value)}
          placeholder="Recipient's email"
        />
      </FormField>
      <Button
        data-testid="send-report"
        disabled={email === ""}
        onClick={() => sendReport(email)}
      >
        Send report
      </Button>
    </SpaceBetween>
  );
};
