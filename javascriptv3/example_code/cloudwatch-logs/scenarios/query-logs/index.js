import { CloudWatchLogsClient } from "@aws-sdk/client-cloudwatch-logs";
import { CloudWatchQuery } from "./cloud-watch-query.js";

const cloudWatchQuery = new CloudWatchQuery(new CloudWatchLogsClient({}), {
  logGroupNames: ["/aws-glue/crawlers"],
  dateRange: [new Date(2022, 1, 1), new Date()],
  queryConfig: { limit: 50 },
});

await cloudWatchQuery.run();
console.log(
  `Queries made: ${cloudWatchQuery.resultsMeta.queryCount}\nLogs found: ${cloudWatchQuery.resultsMeta.logCount}`,
);
