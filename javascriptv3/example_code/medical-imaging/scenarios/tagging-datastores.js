/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

// Snippet for README link.
// snippet-start:[medical-imaging.JavaScript.medical-imaging_tagging_datastores.V3]
// snippet-end:[medical-imaging.JavaScript.medical-imaging_tagging_datastores.V3]

import { fileURLToPath } from "url";

const { tagResource } = await import("../actions/tag-resource.js");
const { untagResource } = await import("../actions/untag-resource.js");
const { listTagsForResource } = await import(
  "../actions/list-tags-for-resource.js"
);

// Invoke the following code if this file is being run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  // snippet-start:[medical-imaging.JavaScript.datastore.tagging.V3]
  try {
    const datastoreArn =
      "arn:aws:medical-imaging:us-east-1:123456789012:datastore/12345678901234567890123456789012";
    const tags = {
      Deployment: "Development",
    };
    await tagResource(datastoreArn, tags);
  } catch (e) {
    console.log(e);
  }
  // snippet-end:[medical-imaging.JavaScript.datastore.tagging.V3]

  // snippet-start:[medical-imaging.JavaScript.datastore.list_tags.V3]
  try {
    const datastoreArn =
      "arn:aws:medical-imaging:us-east-1:123456789012:datastore/12345678901234567890123456789012";
    const { tags } = await listTagsForResource(datastoreArn);
    console.log(tags);
  } catch (e) {
    console.log(e);
  }
  // snippet-end:[medical-imaging.JavaScript.datastore.list_tags.V3]
  // snippet-start:[medical-imaging.JavaScript.datastore.untag.V3]
  try {
    const datastoreArn =
      "arn:aws:medical-imaging:us-east-1:123456789012:datastore/12345678901234567890123456789012";
    const keys = ["Deployment"];
    await untagResource(datastoreArn, keys);
  } catch (e) {
    console.log(e);
  }
  // snippet-end:[medical-imaging.JavaScript.datastore.untag.V3]
}
