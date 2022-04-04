// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import React, {useEffect, useState} from "react";
import Button from "react-bootstrap/Button";
import Form from 'react-bootstrap/Form';
import ListGroup from 'react-bootstrap/ListGroup';
import Modal from "react-bootstrap/Modal";
import $ from "jquery";
import Spinner from "react-bootstrap/Spinner";

/**
 * An element that displays a report of all labels found in all images in your
 * Amazon Simple Storage Service (Amazon S3) bucket. The default state of the
 * element is a button that triggers the report. When you click the button, a modal
 * form is displayed that includes form fields that you can use to send an email of
 * the report.
 *
 * @param props: Properties that determine how the image is displayed.
 *        props.apiUrl: The root URL of the REST API that is called to get the list of
 *                      photos and upload images.
 * @returns {JSX.Element}
 */
export const Report = (props) => {
  const [report, setReport] = useState([]);
  const [sender, setSender] = useState('');
  const [recipient, setRecipient] = useState('');
  const [message, setMessage] = useState('');
  const [show, setShow] = useState(false);
  const [loading, setLoading] = useState(false);
  const [canSend, setCanSend] = useState(false);

  /**
   * Sets a flag that indicates whether an email is ready to send.
   */
  useEffect(() => {
    let can = sender.length > 0 && recipient.length > 0 && message.length > 0;
    setCanSend(can);
  }, [sender, recipient, message]);

  /**
   * Calls the REST service to get a report of all images in your S3 bucket and shows
   * a modal form to display the report.
   */
  const getReport = () => {
    setLoading(true);
    $.get(`${props.apiUrl}/photos/report`,
      (result) => {
        console.log("SUCCESS", result)
        setReport(result);
        setShow(true);
      })
      .fail((error) => {
        console.log(error);
      })
      .always(() => {
        setLoading(false)
      });
  };

  /**
   * Calls the REST service to send an email of the report and dismisses the modal form.
   */
  const sendReport = () => {
    $.post({
      url: `${props.apiUrl}/photos/report`,
      data: JSON.stringify({
        'sender': sender,
        'recipient': recipient,
        'subject': 'Photo analysis report',
        'message': message,
        'analysis_labels': report
      }),
      contentType: 'application/json',
      success: (result) => {
        console.log("Posted report email.");
      },
    })
      .fail((error) => {
        console.log(error);
      });
    setShow(false);
  }

  const handleClose = () => {
    setShow(false);
  }

  return (
    <>
      {loading ?
        <Button variant="primary" onClick={getReport} disabled>
          <Spinner
            as="span"
            animation="border"
            size="sm"
            role="status"
            aria-hidden="true"
            className='me-2'
          />
          Analyzing...
        </Button>
        :
        <Button variant="primary" onClick={getReport}>
          Analyze all photos in your bucket and send a report</Button>}

      <Modal show={show} onHide={handleClose} dialogClassName="modal-90w">
        <Modal.Header closeButton>
          <Modal.Title>Photo analysis report</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form>
            <Form.Group>
              <Form.Label>Sender email address</Form.Label>
              <Form.Control type="email" placeholder="sender@example.com"
                            onChange={(event) => setSender(event.target.value)}/>
              <Form.Text muted>The sender email must be verified with Amazon SES.</Form.Text>
            </Form.Group>
            <Form.Group>
              <Form.Label>Recipient email address</Form.Label>
              <Form.Control type="email" placeholder="recipient@example.com"
                            onChange={(event) => setRecipient(event.target.value)}/>
              <Form.Text muted>The recipient email must be verified with Amazon SES.</Form.Text>
            </Form.Group>
            <Form.Group>
              <Form.Label htmlFor='mailMessage'>Message</Form.Label>
              <Form.Control as="textarea" rows={3} id='mailMessage'
                            onChange={(event) => setMessage(event.target.value)}/>
            </Form.Group>
            <Form.Group>
              <Form.Label>Detected labels</Form.Label>
              <ListGroup style={{height: `calc(100vh - 600px)`, overflowY: "auto"}}>
                {report.map(item => (
                  <ListGroup.Item>{item}</ListGroup.Item>
                ))}
              </ListGroup>
            </Form.Group>
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={handleClose}>Close</Button>
          <Button variant="primary" onClick={sendReport} disabled={!canSend}>Send report</Button>
        </Modal.Footer>
      </Modal>
    </>
  );
};