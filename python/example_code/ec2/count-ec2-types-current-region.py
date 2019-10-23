# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of the
# License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
# OF ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License. 
# snippet-start:[ec2.python.count-ec2-types-current-region.complete]

import boto3
ec2 = boto3.client('ec2')
response = ec2.describe_instances()

ec2InstanceTypes = {}
for i in response['Reservations'] :
    this_it = i['Instances'][0]['InstanceType']
    ec2InstanceTypes[this_it] = ec2InstanceTypes.get(this_it, 0) +1
    
for k,v in ec2InstanceTypes.items() :
    print("{} {}".format(k,v))

# snippet-end:[ec2.python.count-ec2-types-current-region.complete]
# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[count-ec2-types-current-region.py demonstrates how to count the number of Ec2 instance type for the current default region.]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon EC2]
# snippet-service:[ec2]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-10-23]
# snippet-sourceauthor:[copolycube]
