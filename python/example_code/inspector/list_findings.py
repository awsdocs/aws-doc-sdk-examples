# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[list_findings.py lists Amazon Inspector findings.]
# snippet-service:[inspector]
# snippet-keyword:[Amazon Inspector]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[snippet]
# snippet-sourcedate:[2019-05-17]
# snippet-sourceauthor:[walkerk1980]

# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

# snippet-start:[inspector.python.list_findings.complete]

from datetime import datetime
import boto3

region_name = 'us-west-2'
assessment_run_arn_1 = 'arn:aws:inspector:us-west-2:123456789012:target/0-prsTvjAI/template/0-kXLPD9el/run/0-FhvJqB4l'
max_results = 250000
start_date = datetime(2019, 1, 1)
end_date = datetime(2019, 12, 1)

inspector = boto3.client('inspector', region_name = region_name)
paginator = inspector.get_paginator('list_findings')

finding_filter = {
    'severities': [
        'High',
        'Medium',
        'Low',
        'Informational',
    ],
    'creationTimeRange': {
        'beginDate': start_date,
        'endDate': end_date,
    }
}

for findings in paginator.paginate(
        maxResults=max_results,
        assessmentRunArns=[
            assessment_run_arn_1,
        ],
        filter = finding_filter
    ):
    for finding_arn in findings['findingArns']:
        print(finding_arn)

# snippet-end:[inspector.python.list_findings.complete]
