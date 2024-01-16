// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { RDSDataClient } from "@aws-sdk/client-rds-data";
import { SESClient } from "@aws-sdk/client-ses";

const rdsDataClient: RDSDataClient = new RDSDataClient({});
const sesClient: SESClient = new SESClient({});

export { rdsDataClient, sesClient };
