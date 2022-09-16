// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import React, { useRef, useLayoutEffect } from "react";
import { ColorMap } from "./Utils";

/**
 * Displays a PNG image formatted as a Base64 string and draws polygons on top of
 * the image to show the location of bounding polygons associated with Amazon Textract
 * items.
 *
 * @param props: Properties that determine how the image is displayed.
 *        props.imageData: The image data as a Base64 string.
 *        props.shownPolygons: The list of polygons to draw over the image. Polygons
 *                             are drawn on a canvas element placed over the image.
 * @returns {JSX.Element}
 */
export const ImageDisplay = (props) => {
  const imgRef = useRef();
  const canvasRef = useRef();

  useLayoutEffect(() => {
    const positionCanvas = () => {
      const canvas = canvasRef.current;
      const img = imgRef.current;
      if (img) {
        // Update the canvas position to match that of the image.
        canvas.style.position = "absolute";
        canvas.style.left = img.offsetLeft + "px";
        canvas.style.top = img.offsetTop + "px";
        canvas.width = img.offsetWidth;
        canvas.height = img.offsetHeight;

        // Polygon points are defined as fractions of the total height and width of
        // the image.
        const context = canvas.getContext("2d");
        props.shownPolygons.forEach((poly) => {
          context.strokeStyle = ColorMap[poly.BlockType];
          context.lineWidth = 2;
          const points = poly.Geometry.Polygon;
          context.beginPath();
          context.moveTo(
            canvas.width * points[0].X,
            canvas.height * points[0].Y
          );
          points
            .slice(1)
            .forEach((point) =>
              context.lineTo(canvas.width * point.X, canvas.height * point.Y)
            );
          context.closePath();
          context.stroke();
        });
      }
    };

    window.addEventListener("resize", positionCanvas);
    positionCanvas();
    return () => window.removeEventListener("resize", positionCanvas);
  }, [props.shownPolygons]);

  return (
    <div>
      <img
        ref={imgRef}
        src={"data:image/png;base64," + props.imageData}
        className="img-fluid"
        alt="Extraction source"
      />
      <canvas ref={canvasRef} />
    </div>
  );
};
