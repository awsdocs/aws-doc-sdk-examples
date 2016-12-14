# Copyright 2010-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of the
# License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
# OF ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License.

# Demonstrates how to:
#  1. Create a bucket in Amazon S3.
#  2. Define a bucket policy.
#  3. Add the policy to the bucket.
#  4. Change the policy.
#  5. Remove the policy from the bucket.
#  6. Delete the bucket.

require 'aws-sdk'

s3 = Aws::S3::Client.new(region: "us-west-2")

bucket = "example-bucket-name"

s3.create_bucket(bucket: bucket)

policy = {
  "Version" => "2012-10-17",
  "Statement" => [
    {
      "Effect" => "Allow",
      "Principal" => {
        "AWS" => [
          "arn:aws:iam::111122223333:user/Alice"
        ]
      },
      "Action" => "s3:*",
      "Resource" => [
        "arn:aws:s3:::#{bucket}"
      ]
    }
  ]
}.to_json

s3.put_bucket_policy(
  bucket: bucket,
  policy: policy
)

policy_string = s3.get_bucket_policy(bucket: bucket).policy.read
policy_json = JSON.parse(policy_string)

policy_json["Statement"][0]["Principal"]["AWS"] = "arn:aws:iam::111122223333:root"

s3.put_bucket_policy(
  bucket: bucket,
  policy: policy_json.to_json
)

s3.delete_bucket_policy(bucket: bucket)
s3.delete_bucket(bucket: bucket)