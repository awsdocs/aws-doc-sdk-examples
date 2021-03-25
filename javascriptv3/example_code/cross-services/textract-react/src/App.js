// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import React, {useState, useEffect} from "react";
import {ImageLoader} from "./ImageLoader";
import {ImageDisplay} from "./ImageDisplay";
import {ExtractButtons} from "./ExtractButtons";
import {ExplorerCard} from "./ExplorerCard";
import {LoginCard} from "./LoginCard";

/**
 * Main React application element. Includes panels for signing in, loading and
 * displaying an image, and exploring Amazon Textract output.
 *
 * @param props: Properties that determine how the app is displayed.
 *        props.isSignedIn: Indicates whether the user is signed in to Amazon Cognito.
 *        props.config: Configuration elements, such as Amazon S3 bucket name.
 *        props.model: Model data and functions for operating on the data.
 * @returns {JSX.Element} The App element.
 */
function App(props) {
  const [shownPolygons, setShownPolygons] = useState([]);
  const [extracting, setExtracting] = useState(false);

  // Toggles the display of a polygon by adding it to or removing it from the
  // list of shown polygons.
  const togglePolygon = (polyId, blockType, geometry, show) => {
    console.log(`togglePolygon: ${polyId} ${geometry}, ${show}`);
    if (show) {
      setShownPolygons(shownPolygons.concat({
        Id: polyId, BlockType: blockType, Geometry: geometry}));
    }
    else {
      setShownPolygons(shownPolygons.filter((poly) => poly.Id !== polyId));
    }
  };

  useEffect(() => {
    if (extracting && props.model.extraction) {
      setExtracting(false);
    }
  }, [extracting, props.model.extraction]);

  const loadImage = (bucketName, objectKey) => {
    setShownPolygons([]);
    props.model.loadImage(bucketName, objectKey);
  }

  const startExtraction = (syncType, extractType) => {
    setShownPolygons([]);
    setExtracting(true);
    props.model.extractDocument(syncType, extractType);
  }

  const loginCard = (props.isSignedIn) ? null :
    <LoginCard loginUrl={props.config.LoginUrl}/>;

  return (
    <div className="App container">
      <div className="row">
        <div className="col-sm mt-3">
          {loginCard}
          <ImageLoader
            imageBucket={props.config.DefaultBucketName}
            imageKey={props.config.DefaultImageName}
            loadImage={loadImage}
            modelError={props.model.modelError}
            canLoad={props.isSignedIn}
          />
          <div className="card mt-3">
            <div className="card-body">
              <ImageDisplay
                imageData={props.model.imageData.base64Data} shownPolygons={shownPolygons}
              />
              <ExtractButtons
                hasImage={"base64Data" in props.model.imageData}
                extracting={extracting}
                extract={startExtraction}
              />
            </div>
          </div>
        </div>
        <div className="col-sm mt-3">
          <ExplorerCard
            header="Data explorer"
            extracting={extracting}
            extraction={props.model.extraction}
            togglePolygon={togglePolygon}
            shownPolygons={shownPolygons}
          />
        </div>
      </div>
    </div>
  );
}

export default App;
