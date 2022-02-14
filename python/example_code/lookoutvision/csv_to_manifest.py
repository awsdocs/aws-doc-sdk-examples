# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier:  Apache-2.0

"""
Purpose
Shows how to create an Amazon Lookout for Vision manifest file from a CSV file. 
The CSV file format is <image location>,<anomaly classification> (normal or anomaly)
For example:
s3://s3bucket/circuitboard/train/anomaly/train_11.jpg,anomaly
s3://s3bucket/circuitboard/train/normal/train_1.jpg,normal

If necessary, use the bucket argument to specify the S3 bucket folder for the images.
For more information, see https://docs.aws.amazon.com/lookout-for-vision/latest/developer-guide/ex-csv-manifest.html
"""

# snippet-start:[python.example_code.lookoutvision.Scenario_CSVtoManifest]

from datetime import datetime, timezone
import argparse
import logging
import csv
import os

logger = logging.getLogger(__name__)


def check_errors(csv_file):
    """
    Checks for duplicate images and incorrect classifications in a CSV file. 
    If duplicate images or invalid anomaly assignments are found, an errors CSV file
    and deduplicated CSV file are created. Only the first 
    occurence of a duplicate is recorded. Other duplicates are recorded in the errors file.  
    :param csv_file: The source CSV file
    :return: True if errors or duplicates are found, otherwise false.
    """

    logger.info(f"Checking {csv_file}.")

    errors_found = False

    errors_file = f"{csv_file}_errors.csv"
    deduplicated_file = f"{csv_file}_deduplicated.csv"

    # Find errors
    with open(csv_file, 'r') as f,\
            open(deduplicated_file, 'w') as dedup,\
            open(errors_file, 'w') as errors:

        reader = csv.reader(f,  delimiter=',')
        dedup_writer = csv.writer(dedup)
        error_writer = csv.writer(errors)
        line = 1
        entries = set()
        for row in reader:

            # Skip empty lines
            if not ''.join(row).strip():
                continue

            # record any incorrect classifications
            if not row[1].lower() == "normal" and not row[1].lower() == "anomaly":
                error_writer.writerow(
                    [line, row[0], row[1], "INVALID_CLASSIFICATION"])
                errors_found = True

            # write first image entry to dedup file and record duplicates
            key = row[0]
            if key not in entries:
                dedup_writer.writerow(row)
                entries.add(key)
            else:
                error_writer.writerow([line, row[0], row[1], "DUPLICATE"])
                errors_found = True
            line += 1

    if errors_found:
        logger.info(f"Errors found check {errors_file}.")
    else:
        os.remove(errors_file)
        os.remove(deduplicated_file)

    return errors_found


def create_manifest_file(csv_file, manifest_file, s3_path):
    """
    Reads a CSV file and creates a Lookout for Vision classification manifest file
    :param csv_file: The source CSV file
    :param manifest_file: The name of the manifest file to create.
    :param s3_path: The S3 path to the folder that contains the images.
    """
    logger.info(f"Processing CSV file {csv_file}.")

    image_count = 0
    anomalous_count = 0

    with open(csv_file, newline='') as csvfile, open(manifest_file, "w") as output_file:

        image_classifications = csv.reader(
            csvfile, delimiter=',', quotechar='|')

        # process each row (image) in CSV file
        for row in image_classifications:
            # Skip empty lines
            if not ''.join(row).strip():
                continue

            source_ref = str(s3_path) + row[0]
            classification = 0

            if row[1].lower() == 'anomaly':
                classification = 1
                anomalous_count += 1

            json_line = '{"source-ref": "' + source_ref + '",'\
                '"anomaly-label": ' + str(classification) + ','\
                '"anomaly-label-metadata": {' \
                '"confidence": 1,'\
                '"job-name": "labeling-job/anomaly-classification",'\
                '"class-name": "' + row[1] + '",'\
                '"human-annotated": "yes",'\
                '"creation-date": "' + datetime.now(timezone.utc).strftime('%Y-%m-%dT%H:%M:%S.%f') + '",'\
                '"type": "groundtruth/image-classification"'\
                '}}\n'

            output_file.write(json_line)
            image_count += 1

    logger.info(f"Finished creating manifest file {manifest_file}.\n"
                f"Images: {image_count}\nAnomalous: {anomalous_count}")
    return image_count, anomalous_count


def add_arguments(parser):
    """
    Adds command line arguments to the parser.
    :param parser: The command line parser.
    """

    parser.add_argument(
        "csv_file", help="The CSV file that you want to process."
    )

    parser.add_argument(
        "--s3_path",  help="The S3 bucket and folder path for the images."
        " If not supplied, column 1 is assumed to include the S3 path.", required=False
    )


def main():

    logging.basicConfig(level=logging.INFO,
                        format="%(levelname)s: %(message)s")

    try:

        # get command line arguments
        parser = argparse.ArgumentParser(usage=argparse.SUPPRESS)
        add_arguments(parser)
        args = parser.parse_args()
        s3_path = args.s3_path
        if s3_path is None:
            s3_path = ""

        csv_file = args.csv_file
        manifest_file = os.path.splitext(csv_file)[0] + '.manifest'

        # Create manifest file if there are no duplicate images.
        if check_errors(csv_file):
            print(
                f"Issues found. Use {csv_file}_errors to view duplicates and errors.")
            print(f"{csv_file}_deduplicated.csv contains the first occurence of a duplicate."
                  "Update as necessary with the correct information.")
            print(f"Re-run the script with {csv_file}_deduplicated.csv")
        else:
            print('No duplicates found. Creating manifest file')

            image_count, anomalous_count = create_manifest_file(csv_file,
                manifest_file,
                s3_path)

            print(f"Finished creating manifest file: {manifest_file} \n")

            normal_count = image_count-anomalous_count
            print(f"Images processed: {image_count}")
            print(f"Normal: {normal_count}")
            print(f"Anomalous: {anomalous_count}")

    except FileNotFoundError as err:
        logger.exception(f"File not found.:{err}")
        print(f"File not found: {err}. Check your input CSV file")

    except Exception as err:
        logger.exception(f"An error occured:{err}")
        print(f"An error occured:{err}")


if __name__ == "__main__":
    main()

# snippet-end:[python.example_code.lookoutvision.Scenario_CSVtoManifest]