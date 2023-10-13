/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { SupportClient } from "@aws-sdk/client-support";
import { DEFAULT_REGION } from "@aws-sdk-examples/libs/utils/util-aws-sdk.js";

const client = new SupportClient({ region: DEFAULT_REGION });

export { client };
