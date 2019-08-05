# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourceauthor:[Doug-AWS]
# snippet-sourcedescription:[Lists the values of your secrets.]
# snippet-keyword:[AWS Secrets Manager]
# snippet-keyword:[get_secret_value method]
# snippet-keyword:[list_secrets method]
# snippet-keyword:[Ruby]
# snippet-service:[secretsmanager]
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

require 'aws-sdk-secretsmanager'

# Gets all secrets in us-west-2

sm = Aws::SecretsManager::Client.new(region: 'us-west-2')

resp = sm.list_secrets

puts 'Secrets:'

resp.secret_list.each do |s|
  puts '  name: ' + s.name
  puts '  key/value:'

  resp = sm.get_secret_value(secret_id: s.name)

  if resp.secret_string
    puts '    ' + resp.secret_string
  else
    # do something with resp.secret_binary
  end

  puts
end
