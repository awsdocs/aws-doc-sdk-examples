# Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License"). You
# may not use this file except in compliance with the License. A copy of
# the License is located at
#
# http://aws.amazon.com/apache2.0/
#
# or in the "license" file accompanying this file. This file is
# distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
# ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License.
import boto3

regions_to_enable='us-east-1 us-west-2'

def create_detector(client):
    client.create_detector();

def enable_detector(client, detector):
    client.update_detector(
        DetectorId=detector,
        Enable=True
    )
try:
    for region in regions_to_enable.split():
    # Create GuardDuty client
    gd = boto3.client(
        service_name='guardduty',
        region_name=region
    )
    
    #Get the GuardDuty Detector for the current AWS Region
    detector=gd.list_detectors()
    if len(detector['DetectorIds']) > 0:
        detector_id = detector['DetectorIds'][0]
        print('Detector exists in Region ' + region + ' Detector Id: ' + detector_id)
    else:
        print('GuardDuty Detector does not exist in Region ' + region)
        print('Creating Detector in ' + region + ' ...')
        create_detector(client=gd)
    
    detector=gd.list_detectors()
    if len(detector['DetectorIds']) > 0:
        detector_id = detector['DetectorIds'][0]
        detector_details = gd.get_detector(DetectorId=detector_id)
        detector_status = detector_details['Status']
        print('Detector ID ' + detector_id + ' in Region ' + region + ' is ' + detector_status)
        if detector_status == 'DISABLED':
            print('Enabling Detector ' + detector_id + ' in ' + region + ' ...')
            enable_detector(client=gd,detector=detector_id)
    else:
        print('GuardDuty Detector does not exist in Region ' + region)
except Exception as e:
    print(e)

#snippet-end:[guardduty.python.enable_guardduty.complete]
#snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
#snippet-sourcedescription:[enable_guardduty.py creates and or enables Amazon GuardDuty in the specified Regions.]
#snippet-keyword:[Python]
#snippet-keyword:[AWS SDK for Python (Boto3)]
#snippet-keyword:[Code Sample]
#snippet-keyword:[Amazon GuardDuty]
#snippet-service:[guardduty]
#snippet-sourcetype:[full-example]
#snippet-sourcedate:[2019-01-02]
#snippet-sourceauthor:[walkerk1980]