---
combined: true
debug:
  engine: bedrock
  finish: end_turn
  id: null
  model: anthropic.claude-3-sonnet-20240229-v1:0
  usage: null
isolated: false
prompt: |
  Create a windows bash script to download the API ref and extract text from HTML for each SDK action into 10_{ActionName}.md
  Go action urls look like https://pkg.go.dev/github.com/aws/aws-sdk-go-v2/service/s3#Client.GetObjectLegalHold
  Do not use a for loop or variables, list all commands exactly. Do not use a directory for extracted files. Use curl and html2text. Prefer one command for each action.
---
```bash
# GetObjectLegalHold
curl -s https://pkg.go.dev/github.com/aws/aws-sdk-go-v2/service/s3#Client.GetObjectLegalHold | html2text > 10_GetObjectLegalHold.md

# GetObjectLockConfiguration
curl -s https://pkg.go.dev/github.com/aws/aws-sdk-go-v2/service/s3#Client.GetObjectLockConfiguration | html2text > 10_GetObjectLockConfiguration.md

# GetObjectRetention
curl -s https://pkg.go.dev/github.com/aws/aws-sdk-go-v2/service/s3#Client.GetObjectRetention | html2text > 10_GetObjectRetention.md

# PutObjectLegalHold
curl -s https://pkg.go.dev/github.com/aws/aws-sdk-go-v2/service/s3#Client.PutObjectLegalHold | html2text > 10_PutObjectLegalHold.md

# PutObjectLockConfiguration
curl -s https://pkg.go.dev/github.com/aws/aws-sdk-go-v2/service/s3#Client.PutObjectLockConfiguration | html2text > 10_PutObjectLockConfiguration.md

# PutObjectRetention
curl -s https://pkg.go.dev/github.com/aws/aws-sdk-go-v2/service/s3#Client.PutObjectRetention | html2text > 10_PutObjectRetention.md

# PutDefaultObjectLockConfiguration
curl -s https://pkg.go.dev/github.com/aws/aws-sdk-go-v2/service/s3#Client.PutBucketObjectLockConfiguration | html2text > 10_PutDefaultObjectLockConfiguration.md
```

This Bash script uses the `curl` command to download the API reference for each of the specified S3 actions from the Go pkg.go.dev website. The `-s` flag is used to suppress the progress meter and other output from `curl`.

The `html2text` command is then used to extract the plain text content from the HTML response. The output of `html2text` is redirected to a file with the name `10_{ActionName}.md`.

Note that this script assumes that the `html2text` command is installed on your system. If it's not installed, you can install it using your package manager (e.g., `apt-get install html2text` on Ubuntu or `brew install html2text` on macOS).

Also, please note that this script does not use any loops or variables, as per your requirements. Each command is listed explicitly for each action.