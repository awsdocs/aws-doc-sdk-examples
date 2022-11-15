/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { log } from "../log.js";

const deleteStackReminder = () => {
  log(
    "All done. Remember to delete your stack. See the readme for more information."
  );
};

export { deleteStackReminder };
