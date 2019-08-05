# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourceauthor:[Doug-AWS]
# snippet-sourcedescription:[Lists the IAM user access keys, creates an access key for the user, determine when the user's access keys were last used, deactivates an access key, and deletes an access key.]
# snippet-keyword:[AWS Identity and Access Management]
# snippet-keyword:[create_access_key method]
# snippet-keyword:[delete_access_key method]
# snippet-keyword:[get_access_key_last_used method]
# snippet-keyword:[list_access_keys method]
# snippet-keyword:[update_access_key method]
# snippet-keyword:[Ruby]
# snippet-service:[iam]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-03-16]
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

# Demonstrates how to:
# 1. List AWS IAM user access keys.
# 2. Create an access key.
# 3. Determine when access keys were last used.
# 4. Deactivate access keys.
# 5. Delete the access key.

require 'aws-sdk-iam'  # v2: require 'aws-sdk'

iam = Aws::IAM::Client.new(region: 'us-east-1')

user_name = "my-user"

# List user access keys.
def list_keys(iam, user_name)
  begin 
    list_access_keys_response = iam.list_access_keys({ user_name: user_name })

    if list_access_keys_response.access_key_metadata.count == 0
      puts "No access keys."
    else
      puts "Access keys:"
      list_access_keys_response.access_key_metadata.each do |key_metadata|
        puts "  Access key ID: #{key_metadata.access_key_id}"
      end
    end
  
  rescue Aws::IAM::Errors::NoSuchEntity
    puts "Cannot find user '#{user_name}'."
    exit(false)
  end 
end

puts "Before creating access key..."
list_keys(iam, user_name)

# Create an access key.
puts "\nCreating access key..."

begin
  iam.create_access_key({ user_name: user_name })
  puts "\nAfter creating access key..."
  list_keys(iam, user_name)
rescue Aws::IAM::Errors::LimitExceeded
  puts "Too many access keys. Can't create any more."
end

# Determine when access keys were last used.
puts "\nKey(s) were last used..."

list_access_keys_response = iam.list_access_keys({ user_name: user_name })

list_access_keys_response.access_key_metadata.each do |key_metadata|
  resp = iam.get_access_key_last_used({ access_key_id: key_metadata.access_key_id })
  
  puts "  Key '#{key_metadata.access_key_id}' last used on #{resp.access_key_last_used.last_used_date}"

  # Deactivate access keys.
  puts "  Trying to deactivate this key..."
    
  iam.update_access_key({
    user_name: user_name,
    access_key_id: key_metadata.access_key_id,
    status: "Inactive"
  })
end

puts "\nAfter deactivating access key(s)..."
list_keys(iam, user_name)

# Delete the access key.
puts "\nDeleting access key..."

iam.delete_access_key({
  user_name: user_name,
  access_key_id: list_access_keys_response.access_key_metadata[0].access_key_id
})

puts "\nAfter deleting access key..."
list_keys(iam, user_name)
