---
combined: true
debug:
  engine: bedrock
  finish: end_turn
  id: null
  model: anthropic.claude-3-sonnet-20240229-v1:0
  usage: null
isolated: true
prompt: |
  Create a Windows batch script that uses curl commands to download the API documents for each API used.
  API Ref urls look like https://docs.aws.amazon.com/AmazonS3/latest/API/API_GetObjectLegalHold.html
  Initialize the destination files with a markdown yaml header with a single property `skip: true`.
  After the curl command, include a pipe that uses a command to select only the main-col-body: `"#main-col-body"`, pipe that through another command to tranform it to utf-8, and finally append to the file `10_{apiname}.md`.
---
