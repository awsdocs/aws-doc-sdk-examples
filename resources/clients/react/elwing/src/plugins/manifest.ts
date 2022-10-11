/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { AppPlugin } from "./AppPlugin";
import { SqsMessage } from "ewp-sqs-message";
const plugins: AppPlugin[] = [SqsMessage];
export default plugins;
