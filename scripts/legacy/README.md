# Legacy scripts

The scripts contained in this module were at one time for internal use by the AWS
Code Examples team but are no longer actively used. We're keeping them around
*just in case*.

## API Report

#### Purpose

Reads API metadata and writes a report of API coverage. The script can be run to 
scan a folder and its subfolders for metadata descriptions of code examples and 
the APIs they demonstrate. It can also be run on an individual metadata file to
verify that it is formatted correctly and reports as expected.

This report has been discontinued. We are using cleanup_report.py instead, to 
track progress on our legacy code cleanup initiative.

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

## Check Metadata

#### Purpose

Tests for matching snippet tags and screens for possible secret keys and other
disallowed strings. These tests have been superseded by the tests contained in
checkin_tests.py.