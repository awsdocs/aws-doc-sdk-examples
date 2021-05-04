# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
"""
Uses a trained Amazon Lookout for Vision model to detect anomalies
in an image. The image can be local or in an S3 bucket.
"""

import argparse
import logging
import imghdr
import os
import boto3

from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


class Inference:
    """
    Shows how to detect anomalies in an image using a trained Amazon Lookout
    for Vision model.
    """
    @staticmethod
    def detect_anomalies(lookoutvision_client, project_name, model_version, photo):
        """
        Detects anomalies in an image (jpg/png) by using your Amazon Lookout for Vision
        model.
        :param lookoutvision_client: An Amazon Lookout for Vision Boto3 client.
        :param project_name: The name of the project that contains the model that
        you want to use.
        :param model_version: The version of the model that you want to use.
        :param photo: The path and name of the image in which you want to detect
        anomalies.
        """

        try:

            image_type = imghdr.what(photo)
            content_type = ""

            if image_type == "jpeg":
                content_type = "image/jpeg"
            elif image_type == "png":
                content_type = "image/png"
            else:
                logger.info("Invalid image type for %s", photo)
                raise ValueError(
                    f"Invalid file format. Supply a jpeg or png format file: {photo}"
                )

            # Call detect_anomalies
            logger.info("Detecting anomalies in %s", photo)
            with open(photo, "rb") as image:
                response = lookoutvision_client.detect_anomalies(
                    ProjectName=project_name,
                    ContentType=content_type,  # "image/jpeg" or image/png
                    Body=image.read(),
                    ModelVersion=model_version,
                )
            anomalous = response["DetectAnomalyResult"]["IsAnomalous"]
            confidence = response["DetectAnomalyResult"]["Confidence"]

            logger.info("Anomalous?: %s", format(anomalous))
            logger.info("Confidence: %s", format(confidence))
            return anomalous, confidence

        except FileNotFoundError as err:
            logger.info("Couldn't find file: %s", photo)
            raise

        except ClientError as err:
            logger.info(format(err))
            raise

    @staticmethod
    def download_from_s3(s3_resource, photo):
        """
        Downloads an image from an S3 bucket.
        :param photo: The S3 path of a photo to download.
        return: The local path to the downloaded file.
        """

        try:

            bucket, key = photo.replace("s3://", "").split("/", 1)
            local_file = os.path.basename(photo)
        except ValueError as err:
            logger.info("Couldn't get S3 info for %s: %s", photo, format(err))
            raise ValueError("Couldn't get S3 info for {}.".format(photo)) from err

        try:
            logger.info("Downloading %s", photo)

            s3_resource.Bucket(bucket).download_file(key,local_file)

        except ClientError as err:
            logger.exception("Couldn't download %s from S3.", photo)
            err.response["Error"]["Message"] = f"Couldn't download {photo} from S3."
            raise

        return local_file


def add_arguments(parser):
    """
    Adds command line arguments to the parser.
    :param parser: The command line parser.
    """

    parser.add_argument(
        "project", help="The project containing the model that you want to use."
    )
    parser.add_argument(
        "version", help="The version of the model that you want to use."
    )
    parser.add_argument(
        "image",
        help="The file that you want to analyze. "
        "Supply a local file path or a path to an S3 object.",
    )

def main():
    """
    Entrypoint for anomaly detection example.
    """

    try:
        logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")
        lookoutvision_client = boto3.client("lookoutvision")
        s3_resource=boto3.resource('s3')

        parser = argparse.ArgumentParser(usage=argparse.SUPPRESS)

        add_arguments(parser)

        args = parser.parse_args()
        print("Analyzing " + args.image)

        photo = args.image
        if args.image.startswith("s3://"):
            photo = Inference.download_from_s3(s3_resource,args.image)

        # analyze image
        anomalous, confidence = Inference.detect_anomalies(
            lookoutvision_client, args.project, args.version, photo
        )

        # remove local copy of S3 photo
        if args.image.startswith("s3://"):
            os.remove(photo)

        state = "anomalous"
        if anomalous is False:
            state = "normal"

        print(
            f"Your model is {confidence:.0%} confident that the image is {state}."
        )

    except ClientError as err:
        print("A service error occured: " + format(err.response["Error"]["Message"]))
    except FileNotFoundError as err:
        print("The supplied file couldn't be found: " + err.filename)
    except ValueError as err:
        print("A value error occured. " + format(err))
    else:
        print("Successfully completed analysis.")

if __name__ == "__main__":
    main()
