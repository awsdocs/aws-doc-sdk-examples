// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import React, {useEffect, useState} from "react";
import Button from "react-bootstrap/Button";
import Form from 'react-bootstrap/Form';
import Modal from "react-bootstrap/Modal";

import * as service from './AwsService';

/**
 * An element that displays an 'Add item' button that lets you add an item to the work
 * item list. When you click the 'Add item' button, a modal form is displayed that
 * includes form fields that you can use to define the work item. When you click the
 * 'Add' button on the form, your new work item is sent to the server so it can be
 * added to the database.
 *
 * @returns {JSX.Element}
 */
export const WorkItem = () => {
  const [user, setUser] = useState('');
  const [guide, setGuide] = useState('');
  const [description, setDescription] = useState('');
  const [status, setStatus] = useState('');
  const [show, setShow] = useState(false);
  const [canAdd, setCanAdd] = useState(false);

  useEffect(() => {
    let can = user.length > 0 && guide.length > 0 && description.length > 0 && status.length > 0;
    setCanAdd(can);
  }, [user, guide, description, status]);

  const handleAdd = () => {
    service.addWorkItem({name: user, guide: guide, description: description, status: status})
      .catch(console.error);
    setShow(false);
  };

  const handleClose = () => {
    setShow(false);
  };

  return (
    <>
      <Button onClick={() => setShow(true)} variant="primary">Add item</Button>

      <Modal show={show} onHide={handleClose} dialogClassName="modal-90w">
        <Modal.Header closeButton>
          <Modal.Title>Add a work item</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form>
            <Form.Group>
              <Form.Label htmlFor='userField'>User</Form.Label>
              <Form.Control id='userField' type="text" placeholder="User name"
                            onChange={(event) => setUser(event.target.value)}/>
            </Form.Group>
            <Form.Group>
              <Form.Label htmlFor='guideField'>Guide</Form.Label>
              <Form.Control id='guideField' type="text" placeholder="Developer guide"
                            onChange={(event) => setGuide(event.target.value)}/>
            </Form.Group>
            <Form.Group>
              <Form.Label htmlFor='descriptionField'>Description</Form.Label>
              <Form.Control as="textarea" rows={3} id='descriptionField'
                            onChange={(event) => setDescription(event.target.value)}/>
            </Form.Group>
            <Form.Group>
              <Form.Label htmlFor='statusField'>Status</Form.Label>
              <Form.Control as="textarea" rows={3} id='statusField'
                            onChange={(event) => setStatus(event.target.value)}/>
            </Form.Group>
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={handleClose}>Close</Button>
          <Button variant="primary" disabled={!canAdd} onClick={handleAdd}>Add</Button>
        </Modal.Footer>
      </Modal>
    </>
  );
};