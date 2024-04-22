# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[python.example_code.medical-imaging.workflow]
"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) to manage and invoke AWS HealthImaging
functions.
"""

import datetime
import gzip
import json
import logging
import random
import time
import sys
import os

import boto3
from botocore.exceptions import ClientError
from threading import Thread

# Import the wrapper for the service functionality.
from medicalimaging import MedicalImagingWrapper

# Add relative path to include demo_tools in this code example without need for setup.
sys.path.append("../../..")
from demo_tools import demo_func
import demo_tools.question as q

logger = logging.getLogger(__name__)

IDC_S3_BUCKET_NAME = "idc-open-data"

IDC_IMAGE_CHOICES = [
    {
        "Description": "CT of chest (2 images)",
        "Directory": "00029d25-fb18-4d42-aaa5-a0897d1ac8f7",
    },
    {
        "Description": "CT of pelvis (57 images)",
        "Directory": "00025d30-ef8f-4135-a35a-d83eff264fc1",
    },
    {
        "Description": "MRI of head (192 images)",
        "Directory": "0002d261-8a5d-4e63-8e2e-0cbfac87b904",
    },
    {
        "Description": "MRI of breast (92 images)",
        "Directory": "0002dd07-0b7f-4a68-a655-44461ca34096",
    },
]

IMPORT_JOB_MANIFEST_FILE_NAME = "job-output-manifest.json"


class MedicalImagingWorkflowScenario:
    input_bucket_name = ""
    output_bucket_name = ""
    role_arn = ""
    data_store_id = ""

    def __init__(self, medical_imaging_wrapper, s3_client, cf_resource):
        self.medical_imaging_wrapper = medical_imaging_wrapper
        self.s3_client = s3_client
        self.cf_resource = cf_resource

    def run_scenario(self):
        print("-" * 88)
        print(
            "\t\tWelcome to the AWS HealthImaging working with image sets and frames workflow."
        )
        print("-" * 88)

        print(
            """\
        This workflow will import DICOM files into a HealthImaging data store.
        DICOM® — Digital Imaging and Communications in Medicine — is the international
        standard for medical images and related information.
        
        The workflow will then download all the image frames created during the DICOM import and decode
        the image frames from their HTJ2K format to a bitmap format.
        The bitmaps will then be validated with a checksum to ensure they are correct.
        This workflow requires a number of AWS resources to run.
        
        It requires a HealthImaging data store, an Amazon Simple Storage Service (Amazon S3)
        bucket for uploaded DICOM files, an Amazon S3 bucket for the output of a DICOM import, and
        an AWS Identity and Access Management (IAM) role for importing the DICOM files into
        the data store.
        
        These resources are created using the provided AWS CloudFormation stack
        which will be deployed now.
        """
        )
        cf_stack = self.deploy()

        print(
            """\
        This workflow uses DICOM files from the National Cancer Institute Imaging Data Commons (IDC)
        Collections.
        
        Here is the link to their website:
        https://registry.opendata.aws/nci-imaging-data-commons/
        We will use DICOM files stored in an S3 bucket managed by the IDC.

        First one of the DICOM folders in the IDC collection must be copied to your
        input S3 bucket.
        """
        )

        print(
            f"\t\tYou have the choice of one of the following {len(IDC_IMAGE_CHOICES)} folders to copy."
        )

        for index, idcChoice in enumerate(IDC_IMAGE_CHOICES):
            print(f"\t\t{index + 1}. {idcChoice['Description']}")
        choice = q.ask(
            "\t\tWhich DICOM files do you want to import? ",
            q.is_int,
            q.in_range(1, len(IDC_IMAGE_CHOICES) + 1),
        )

        from_directory = IDC_IMAGE_CHOICES[choice - 1]["Directory"]
        input_directory = "input"
        output_directory = "output"

        print(
            f"\n\t\tThe files in the directory {from_directory} in the bucket {IDC_S3_BUCKET_NAME} will be copied "
        )
        print(
            f"\t\tto the folder {input_directory}/{from_directory}in the bucket {self.input_bucket_name}."
        )
        q.ask("\t\tPress Enter to start the copy.")
        self.copy_images(
            IDC_S3_BUCKET_NAME, from_directory, self.input_bucket_name, input_directory
        )

        print(
            f"\n\t\tNow the DICOM images will be imported into the datastore with ID {self.data_store_id}."
        )
        import_job_id = self.medical_imaging_wrapper.start_dicom_import_job(
            self.data_store_id,
            self.input_bucket_name,
            input_directory,
            self.output_bucket_name,
            output_directory,
            self.role_arn,
        )
        print(
            f"\n\t\tThe DICOM files were successfully imported. The import job ID is {self.data_store_id}."
        )

        print(
            f"""\
        Information about the import job, including the IDs of the created image sets,
        is located in a file named {IMPORT_JOB_MANIFEST_FILE_NAME} 
        This file is located in a folder specified by the import job's 'outputS3Uri'.
        The 'outputS3Uri' is retrieved by calling the 'GetDICOMImportJob' action.
        """
        )

        print(
            f"""\
        The image set IDs will be retrieved by downloading '{IMPORT_JOB_MANIFEST_FILE_NAME}' 
        file from the output S3 bucket.
        """
        )
        q.ask("\t\tPress Enter to continue.")

        image_sets = self.medical_imaging_wrapper.get_image_sets_for_dicom_import_job(
            self.data_store_id, import_job_id
        )

        print("\t\tThe image sets created by this import job are:")
        for image_set in image_sets:
            print("\t\tImage set:", image_set)

        print(
            """\
        If you would like information about how HealthImaging organizes image sets,
        go to the following link.
        https://docs.aws.amazon.com/healthimaging/latest/devguide/understanding-image-sets.html
        """
        )

        q.ask("\t\tPress Enter to continue.")

        print(
            """\
        Next this workflow will download all the image frames created in this import job. 
        The IDs of all the image frames in an image set are stored in the image set metadata.
        The image set metadata will be downloaded and parsed for the image frame IDs.
        """
        )

        q.ask("\t\tPress Enter to continue.")

        out_dir = f"output/import_job_{import_job_id}"
        os.makedirs(out_dir, exist_ok=True)

        all_image_frame_ids = []
        for image_set in image_sets:
            image_frames = self.medical_imaging_wrapper.get_image_frames_for_image_set(
                self.data_store_id, image_set, out_dir
            )

            all_image_frame_ids.extend(image_frames)

        print(
            f"\t\t{len(all_image_frame_ids)} image frames were created by this import job."
        )

        print(
            """\
        The image frames are encoded in the HTJ2K format. This example will convert
        the image frames to bitmaps. The decoded images will be verified using 
        a CRC32 checksum retrieved from the image set metadata.
        The OpenJPEG open-source library will be used for the conversion.  
        The following link contains information about HTJ2K decoding libraries.
        https://docs.aws.amazon.com/healthimaging/latest/devguide/reference-htj2k.html
        """
        )

        q.ask("\t\tPress Enter to download and convert the images.")

        self.medical_imaging_wrapper.download_decode_and_check_image_frames(
            self.data_store_id, all_image_frame_ids, out_dir
        )

        print(
            f"""\
        The image files were successfully decoded and validated.
        The HTJ2K image files are located in the directory
        {out_dir} in the working directory of this example.
        """
        )

        print("\t\tThis concludes this workflow.")
        if q.ask(
            f"\t\tClean up resources created by the workflow? (y/n) ",
            q.is_yesno,
        ):
            self.destroy(cf_stack)
            print("\t\tRemoved resources created by the workflow.")
        print("\t\tThanks for watching!")
        print("-" * 88)

    # snippet-start:[python.example_code.medical-imaging.workflow.copy]
    def copy_single_object(self, key, source_bucket, target_bucket, target_directory):
        """
        Copies a single object from a source to a target bucket.

        :param key: The key of the object to copy.
        :param source_bucket: The source bucket for the copy.
        :param target_bucket: The target bucket for the copy.
        :param target_directory: The target directory for the copy.
        """
        new_key = target_directory + "/" + key
        copy_source = {"Bucket": source_bucket, "Key": key}
        self.s3_client.copy_object(
            CopySource=copy_source, Bucket=target_bucket, Key=new_key
        )
        print(f"\n\t\tCopying {key}.")

    def copy_images(
        self, source_bucket, source_directory, target_bucket, target_directory
    ):
        """
        Copies the images from the source to the target bucket using multiple threads.

        :param source_bucket: The source bucket for the images.
        :param source_directory: Directory within the source bucket.
        :param target_bucket: The target bucket for the images.
        :param target_directory: Directory within the target bucket.
        """

        # Get list of all objects in source bucket.
        list_response = self.s3_client.list_objects_v2(
            Bucket=source_bucket, Prefix=source_directory
        )
        objs = list_response["Contents"]
        keys = [obj["Key"] for obj in objs]

        # Copy the objects in the bucket.
        for key in keys:
            self.copy_single_object(key, source_bucket, target_bucket, target_directory)

        print("\t\tDone copying all objects.")

    # snippet-end:[python.example_code.medical-imaging.workflow.copy]

    # snippet-start:[python.example_code.medical-imaging.workflow.deploy]
    def deploy(self):
        """
        Deploys prerequisite resources used by the scenario. The resources are
        defined in the associated `setup.yaml` AWS CloudFormation script and are deployed
        as a CloudFormation stack, so they can be easily managed and destroyed.
        """

        print("\t\tLet's deploy the stack for resource creation.")
        stack_name = q.ask("\t\tEnter a name for the stack: ", q.non_empty)

        data_store_name = q.ask(
            "\t\tEnter a name for the Health Imaging Data Store: ", q.non_empty
        )

        account_id = boto3.client("sts").get_caller_identity()["Account"]

        with open(
            "../../../../workflows/healthimaging_image_sets/resources/cfn_template.yaml"
        ) as setup_file:
            setup_template = setup_file.read()
        print(f"\t\tCreating {stack_name}.")
        stack = self.cf_resource.create_stack(
            StackName=stack_name,
            TemplateBody=setup_template,
            Capabilities=["CAPABILITY_NAMED_IAM"],
            Parameters=[
                {
                    "ParameterKey": "datastoreName",
                    "ParameterValue": data_store_name,
                },
                {
                    "ParameterKey": "userAccountID",
                    "ParameterValue": account_id,
                },
            ],
        )
        print("\t\tWaiting for stack to deploy. This typically takes a minute or two.")
        waiter = self.cf_resource.meta.client.get_waiter("stack_create_complete")
        waiter.wait(StackName=stack.name)
        stack.load()
        print(f"\t\tStack status: {stack.stack_status}")

        outputs_dictionary = {
            output["OutputKey"]: output["OutputValue"] for output in stack.outputs
        }
        self.input_bucket_name = outputs_dictionary["BucketName"]
        self.output_bucket_name = outputs_dictionary["BucketName"]
        self.role_arn = outputs_dictionary["RoleArn"]
        self.data_store_id = outputs_dictionary["DatastoreID"]
        return stack

    # snippet-end:[python.example_code.medical-imaging.workflow.deploy]

    # snippet-start:[python.example_code.medical-imaging.workflow.destroy]
    def destroy(self, stack):
        """
        Destroys the resources managed by the CloudFormation stack, and the CloudFormation
        stack itself.

        :param stack: The CloudFormation stack that manages the example resources.
        """

        print(f"\t\tCleaning up resources and {stack.name}.")
        data_store_id = None
        for oput in stack.outputs:
            if oput["OutputKey"] == "DatastoreID":
                data_store_id = oput["OutputValue"]
        if data_store_id is not None:
            print(f"\t\tDeleting image sets in data store {data_store_id}.")
            image_sets = self.medical_imaging_wrapper.search_image_sets(
                data_store_id, {}
            )
            image_set_ids = [image_set["imageSetId"] for image_set in image_sets]

            for image_set_id in image_set_ids:
                self.medical_imaging_wrapper.delete_image_set(
                    data_store_id, image_set_id
                )
                print(f"\t\tDeleted image set with id : {image_set_id}")

        print(f"\t\tDeleting {stack.name}.")
        stack.delete()
        print("\t\tWaiting for stack removal. This may take a few minutes.")
        waiter = self.cf_resource.meta.client.get_waiter("stack_delete_complete")
        waiter.wait(StackName=stack.name)
        print("\t\tStack delete complete.")

    # snippet-end:[python.example_code.medical-imaging.workflow.destroy]


if __name__ == "__main__":
    try:
        s3 = boto3.client("s3")
        cf = boto3.resource("cloudformation")
        medical_imaging_wrapper = MedicalImagingWrapper.from_client()

        scenario = MedicalImagingWorkflowScenario(medical_imaging_wrapper, s3, cf)
        scenario.run_scenario()
    except Exception:
        logging.exception("Something went wrong with the workflow.")
# snippet-end:[python.example_code.medical-imaging.workflow]
