# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[app.py creates an Amazon S3 bucket.
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[AWS CDK]
# snippet-keyword:[Amazon S3]
# snippet-service:[s3]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-04-01]
# snippet-sourceauthor:[Doug-AWS]
# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
# snippet-start:[cdk.python.bucket2]
from aws_cdk import (
    aws_s3 as s3,
    cdk,
)


class S3Stack(cdk.Stack):
    def __init__(self, app: cdk.App, id: str) -> None:
        super().__init__(app, id)

        bucket = aws_s3.Bucket(
            self, "MyBucket",
            versioned=True,
            encryption=aws_s3.BucketEncryption.KmsManaged)

app = cdk.App()
S3Stack(app, "MyStack")
app.run()
# snippet-end:[cdk.python.bucket2]
