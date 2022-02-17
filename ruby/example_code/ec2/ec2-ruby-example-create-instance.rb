# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# ec2-ruby-example-create-instance.rb demonstrates how to create and tag an
# Amazon Elastic Compute Cloud (Amazon EC2) instance using AWS SDK for Ruby.

# snippet-start:[ec2.Ruby.createInstances]

require 'aws-sdk-ec2'
require 'base64'

# Prerequisites:
#
# - An EC2 key pair.
# - If you want to run any commands on the instance after it starts, a
#   file containing those commands.
#
# @param ec2_resource [Aws::EC2::Resource] An initialized EC2 resource object.
# @param image_id [String] The ID of the target Amazon Machine Image (AMI).
# @param key_pair_name [String] The name of the existing EC2 key pair.
# @param tag_key [String] The key portion of the tag for the instance.
# @param tag_value [String] The value portion of the tag for the instance.
# @param instance_type [String] The ID of the type of instance to create.
#   If not specified, the default value is 't2.micro'.
# @param user_data_file [String] The path to the file containing any commands
#   to run on the instance after it starts. If not specified, the default
#   value is an empty string.
# @return [Boolean] true if the instance was created and tagged;
#   otherwise, false.
# @example
#   exit 1 unless instance_created?(
#     Aws::EC2::Resource.new(region: 'us-west-2'),
#     'ami-0947d2ba12EXAMPLE',
#     'my-key-pair',
#     'my-key',
#     'my-value',
#     't2.micro',
#     'my-user-data.txt'
#   )
def instance_created?(
  ec2_resource,
  image_id,
  key_pair_name,
  tag_key,
  tag_value,
  instance_type = 't2.micro',
  user_data_file = ''
)
  encoded_script = ''

  unless user_data_file == ''
    script = File.read(user_data_file)
    encoded_script = Base64.encode64(script)
  end

  instance = ec2_resource.create_instances(
    image_id: image_id,
    min_count: 1,
    max_count: 1,
    key_name: key_pair_name,
    instance_type: instance_type,
    user_data: encoded_script
  )

  puts 'Creating instance...'

  # Check whether the new instance is in the "running" state.
  polls = 0
  loop do
    polls += 1
    response = ec2_resource.client.describe_instances(
      instance_ids: [
        instance.first.id
      ]
    )
    # Stop polling after 10 minutes (40 polls * 15 seconds per poll) if not running.
    break if response.reservations[0].instances[0].state.name == 'running' || polls > 40

    sleep(15)
  end

  puts "Instance created with ID '#{instance.first.id}'."

  instance.batch_create_tags(
    tags: [
      {
        key: tag_key,
        value: tag_value
      }
    ]
  )
  puts 'Instance tagged.'

  return true
rescue StandardError => e
  puts "Error creating or tagging instance: #{e.message}"
  return false
end

# Full example call:
def run_me
  image_id = ''
  key_pair_name = ''
  tag_key = ''
  tag_value = ''
  instance_type = ''
  region = ''
  user_data_file = ''
  # Print usage information and then stop.
  if ARGV[0] == '--help' || ARGV[0] == '-h'
    puts 'Usage: ruby ec2-ruby-example-create-instance.rb ' \
      'IMAGE_ID KEY_PAIR_NAME TAG_KEY TAG_VALUE INSTANCE_TYPE ' \
      'REGION [USER_DATA_FILE]'
     # Replace us-west-2 with the AWS Region you're using for Amazon EC2.
    puts 'Example: ruby ec2-ruby-example-create-instance.rb ' \
      'ami-0947d2ba12EXAMPLE my-key-pair my-key my-value t2.micro ' \
      'us-west-2 my-user-data.txt'
    exit 1
  # If no values are specified at the command prompt, use these default values.
  elsif ARGV.count.zero?
    image_id = 'ami-0947d2ba12EXAMPLE'
    key_pair_name = 'my-key-pair'
    tag_key = 'my-key'
    tag_value = 'my-value'
    instance_type = 't2.micro'
    # Replace us-west-2 with the AWS Region you're using for Amazon EC2.
    region = 'us-west-2'
    user_data_file = 'my-user-data.txt'
  # Otherwise, use the values as specified at the command prompt.
  else
    image_id = ARGV[0]
    key_pair_name = ARGV[1]
    tag_key = ARGV[2]
    tag_value = ARGV[3]
    instance_type = ARGV[4]
    region = ARGV[5]
    user_data_file = ARGV[6] if ARGV.count == 7 # If user data file specified.
  end

  ec2_resource = Aws::EC2::Resource.new(region: region)

  if instance_created?(
    ec2_resource,
    image_id,
    key_pair_name,
    tag_key,
    tag_value,
    instance_type,
    user_data_file
  )
    puts 'Created and tagged instance.'
  else
    puts 'Could not create or tag instance.'
  end
end

run_me if $PROGRAM_NAME == __FILE__

# snippet-end:[ec2.Ruby.createInstances]
