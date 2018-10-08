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

# passing instance id as a parameter

param (
    [string]$id = ""
)

Clear-History

# For no parameter passed
if($id -eq ""){
    Write-Host "Listing the public ip of all the running instances"
}

Function Get-Ip($ip){
    # aws cli must be installed

    $ipString = aws ec2 describe-instances --instance-ids $id --query 'Reservations[].Instances[].PublicIpAddress';
    $Array = @();

    # Removing unwanted characters and adding them to array
    # return array 
    forEach($ip In $ipString){
        if ($ip -eq "[" -or $ip -eq "]"){
            continue;
        }else{
            $ipString = $ip -replace '"' , "";
            $ipString = $ipString -replace ',' , "";
            $ip =  $ipString.Trim();
            $Array += $ip;
        }

    }
    return $Array;
}

Try
{
    Get-Ip($ip);

    
}
Catch
{
    Write-Host "Failed";
    Write-host $_.Exception.Message;
}
