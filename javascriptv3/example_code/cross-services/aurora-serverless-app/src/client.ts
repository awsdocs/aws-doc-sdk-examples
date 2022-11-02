import { RDSDataClient } from "@aws-sdk/client-rds-data";
import { SESClient } from "@aws-sdk/client-ses";
import { createClientForDefaultRegion } from "../../../libs/utils/util-aws-sdk.js";

const rdsDataClient: RDSDataClient =
  createClientForDefaultRegion(RDSDataClient);
const sesClient: SESClient = createClientForDefaultRegion(SESClient);

export { rdsDataClient, sesClient };
