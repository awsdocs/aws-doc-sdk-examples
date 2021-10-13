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
Reads cleanup metadata and writes a report of files cleaned up vs. files still
needing cleanup. A cleaned file contains code that has been brought up to coding
standard, has been tested, and has at least minimal comments. To include a file
in the cleaned report, list it in a metadata.yaml file somewhere in the repo.

How to run a report
    To run a full report over the entire repo, in the base folder of your repo,
    run the following command.

        python -m scripts.cleanup_report

    This scans the full repository for files named 'metadata.yaml', digests them
    into a list of cleaned files that are compared against existing code files,
    and write a report to a file named 'report.csv' in the same folder.

    You can also run the script against a subfolder and output the report to a custom
    location, which can be useful for testing new metadata files.

        python -m scripts.cleanup_report --root python/example_code/sqs --report ~/temp/sqs_rep.csv
"""

import argparse
import os
import sys
from urllib.parse import urljoin
import urllib.request as request
import yaml
from yaml.scanner import ScannerError
from yaml.parser import ParserError

METADATA_FILENAMES = ['metadata.yaml', '.metadata.yaml']
GITHUB_URL = 'https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/'

# A file must have one of these extensions to be counted in file total.
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
    'ts': 'TypeScript'
}

IGNORE_FOLDERS = {
    'venv',
    'scripts',
    '__pycache__',
    '.pytest_cache',
    '.vscode',
    'vendor',
    'node_modules',
}


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


def gather_data(examples_folder):
    """
    Scan a folder and its subfolders for metadata files and read them into a
    list of example dictionaries. Also collect all files that have one of the
    specified code extensions.

    :param examples_folder: The root folder where the scan is started.
    :return: A tuple of examples and file names.
    """
    if not os.path.isdir(examples_folder):
        raise FileNotFoundError(f"{examples_folder} is not a directory.")

    examples = []
    files = []
    for folder_name, dirs, file_list in os.walk(examples_folder, topdown=True):
        dirs[:] = [d for d in dirs if d not in IGNORE_FOLDERS]

        for file_name in file_list:
            ext = os.path.splitext(file_name)[1].lstrip('.')
            if ext.lower() in EXT_LOOKUP:
                file_url = make_github_url(folder_name, file_name)
                files.append(file_url)
            elif file_name.lower() in METADATA_FILENAMES:
                file_path = os.path.join(folder_name, file_name)
                print(f"Found metadata: {file_path}.")
                read_metadata(file_path, examples)
    return examples, files


def read_metadata(file_path, examples):
    """
    Read the specified metadata file and append its contents into the specified list
    of dictionaries.

    :param file_path: That path to a metadata file that contains example metadata
                      in yaml format.
    :param examples: A list of example dictionaries. Examples read from the metadata
                     are appended to this list.
    """
    with open(file_path, 'r') as meta_stream:
        try:
            meta_docs = yaml.safe_load_all(meta_stream)
            for example_meta in meta_docs:
                if example_meta is None:
                    print(f"Empty section found in {file_path}.")
                else:
                    example_meta['metadata_path'] = file_path
                    examples.append(example_meta)
        except (ScannerError, ParserError) as err:
            print(f"Yaml parser error in {file_path}, skipping.")
            print(err)


def write_report(examples, repo_files, report_path=None, summarize=False, dirty=False):
    """
    Writes a report of files cleaned versus files awaiting cleanup.
    Files that are listed in metadata but do not exist in the repo are output
    as missing files.
    Files that are listed more than once in any metadata are output as duplicates.
    The report includes the full list of example metadata in CSV format.

    :param examples: A list of example dictionaries.
    :param repo_files:
    :param report_path: The output file to write the report. If this file exists,
                        it is overwritten. If no file is specified, the report
                        is written to sys.stdout.
    :param summarize: Omit CSV output and only print the summary.
    :param dirty: Include dirty files in the full report.
    """
    lines = ["File,Language,Service"]
    clean_files = []
    missing_files = []
    bad_examples = []
    repo_files_lookup = {rf.lower() for rf in repo_files}

    for example in examples:
        try:
            for file in example['files']:
                metadata_folder = os.path.split(example['metadata_path'])[0]
                file_url = make_github_url(metadata_folder, file['path'])
                if file_url.lower() in repo_files_lookup:
                    if file_url not in clean_files:
                        clean_files.append(file_url)
                        ext = os.path.splitext(file_url)[1].lstrip('.')
                        language = EXT_LOOKUP[ext]
                        for service in file.get('services', ['']):
                            lines.append(
                                ','.join([file_url, language, service]))
                    else:
                        print(f"File '{file_url}' reported a second time in "
                              f"{example['metadata_path']}.")
                else:
                    missing_files.append(file_url)
                    print(
                        f"File '{file_url}' reported in metadata "
                        f"does not exist in the repo.")
        except KeyError as error:
            print(f"ERROR: example missing a required {error} key: {example}.")
            bad_examples.append(example)

    report = open(report_path, 'w') if report_path else sys.stdout
    try:
        clean_count = len(clean_files)
        total_count = len(repo_files)
        report.write(f"Total number of examples: "
                     f"{len(examples) - len(bad_examples)}.\n")
        report.write(f"Total number of cleaned files: {clean_count}.\n")
        report.write(f"Total number of files: {total_count}.\n")
        if total_count > 0:
            report.write(f"Percent clean: "
                         f"{clean_count/total_count:.0%}.")
        if not summarize:
            if len(lines) > 1:
                report.write("\n")
                report.write('\n'.join(lines))
            if dirty:
                clean_lookup = [file.lower() for file in clean_files]
                dirty_files = sorted([file for file in repo_files_lookup
                                      if file not in clean_lookup])
                report.write("\n")
                if dirty_files:
                    report.write("**Dirty files found:**\n")
                    report.write('\n'.join(dirty_files))
                else:
                    report.write("**No dirty files found!**")
    finally:
        if report is not sys.stdout:
            report.close()
            print(f"Report written to {report_path}.")


def main():
    parser = argparse.ArgumentParser(
        description="Reads file metadata and writes a report of cleanup progress.")
    parser.add_argument(
        "--root",
        default=".",
        help="The folder to start the search for metadata files. Defaults to the "
             "current folder."
    )
    parser.add_argument(
        "--report",
        help="The file path to write the report. When not specified, writes "
             "to stdout."
    )
    parser.add_argument(
        "--summarize",
        action='store_true',
        help="Omits full CSV report and outputs only a summary."
    )
    parser.add_argument(
        "--dirty",
        action='store_true',
        help="Includes dirty files in the full report. This is most useful along with "
             "--root, to verify that you've added all new files in a subfolder."
    )
    args = parser.parse_args()

    try:
        examples, files = gather_data(args.root)
        write_report(examples, files, args.report, args.summarize, args.dirty)
    except KeyError as error:
        print(error)


if __name__ == '__main__':
    main()
