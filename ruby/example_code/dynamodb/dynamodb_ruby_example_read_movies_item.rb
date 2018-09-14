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

require 'aws-sdk-dynamodb'  # v2: require 'aws-sdk'

# Create dynamodb client in us-west-2 region
dynamodb = Aws::DynamoDB::Client.new(region: 'us-west-2')

params = {
    table_name: 'Movies',
    key: {
        year: 2015,
        title: 'The Big New Movie'
    }
}

begin
  result = dynamodb.get_item(params)

  if result.item == nil
    puts 'Could not find movie'
    exit 0
  end

  puts 'Found movie:'
  puts '  Year:   ' + result.item['year'].to_i.to_s
  puts '  Title:  ' + result.item['title']
  puts '  Plot:   ' + result.item['info']['plot']
  puts '  Rating: ' + result.item['info']['rating'].to_f.to_s
rescue  Aws::DynamoDB::Errors::ServiceError => error
  puts 'Unable to find movie:'
  puts error.message
end
