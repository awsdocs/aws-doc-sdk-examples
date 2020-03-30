# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of
# the License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
# CONDITIONS OF ANY KIND, either express or implied. See the License for the
# specific language governing permissions and limitations under the License.
#

# snippet-sourcedescription:[jupyterhub-install-libraries.py demonstrates a Python program that runs a bash script using AWS-RunShellScript of AWS Systems Manager to install additional libraries on cluster core nodes.]
# snippet-service:[elasticmapreduce]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[Amazon EMR]
# snippet-keyword:[Code Sample]
# snippet-keyword:[list_instances]
# snippet-sourcetype:[snippet]
# snippet-sourcedate:[2019-01-31]
# snippet-sourceauthor:[AWS]
# snippet-start:[emr.python.jupyterhub.installlibraries]
# Install Python libraries on running cluster nodes
from boto3 import client
from sys import argv

try:
  clusterId=argv[1]
  script=argv[2]
except:
  print("Syntax: librariesSsm.py [ClusterId] [S3_Script_Path]")
  import sys
  sys.exit(1)

emrclient=client('emr')

# Get list of core nodes
instances=emrclient.list_instances(ClusterId=clusterId,InstanceGroupTypes=['CORE'])['Instances']
instance_list=[x['Ec2InstanceId'] for x in instances]

# Attach tag to core nodes
ec2client=client('ec2')
ec2client.create_tags(Resources=instance_list,Tags=[{"Key":"environment","Value":"coreNodeLibs"}])

ssmclient=client('ssm')

# Download shell script from S3
command = "aws s3 cp " + script + " /home/hadoop"
try:
  first_command=ssmclient.send_command(Targets=[{"Key":"tag:environment","Values":["coreNodeLibs"]}],
                  DocumentName='AWS-RunShellScript',
                  Parameters={"commands":[command]}, 
                  TimeoutSeconds=3600)['Command']['CommandId']
  
  # Wait for command to execute 
  import time
  time.sleep(15)

  first_command_status=ssmclient.list_commands(
      CommandId=first_command,
      Filters=[
          {
              'key': 'Status',
              'value': 'SUCCESS'
          },
      ]
  )['Commands'][0]['Status']

  second_command=""
  second_command_status=""
  
  # Only execute second command if first command is successful

  if (first_command_status=='Success'):
    # Run shell script to install libraries

    second_command=ssmclient.send_command(Targets=[{"Key":"tag:environment","Values":["coreNodeLibs"]}],
      DocumentName='AWS-RunShellScript',
      Parameters={"commands":["bash /home/hadoop/install_libraries.sh"]}, 
      TimeoutSeconds=3600)['Command']['CommandId']
    
    second_command_status=ssmclient.list_commands(
      CommandId=first_command,
      Filters=[
          {
              'key': 'Status',
              'value': 'SUCCESS'
          },
      ]
    )['Commands'][0]['Status']
    time.sleep(30)
    print("First command, " + first_command + ": " + first_command_status)
    print("Second command:" + second_command + ": " + second_command_status)

except Exception as e:
  print(e)
# snippet-end:[emr.python.jupyterhub.installlibraries]
