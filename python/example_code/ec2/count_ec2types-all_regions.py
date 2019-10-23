# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of the
# License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
# OF ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License. 
# snippet-start:[ec2.python.aws-count_ec2types-all_regions.complete]


import boto3

client = boto3.client('ec2')
regions = [region['RegionName'] for region in client.describe_regions()['Regions']]

for r in regions :
    ec2 = boto3.client('ec2', region_name = r)
    response = ec2.describe_instances()

    ec2InstanceTypes = {}
    for i in response['Reservations'] :
        this_it = i['Instances'][0]['InstanceType']
        ec2InstanceTypes[this_it] = ec2InstanceTypes.get(this_it, 0) +1
        
    for k,v in ec2InstanceTypes.items() :
        print("%-20s%-20s%-20s" %(r, k, v))
        
# snippet-end:[ec2.python.aws-count_ec2types-all_regions.complete]
# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[count_ec2types-all_regions.py demonstrates how to count the number of Ec2 instance type for the each existing regions.]
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
