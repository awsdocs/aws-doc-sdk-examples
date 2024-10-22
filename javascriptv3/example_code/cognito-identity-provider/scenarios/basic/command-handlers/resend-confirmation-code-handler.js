// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { getFirstEntry } from "@aws-doc-sdk-examples/lib/utils/util-csv.js";
import { logger } from "@aws-doc-sdk-examples/lib/utils/util-log.js";
import { resendConfirmationCode } from "../../../actions/resend-confirmation-code.js";
import { FILE_USER_POOLS } from "./constants.js";

const resendConfirmationCodeHandler = async ([_cmd, username]) => {
  try {
    const [_userPoolId, clientId] = getFirstEntry(FILE_USER_POOLS);
    await resendConfirmationCode({ clientId, username });
    logger.log("Confirmation code sent.");
  } catch (err) {
    logger.error(err);
  }
};

export { resendConfirmationCodeHandler };
