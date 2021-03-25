// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import React from 'react';
import {ColorMap, FilterMap} from "./Utils";

/**
 * An individual node that displays a block item from Amazon Textract as a checkbox.
 *
 * @param props: Properties that determine how the item is displayed.
 *        props.BlockType: The type of block detected by Amazon Textract.
 *        props.Children: The hierarchical children of this item.
 *        props.Geometry: Contains the list of points that define the bounding polygon
 *                        for the item.
 *        props.Id: The ID of the block.
 *        props.shownPolygons: The list of polygons currently being displayed.
 *        props.togglePolygon: A function that turns a polygon on or off.
 *        props.Text: The text detected for this item.
 * @returns {JSX.Element}
 */
const ExplorerNode = (props) => {
  return (
    <li className="list-group-item">
      <div className="form-check" style={{color: ColorMap[props.BlockType]}}>
        <input
          className="form-check-input" type="checkbox" id={props.Id}
          checked={props.shownPolygons.some((poly) => poly.Id === props.Id)}
          onChange={(event) =>
            props.togglePolygon(
              props.Id, props.BlockType, props.Geometry, event.target.checked)
          }
        />
        <label className="form-check-label" htmlFor={props.Id}>
          {props.BlockType} {props.Text}
        </label>
      </div>
      <ExplorerList
        listClasses={["list-group"]}
        Children={props.Children}
        togglePolygon={props.togglePolygon}
        shownPolygons={props.shownPolygons}
        extractType={props.extractType}
      />
    </li>
  );
}

/**
 * A list of Amazon Textract block items that share a common hierarchical parent.
 *
 * @param props: Properties that determines how the list is displayed.
 *        props.Children: The list of items to display.
 *        props.extractType: The type of extraction data to display.
 *        props.listClasses: Bootstrap classes that determine how the list is displayed.
 *        props.shownPolygons: The list of polygons currently being displayed.
 *        props.togglePolygon: A function that turns a polygon on or off.
 * @returns {JSX.Element}
 */
const ExplorerList = (props) => {
  let childNodes = props.Children ?
    props.Children
      .filter((child) => {
        return FilterMap[props.extractType].includes(child.BlockType);
      }).map((child) => {
        return <ExplorerNode
          key={child.Id} Id={child.Id} BlockType={child.BlockType}
          Text={child.Text} Children={child.Children} Geometry={child.Geometry}
          togglePolygon={props.togglePolygon}
          shownPolygons={props.shownPolygons}
          extractType={props.extractType}
        />
      }) : null;

  return (
    <ul className={props.listClasses.join(" ")}>
      {childNodes}
    </ul>);
}

/**
 * A hierarchical tree of block items returned by Amazon Textract. Block items
 * represent parts of a document, such as pages, lines, and words.
 *
 * @param props: Properties that determine how the tree is displayed.
 *        props.extraction: Data returned by Amazon Textract in a hierarchical
 *                          structure.
 *        props.shownPolygons: The list of polygons currently being displayed.
 *        props.togglePolygon: A function that turns a polygon on or off.
 * @returns {JSX.Element}
 */
export const ExplorerTree = (props) => {
  return (
    <div>
      <div className="card-header">{props.extraction.Name}</div>
      <div style={{height: `calc(100vh - 200px)`, overflowY: "auto"}}>
        <ExplorerList
          listClasses={["list-group", "list-group-flush"]}
          Children={props.extraction.Children}
          extractType={props.extraction.ExtractType}
          togglePolygon={props.togglePolygon}
          shownPolygons={props.shownPolygons}
        />
      </div>
    </div>);
}