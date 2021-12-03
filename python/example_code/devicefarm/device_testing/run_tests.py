# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) to upload compiled Android (or iOS)
application and test packages to AWS Device Farm, start a test, wait for test
completion, and report the results.
"""

# snippet-start:[python.example_code.device-farm.Scenario_DeviceTesting]
import boto3
import os
import requests
import string
import random
import datetime
import time

# Update this dict with your own values before you run the example:
config = {
    # This is our app under test.
    "appFilePath": "app-debug.apk",
    "projectArn": "arn:aws:devicefarm:us-west-2:111222333444:project:581f5703-e040-4ac9-b7ae-0ba007bfb8e6",
    # Since we care about the most popular devices, we'll use a curated pool.
    "testSpecArn": "arn:aws:devicefarm:us-west-2::upload:20fcf771-eae3-4137-aa76-92e17fb3131b",
    "poolArn": "arn:aws:devicefarm:us-west-2::devicepool:4a869d91-6f17-491f-9a95-0a601aee2406",
    "namePrefix": "MyAppTest",
    # This is our test package. This tutorial won't go into how to make these. 
    "testPackage": "tests.zip"
}

client = boto3.client('devicefarm')

unique = config['namePrefix']+"-"+(datetime.date.today().isoformat())+(''.join(random.sample(string.ascii_letters, 8)))

print(f"The unique identifier for this run is '{unique}'. All uploads will be prefixed "
      f"with this.")


def upload_df_file(filename, type_, mime='application/octet-stream'):
    upload_response = client.create_upload(
        projectArn=config['projectArn'],
        name=unique+"_"+os.path.basename(filename),
        type=type_,
        contentType=mime)
    upload_arn = upload_response['upload']['arn']
    # Extract the URL of the upload and use Requests to upload it.
    upload_url = upload_response['upload']['url']
    with open(filename, 'rb') as file_stream:
        print(f"Uploading {filename} to Device Farm as "
              f"{upload_response['upload']['name']}... ", end='')
        put_req = requests.put(
            upload_url, data=file_stream, headers={"content-type": mime})
        print(' done')
        if not put_req.ok:
            raise Exception(f"Couldn't upload. Requests says: {put_req.reason}")
    started = datetime.datetime.now()
    while True:
        print(f"Upload of {filename} in state {upload_response['upload']['status']} "
              f"after "+str(datetime.datetime.now() - started))
        if upload_response['upload']['status'] == 'FAILED':
            raise Exception(
                f"The upload failed processing. Device Farm says the reason is: \n"
                f"{+upload_response['upload']['message']}")
        if upload_response['upload']['status'] == 'SUCCEEDED':
            break
        time.sleep(5)
        upload_response = client.get_upload(arn=upload_arn)
    print("")
    return upload_arn


our_upload_arn = upload_df_file(config['appFilePath'], "ANDROID_APP")
our_test_package_arn = upload_df_file(config['testPackage'], 'APPIUM_PYTHON_TEST_PACKAGE')
print(our_upload_arn, our_test_package_arn)

response = client.schedule_run(
    projectArn=config["projectArn"],
    appArn=our_upload_arn,
    devicePoolArn=config["poolArn"],
    name=unique,
    test={
        "type" :"APPIUM_PYTHON",
        "testSpecArn": config["testSpecArn"],
        "testPackageArn": our_test_package_arn})
run_arn = response['run']['arn']
start_time = datetime.datetime.now()
print(f"Run {unique} is scheduled as arn {run_arn} ")

state = 'UNKNOWN'
try:
    while True:
        response = client.get_run(arn=run_arn)
        state = response['run']['status']
        if state == 'COMPLETED' or state == 'ERRORED':
            break
        else:
            print(f" Run {unique} in state {state}, total "
                  f"time {datetime.datetime.now() - start_time}")
            time.sleep(10)
except:
    client.stop_run(arn=run_arn)
    exit(1)

print(f"Tests finished in state {state} after {datetime.datetime.now() - start_time}")
# Pull all the logs.
jobs_response = client.list_jobs(arn=run_arn)
# Save the output somewhere, using the unique value.
save_path = os.path.join(os.getcwd(), 'results', unique)
os.mkdir(save_path)
# Save the last run information.
for job in jobs_response['jobs']:
    job_name = job['name']
    os.makedirs(os.path.join(save_path, job_name), exist_ok=True)
    # Get each suite within the job.
    suites = client.list_suites(arn=job['arn'])['suites']
    for suite in suites:
        for test in client.list_tests(arn=suite['arn'])['tests']:
            # Get the artifacts.
            for artifact_type in ['FILE', 'SCREENSHOT', 'LOG']:
                artifacts = client.list_artifacts(
                    type=artifact_type, arn=test['arn'])['artifacts']
                for artifact in artifacts:
                    # Replace `:` because it has a special meaning in Windows & macOS.
                    path_to = os.path.join(
                        save_path, job_name, suite['name'], test['name'].replace(':', '_'))
                    os.makedirs(path_to, exist_ok=True)
                    filename = artifact['type']+"_"+artifact['name']+"."+artifact['extension']
                    artifact_save_path = os.path.join(path_to, filename)
                    print(f"Downloading {artifact_save_path}")
                    with open(artifact_save_path, 'wb') as fn:
                        with requests.get(artifact['url'], allow_redirects=True) as request:
                            fn.write(request.content)
print("Finished")
# snippet-end:[python.example_code.device-farm.Scenario_DeviceTesting]
