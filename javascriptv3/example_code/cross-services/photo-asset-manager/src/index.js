// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { handler as upload } from "./functions/upload.js";
import { handler as detectLabels } from "./functions/detect-labels.js";
import { handler as labels } from "./functions/labels.js";
import { handler as download } from "./functions/download.js";

export const handlers = {
  detectLabels,
  labels,
  download,
  upload,
};
