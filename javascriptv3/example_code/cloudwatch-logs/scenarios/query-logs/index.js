import { CloudWatchLogsClient } from "@aws-sdk/client-cloudwatch-logs";
import { CloudWatchQuery } from "./cloud-watch-query.js";

const cloudWatchQuery = new CloudWatchQuery(new CloudWatchLogsClient({}), {
  logGroupNames: ["/aws-glue/crawlers"],
  queryString: "fields @timestamp, @message | sort @timestamp desc",
  startDate: new Date(2023, 9, 1),
  endDate: new Date(2023, 9, 31),
});

cloudWatchQuery.run((subQueries) => {
  console.log(subQueries.map((query) => query.response.results).flat());
});
