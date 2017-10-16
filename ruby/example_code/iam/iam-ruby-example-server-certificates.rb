# Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
# 1. Update a server certificate.
# 2. Delete the server certificate.
# 3. List information about any remaining server certificates.

require 'aws-sdk-iam'  # v2: require 'aws-sdk'

iam = Aws::IAM::Client.new(region: 'us-east-1')

server_certificate_name = "my-server-certificate"
changed_server_certificate_name = "my-changed-server-certificate"

# Update a server certificate.
iam.update_server_certificate({
  server_certificate_name: server_certificate_name,
  new_server_certificate_name: changed_server_certificate_name
})

# Delete the server certificate.
iam.delete_server_certificate({
  server_certificate_name: changed_server_certificate_name
})

# List information about any remaining server certificates.
list_server_certificates_response = iam.list_server_certificates

if list_server_certificates_response.server_certificate_metadata_list.count == 0
  puts "No server certificates."
else
  list_server_certificates_response.server_certificate_metadata_list.each do |certificate_metadata|
    puts "-" * certificate_metadata.server_certificate_name.length
    puts "Name: #{certificate_metadata.server_certificate_name}"

    get_server_certificate_response = iam.get_server_certificate({ 
      server_certificate_name: "certificate_metadata.server_certificate_name" 
    })
    puts "ID: #{get_server_certificate_response.server_certificate.server_certificate_metadata.server_certificate_id}"
  end
end
