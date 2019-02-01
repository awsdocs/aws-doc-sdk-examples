# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourceauthor:[Doug-AWS]
# snippet-sourcedescription:[Stops, starts, and reboots an EC2 instance; monitors an instance, and displays information about the instances.]
# snippet-keyword:[Amazon Elastic Compute Cloud]
# snippet-keyword:[describe_instances method]
# snippet-keyword:[monitor_instances method]
# snippet-keyword:[reboot_instances method]
# snippet-keyword:[start_instances method]
# snippet-keyword:[stop_instances method]
# snippet-keyword:[wait_until method]
# snippet-keyword:[Ruby]
# snippet-service:[ec2]
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
# 1. Stop an existing Amazon EC2 instance.
# 2. Restart the instance.
# 3. Reboot the instance.
# 4. Enable detailed monitoring for the instance.
# 5. Get information about available instances.

require 'aws-sdk-ec2'  # v2: require 'aws-sdk'

# Uncomment for Windows.
# Aws.use_bundled_cert!

def wait_for_instances(ec2, state, ids)
  begin
    ec2.wait_until(state, instance_ids: ids)
    puts "Success: #{state}."
  rescue Aws::Waiters::Errors::WaiterFailed => error
    puts "Failed: #{error.message}"
  end
end

ec2 = Aws::EC2::Client.new(region: 'us-east-1')

instance_id = "INSTANCE-ID" # For example, "i-0a123456b7c8defg9"

puts "Attempting to stop instance '#{instance_id}'. This may take a few minutes..."
ec2.stop_instances({ instance_ids: [instance_id] })
wait_for_instances(ec2, :instance_stopped, [instance_id])

puts "\nAttempting to restart instance '#{instance_id}'. This may take a few minutes..."
ec2.start_instances({ instance_ids: [instance_id] })
wait_for_instances(ec2, :instance_running, [instance_id])

puts "\nAttempting to reboot instance '#{instance_id}'. This may take a few minutes..."
ec2.reboot_instances({ instance_ids: [instance_id] })
wait_for_instances(ec2, :instance_status_ok, [instance_id])

# Enable detailed monitoring for the instance.
puts "\nAttempting to enable detailed monitoring for instance '#{instance_id}'..."

begin
  monitor_instances_result = ec2.monitor_instances({
    instance_ids: [instance_id]
  })
  puts "Detailed monitoring state for instance '#{instance_id}': #{monitor_instances_result.instance_monitorings[0].monitoring.state}"
rescue Aws::EC2::Errors::InvalidState
  puts "Instance '#{instance_id}' is not in a monitorable state. Continuing on..."
end

# Get information about available instances.
puts "\nAvailable instances:"

describe_instances_result = ec2.describe_instances

describe_instances_result.reservations.each do |reservation|
  if reservation.instances.count > 0
    reservation.instances.each do |instance|
      puts "=" * (instance.instance_id.length + 13)
      puts "Instance ID: #{instance.instance_id}"
      puts "State: #{instance.state.name}"
      puts "Image ID: #{instance.image_id}"
      puts "Instance Type: #{instance.instance_type}"
      puts "Architecure: #{instance.architecture}"
      puts "IAM Instance Profile: #{instance.iam_instance_profile}"
      puts "Key Name: #{instance.key_name}"
      puts "Launch Time: #{instance.launch_time}"
      puts "Detailed Monitoring State: #{instance.monitoring.state}"
      puts "Public IP Address: #{instance.public_ip_address}"
      puts "Public DNS Name: #{instance.public_dns_name}"
      puts "VPC ID: #{instance.vpc_id}"
      puts "Subnet ID: #{instance.subnet_id}"
      if instance.tags.count > 0
        puts "Tags:"
        instance.tags.each do |tag|
          puts "  #{tag.key} = #{tag.value}"
        end
      end
    end
  end

end
