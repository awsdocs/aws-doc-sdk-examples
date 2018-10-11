# program to create instance from ami with all configaration
import boto3
try:
    ec2 = boto3.resource('ec2', region_name='REGION')
    subnet = ec2.Subnet('SUBNET')
    instances = subnet.create_instances(ImageId='IMAGE_ID', InstanceType='INSTANCE_TYPE',
                                        MaxCount='NO_OF_INSTANCE',
                                        MinCount='NO_OF_INSTANCE',
                                        KeyName='KEY_PAIR_NAME', SecurityGroups=[], SecurityGroupIds=['SECURITY_GROUP'])
    print(instances)

except BaseException as exe:
    print(exe)
