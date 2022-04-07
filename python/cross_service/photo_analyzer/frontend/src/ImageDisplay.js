// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import React, { useRef, useLayoutEffect } from "react";
import Image from "react-bootstrap/Image";

/**
 * Displays an image and draws bounding boxes on top of the image to show the
 * location of items that were detected by Amazon Rekognition.
 *
 * @param props: Properties that determine how the image is displayed.
 *        props.alt: The alt text of the image.
 *        props.boxes: The list of boxes to draw over the image. Boxes are drawn on
 *                     a canvas element that is placed over the image.
 *        props.src: A URL where the image is stored.
 * @returns {JSX.Element}
 */
export const ImageDisplay = (props) => {
  const imgRef = useRef();
  const canvasRef = useRef();

  useLayoutEffect(() => {
    const positionCanvas = (event) => {
      const canvas = canvasRef.current;
      const img = imgRef.current;
      if (img) {
        // Update the canvas position to match that of the image.
        canvas.style.position = "absolute";
        canvas.style.left = img.offsetLeft + "px";
        canvas.style.top = img.offsetTop + "px";
        canvas.width = img.offsetWidth;
        canvas.height = img.offsetHeight;

        // Boxes are defined as fractions of the total height and width of
        // the image.
        const context = canvas.getContext("2d");
        context.strokeStyle = 'LimeGreen';
        context.lineWidth = 1;
        props.boxes.forEach((box) => {
          context.rect(
            canvas.width * box.BoundingBox.Left,
            canvas.height * box.BoundingBox.Top,
            canvas.width * box.BoundingBox.Width,
            canvas.height * box.BoundingBox.Height);
          context.stroke();
        });
      }
    };

    window.addEventListener("resize", positionCanvas);
    positionCanvas();
    return () => window.removeEventListener("resize", positionCanvas);
  }, [props.boxes]);

  return (
    <div>
      <Image fluid
        ref={imgRef}
        src={props.src}
        alt={props.alt}
      />
      <canvas ref={canvasRef}/>
    </div>
  );
};
