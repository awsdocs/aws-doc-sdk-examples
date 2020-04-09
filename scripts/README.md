<!--Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. 
The full license text is provided in the LICENSE file accompanying this repository.

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
CONDITIONS OF ANY KIND, either express or implied. See the License for the 
specific language governing permissions and limitations under the License.
-->
# Scripts

The scripts contained in this module are primarily for internal use by the AWS
Code Examples team.

## Cleanup report

#### Purpose

Reads cleanup metadata and writes a report of files cleaned up vs. files still
needing cleanup. A cleaned file contains code that has been brought up to coding
standard, has been tested, and has at least minimal comments. To include a file
in the cleaned report, list it in a metadata.yaml file somewhere in the repo.

#### Prerequisites

To run this script, you must have the following installed globally or in a virtual
environment:
 
* Python 3.6 or later
* PyYaml 5.3 or later
* PyTest 5.3.5 or later (to run unit tests)

#### Running the script

The typical usage of this script is to determine the cleanup coverage in this
GitHub repository. To generate a CSV-formatted report of cleanup coverage, in a command
window at the root folder of the repository, run the following.

```
python -m scripts.cleanup_report
``` 

You can also run the script against a subfolder and output the report to a custom
location, which can be useful for testing new metadata files.

    python -m scripts.cleanup_report --root python/example_code/sqs --report ~/temp/sqs_rep.csv

#### Running the tests

To run the unit tests associated with this script, in a command window at the 
`scripts\tests` folder of the repository, run `python -m pytest test_cleanup_report.py`.


## Checkin tests

#### Purpose

The checkin tests are run whenever a pull request is submitted or changed 
(using Travis CI, configured in .travis.yml).

The checkin tests walk the full repository and scan code files to look for 
the following issues.

* Disallow a list of specified words.
* Disallow any 20- or 40- character strings that fit a specified regex profile
  that indicates they might be secret access keys. Allow strings that fit the
  regex profile if they are in the allow list.
* Disallow file names that contain 20- or 40- character strings that fit the same
  regex profile, unless the filename is in the allow list.
* Verify that snippet-start and snippet-end tags are in matched pairs. You are
  not required to include these tags, but if you do they must be in pairs.
  
A count of errors found is returned. When Travis CI receives a non-zero return code,
it treats the checks as failed and displays a message in the pull request.

#### Prerequisites

To run this script, you must have the following installed globally or in a virtual
environment:
 
* Python 3.6 or later
* PyTest 5.3.5 or later (to run unit tests)

#### Running the script

The typical usage of this script is to check for certain disallowed strings and
verify matched snippet tags when submitting a pull request. You can run the script
manually by running the following in the root folder of your GitHub clone. 

```
python -m scripts.checkin_tests
``` 

The script can also be used to verify individual folders. If you want to verify
just the files in a specific folder and its subfolders, use the `--root` option.
For example, to scan just the `file_transfer` folder in the Python S3 example section,
run the following command. 

```
python -m scripts.checkin_tests --root python/example_code/s3/file_transfer 
``` 

To suppress most output, add the `--quiet` option.

#### Running the tests

To run the unit tests associated with this script, in a command window at the 
`scripts\tests` folder of the repository, run `python -m pytest test_checkin_tests.py`.


## API Report

#### Purpose

Reads API metadata and writes a report of API coverage. The script can be run to 
scan a folder and its subfolders for metadata descriptions of code examples and 
the APIs they demonstrate. It can also be run on an individual metadata file to
verify that it is formatted correctly and reports as expected.

#### Prerequisites

To run this script, you must have the following installed globally or in a virtual
environment:
 
* Python 3.6 or later
* PyYaml 5.3 or later
* PyTest 5.3.5 or later (to run unit tests)

#### Running the script

The typical usage of this script is to determine the API coverage contained in this
GitHub repository. To generate a CSV-formatted report of API coverage, in a command
window at the root folder of the repository, run the following.

```
python -m scripts.api_report
``` 

This script can also be used to verify an individual metadata file. To verify a
single file and write a report to the command window output, in a command window at
the root folder of the repository, run the following.

```
python -m scripts.api_report --verify aws-cli\bash-linux\s3\metadata.yaml
``` 

#### Running the tests

To run the unit tests associated with this script, in a command window at the 
`scripts\tests` folder of the repository, run `python -m pytest test_api_report.py`.
