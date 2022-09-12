# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
"""
This script contains the checkin tests that are run whenever a pull request is
submitted or changed (using Travis CI, configured in .travis.yml).

The script scans code files and does the following:

    * Disallows a list of specific words.
    * Disallows any 20- or 40- character strings that fit a specific regex profile
      that indicates they might be secret access keys. Allows strings that fit the
      regex profile if they are in the allow list.
    * Disallows filenames that contain 20- or 40- character strings that fit the same
      regex profile, unless the filename is in the allow list.
    * Verifies that snippet-start and snippet-end tags are in matched pairs. You are
      not required to include these tags, but if you do they must be in pairs.
"""

import os
import re
import argparse
import logging
import sys

logger = logging.getLogger(__name__)

# Only files with these extensions are scanned.
EXT_LOOKUP = {
    'c': 'C',
    'cpp': 'C++',
    'cs': 'C#',
    'go': 'Go',
    'html': 'JavaScript',
    'java': 'Java',
    'js': 'JavaScript',
    'kt': 'Kotlin',
    'php': 'PHP',
    'py': 'Python',
    'rb': 'Ruby',
    'rs': 'Rust',
    'swift': 'Swift',
    'ts': 'TypeScript',
    'sh': 'AWS-CLI',
    'cmd': 'AWS-CLI',
    'json': 'JSON',
    'yml': 'YAML',
    'yaml': 'YAML',
    'md': 'Markdown'
}

# folders to skip
IGNORE_FOLDERS = {
    'venv',
    '__pycache__',
    '.pytest_cache',
    '.doc_gen'
}

# files to skip
IGNORE_FILES = {'AssemblyInfo.cs', 'metadata.yaml', '.travis.yml'}

# list of words that should never be in code examples
DENY_LIST = {'alpha-docs-aws.amazon.com', 'integ-docs-aws.amazon.com'}

# whitelist of 20- or 40-character strings to allow
ALLOW_LIST = {
    'AGPAIFFQAVRFFEXAMPLE',
    'AKIA111111111EXAMPLE',
    'AKIA6OHTTRXXTEXAMPLE',
    'AKIAEXAMPLEACCESSKEY',
    'AKIAIOSFODNN7EXAMPLE',
    'APKAEIBAERJR2EXAMPLE',
    'AppStreamUsageReportsCFNGlueAthenaAccess',
    'aws/acm/model/DescribeCertificateRequest',
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
    'aws/kms/model/ScheduleKeyDeletionRequest',
    'KMSWithContextEncryptionMaterialsExample',
    'CertificateTransparencyLoggingPreference',
    'ChangeMessageVisibilityBatchRequestEntry',
    'com/greengrass/latest/developerguide/lra',
    'com/greengrass/latest/developerguide/sns',
    'com/samples/JobStatusNotificationsSample',
    'generate_presigned_url_and_upload_object',
    'KinesisStreamSourceConfiguration=kinesis',
    'ListOrganizationalUnitsForParentResponse',
    'nFindProductsWithNegativePriceWithConfig',
    's3_client_side_encryption_sym_master_key',
    'serial/CORE_THING_NAME/write/dev/serial1',
    'TargetTrackingScalingPolicyConfiguration',
    'targetTrackingScalingPolicyConfiguration',
    'upload_files_using_managed_file_uploader',
    'videoMetaData=celebrityRecognitionResult',
    'wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY',
    'com/v1/documentation/api/latest/guide/s3',
    'iam/commands/GetServerCertificateCommand',
    'iam/commands/GetAccessKeyLastUsedCommand',
    'iam/commands/GetAccessKeyLastUsedCommand',
    'iam/commands/GetServerCertificateCommand',
    'cloudwatch/commands/PutMetricDataCommand',
    'ses/commands/VerifyDomainIdentityCommand',
    'ses/commands/DeleteReceiptRuleSetCommand',
    'ses/commands/DeleteReceiptRuleSetCommand',
    'ses/commands/CreateReceiptRuleSetCommand',
    'ses/commands/VerifyDomainIdentityCommand',
    'ses/commands/VerifyDomainIdentityCommand',
    'com/amazondynamodb/latest/developerguide',
    'DynamodbRubyExampleCreateUsersTableStack',
    'com/rekognition/latest/dg/considerations',
    'ListTagsForVaultExample/ListTagsForVault',
    'TerminateInstanceInAutoScalingGroupAsync',
    'GetIdentityVerificationAttributesRequest',
    'com/transcribe/latest/APIReference/index'
}

def check_files(root, quiet):
    """
    Walk a folder system, scanning all files with specified extensions.
    Errors are logged and counted and the count of errors is returned.

    :param root: The root folder to start the walk.
    :param quiet: When True, suppress most output.
    :return: The number of errors found in the scanned files.
    """
    file_count = 0
    error_count = 0
    for path, dirs, files in os.walk(root, topdown=True):
        dirs[:] = [d for d in dirs if d not in IGNORE_FOLDERS]
        for filename in files:
            ext = os.path.splitext(filename)[1].lstrip('.')
            if ext.lower() in EXT_LOOKUP:
                file_path = os.path.join(path, filename)
                if filename in IGNORE_FILES:
                    if not quiet:
                        print("\nFile: " + file_path + ' is skipped')
                    continue
                file_count += 1
                if not quiet:
                    print("\nChecking File: " + file_path)
                with open(file_path) as f:
                    file_contents = f.read()

                error_count += verify_no_deny_list_words(file_contents, file_path)
                error_count += verify_no_secret_keys(file_contents, file_path)
                error_count += verify_no_secret_keys(filename, file_path)
                error_count += verify_snippet_start_end(file_contents, file_path)

    print(f"{file_count} files scanned in {root}.\n")
    return error_count


def verify_no_deny_list_words(file_contents, file_location):
    """Verify no segments of the file are in the list of denied words."""
    error_count = 0
    segments = file_contents.split('/')
    for word in segments:
        if word in DENY_LIST:
            logger.error(f"Word '%s' in %s is not allowed.", word, file_location)
            error_count += 1
    return error_count


def verify_no_secret_keys(file_contents, file_location):
    """Verify the file does not contain 20- or 40- length character strings,
    which may be secret keys. Allow strings in the allow list in
    https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/scripts/checkin_tests.py."""
    error_count = 0
    twenties = re.findall("[^A-Z0-9][A][ACGIKNPRS][A-Z]{2}[A-Z0-9]{16}[^A-Z0-9]",
                          file_contents)
    for word in twenties:
        if word[1:-1] in ALLOW_LIST:
            continue
        logger.error("20 character string '%s' found in %s and might be a secret "
                     "access key. If not, add it to the allow list in https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/scripts/checkin_tests.py.", {word[1:-1]}, file_location)
        error_count += 1

    forties = re.findall("[^a-zA-Z0-9/+=][a-zA-Z0-9/+=]{40}[^a-zA-Z0-9/+=]",
                         file_contents)
    for word in forties:
        if word[1:-1] in ALLOW_LIST:
            continue
        logger.error("40 character string '%s' found in %s and might be a secret "
                     "access key. If not, add it to the allow list in https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/scripts/checkin_tests.py.", {word[1:-1]}, file_location)
        error_count += 1

    return error_count


def verify_snippet_start_end(file_contents, file_location):
    """Scan the file contents for snippet-start and snippet-end tags and verify
    that they are in matched pairs. Log errors and return the count of errors."""
    error_count = 0
    snippet_start = 'snippet' + '-start:['
    snippet_end = 'snippet' + '-end:['
    snippet_tags = set()
    for word in file_contents.split():
        if snippet_start in word:
            tag = word.split('[')[1]
            if tag in snippet_tags:
                logger.error(f"Duplicate tag {tag[:-1]} found in {file_location}.")
                error_count += 1
            else:
                snippet_tags.add(tag)
        elif snippet_end in word:
            tag = word.split('[')[1]
            if tag in snippet_tags:
                snippet_tags.remove(tag)
            else:
                logger.error(f"End tag {tag[:-1]} with no matching start tag "
                             f"found in {file_location}.")
                error_count += 1

    for tag in snippet_tags:
        logger.error("Start tag %s with no matching end tag found in %s.",
                     tag[:-1], file_location)
        error_count += 1

    return error_count


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--quiet', action='store_true',
                        help="Suppresses output of filenames while parsing. "
                             "The default is False.")
    parser.add_argument('--root', help="The root path from which to search for files "
                                       "to check. The default is the current working "
                                       "folder.")
    args = parser.parse_args()

    root_path = os.path.abspath('.') if not args.root else os.path.abspath(args.root)

    print('----------\n\nRun Tests\n')
    error_count = check_files(root_path, args.quiet)
    if error_count > 0:
        print(f"{error_count} errors found, please fix them.")
    else:
        print("All checks passed, you are cleared to check in.")
    # Travis CI reports an error if the script exits with a non-zero code.
    sys.exit(error_count)


if __name__ == '__main__':
    main()
