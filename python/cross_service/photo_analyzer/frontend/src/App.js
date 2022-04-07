// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/**
 * Shows how to use React to create a web page that connects to a REST service that
 * lets you do the following:
 *
 * * Get a list of images that are stored in an Amazon Simple Storage Service
 *   (Amazon S3) bucket.
 * * Upload images from your computer to your S3 bucket.
 * * Use Amazon Rekognition to analyze individual images. Images are displayed along
 *   with labels that identify items that were detected in the image.
 * * Use Amazon Rekognition to generate a report of all images in your S3 bucket and
 *   send an email of the report.
 */

import React, { useState } from 'react';
import Container from 'react-bootstrap/Container'
import Col from 'react-bootstrap/Col'
import Row from 'react-bootstrap/Row'
import {PhotoList} from './PhotoList';
import {Analysis} from './Analysis';

/**
 * An element that displays a list of images in an S3 bucket and lets you upload images
 * to the bucket. When an image is selected in the list, it is analyzed by Amazon
 * Rekognition and displayed along with labels that identify items that were detected
 * in the image.
 *
 * @param props: Properties that control how the element is displayed.
 *        props.apiUrl: The root URL of the REST service that lists images and performs
 *                      analysis.
 * @returns {JSX.Element}
 */
function App(props) {
  const [selectedPhoto, setSelectedPhoto] = useState(null)

  const handleSelectedPhotoChange = (photo) => {
    setSelectedPhoto(photo);
  };

  return (
    <Container>
      <Row>
        <h1 className='display-3 text-center'>Amazon Rekognition Photo Analyzer</h1>
        <hr/>
      </Row>
      <Row className='gx-5'>
        <Col className='border-end'>
          <PhotoList apiUrl={props.apiUrl} onSelectedPhotoChange={handleSelectedPhotoChange}/>
        </Col>
        <Col>
          <Analysis apiUrl={props.apiUrl} photo={selectedPhoto}/>
        </Col>
      </Row>
    </Container>
  );
}

export default App;