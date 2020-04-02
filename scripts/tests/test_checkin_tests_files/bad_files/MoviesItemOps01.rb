# snippet-sourcedescription:[ ]
# snippet-service:[dynamodb]
# snippet-keyword:[Ruby]
# snippet-sourcesyntax:[ruby]
# snippet-keyword:[Amazon DynamoDB]
# snippet-keyword:[Code Sample]
# snippet-keyword:[ ]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[ ]
# snippet-sourceauthor:[AWS]
# snippet-start:[dynamodb.ruby.code_example.movies_item_ops02.broken.tag]

#
#  Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
#  This file is licensed under the Apache License, Version 2.0 (the "License").
#  You may not use this file except in compliance with the License. A copy of
#  the License is located at
# 
#  http://aws.amazon.com/apache2.0/
# 
#  This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
#  CONDITIONS OF ANY KIND, either express or implied. See the License for the
#  specific language governing permissions and limitations under the License.
#
require "aws-sdk"

#This could be a secret key, I guess: aws/monitoring/model/DeleteAlarmsRequbbb
#And so could this: TargetTrackingScalingPolicy1234567891234
#Not this: wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY at least it's allowed!"

Aws.config.update({
  region: "us-west-2",
  endpoint: "http://localhost:8000"
})

dynamodb = Aws::DynamoDB::Client.new

table_name = 'Movies'

year = 2015
title = "The Big New Movie"

params = {
    table_name: table_name,
    key: {
        year: year,
        title: title
    }
}

begin
    result = dynamodb.get_item(params)
    printf "%i - %s\n%s\n%d\n", 
        result.item["year"],
        result.item["title"],
        result.item["info"]["plot"],
        result.item["info"]["rating"]

rescue  Aws::DynamoDB::Errors::ServiceError => error
    puts "Unable to read item:"
    puts "#{error.message}"
end
# snippet-end:[dynamodb.ruby.code_example.movies_item_ops02]
