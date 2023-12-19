import { CloudWatchLogsClient } from "@aws-sdk/client-cloudwatch-logs";
import { CloudWatchQuery } from "./cloud-watch-query.js";

const cloudWatchQuery = new CloudWatchQuery(new CloudWatchLogsClient({}), {
  logGroupNames: ["/workflows/cloudwatch-log/big-query"],
  dateRange: [new Date(2023, 1, 1), new Date()],
  queryConfig: { limit: 100 },
});

await cloudWatchQuery.run();

console.log(JSON.stringify(cloudWatchQuery.resultsMeta, null, 2));
