import { CloudWatchLogsClient } from "@aws-sdk/client-cloudwatch-logs";
import { CloudWatchQuery } from "./cloud-watch-query.js";

const cloudWatchQuery = new CloudWatchQuery(new CloudWatchLogsClient({}), {
  logGroupNames: ["/aws-glue/crawlers"],
  dateRange: [new Date(2022, 1, 1), new Date()],
});

const results = await cloudWatchQuery.run();
console.log(
  results.map((resultFields) =>
    resultFields.map((field) => `${field.field}: ${field.value}`),
  ),
  JSON.stringify(cloudWatchQuery.resultsMeta, null, 2),
);
