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

import boto3

# Create an S3 client
S3 = boto3.client('s3')

SOURCE_FILENAME = 'filename'
BUCKET_NAME = 'bucket-name'

# Uploads the given file using a managed uploader, which will split up large
# files automatically and upload parts in parallel.
S3.upload_file(SOURCE_FILENAME, BUCKET_NAME, SOURCE_FILENAME)
