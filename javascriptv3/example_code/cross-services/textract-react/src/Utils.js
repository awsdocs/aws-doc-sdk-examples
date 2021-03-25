// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/**
 * A map of Amazon Textract block types to colors used to display the items.
 */
export const ColorMap = {
  PAGE: 'RebeccaPurple',
  LINE: 'LimeGreen',
  WORD: 'SaddleBrown',
  TABLE: 'Teal',
  CELL: 'Salmon',
  KEY_VALUE_SET: 'CornflowerBlue',
  SELECTION_ELEMENT: 'Tomato'
};

/**
 * A map of Amazon Textract extraction types to the block types that are displayed
 * in the explorer panel.
 */
export const FilterMap = {
  text: ['PAGE', 'LINE', 'WORD'],
  table: ['PAGE', 'TABLE', 'CELL', 'WORD'],
  form: ['PAGE', 'KEY_VALUE_SET', 'WORD', 'SELECTION_ELEMENT']
}
