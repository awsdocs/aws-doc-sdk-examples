# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) to tag AWS HealthImaging image sets.
"""

import boto3

from medical_imaging_basics import MedicalImagingWrapper


def tagging_image_sets(medical_imaging_wrapper, image_set_arn):
    """
    Taggging an image set.

    :param medical_imaging_wrapper: A MedicalImagingWrapper instance.
    :param image_set_arn: The Amazon Resource Name (ARN) of the image set.
        For example: 'arn:aws:medical-imaging:us-east-1:123456789012:datastore/12345678901234567890123456789012/' \
                                'imageset/12345678901234567890123456789012'
    """

    # snippet-start:[python.example_code.medical-imaging.tagging_image_set.tag]
    medical_imaging_wrapper.tag_resource(image_set_arn, {"Deployment": "Development"})
    # snippet-end:[python.example_code.medical-imaging.tagging_image_set.tag]

    # snippet-start:[python.example_code.medical-imaging.tagging_image_set.list]
    medical_imaging_wrapper.list_tags_for_resource(image_set_arn)
    # snippet-end:[python.example_code.medical-imaging.tagging_image_set.list]

    # snippet-start:[python.example_code.medical-imaging.tagging_image_set.untag]
    medical_imaging_wrapper.untag_resource(image_set_arn, ["Deployment"])
    # snippet-end:[python.example_code.medical-imaging.tagging_image_set.untag]


if __name__ == "__main__":
    # snippet-start:[python.example_code.medical-imaging.tagging_image_set.arn]
    an_image_set_arn = (
        "arn:aws:medical-imaging:us-east-1:123456789012:datastore/12345678901234567890123456789012/"
        "imageset/12345678901234567890123456789012"
    )
    # snippet-end:[python.example_code.medical-imaging.tagging_image_set.arn]

    an_image_set_arn = input(f"Enter the ARN of the image set to tag: ")

    client = boto3.client("medical-imaging")
    a_medical_imaging_wrapper = MedicalImagingWrapper(client)

    tagging_image_sets(a_medical_imaging_wrapper, an_image_set_arn)
