# Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# This file is licensed under the Apache License, Version 2.0 (the 'License').
# You may not use this file except in compliance with the License. A copy of the
# License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an 'AS IS' BASIS, WITHOUT WARRANTIES OR CONDITIONS
# OF ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License.


# passing instance id as a parameter and terminating all the instances
param(
  [alias("id")][string[]] $id_list=@()
 )
 
# terminate instance here
ForEach ($id In $id_list){
   Write-Host $id
   aws ec2 terminate-instances --instance-ids $id
}
