import { CloudWatchLogsClient } from "@aws-sdk/client-cloudwatch-logs";
import { CloudWatchQuery } from "./cloud-watch-query.js";

const cloudWatchQuery = new CloudWatchQuery(new CloudWatchLogsClient({}), {
  logGroupNames: ["/aws-glue/crawlers"],
  dateRange: [new Date(2023, 1, 1), new Date()],
});

await cloudWatchQuery.run();

console.log(JSON.stringify(cloudWatchQuery.resultsMeta, null, 2));
