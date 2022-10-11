# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# eb_list_stacks.rb demonstrates how to list your Amazon Elastic Beanstalk
# solution stacks using the AWS SDK for Ruby.

# snippet-start:[eb.Ruby.listStacks]
require "aws-sdk-elasticbeanstalk"  # v2: require 'aws-sdk'
require "os"

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
# Replace us-west-2 with the AWS Region you're using for Elastic Beanstalk.
region = "us-west-2"
filter = ""

i = 0

while i < ARGV.length
  case ARGV[i]
    when "-F"
      i += 1
      filter = ARGV[i]

    when "-r"
      i += 1
      region = ARGV[i]

    when "-h"
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

  if filter != ""
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

if filter != ""
  puts "Showed #{filtered_length} stack(s) of #{orig_length}"
else
  puts "Showed #{orig_length} stack(s)"
end
# snippet-end:[eb.Ruby.listStacks]
