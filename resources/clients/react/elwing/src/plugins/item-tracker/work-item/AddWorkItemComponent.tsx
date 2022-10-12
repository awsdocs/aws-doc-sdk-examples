// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import {
  Button,
  Form,
  FormField,
  Input,
  Modal,
  Select,
  Textarea,
} from "@cloudscape-design/components";
import { OptionDefinition } from "@cloudscape-design/components/internal/components/option/interfaces";
import { useEffect, useState } from "react";
import { useItemTrackerAction } from "../ItemTrackerStore";
import { workItemService as service } from "./WorkItemService";

const GUIDE_OPTIONS: OptionDefinition[] = [
  { value: "dotnet", label: ".NET" },
  { value: "cpp", label: "C++" },
  { value: "go", label: "Golang" },
  { value: "java", label: "Java" },
  { value: "js", label: "JavaScript" },
  { value: "php", label: "PHP" },
  { value: "python", label: "Python" },
  { value: "ruby", label: "Ruby" },
  { value: "rust", label: "Rust" },
];

/**
 * An element that displays an 'Add item' button that lets you add an item to the work
 * item list. When you click the 'Add item' button, a modal form is displayed that
 * includes form fields that you can use to define the work item. When you click the
 * 'Add' button on the form, your new work item is sent to the server so it can be
 * added to the database.
 */
export const AddWorkItem = () => {
  const [user, setUser] = useState("");
  const [guide, setGuide] = useState(GUIDE_OPTIONS[0]);
  const [description, setDescription] = useState("");
  const [show, setShow] = useState(false);
  const [canAdd, setCanAdd] = useState(false);
  const { loadItems } = useItemTrackerAction();

  useEffect(() => {
    let can = user.length > 0 && description.length > 0;
    setCanAdd(can);
  }, [user, description]);

  const handleAdd = () => {
    service
      .create({
        name: user,
        guide: guide.value!,
        description,
        status: "ACT",
      })
      .catch(console.error);
    setShow(false);
    loadItems();
  };

  const handleClose = () => {
    setShow(false);
  };

  return (
    <>
      <Button onClick={() => setShow(true)} variant="primary">
        Add item
      </Button>

      <Modal
        visible={show}
        onDismiss={handleClose}
        header="Add a work item"
        footer={
          <>
            <Button variant="normal" onClick={handleClose}>
              Close
            </Button>
            <Button variant="primary" disabled={!canAdd} onClick={handleAdd}>
              Add
            </Button>
          </>
        }
      >
        <Form>
          <FormField label="User">
            <Input
              value={user}
              placeholder="User name"
              onChange={(event) => setUser(event.detail.value)}
            />
          </FormField>
          <FormField label="Guide">
            <Select
              selectedOption={guide}
              onChange={({ detail }) => setGuide(detail.selectedOption)}
              options={GUIDE_OPTIONS}
            />
          </FormField>
          <FormField label="Description">
            <Textarea
              value={description}
              rows={3}
              id="descriptionField"
              onChange={(event) => setDescription(event.detail.value)}
            />
          </FormField>
        </Form>
      </Modal>
    </>
  );
};
