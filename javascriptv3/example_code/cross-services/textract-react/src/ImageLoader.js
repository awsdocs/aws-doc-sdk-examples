// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import React, { useState } from "react";

/**
 * A panel of inputs to specify an image location in an Amazon S3 bucket.
 *
 * @param props: Properties that control how the panel is displayed.
 *        props.canLoad: Indicates whether an image can be loaded.
 *        props.loadImage: A function that loads the image from the specified location.
 *        props.imageBucket: The name of the Amazon S3 bucket where the image is stored.
 *        props.imageKey: The name of the image file stored in Amazon S3.
 *        props.modelError: Indicates an underlying error.
 * @returns {JSX.Element}
 */
export const ImageLoader = (props) => {
  const [imageBucket, setImageBucket] = useState(props.imageBucket);
  const [imageKey, setImageKey] = useState(props.imageKey);

  return (
    <div className="card">
      <div className="card-body">
        <h5 className="card-title mb-3">Image location (in Amazon S3)</h5>
        <form>
          <div className="form-text">
            Load an image file from Amazon S3, then select Extract to explore
            image data.
          </div>
          <div className="mb-3">
            <label htmlFor="imageBucket">Bucket</label>
            <input
              type="text"
              className="form-control"
              id="imageBucket"
              value={imageBucket}
              onChange={(event) => setImageBucket(event.target.value)}
            />
          </div>
          <div className="mb-3">
            <label htmlFor="imageKey">Image name</label>
            <input
              type="text"
              className="form-control"
              id="imageKey"
              value={imageKey}
              onChange={(event) => setImageKey(event.target.value)}
            />
          </div>
          <div className="mb-3">
            <button
              type="button"
              className="btn btn-primary"
              disabled={!props.canLoad}
              onClick={() => props.loadImage(imageBucket, imageKey)}
            >
              Load
            </button>
          </div>
          <div
            className={`alert alert-danger ${
              props.modelError ? "" : "visually-hidden"
            }`}
            role="status"
          >
            {props.modelError}
          </div>
        </form>
      </div>
    </div>
  );
};
