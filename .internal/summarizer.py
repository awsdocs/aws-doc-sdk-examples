# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Gathers titles and purpose statements from README.md files and generates a top-level
README.md file at the specified root that lists all of the examples and their
purposes. Any existing README.md file at the root is overwritten.

Currently this script requires a very specific README format and works only for the
Python section, but it could be expanded to encompass other parts of the repo.
"""

import argparse
import os
from urllib.parse import urljoin
import urllib.request as request

README_FILENAME = 'readme.md'
GITHUB_URL = 'https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/'
PYTHON_VERSION = '3.6'
BOTO3_VERSION = '1.11.10'
PYTEST_VERSION = '5.3.5'

IGNORE_FOLDERS = {
    'venv',
    'scripts',
    '__pycache__',
    '.pytest_cache',
    'scheduled_lambda',
    'websocket',
    'ses_replicateidentities'
}

README_HEADER = \
    f"# AWS SDK for Python (Boto3) examples\n\n" \
    f"Code examples that show how to use Boto3 to access Amazon Web Services (AWS).\n\n" \
    f"## Prerequisites\n\n" \
    f"- You must have an AWS account, and have your default credentials and AWS Region " \
    f"configured as described in the [AWS Tools and SDKs Shared Configuration and " \
    f"Credentials Reference Guide]" \
    f"(https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).\n" \
    f"- Python {PYTHON_VERSION} or later\n" \
    f"- Boto3 {BOTO3_VERSION} or later\n" \
    f"- PyTest {PYTEST_VERSION} or later (to run unit tests)\n\n" \
    f"## Documentation\n\n" \
    f"For Boto3 and AWS documentation, see the following:\n\n" \
    f"- [AWS SDK for Python (Boto3) Documentation]" \
    f"(https://boto3.amazonaws.com/v1/documentation/api/latest/index.html)\n" \
    f"- [AWS Documentation](https://docs.aws.amazon.com/)\n\n" \
    f"## Examples\n\n"

README_FOOTER = \
    "## Additional information\n\n" \
    "- As an AWS best practice, grant this code least privilege, or only the " \
    "permissions required to perform a task. For more information, see " \
    "[Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) " \
    "in the *AWS Identity and Access Management User Guide*.\n" \
    "- This code has not been tested in all AWS Regions. Some AWS services are " \
    "available only in specific Regions. For more information, see the " \
    "[AWS Regional Table](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/)\n" \
    "- Running this code might result in charges to your AWS account.\n\n" \
    "---\n\n" \
    "Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.\n\n" \
    "SPDX-License-Identifier: Apache-2.0\n"


def make_github_url(folder_name, file_name):
    """
    Concatenate the GitHub base URL, a relative path to a folder, and a file name
    to form a full URL to the file.

    :param folder_name: The relative path to the folder that contains the file.
    :param file_name: The name of the file.
    :return: The full URL to the file on GitHub.
    """
    folder_url = request.pathname2url(folder_name)
    base_url = urljoin(GITHUB_URL, folder_url) + '/'
    file_url = request.pathname2url(file_name)
    file_url = urljoin(base_url, file_url)
    return file_url


def gather_data(root_folder):
    """
    Scan a folder and its subfolders for README.md files and read them into a
    list of example summaries.

    :param root_folder: The root folder where the scan is started.
    """
    if not os.path.isdir(root_folder):
        raise FileNotFoundError(f"{root_folder} is not a directory.")

    summaries = []
    for folder_name, dirs, file_list in os.walk(root_folder, topdown=True):
        dirs[:] = [d for d in dirs if d not in IGNORE_FOLDERS]
        for file_name in file_list:
            if file_name.lower() == README_FILENAME and folder_name != root_folder:
                file_path = os.path.join(folder_name, file_name)
                summaries.append({
                    'url': make_github_url(folder_name, file_name),
                    'summary': read_summary(file_path)
                })
                print(f"Found readme: {file_path}.")
    return summaries


def read_summary(file_path):
    """
    Reads a title and purpose summary from a file.

    :param file_path: The file to read.
    :return: A string that contains everything before the Prerequisites section.
    """
    with open(file_path) as readme:
        summary = readme.read().split('## Prerequisites')[0]
    return summary


def write_super_summary(root_folder, summaries):
    """
    Write the summary of summaries, prefaced by a header block and followed by
    a footer block.

    :param root_folder: The folder where the super summary is stored.
    :param summaries: The list of summaries to write.
    """
    summary_lines = []
    for summary in summaries:
        sum_parts = summary['summary'].split('##')
        summary_lines.append(
            f"### [{sum_parts[0][1:].strip()}]({summary['url']})\n\n"
            f"####{'####'.join(sum_parts[1:])}")
    super_sum_path = os.path.join(root_folder, 'README.md')
    with open(super_sum_path, 'w') as super_summary:
        super_summary.write(README_HEADER)
        super_summary.writelines(summary_lines)
        super_summary.write(README_FOOTER)
    print(f"Wrote {super_sum_path}")


def main():
    parser = argparse.ArgumentParser(
        description="Reads README files and writes a summary of their purposes.")
    parser.add_argument(
        "--root",
        default=".",
        help="The folder to start the search for README files. Defaults to the "
             "current folder."
    )
    args = parser.parse_args()

    summaries = gather_data(args.root)
    write_super_summary(args.root, summaries)


if __name__ == '__main__':
    main()
