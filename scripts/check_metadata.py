# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of the
# License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
# OF ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License.
# 
# This script is used to validate metadata in the awsdocs/aws-doc-sdk-examples/
# repository on Github.
# 
"""
This script was the original checkin test suite but it has been refactored and
superseded by checkin_tests.py, os this script is no longer used.

It's being kept because it contains code to verify the full set of legacy snippet tags.
In the event we revive the full snippet tag system, we may want to revive some of
the verification code in this script.
"""

import os
import fnmatch
import sys
import re
import argparse

# files to never check
do_not_scan = {'AssemblyInfo.cs', 'metadata.yaml', 'CMakeLists.txt',
               'check_metadata.py', 'movie_data.json'}

# folders to skip
skip_folders = {'venv'}

# list of words that should never be in code examples
deny_list = {'alpha-docs-aws.amazon.com', 'integ-docs-aws.amazon.com'}

# whitelist of 20- or 40-character strings to allow
allow_list = {
    'AKIAIOSFODNN7EXAMPLE',
    'AppStreamUsageReportsCFNGlueAthenaAccess',
    'aws/cloudtrail/model/LookupEventsRequest',
    'aws/codebuild/model/BatchGetBuildsResult',
    'aws/codecommit/model/DeleteBranchRequest',
    'aws/codecommit/model/ListBranchesRequest',
    'aws/dynamodb/model/ProvisionedThroughput',
    'aws/ec2/model/CreateSecurityGroupRequest',
    'aws/ec2/model/DeleteSecurityGroupRequest',
    'aws/ec2/model/UnmonitorInstancesResponse',
    'aws/email/model/CreateReceiptRuleRequest',
    'aws/email/model/DeleteReceiptRuleRequest',
    'aws/email/model/ListReceiptFiltersResult',
    'aws/email/model/SendTemplatedEmailResult',
    'aws/guardduty/model/ListDetectorsRequest',
    'aws/iam/model/GetAccessKeyLastUsedResult',
    'aws/iam/model/GetServerCertificateResult',
    'aws/kinesis/model/GetShardIteratorResult',
    'aws/kinesis/model/PutRecordsRequestEntry',
    'aws/monitoring/model/DeleteAlarmsRequest',
    'aws/neptune/model/CreateDBClusterRequest',
    'aws/neptune/model/DeleteDBClusterRequest',
    'aws/neptune/model/ModifyDBClusterRequest',
    'CertificateTransparencyLoggingPreference',
    'ChangeMessageVisibilityBatchRequestEntry',
    'com/greengrass/latest/developerguide/lra',
    'com/greengrass/latest/developerguide/sns',
    'com/samples/JobStatusNotificationsSample',
    'generate_presigned_url_and_upload_object',
    'KinesisStreamSourceConfiguration=kinesis',
    'nFindProductsWithNegativePriceWithConfig',
    's3_client_side_encryption_sym_master_key',
    'serial/CORE_THING_NAME/write/dev/serial1',
    'TargetTrackingScalingPolicyConfiguration',
    'targetTrackingScalingPolicyConfiguration',
    'upload_files_using_managed_file_uploader',
    'videoMetaData=celebrityRecognitionResult',
    'wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY',
}


def check_files(root, file_filter, quiet, full, warn=True):
    filecount = 0
    for path, dirs, files in os.walk(root):
        dirs[:] = [d for d in dirs if d not in skip_folders]
        for filename in fnmatch.filter(files, file_filter):
            if filename in do_not_scan:
                if not quiet:
                    print("\nFile: " + filename + ' is skipped')
                continue
            filecount += 1
            errors = []
            file_path = os.path.join(path, filename)
            if not quiet:
                print("\nChecking File: " + file_path)
            with open(file_path) as f:
                s = f.read()
                
                #Check for Deny List words in file
                verify_no_deny_list_words(s, file_path)
                
                #Check for SecretKeys
                character_scan(s, file_path)
                file_name_check(filename, file_path)
                
                # Split file into list of strings separated by space
                words = s.split()

            # Check for mismatched snippet start and end tags.
            snippets = s.split('snippet-')
            errors.append(snippet_start_check(words, file_path))

            if full:
                # Check for optional metadata
                errors.append(snippet_author_check(snippets, warn))
                errors.append(snippet_service_check(snippets, warn))
                errors.append(snippet_description_check(snippets, warn))
                errors.append(snippet_type_check(snippets, warn))
                errors.append(snippet_date_check(snippets, warn))
                errors.append(snippet_keyword_check(snippets, warn))

            if not quiet:
                print(str(len(words)) + " words found.")
            if warn:
                # Filter to only warning messages
                errors = list(filter(None, errors))
                # print out file name, if warnings found
                if len(errors) > 0 and quiet:
                    print("\nChecking File: " + file_path)
                for error in errors:
                    if error:
                        print(error)
    print(str(filecount) + " files scanned in " + root)
    print("")


def verify_no_deny_list_words(file_contents, file_location):
    file_contents = file_contents.split('/')
    for word in file_contents:
        if word in deny_list:
            print("ERROR -- Found in " + file_location)
            sys.exit("ERROR -- " + word + " found, and is not allowed.")


def character_scan(file_contents, file_location):
    twenty = re.findall("[^A-Z0-9][A][ACGIKNPRS][A-Z]{2}[A-Z0-9]{16}[^A-Z0-9]",
                        file_contents)
    if (twenty) :
        for word in twenty:
            if word[1:-1] in allow_list:
                continue
            print("ERROR -- " + word[1:-1] + " Found in " + file_location)
            sys.exit("ERROR -- 20 character string found, and might be a secret "
                     "access key.")
    forty = re.findall("[^a-zA-Z0-9/+=][a-zA-Z0-9/+=]{40}[^a-zA-Z0-9/+=]",
                       file_contents)
    if (forty) :
        for word in forty:
            if word[1:-1] in allow_list:
                continue
            print("ERROR -- " + word[1:-1] + " Found in " + file_location)
            sys.exit("ERROR -- 40 character string found, and might be a secret key.") 


def file_name_check(filename, file_location):
    forty = re.findall("[^a-zA-Z0-9/+=][a-zA-Z0-9/+=]{40}[^a-zA-Z0-9/+=]", filename)
    if forty:
        if forty[0][-1] not in allow_list:
           print("ERROR -- " + forty[0][:-1] + " Found in " + file_location)
           sys.exit("ERROR -- Filename is 40 characters long, and should be renamed "
                    "or added to allow list.")


def snippet_start_check(words, file_location):
    snippet_start = 'snippet-start:['
    snippet_end = 'snippet-end:['
    snippet_tags = set()
    for s in words:
        if snippet_start in s:
            s = s.split('[')[1]
            snippet_tags.add(s)
        elif snippet_end in s:
            s = s.split('[')[1]
            if s in snippet_tags:
                snippet_tags.remove(s)
            else:
                print("ERROR -- Found in " + file_location)
                sys.exit("ERROR -- " + s + "'s matching start tag not found.") 
        
    if len(snippet_tags) > 0 :
        print("ERROR -- Found in " + file_location)
        print(snippet_tags)
        sys.exit("ERROR -- " + snippet_tags.pop() + "'s matching end tag not found.")


def snippet_author_check(words, warn):
    author = 'sourceauthor:['
    matching = [s for s in words if author in s]
    if not matching:
        if warn:
            return "WARNING -- Missing snippet-sourceauthor:[Your Name]"


def snippet_service_check(words, warn):
    service = 'service:['
    matching = [s for s in words if service in s]
    if not matching:
        if warn:
            return "WARNING -- Missing snippet-service:[AWS service name] \n" \
                   "Find a list of AWS service names under AWS Service Namespaces in " \
                   "the General Reference Guide: " \
                   "https://docs.aws.amazon.com/general/latest/gr/aws-arns-and-namespaces.html"


def snippet_description_check(words, warn):
    desc = 'sourcedescription:['
    matching = [s for s in words if desc in s]
    if not matching:
        if warn:
            return "WARNING -- Missing snippet-sourcedescription:[Filename " \
                   "demonstrates how to ... ]"


def snippet_type_check(words, warn):
    author = 'sourcetype:['
    matching = [s for s in words if author in s]
    contains_type = False
    if not matching:
        contains_type = False
    for match in matching:
        if match.startswith('sourcetype:[full-example'):
            contains_type = True
            break
        elif match.startswith('sourcetype:[snippet'):
            contains_type = True
            break
    if not contains_type:
        if warn:
            return "WARNING -- Missing snippet-sourcetype:[full-example] or " \
                   "snippet-sourcetype:[snippet]"
        

def snippet_date_check(words, warn):
    date_tag = 'sourcedate:['
    matching = [s for s in words if date_tag in s]
    if not matching:
        if warn:
            return "WARNING -- Missing snippet-sourcedate:[YYYY-MM-DD]"


def snippet_keyword_check(words, warn):
    snippet_keyword = 'keyword:['
    matching = [s for s in words if snippet_keyword in s]
    # print(matching)
    code_sample = [s for s in words if 'keyword:[Code Sample]\n' in s]
    if not code_sample:
        if warn:
            return "WARNING -- Missing snippet-keyword:[Code Sample]"
    keyword_service_name(matching, warn)
    keyword_language_check(matching, warn)
    keyword_sdk_check(matching, warn)


def keyword_service_name(words, warn):
    contains_service_tag = False
    AWS = 'keyword:[AWS'
    matching = [s for s in words if AWS in s]
    if matching:
        contains_service_tag = True
    Amazon = 'keyword:[Amazon'
    matching = [s for s in words if Amazon in s]
    if matching:
        contains_service_tag = True
    if not contains_service_tag:
        if warn:
            return "WARNING -- Missing snippet-keyword:[FULL SERVICE NAME]"


def keyword_language_check(words, warn):
    languages = ['C++', 'C', '.NET', 'Go', 'Java', 'JavaScript', 'PHP', 'Python',
                 'Ruby', 'TypeScript' ]
    contains_language_tag = False
    for language in languages:
        language_keyword = [s for s in words if 'keyword:[' + language + ']' in s]
        if language_keyword:
            contains_language_tag = True
            break
    if not contains_language_tag:
        if warn:
            return "WARNING -- Missing snippet-keyword:[Language] \n" \
                   "Options include:" + ', '.join(languages)
            

def keyword_sdk_check(words, warn):
    sdk_versions = ['AWS SDK for PHP v3', 'AWS SDK for Python (Boto3)', 'CDK V0.14.1' ]
    contains_sdk_tag = False
    for sdk in sdk_versions:
        sdkkeyword = [s for s in words if 'keyword:[' + sdk + ']']
        if sdkkeyword:
            contains_sdk_tag = True
            break
    if not contains_sdk_tag:
        if warn:
            return "WARNING -- Missing snippet-keyword:[SDK Version used] \n" \
                   "Options include:" + ', '.join(sdk_versions)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--full', action='store_true',
                        help="Performs a full check of all snippet metadata, including"
                             "author, description, etc. Typically, only start and end "
                             "tags are checked. The default is False.")
    parser.add_argument('--quiet', action='store_true',
                        help="Suppresses output of filenames while parsing. "
                             "The default is False.")
    parser.add_argument('--root', help="The root path from which to search for files "
                                       "to check. The default is the current working "
                                       "folder.")
    args = parser.parse_args()

    root_path = os.path.abspath('.') if not args.root else os.path.abspath(args.root)

    check_list = [
        {'filters': ['*.c'], 'description': "C Code Examples"},
        {'filters': ['*.json', '*.yml', '*.yaml'],
         'description': "CloudFormation and IAM Policy Code Examples"},
        {'filters': ['*.cpp'], 'description': "C++ Code Examples"},
        {'filters': ['*.cs'], 'description': "C# Code Examples"},
        {'filters': ['*.go'], 'description': "Go Code Examples"},
        {'filters': ['*.java'], 'description': "Java Code Examples"},
        {'filters': ['*.js', '*.html'], 'description': "JavaScript Code Examples"},
        {'filters': ['*.php'], 'description': "PHP Code Examples"},
        {'filters': ['*.py'], 'description': "Python Code Examples"},
        {'filters': ['*.rb'], 'description': "Ruby Code Examples"},
        {'filters': ['*.ts'], 'description': "TypeScript Code Examples"},
    ]

    print('----------\n\nRun Tests\n')

    for item in check_list:
        print('-'*60 + '\n\n')
        print(f"{item['description']} {item['filters']}\n")
        for file_filter in item['filters']:
            check_files(root_path, file_filter, args.quiet, args.full)


if __name__ == '__main__':
    main()
