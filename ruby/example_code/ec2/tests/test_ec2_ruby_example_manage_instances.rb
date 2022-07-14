# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative "../ec2-ruby-example-manage-instances"

describe "#wait_for_instance" do
  let(:instance_id) { "i-033c48ef067af3dEX" }
  let(:instance_state) { :instance_stopped }
  let(:ec2_client) do
    Aws::EC2::Client.new(
      stub_responses: {
        describe_instances: {
          reservations: [
            instances: [
              instance_id: instance_id,
              state: {
                name: "stopped"
              }
            ]
          ]
        }
      }
    )
  end

  it "waits for the instance to be in a stopped state" do
    expect { wait_for_instance(ec2_client, instance_state, instance_id) }.not_to raise_error
  end
end

describe "#instance_stopped?" do
  let(:instance_id) { "i-033c48ef067af3dEX" }
  let(:ec2_client) do
    Aws::EC2::Client.new(
      stub_responses: {
        stop_instances: {
          stopping_instances: [
            {
              current_state: {
                code: 64,
                name: "stopping"
              },
              instance_id: instance_id,
              previous_state: {
                code: 16,
                name: "running"
              }
            }
          ]
        },
        describe_instances: {
          reservations: [
            instances: [
              instance_id: instance_id,
              state: {
                name: "stopped"
              }
            ]
          ]
        }
      }
    )
  end

  it "stops an instance" do
    expect(instance_stopped?(ec2_client, instance_id)).to be(true)
  end
end

describe "#instance_restarted?" do
  let(:instance_id) { "i-033c48ef067af3dEX" }
  let(:ec2_client) do
    Aws::EC2::Client.new(
      stub_responses: {
        start_instances: {
          starting_instances: [
            {
              current_state: {
                code: 0,
                name: "pending"
              },
              instance_id: instance_id,
              previous_state: {
                code: 80,
                name: "stopped"
              }
            }
          ]
        },
        describe_instances: {
          reservations: [
            instances: [
              instance_id: instance_id,
              state: {
                name: "running"
              }
            ]
          ]
        }
      }
    )
  end

  it "restarts an instance" do
    expect(instance_restarted?(ec2_client, instance_id)).to be(true)
  end
end

describe "#instance_rebooted?" do
  let(:instance_id) { "i-033c48ef067af3dEX" }
  let(:ec2_client) do
    Aws::EC2::Client.new(
      stub_responses: {
        reboot_instances: {},
        describe_instance_status: {
          instance_statuses: [
            {
              instance_status: {
                status: "ok"
              }
            }
          ]
        }
      }
    )
  end

  it "reboots an instance" do
    expect(instance_rebooted?(ec2_client, instance_id)).to be(true)
  end
end

describe "#instance_detailed_monitoring_enabled?" do
  let(:instance_id) { "i-033c48ef067af3dEX" }
  let(:ec2_client) do
    Aws::EC2::Client.new(
      stub_responses: {
        monitor_instances: {
          instance_monitorings: [
            monitoring: {
              state: "enabled"
            }
          ]
        }
      }
    )
  end

  it "checks whether detailed instance monitoring is enabled" do
    expect(instance_detailed_monitoring_enabled?(ec2_client, instance_id)).to be(true)
  end
end

describe "#list_instances_information" do
  let(:ec2_client) do
    Aws::EC2::Client.new(
      stub_responses: {
        describe_instances: {
          reservations: [
            instances: [
              {
                instance_id: "i-033c48ef067af3dEX",
                state: {
                  name: "running"
                },
                image_id: "ami-0947d2ba12ee1ffEX",
                instance_type: "t2.large",
                architecture: "x86_64",
                iam_instance_profile: {
                  arn: "arn:aws:iam::111111111111:instance-profile/my-instance-profile"
                },
                key_name: "my-key-pair",
                launch_time: Time.new(2020, 3, 10, 14, 51, 17, "-08:00"),
                monitoring: {
                  state: "enabled"
                },
                public_ip_address: "192.0.2.0",
                public_dns_name: "ec2-12-345-67-8EX.compute-1.amazonaws.com",
                vpc_id: "vpc-6713dfEX",
                subnet_id: "subnet-ecf662EX",
                tags: [
                  {
                    key: "my-key-1",
                    value: "my-value-1"
                  },
                  {
                    key: "my-key-2",
                    value: "my-value-2"
                  }
                ]
              },
              {
                instance_id: "i-033c48ef067af4dEX",
                state: {
                  name: "running"
                },
                image_id: "ami-0947d2ba12ee1ffEX",
                instance_type: "m4.large",
                architecture: "x86_64",
                iam_instance_profile: {
                  arn: "arn:aws:iam::111111111111:instance-profile/my-instance-profile"
                },
                key_name: "my-key-pair",
                launch_time: Time.new(2020, 5, 18, 9, 10, 59, "-08:00"),
                monitoring: {
                  state: "enabled"
                },
                public_ip_address: "192.0.2.0",
                public_dns_name: "ec2-34-567-89-0EX.compute-1.amazonaws.com",
                vpc_id: "vpc-6713dfEX",
                subnet_id: "subnet-ecf662EX",
                tags: [
                  {
                    key: "my-other-key-1",
                    value: "my-other-value-1"
                  },
                  {
                    key: "my-other-key-2",
                    value: "my-other-value-2"
                  }
                ]
              }
            ]
          ]
        }
      }
    )
  end

  it "displays instance information" do
    expect { list_instances_information(ec2_client) }.not_to raise_error
  end
end
