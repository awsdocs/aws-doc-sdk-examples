// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import React, {useEffect, useState} from "react";
import Button from 'react-bootstrap/Button'
import Form from 'react-bootstrap/Form'
import InputGroup from 'react-bootstrap/InputGroup'
import Row from 'react-bootstrap/Row'
import ListGroup from 'react-bootstrap/ListGroup'
import {Report} from './Report';
import $ from "jquery";

/**
 * An element that displays a list of photos that are stored in an Amazon Simple Storage
 * Service (Amazon S3) bucket. The element also includes controls that let you browse
 * for image files on your computer and upload them to the S3 bucket.
 *
 * @param props: Properties that determine how the image is displayed.
 *        props.apiUrl: The root URL of the REST API that is called to get the list of
 *                      photos and upload images.
 *        props.onSelectedPhotoChange: A function to call when the selected photo
 *                                     changes.
 * @returns {JSX.Element}
 */
export const PhotoList = (props) => {
  const [photosError, setPhotosError] = useState(null);
  const [photos, setPhotos] = useState([]);
  const [imageFile, setImageFile] = useState();
  const [uploadError, setUploadError] = useState(null);

  /**
   * Calls the REST service to get a list of photos that are stored in your S3 bucket.
   */
  useEffect(() => {
    $.get(`${props.apiUrl}/photos`,
      (result) => {
        console.log("SUCCESS", result)
        setPhotos(result)
      })
      .fail((error) => {
        setPhotosError(error);
      })
  }, [props.apiUrl]);

  /**
   * Uploads an image to your S3 bucket. The image is stored as form data in the request
   * that is sent to the REST service.
   */
  const uploadFile = () => {
    console.log("uploadFile called");
    const formData = new FormData();
    formData.append("image_file", imageFile, imageFile.name);
    $.post({
      url: `${props.apiUrl}/photos`,
      data: formData,
      processData: false,
      contentType: false,
      success: () => {
        console.log(`Posted image ${imageFile.name}.`);
        setPhotos(photos.concat({'name': imageFile.name})
          .sort((a, b) => (a.name > b.name) ? 1 : ((b.name > a.name) ? -1 : 0)));
        setImageFile(undefined);
      },
      fail: (error) => {
        setUploadError(error);
      }
    });
  };

  return (
    <>
      <Row className="mb-3">
        <h3>Photos</h3>
        {photos.length < 1 ?
          <p className="lead">Upload some photos to analyze them.</p>
          :
          <p className="lead">Select a photo to analyze it with Amazon Rekognition.</p>
        }
        <p className='text-danger'>{photosError}</p>
        <ListGroup style={{maxHeight: `calc(100vh - 400px)`, overflowY: "auto"}}>
          {photos.map(photo => (
            <ListGroup.Item action
              key={photo.name}
              onClick={(event) => props.onSelectedPhotoChange(photo)}
            >{photo.name}</ListGroup.Item>
          ))}
        </ListGroup>
      </Row>
      <Row className="mb-3">
        <Form className="ps-0">
          <Form.Group>
            <Form.Label className="ms-2" htmlFor="imageFile">Upload a photo</Form.Label>
            <InputGroup>
              <Form.Control type="file" id="imageFile" accept=".jpg,.png"
                onChange={(event) => setImageFile(event.target.files[0])}
              />
              <Button variant="outline-secondary" disabled={!imageFile} onClick={uploadFile}>Upload</Button>
            </InputGroup>
          </Form.Group>
        </Form>
        <p className='text-danger'>{uploadError}</p>
      </Row>
      {photos.length < 1 ? null :
      <Row className="me-0">
        <Report apiUrl={props.apiUrl}/>
      </Row>}
    </>
  );
};
