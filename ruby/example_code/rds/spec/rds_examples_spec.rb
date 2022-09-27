require "rspec"
require_relative "../create_snapshot"
require_relative "../create_cluster_snapshot"
require_relative "../list_cluster_snapshots"
require_relative "../list_security_groups"
require_relative "../list_instance_snapshots"
require_relative "../list_instances"
require_relative "../list_subnet_groups"
require_relative "../list_parameter_groups"

describe "RDS example code" do
  let(:rds_resource) { Aws::RDS::Resource.new }
  let(:multi_az_cluster) { "database-multi-az-cluster" }
  let(:multi_az_instance) { "database-multi-az-instance" }
  let(:instance) { "database-single" }

  it "list all instances" do
    instances = list_instances(rds_resource)
    expect(instances).not_to be_empty
  end

  it "creates an instance snapshot" do
    snapshot = create_snapshot(rds_resource, instance)
    puts
    expect(snapshot).to be_an_instance_of(Aws::RDS::DBSnapshot)
  end

  it "creates a cluster snapshot" do
    db_cluster_snapshot = create_cluster_snapshot(rds_resource, multi_az_cluster)
    expect(db_cluster_snapshot).to be_an_instance_of(Aws::RDS::DBClusterSnapshot)
  end

  it "lists all cluster snapshots" do
    cluster_snapshots = list_multi_az_db_cluster_snapshots(rds_resource, multi_az_cluster)
    expect(cluster_snapshots).not_to be_empty
  end

  it "lists all instance snapshots" do
    snapshots = list_instance_snapshots(rds_resource)
    expect(snapshots).not_to be_empty
  end

  it "list parameter groups" do
    parameter_groups = list_parameter_groups(rds_resource)
    expect(parameter_groups).not_to be_empty
  end

  it "list security groups" do
    security_groups = list_security_groups(rds_resource)
    expect(security_groups).not_to be_empty
  end

  it "list subnet groups" do
    subnet_groups = list_subnet_groups(rds_resource)
    expect(subnet_groups).not_to be_nil
  end
end
