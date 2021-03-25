// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Test data used by several unit tests.

/**
 * Data in the format returned by Amazon Textract.
 */
export const TestExtractResponse = {
  Blocks: [{
    BlockType: 'PAGE',
    Geometry: {test: 'test geometry'},
    Id: 'page1',
    Relationships: [{
      Ids: [
        'line1-1',
      ],
      Type: 'CHILD'
    }]
  }, {
    BlockType: 'LINE',
    Geometry: {},
    Id: 'line1-1',
    Text: 'LINE 1',
    Relationships: [{
      Ids: [
        'word1-1-1',
      ],
      Type: 'CHILD'
    }]
  }, {
    BlockType: 'WORD',
    Geometry: {},
    Id: 'word1-1-1',
    Text: 'WORD 1',
  }]
};

/**
 * Data formatted into a hierarchical structure.
 */
export const TestExtractDocument = {
  Name: "Test extract document",
  ExtractType: "text",
  Children: [{
    BlockType: "PAGE",
    Id: "page1",
    Geometry: {test: "test geometry"},
    Children: [{
      BlockType: "LINE",
      Id: "line1-1",
      Text: "LINE 1",
      Geometry: {},
      Children: [{
        BlockType: 'WORD',
        Id: 'word1-1-1',
        Text: 'WORD 1',
        Geometry: {},
      }],
      Relationships: [{
        Ids: [
          'word1-1-1',
        ],
        Type: 'CHILD'
      }]
    }],
    Relationships: [{
      Ids: [
        'line1-1',
      ],
      Type: 'CHILD'
    }]
  }]
};
