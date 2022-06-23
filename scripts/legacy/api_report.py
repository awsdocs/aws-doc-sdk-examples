# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

"""
Reads API metadata and writes a report of API coverage.

This module can be run in two modes: report or verify.

Report mode

Specify a root folder and an output file path. The module scans the root folder
and all of its subfolders for 'metadata.yaml' files and writes their data in CSV
format to the specified output file path. The module also reports a count of unique
APIs along with the total number of APIs and examples. A 'unique API' is defined
by its language, service, and operation name, such as Python, S3, create_bucket.

Verify mode

Specify a metadata file by name. The module reads only that file and writes a report
to the command window. This can be used when updating a metadata file to verify that
the count and coverage output is what you expect.
"""

import argparse
import os
import sys
from urllib.parse import urljoin
import urllib.request as request
import yaml
from yaml.scanner import ScannerError

METADATA_FILENAME = 'metadata.yaml'
GITHUB_URL = 'https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/'

EXT_LOOKUP = {
    'c': 'C',
    'cpp': 'C++',
    'cs': 'C#',
    'go': 'Go',
    'html': 'JavaScript',
    'java': 'Java',
    'js': 'JavaScript',
    'php': 'PHP',
    'py': 'Python',
    'rb': 'Ruby',
    'ts': 'TypeScript',
    'sh': 'AWS-CLI',
    'cmd': 'AWS-CLI'
}


def gather_data(examples_folder):
    """
    Scan a folder and its subfolders for metadata files and read them into a
    list of example dictionaries.

    :type examples_folder: string
    :param examples_folder: The root folder where the scan is started.
    """
    if not os.path.isdir(examples_folder):
        raise FileNotFoundError(f"{examples_folder} is not a directory.")

    examples = []
    for folder_name, _, file_list in os.walk(examples_folder):
        for file_name in [f for f in file_list if f.lower() == METADATA_FILENAME]:
            file_path = os.path.join(folder_name, file_name)
            print(f"Found metadata: {file_path}.")
            read_metadata(file_path, examples)
    return examples


def read_metadata(file_path, examples):
    """
    Read the specified file and append its contents into the specified list
    of dictionaries.

    :type file_path: string
    :param file_path: A metadata file that contains example metadata in yaml format.
    :type examples: list
    :param examples: A list of example dictionaries. Examples read from the metadata
                     are appended to this list.
    """
    with open(file_path, 'r') as meta_stream:
        try:
            meta_docs = yaml.safe_load_all(meta_stream)
            for example_meta in meta_docs:
                example_meta['metadata_path'] = file_path
                examples.append(example_meta)
        except ScannerError as err:
            print(f"Yaml parser error in {file_path}, skipping.")
            print(err)


def write_report(examples, report_path=None):
    """
    Writes a report of APIs covered by the list of examples. The report includes
    the count of unique APIs, total API and example counts, and the full list of
    examples in CS format.

    :type examples: list
    :param examples: A list of example dictionaries.
    :type report_path: string
    :param report_path: The output file to write the report. If this file exists,
                        it is overwritten. If no file is specified, the report
                        is written to sys.stdout.
    :rtype: int
    :return: The count of unique APIs covered by the examples list.
    """
    lines = ["Created,File,Language,Service,Operation"]
    unique_apis = set()

    for example in examples:
        try:
            created = example['created']
            for file in example['files']:
                metadata_folder = os.path.split(example['metadata_path'])[0]
                metadata_url = request.pathname2url(metadata_folder)
                base_url = urljoin(GITHUB_URL, metadata_url) + '/'
                file_url = request.pathname2url(file['path'])
                file_url = urljoin(base_url, file_url)
                ext = os.path.splitext(file_url)[1].lstrip('.')
                language = EXT_LOOKUP[ext]
                for api in file['apis']:
                    service = api['service']
                    for operation in api['operations']:
                        unique_apis.add((language, service, operation))
                        lines.append(
                            ','.join([str(created), file_url, language,
                                      service, operation]))
        except KeyError as error:
            print(f"ERROR: example missing a required {error} key: {example}.")

    report = open(report_path, 'w') if report_path else sys.stdout
    try:
        report.write(f"Number of unique APIs: {len(unique_apis)}.\n")
        report.write(f"Total number of APIs: {len(lines) - 1}.\n")
        report.write(f"Total number of examples: {len(examples)}.\n")
        if len(lines) > 1:
            report.write("\n")
            report.write('\n'.join(lines))
    finally:
        if report is not sys.stdout:
            report.close()
            print(f"Report written to {report_path}.")

    return len(unique_apis)


def main():
    parser = argparse.ArgumentParser(
        description="Reads API metadata and writes a report of API coverage. To"
                    "scan a folder tree and write to a file, specify root and report. "
                    "To verify a single file and write to the console, specify verify.")
    parser.add_argument(
        "--root",
        default=".",
        help="The folder to start the search for metadata files. Defaults to the"
             "current folder."
    )
    parser.add_argument(
        "--report",
        default="report.csv",
        help="The file path to write the report. Defaults to 'report.csv'."
    )
    parser.add_argument(
        "--verify",
        help="A single metadata file to verify. If specified, root and report "
             "arguments are ignored and the output is written to the console."
    )
    args = parser.parse_args()

    try:
        if args.verify:
            examples = []
            read_metadata(args.verify, examples)
            write_report(examples)
        else:
            examples = gather_data(args.root)
            write_report(examples, args.report)
    except KeyError as error:
        print(error)


if __name__ == '__main__':
    main()
