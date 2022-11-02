/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { getFirstEntry } from "../../../../libs/utils/util-csv.js";
import { log } from "../../../../libs/utils/util-log.js";
import { resendConfirmationCode } from "../../../actions/resend-confirmation-code.js";
import { FILE_USER_POOLS } from "./constants.js";

const resendConfirmationCodeHandler = async ([_cmd, username]) => {
  try {
    const [_userPoolId, clientId] = getFirstEntry(FILE_USER_POOLS);
    await resendConfirmationCode({ clientId, username });
    log("Confirmation code sent.");
  } catch (err) {
    log(err);
  }
};

export { resendConfirmationCodeHandler };
