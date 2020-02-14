<!--Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This work is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0
International License (the "License"). You may not use this file except in compliance with the
License. A copy of the License is located at http://creativecommons.org/licenses/by-nc-sa/4.0/.

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
either express or implied. See the License for the specific language governing permissions and
limitations under the License.
-->
# Scripts

The scripts contained in this module are primarily for internal use by the AWS
Code Examples team.

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
window at the root folder of the repository, run the following:

```
python -m scripts.api_report --root . --report report.csv
``` 

This script can also be used to verify an individual metadata file. To verify a
single file and write a report to the command window output, in a command window at
the root folder of the repository, run the following:

```
python -m scripts.api_report --verify aws-cli\bash-linux\s3\metadata.yaml
``` 

#### Running the tests

To run the unit tests associated with this script, in a command window at the 
`scripts\tests` folder of the repository, run `pytest`.
