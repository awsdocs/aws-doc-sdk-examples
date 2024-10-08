import boto3

def delete_all_ec2_instances():
    # Initialize a boto3 session and EC2 resource
    ec2 = boto3.resource('ec2')
    
    # Get all running EC2 instances
    instances = ec2.instances.filter(Filters=[{'Name': 'instance-state-name', 'Values': ['running', 'stopped']}])

    # Create a list of instance IDs
    instance_ids = [instance.id for instance in instances]
    
    if instance_ids:
        print(f"Terminating instances: {instance_ids}")
        # Terminate all instances
        ec2.instances.filter(InstanceIds=instance_ids).terminate()
        print("Termination initiated.")
    else:
        print("No instances found.")

if __name__ == "__main__":
    delete_all_ec2_instances()
