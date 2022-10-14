/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { AppPlugin } from "./AppPlugin";
import { ItemTracker } from "ewp-item-tracker";
import { SqsMessage } from "ewp-sqs-message";
const plugins: AppPlugin[] = [ItemTracker, SqsMessage];
export default plugins;
