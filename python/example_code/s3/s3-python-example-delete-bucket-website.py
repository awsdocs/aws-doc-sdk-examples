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
s3 = boto3.client('s3')

# Call S3 to delete the website policy for the given bucket
s3.delete_bucket_website(Bucket='BUCKET_NAME')
 

#snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
#snippet-sourcedescription:[s3-python-example-delete-bucket-website.py demonstrates how to delete the website configuration of an Amazon S3 bucket.]
#snippet-keyword:[Python]
#snippet-keyword:[AWS SDK for Python (Boto3)]
#snippet-keyword:[Code Sample]
#snippet-keyword:[Amazon S3]
#snippet-service:[s3]
#snippet-sourcetype:[full-example]
#snippet-sourcedate:[2018-06-25]
#snippet-sourceauthor:[jschwarzwalder (AWS)]

