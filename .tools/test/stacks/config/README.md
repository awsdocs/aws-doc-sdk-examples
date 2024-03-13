# Config
This directory contains the configuration for this stack.

### [resources.yaml](resources.yaml)
Names of AWS resources in this testing stack with cross-account configuration.
* `topic_name` [String] Where scheduled testing events are published. Allows cross-account `Publish`.
* `bucket_name` [String] Where testing results are published. Allows cross-account `PutObject`.

### [targets.yaml](targets.yaml).
Name and configuration for specific SDK languages. Includes:
* `account_id` [String] The AWS Account where a set of AWS SDK test are running
* `status` [String] Whether the AWS SDK tests are running
