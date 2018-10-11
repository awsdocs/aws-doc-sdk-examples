#snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
#snippet-keyword:[Ruby]
#snippet-keyword:[Code Sample]
#snippet-service:[<<ADD SERVICE>>]
#snippet-sourcetype:[<<snippet or full-example>>]
#snippet-sourcedate:[]
#snippet-sourceauthor:[AWS]
# Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
# 1. List AWS account aliases.
# 2. Create an account alias.
# 3. Delete the account alias.

require 'aws-sdk-iam'  # v2: require 'aws-sdk'

iam = Aws::IAM::Client.new(region: 'us-east-1')

account_alias = "my-account-alias"

# List account aliases.
def list_aliases(iam)
  list_account_aliases_response = iam.list_account_aliases

  if list_account_aliases_response.account_aliases.count == 0
    puts "No account aliases."
  else
    puts "Aliases:"
    list_account_aliases_response.account_aliases.each do |account_alias|
      puts account_alias
    end
  end

end

puts "Before creating account alias..."
list_aliases(iam)

# Create an account alias.
puts "\nCreating account alias..."
iam.create_account_alias({ account_alias: account_alias })

puts "\nAfter creating account alias..."
list_aliases(iam)

# Delete the account alias.
puts "\nDeleting account alias..."
iam.delete_account_alias({ account_alias: account_alias })

puts "\nAfter deleting account alias..."
list_aliases(iam)
