# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourceauthor:[Doug-AWS]
# snippet-sourcedescription:[Lists your Elastic Beanstalk solution stacks.]
# snippet-keyword:[AWS Elastic Beanstalk]
# snippet-keyword:[list_available_solution_stacks method]
# snippet-keyword:[Ruby]
# snippet-sourcesyntax:[ruby]
# snippet-service:[elasticbeanstalk]
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

require 'aws-sdk-elasticbeanstalk'  # v2: require 'aws-sdk'
require 'os'

if OS.windows?
  Aws.use_bundled_cert!
end

$debug = false
$verbose = false

def print_debug(s)
  if $debug
    puts s
  end
end

USAGE = <<DOC

Usage: ruby eb_list_stacks [FILTER] [-r REGION] [-h]

Lists some or all ElasticBeanstalk solution stacks

If FILTER is supplied, only shows stacks containing FILTER, case-insensitive,
so "java" matches "Java", "JAVA", etc.

If REGION is not supplied, defaults to 'us-east-1'

-h     Shows this message and quits

DOC

region = 'us-east-1'
filter = ''

i = 0

while i < ARGV.length
  case ARGV[i]
    when '-F'
      i += 1
      filter = ARGV[i]

    when '-r'
      i += 1
      region = ARGV[i]

    when '-h'
      puts USAGE
      exit 1

    else
      filter = ARGV[i].downcase

  end

  i += 1
end

eb = Aws::ElasticBeanstalk::Client.new(region: region)

stacks = eb.list_available_solution_stacks.solution_stacks
orig_length = stacks.length
filtered_length = 0

stacks.each do |s|

  if filter != ''
      d = s.downcase

      if d.include? filter
        puts s
        filtered_length += 1
      end
  else
    puts s
  end
end

puts

if filter != ''
  puts "Showed #{filtered_length} stack(s) of #{orig_length}"
else
  puts "Showed #{orig_length} stack(s)"
end
