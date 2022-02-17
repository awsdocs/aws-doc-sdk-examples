# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

# Purpose
# This code example demonstrates how to list the documents for each of your Amazon WorkDocs users.

# snippet-start:[s3.wd_list_user_docs.rb]

require 'aws-sdk-workdocs'  # v2: require 'aws-sdk'

def get_user_folder(client, orgId, user_email)
  root_folder = ''

  resp = client.describe_users({
    organization_id: orgId,
  })

  # resp.users should have only one entry
  resp.users.each do |user|
    if user.email_address == user_email
      root_folder = user.root_folder_id
    end
  end

  return root_folder
end
# Replace us-west-2 with the AWS Region you're using for Amazon WorkDocs.
client = Aws::WorkDocs::Client.new(region: 'us-west-2')

# Set to the email address of a user
user_email = 'someone@somewhere'

# Set to the OrganizationId of your WorkDocs site.
orgId = 'd-123456789c'

user_folder = get_user_folder(client, orgId, user_email)

if user_folder == ''
  puts 'Could not get root folder for user with email address ' + user_email
  exit(1)
end

resp = client.describe_folder_contents({
  folder_id: user_folder, # required
  sort: "NAME", # accepts DATE, NAME
  order: "ASCENDING", # accepts ASCENDING, DESCENDING
})

resp.documents.each do |doc|
  md = doc.latest_version_metadata

  puts "Name:          #{md.name}"
  puts "Size (bytes):  #{md.size}"
  puts "Last modified: #{doc.modified_timestamp}"
  puts "Doc ID:        #{doc.id}"
  puts "Version ID:    #{md.id}"
  puts
end
# snippet-end:[s3.wd_list_user_docs.rb]
