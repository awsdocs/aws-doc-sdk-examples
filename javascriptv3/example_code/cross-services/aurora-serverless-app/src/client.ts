import { RDSDataClient } from "@aws-sdk/client-rds-data";
import { SESClient } from "@aws-sdk/client-ses";

const rdsDataClient: RDSDataClient = new RDSDataClient({});
const sesClient: SESClient = new SESClient({});

export { rdsDataClient, sesClient };
