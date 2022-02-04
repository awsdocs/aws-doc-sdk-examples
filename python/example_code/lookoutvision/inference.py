# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

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


# snippet-start:[python.example_code.lookoutvision.DetectAnomalies]
class Inference:
    """
    Shows how to detect anomalies in an image using a trained Lookout for Vision model.
    """

    @staticmethod
    def detect_anomalies(lookoutvision_client, project_name, model_version, photo):
        """
        Detects anomalies in an image (jpg/png) by using your Lookout for Vision model.

        :param lookoutvision_client: A Boto3 Lookout for Vision client.
        :param project_name: The name of the project that contains the model that
                             you want to use.
        :param model_version: The version of the model that you want to use.
        :param photo: The path and name of the image in which you want to detect
                      anomalies.
        :return: Whether anomalies were detected and with what confidence.
        """
        try:
            image_type = imghdr.what(photo)
            if image_type == "jpeg":
                content_type = "image/jpeg"
            elif image_type == "png":
                content_type = "image/png"
            else:
                logger.info("Invalid image type for %s", photo)
                raise ValueError(
                    f"Invalid file format. Supply a jpeg or png format file: {photo}")

            logger.info("Detecting anomalies in %s", photo)
            with open(photo, "rb") as image:
                response = lookoutvision_client.detect_anomalies(
                    ProjectName=project_name,
                    ContentType=content_type,
                    Body=image.read(),
                    ModelVersion=model_version)
            anomalous = response["DetectAnomalyResult"]["IsAnomalous"]
            confidence = response["DetectAnomalyResult"]["Confidence"]

            logger.info("Anomalous?: %s", anomalous)
            logger.info("Confidence: %s", confidence)
        except FileNotFoundError:
            logger.exception("Couldn't find file: %s", photo)
            raise
        except ClientError:
            logger.exception("Couldn't detect anomalies.")
            raise
        else:
            return anomalous, confidence

    @staticmethod
    def download_from_s3(s3_resource, photo):
        """
        Downloads an image from an S3 bucket.

        :param s3_resource: A Boto3 Amazon S3 resource.
        :param photo: The Amazon S3 path of a photo to download.
        return: The local path to the downloaded file.
        """
        try:
            bucket, key = photo.replace("s3://", "").split("/", 1)
            local_file = os.path.basename(photo)
        except ValueError as err:
            logger.exception("Couldn't get S3 info for %s", photo)
            raise

        try:
            logger.info("Downloading %s", photo)
            s3_resource.Bucket(bucket).download_file(key, local_file)
        except ClientError:
            logger.exception("Couldn't download %s from S3.", photo)
            raise

        return local_file


def main():
    """
    Detects anomalies in an image file.
    """
    try:
        logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")
        lookoutvision_client = boto3.client("lookoutvision")
        s3_resource = boto3.resource('s3')

        parser = argparse.ArgumentParser(usage=argparse.SUPPRESS)
        parser.add_argument(
            "project", help="The project containing the model that you want to use.")
        parser.add_argument(
            "version", help="The version of the model that you want to use.")
        parser.add_argument(
            "image",
            help="The file that you want to analyze. Supply a local file path or a "
                 "path to an S3 object.")
        args = parser.parse_args()

        if args.image.startswith("s3://"):
            photo = Inference.download_from_s3(s3_resource, args.image)
        else:
            photo = args.image

        print(f"Analyzing {photo}.")
        anomalous, confidence = Inference.detect_anomalies(
            lookoutvision_client, args.project, args.version, photo)

        if args.image.startswith("s3://"):
            os.remove(photo)

        state = "anomalous" if anomalous else "normal"
        print(
            f"Your model is {confidence:.0%} confident that the image is {state}.")
    except ClientError as err:
        print(f"Service error: {err.response['Error']['Message']}")
    except FileNotFoundError as err:
        print(f"The supplied file couldn't be found: {err.filename}.")
    except ValueError as err:
        print(f"A value error occurred: {err}.")
    else:
        print("Successfully completed analysis.")


if __name__ == "__main__":
    main()
# snippet-end:[python.example_code.lookoutvision.DetectAnomalies]
