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


# snippet-sourcedescription:[auth_federation_token_request_test.rb allows a federated user with a limited set of permissions to lists keys in the specified bucket.] 
# snippet-service:[s3]
# snippet-keyword:[Ruby]
# snippet-keyword:[Amazon S3]
# snippet-keyword:[Code Sample]
# snippet-keyword:[GET Bucket, GET Object, GET Keys]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[YYYY-MM-DD]
# snippet-sourceauthor:[AWS]

# snippet-start:[s3.ruby.auth_federation_token_request_test.rb]

require 'aws-sdk-s3'
require 'aws-sdk-iam'

USAGE = << DOC

Usage: federated_create_bucket_policy.rb -b BUCKET -u USER [-r REGION] [-d] [-h]

  Creates a federated policy for USER to list items in BUCKET for one hour.

  BUCKET is required and must already exist.

  USER is required and if not found, is created.

  If REGION is not supplied, defaults to us-west-2.

  -d gives you extra (debugging) information.

  -h displays this message and quits.

DOC

$debug = false

def print_debug(s)
  if $debug
    puts s
  end
end

def get_user(region, user_name, create)
  user = nil
  iam = Aws::IAM::Client.new(region: 'us-west-2')
  
begin
  user = iam.create_user(user_name: user_name)
  iam.wait_until(:user_exists, user_name: user_name)
  print_debug("Created new user #{user_name}")
rescue Aws::IAM::Errors::EntityAlreadyExists
  print_debug("Found user #{user_name} in region #{region}")
end
end

# main
region = 'us-west-2'
user_name = ''
bucket_name = ''

i = 0

while i &lt; ARGV.length
  case ARGV[i]

    when '-b'
      i += 1
      bucket_name = ARGV[i]

    when '-u'
      i += 1
      user_name = ARGV[i]

    when '-r'
      i += 1

      region = ARGV[i]

    when '-d'
      puts 'Debugging enabled'
      $debug = true

    when '-h'
      puts USAGE
      exit 0

    else
      puts 'Unrecognized option: ' + ARGV[i]
      puts USAGE
      exit 1

  end

  i += 1
end

if bucket_name == ''
  puts 'You must supply a bucket name'
  puts USAGE
  exit 1
end

if user_name == ''
  puts 'You must supply a user name'
  puts USAGE
  exit 1
end

#Identify the IAM user we allow to list Amazon S3 bucket items for an hour.
user = get_user(region, user_name, true)

# Create a new STS client and get temporary credentials.
sts = Aws::STS::Client.new(region: region)

creds = sts.get_federation_token({
  duration_seconds: 3600,
  name: user_name,
  policy: "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Sid\":\"Stmt1\",\"Effect\":\"Allow\",\"Action\":\"s3:ListBucket\",\"Resource\":\"arn:aws:s3:::#{bucket_name}\"}]}",
})

# Create an Amazon S3 resource with temporary credentials.
s3 = Aws::S3::Resource.new(region: region, credentials: creds)

puts "Contents of '%s':" % bucket_name
puts '  Name => GUID'

 s3.bucket(bucket_name).objects.limit(50).each do |obj|
      puts "  #{obj.key} => #{obj.etag}"
end
# snippet-end:[s3.ruby.auth_federation_token_request_test.rb]