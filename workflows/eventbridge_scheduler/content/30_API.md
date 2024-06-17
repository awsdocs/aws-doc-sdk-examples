---
combined: true
debug:
  engine: bedrock
  finish: end_turn
  id: msg_bdrk_017KSLR8avpTTy1FwLLN9Nim
  lastRun: 2024-06-11T14:47:07.713Z
  model: claude-3-haiku-20240307
  statistics:
    firstByteLatency: 605
    inputTokenCount: 3322
    invocationLatency: 6677
    outputTokenCount: 717
prompt: |
  Create a windows bash script to download the API ref and extract text from HTML for each SDK action into 30_{ActionName}.md
  .NET action urls look like https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/Scheduler/MISchedulerCreateScheduleAsyncCreateScheduleRequestCancellationToken.html
  Do not use a for loop or variables, list all commands exactly. Do not use a directory for extracted files. Use curl and html2text. Prefer one command for each action.
---
Here's a Windows Bash script that downloads the API reference and extracts the text from the HTML for each .NET SDK action:

```bash
curl https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/Scheduler/MISchedulerCreateScheduleAsyncCreateScheduleRequestCancellationToken.html | html2text > 30_CreateSchedule.md
curl https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/Scheduler/MISchedulerCreateScheduleGroupAsyncCreateScheduleGroupRequestCancellationToken.html | html2text > 30_CreateScheduleGroup.md
curl https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/Scheduler/MISchedulerDeleteScheduleAsyncDeleteScheduleRequestCancellationToken.html | html2text > 30_DeleteSchedule.md
curl https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/Scheduler/MISchedulerDeleteScheduleGroupAsyncDeleteScheduleGroupRequestCancellationToken.html | html2text > 30_DeleteScheduleGroup.md
```

This script uses the `curl` command to download the HTML content of the API reference pages for each .NET SDK action, and then uses the `html2text` tool to extract the plain text from the HTML. The extracted text is then saved to a file with the format `30_{ActionName}.md`.

Note that this script assumes you have `curl` and `html2text` installed and available in your Windows Bash environment. If you don't have `html2text`, you can use the `sed` command to remove the HTML tags instead:

```bash
curl https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/Scheduler/MISchedulerCreateScheduleAsyncCreateScheduleRequestCancellationToken.html | sed 's/<[^>]*>//g' > 30_CreateSchedule.md
curl https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/Scheduler/MISchedulerCreateScheduleGroupAsyncCreateScheduleGroupRequestCancellationToken.html | sed 's/<[^>]*>//g' > 30_CreateScheduleGroup.md
curl https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/Scheduler/MISchedulerDeleteScheduleAsyncDeleteScheduleRequestCancellationToken.html | sed 's/<[^>]*>//g' > 30_DeleteSchedule.md
curl https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/Scheduler/MISchedulerDeleteScheduleGroupAsyncDeleteScheduleGroupRequestCancellationToken.html | sed 's/<[^>]*>//g' > 30_DeleteScheduleGroup.md
```

This alternative script uses the `sed` command to remove the HTML tags from the downloaded content.