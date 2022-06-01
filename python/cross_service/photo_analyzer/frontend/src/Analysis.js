// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import React, {useEffect, useState} from "react";
import $ from "jquery";
import Row from "react-bootstrap/Row";
import ListGroup from "react-bootstrap/ListGroup";
import ButtonGroup from "react-bootstrap/ButtonGroup";
import Button from "react-bootstrap/Button";
import {ImageDisplay} from "./ImageDisplay";

/**
 * An element that displays an image and a list of labels that identify items that
 * were detected in the image. Labels that have bounding boxes associated with them
 * are included in a separate list. Clicking one of these labels draws boxes around
 * the parts of the image where the items were detected.
 *
 * @param props: Properties that determine how the image and labels are displayed.
 *        props.apiUrl: The root URL of the REST API that is called to get
 *                      image and analysis data.
 *        props.photo: The name of the photo to display.
 * @returns {JSX.Element}
 */
export const Analysis = (props) => {
  const [photoError, setPhotoError] = useState(null);
  const [labelsError, setLabelsError] = useState(null);
  const [displayPhotoUrl, setDisplayPhotoUrl] = useState(null);
  const [labels, setLabels] = useState([]);
  const [boxedLabels, setBoxedLabels] = useState([]);
  const [selectedLabel, setSelectedLabel] = useState(null);

  /**
   * Calls the REST service to get a presigned URL that can be used to download the
   * image data from Amazon Simple Storage Service (Amazon S3).
   */
  useEffect(() => {
    let isMounted = true;
    setSelectedLabel(null);
    if (props.photo) {
      setLabels([]);
      setBoxedLabels([]);
      $.get(`${props.apiUrl}/photos/${props.photo.name}`,
        (result) => {
          if (isMounted) {
            setDisplayPhotoUrl(result.url);
          }
          console.log(`Got URL: ${result.url}`);
        })
        .fail((error) => {
          if (isMounted) {
            setPhotoError(error);
          }
        });
    }
    return () => { isMounted = false; };
  }, [props.photo, props.apiUrl]);

  /**
   * Calls the REST service to analyze the photo and get a list of labels that identify
   * items that are detected in the image.
   */
  useEffect(() => {
    let isMounted = true;
    if (props.photo) {
      $.get(`${props.apiUrl}/photos/${props.photo.name}/labels`,
        (result) => {
          if (isMounted) {
            if (!result || result.length < 1) {
              setLabelsError("No labels found from image analysis.");
            } else {
              console.log(`Detected ${result.length} labels.`);
              setLabels(result);
              setBoxedLabels(result.filter(label => label.Instances.length > 0));
            }
          }
        })
        .fail((error) => {
          if (isMounted) {
            setLabelsError(error);
          }
        });
    }
    return () => { isMounted = false; };
  }, [props.photo, props.apiUrl])

  return (
    <>
      <h3>Photo analysis</h3>
      <Row className='mb-3'>
        <p className='text-danger'>{photoError}</p>
        <ImageDisplay src={displayPhotoUrl}
                      alt={props.photo ? props.photo.name : "Nothing to show yet!"}
                      boxes={(selectedLabel) ? selectedLabel.Instances : []}
        />
      </Row>
      <Row className='mb-3'>
        <p className='text-danger'>{labelsError}</p>
        {(labels.length === 0) ? null :
        <>
          <p className="lead">Items found in the image.</p>
          <ListGroup className='flex-wrap' horizontal>
            {labels.map(label => (
              <ListGroup.Item className='mb-2 flex-md-grow-0'
                      key={label.Name}
              >{label.Name}</ListGroup.Item>
              ))
            }
          </ListGroup>
          {boxedLabels.length < 1 ? null :
          <p className="lead">Select a boxed item to outline it in the image.</p>}
          <ButtonGroup className='flex-wrap'>
            {boxedLabels.map(label => (
              <Button className='mb-2 flex-md-grow-0' variant='outline-secondary'
                      key={label.Name}
                      onClick={() => setSelectedLabel(label)}
              >{label.Name}</Button>
              ))
            }
          </ButtonGroup>
        </>}
      </Row>
    </>
  );
};
