#**
 #* Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 #*
 #* This file is licensed under the Apache License, Version 2.0 (the "License").
 #* You may not use this file except in compliance with the License. A copy of
 #* the License is located at
 #*
 #* http://aws.amazon.com/apache2.0/
 #*
 #* This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 #* CONDITIONS OF ANY KIND, either express or implied. See the License for the
 #* specific language governing permissions and limitations under the License.
#**
# snippet-sourcedescription:[auth_request_test.rb uses the credentials in a  shared AWS credentials file on a local computer to authenticate a request to get all of the object key names  in a specific bucket.] 
# snippet-service:[s3]
# snippet-keyword:[Ruby]
# snippet-keyword:[Amazon S3]
# snippet-keyword:[Code Sample]
# snippet-keyword:[GET Bucket]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-02-11]
# snippet-sourceauthor:[AWS]
# snippet-start:[s3.ruby.auth_request_test.rb]
# This snippet example does the following:
# Creates an instance of the Aws::S3::Resource class. 
# Makes a request to Amazon S3 by enumerating objects in a bucket using the bucket method of Aws::S3::Resource. 
# The client generates the necessary signature value from the credentials in the AWS credentials file on your computer, 
# and includes it in the request it sends to Amazon S3.
# Prints the array of object key names to the terminal.
# The credentials that are used for this example come from a local AWS credentials file on the computer that is running this application. 
# The credentials are for an IAM user who can list objects in the bucket that the user specifies when they run the application.

# Use the Amazon S3 modularized gem for version 3 of the AWS Ruby SDK.
require 'aws-sdk-s3'

# Usage: ruby auth_request_test.rb OPERATION BUCKET
# Currently only the list operation is supported

# The operation to perform on the bucket.
operation = 'list' # default
operation = ARGV[0] if (ARGV.length > 0)

if ARGV.length > 1
  bucket_name = ARGV[1]
else
  exit 1
end

# Get an Amazon S3 resource.
s3 = Aws::S3::Resource.new(region: 'us-west-2')

# Get the bucket by name.
bucket = s3.bucket(bucket_name)

case operation

when 'list'
  if bucket.exists?
    # Enumerate the bucket contents and object etags.
    puts "Contents of '%s':" % bucket_name
    puts '  Name => GUID'

    bucket.objects.limit(50).each do |obj|
      puts "  #{obj.key} => #{obj.etag}"
    end
  else
    puts "The bucket '%s' does not exist!" % bucket_name
  end

else
  puts "Unknown operation: '%s'! Only list is supported." % operation
end
# snippet-end:[s3.ruby.auth_request_test.rb]
