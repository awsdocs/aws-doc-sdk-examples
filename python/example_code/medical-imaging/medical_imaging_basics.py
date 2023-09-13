# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) to manage and invoke AWS HealthImaging
functions.
"""

import logging
import time

import boto3

from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


class MedicalImagingWrapper:
    def __init__(self, health_imaging_client):
        self.health_imaging_client = health_imaging_client

    # snippet-start:[python.example_code.medical-imaging.CreateDatastore]
    def create_datastore(self, name):
        """
        Create a data store.

        :param name: The name of the data store to create.
        :return: The data store ID.
        """
        try:
            data_store = self.health_imaging_client.create_datastore(datastoreName=name)
        except ClientError as err:
            logger.error(
                "Couldn't create data store %s. Here's why: %s: %s", name, err.response['Error']['Code'],
                err.response['Error']['Message'])
            raise
        else:
            return data_store['datastoreId']

    # snippet-end:[python.example_code.medical-imaging.CreateDatastore]

    # snippet-start:[python.example_code.medical-imaging.GetDatastore]
    def get_datastore_properties(self, datastore_id):
        """
        Get the properties of a data store.

        :param datastore_id: The ID of the data store to get.
        :return: The data store properties.
        """
        try:
            data_store = self.health_imaging_client.get_datastore(datastoreId=datastore_id)
        except ClientError as err:
            logger.error(
                "Couldn't get data store %s. Here's why: %s: %s", id, err.response['Error']['Code'],
                err.response['Error']['Message'])
            raise
        else:
            return data_store['datastoreProperties']

    # snippet-end:[python.example_code.medical-imaging.GetDatastore]

    # snippet-start:[python.example_code.medical-imaging.ListDatastores]
    def list_datastores(self):
        """
        List the data stores.

        :return: The list of data stores.
        """
        try:
            data_stores = self.health_imaging_client.list_datastores()
        except ClientError as err:
            logger.error(
                "Couldn't list data stores. Here's why: %s: %s", err.response['Error']['Code'],
                err.response['Error']['Message'])
            raise
        else:
            return data_stores['datastoreSummaries']

    # snippet-end:[python.example_code.medical-imaging.ListDatastores]

    # snippet-start:[python.example_code.medical-imaging.DeleteDatastore]
    def delete_datastore(self, datastore_id):
        """
        Delete a data store.

        :param datastore_id: The ID of the data store to delete.
        """
        try:
            self.health_imaging_client.delete_datastore(datastoreId=datastore_id)
        except ClientError as err:
            logger.error(
                "Couldn't delete data store %s. Here's why: %s: %s", datastore_id, err.response['Error']['Code'],
                err.response['Error']['Message'])
            raise

    # snippet-end:[python.example_code.medical-imaging.DeleteDatastore]

    # snippet-start:[python.example_code.medical-imaging.StartDICOMImportJob]
    def start_dicom_import_job(self, job_name, datastore_id, role_arn, input_s3_uri, output_s3_uri):
        """
        Start a DICOM import job.

        :param job_name: The name of the job.
        :param datastore_id: The ID of the data store to import into.
        :param role_arn: The Amazon Resource Name (ARN) of the role to use for the job.
        :param input_s3_uri: The S3 bucket input prefix path containing the DICOM files.
        :param output_s3_uri: The S3 bucket output prefix path for the result.
        :return: The job ID.
        """
        try:
            job = self.health_imaging_client.start_dicom_import_job(
                jobName=job_name,
                datastoreId=datastore_id,
                dataAccessRoleArn=role_arn,
                inputS3Uri=input_s3_uri,
                outputS3Uri=output_s3_uri,
            )
        except ClientError as err:
            logger.error(
                "Couldn't start DICOM import job. Here's why: %s: %s", err.response['Error']['Code'],
                err.response['Error']['Message'])
            raise
        else:
            return job['jobId']

    # snippet-end:[python.example_code.medical-imaging.StartDICOMImportJob]

    # snippet-start:[python.example_code.medical-imaging.GetDICOMImportJob]
    def get_dicom_import_job(self, job_id, datastore_id):
        """
        Get the properties of a DICOM import job.

        :param job_id: The ID of the job.
        :param datastore_id: The ID of the data store the job is importing into.
        :return: The job properties.
        """
        try:
            job = self.health_imaging_client.get_dicom_import_job(jobId=job_id, datastoreId=datastore_id)
        except ClientError as err:
            logger.error(
                "Couldn't get DICOM import job. Here's why: %s: %s", err.response['Error']['Code'],
                err.response['Error']['Message'])
            raise
        else:
            return job['jobProperties']

    # snippet-end:[python.example_code.medical-imaging.GetDICOMImportJob]

    # snippet-start:[python.example_code.medical-imaging.ListDICOMImportJobs]
    def list_dicom_import_jobs(self, datastore_id):
        """
        List the DICOM import jobs.

        :param datastore_id: The ID of the data store the jobs are importing into.
        :return: The list of jobs.
        """
        try:
            jobs = self.health_imaging_client.list_dicom_import_jobs(datastoreId=datastore_id)
        except ClientError as err:
            logger.error(
                "Couldn't list DICOM import jobs. Here's why: %s: %s", err.response['Error']['Code'],
                err.response['Error']['Message'])
            raise
        else:
            return jobs['jobSummaries']

    # snippet-end:[python.example_code.medical-imaging.ListDICOMImportJobs]

    # snippet-start:[python.example_code.medical-imaging.SearchImageSets]
    def search_image_sets(self, datastore_id, search_filter):
        """
        Search for image sets.

        :param datastore_id: The ID of the data store the image sets are stored in.
        :param search_filter: The search filter.
        :return: The list of image sets.
        """
        try:
            image_sets = self.health_imaging_client.search_image_sets(datastoreId=datastore_id, filter=search_filter)
        except ClientError as err:
            logger.error(
                "Couldn't search image sets. Here's why: %s: %s", err.response['Error']['Code'],
                err.response['Error']['Message'])
            raise
        else:
            return image_sets['imageSetSummaries']

    # snippet-end:[python.example_code.medical-imaging.SearchImageSets]

    # snippet-start:[python.example_code.medical-imaging.GetImageSet]
    def get_image_set(self, image_set_id, datastore_id, version):
        """
        Get the properties of an image set.

        :param image_set_id: The ID of the image set.
        :param datastore_id: The ID of the data store the image set is stored in.
        :param version: The version of the image set.
        :return: The image set properties.
        """
        try:
            image_set = self.health_imaging_client.get_image_set(imageSetId=image_set_id, datastoreId=datastore_id,

                                                                 version=version)
        except ClientError as err:
            logger.error(
                "Couldn't get image set. Here's why: %s: %s", err.response['Error']['Code'],
                err.response['Error']['Message'])
            raise
        else:
            return image_set

    # snippet-end:[python.example_code.medical-imaging.GetImageSet]

    # snippet-start:[python.example_code.medical-imaging.GetImageSetMetadata]
    def get_image_set_metadata(self, image_set_id, datastore_id, version=None):
        """
        Get the metadata of an image set.

        :param image_set_id: The ID of the image set.
        :param datastore_id: The ID of the data store the image set is stored in.
        :param version: Optional image set version.

        :return: The image set metadata.
        """
        try:
            image_set = self.health_imaging_client.get_image_set_metadata(imageSetId=image_set_id,
                                                                          datastoreId=datastore_id,
                                                                          version=version)
        except ClientError as err:
            logger.error(
                "Couldn't get image set. Here's why: %s: %s", err.response['Error']['Code'],
                err.response['Error']['Message'])
            raise
        else:
            return image_set

    # snippet-end:[python.example_code.medical-imaging.GetImageSetMetadata]

    # snippet-start:[python.example_code.medical-imaging.GetImageFrame]
    def get_pixel_data(self, image_set_id, image_frame_id, datastore_id):
        """
        Get an image frame's pixel data.

        :param image_set_id: The ID of the image set.
        :param image_frame_id: The ID of the image frame.
        :param datastore_id: The ID of the data store the image set is stored in.
        :return: The pixel data encoded as HTJ2K.
        """
        try:
            image_frame = self.health_imaging_client.get_image_frame(imageSetId=image_set_id,
                                                                     datastoreId=datastore_id,
                                                                     imageFrameInformation={
                                                                         "imageFrameId": image_frame_id})
        except ClientError as err:
            logger.error(
                "Couldn't get image frame. Here's why: %s: %s", err.response['Error']['Code'],
                err.response['Error']['Message'])
            raise
        else:
            return image_frame

    # snippet-end:[python.example_code.medical-imaging.GetImageFrame]

    # snippet-start:[python.example_code.medical-imaging.ListImageSetVersions]
    def list_image_set_versions(self, image_set_id, datastore_id):
        """
        List the image set versions.

        :param image_set_id: The ID of the image set.
        :param datastore_id: The ID of the data store the image set is stored in.
        :return: The list of image set versions.
        """
        try:
            versions = self.health_imaging_client.list_image_set_versions(imageSetId=image_set_id,
                                                                          datastoreId=datastore_id)
        except ClientError as err:
            logger.error(
                "Couldn't list image set versions. Here's why: %s: %s", err.response['Error']['Code'],
                err.response['Error']['Message'])
            raise
        else:
            return versions['imageSetPropertiesList']

    # snippet-end:[python.example_code.medical-imaging.ListImageSetVersions]

    # snippet-start:[python.example_code.medical-imaging.UpdateImageSetMetadata]
    def update_image_set_metadata(self, image_set_id, datastore_id, version_id, metadata):
        """
        Update the metadata of an image set.

        :param image_set_id: The ID of the image set.
        :param datastore_id: The ID of the data store the image set is stored in.
        :param version_id: The ID of the image set version.
        :param metadata: The image set metadata.
        :return: The updated image set metadata.
        """
        try:
            updated_metadata = self.health_imaging_client.update_image_set_metadata(
                imageSetId=image_set_id, datastoreId=datastore_id, latestVersionId=version_id,
                updateImageSetMetadataUpdates=metadata)
        except ClientError as err:
            logger.error(
                "Couldn't update image set metadata. Here's why: %s: %s", err.response['Error']['Code'],
                err.response['Error']['Message'])
            raise
        else:
            return updated_metadata

    # snippet-end:[python.example_code.medical-imaging.UpdateImageSetMetadata]

    # snippet-start:[python.example_code.medical-imaging.CopyImageSet]
    def copy_image_set(self, image_set_id, datastore_id, version_id, destination_image_set_id,
                       destination_version_id):
        """
        Copy an image set.

        :param image_set_id: The ID of the image set.
        :param datastore_id: The ID of the data store the image set is stored in.
        :param version_id: The ID of the image set version.
        :param destination_image_set_id: The ID of the destination image set.
        :param destination_version_id: The ID of the destination image set version.
        :return: The copy results.
        """
        try:
            copy_image_set_information = {"sourceImageSet": {"latestVersionId": version_id}}
            if destination_image_set_id and destination_version_id:
                copy_image_set_information["destinationImageSet"] = {"imageSetId": destination_image_set_id,
                                                                     "latestVersionId": destination_version_id}
            copy_results = self.health_imaging_client.copy_image_set(
                imageSetId=image_set_id,
                datastoreId=datastore_id,
                latestVersionId=version_id,
                copyImageSetInformation=copy_image_set_information)
        except ClientError as err:
            logger.error(
                "Couldn't copy image set. Here's why: %s: %s", err.response['Error']['Code'],
                err.response['Error']['Message'])
            raise
        else:
            return copy_results

    # snippet-end:[python.example_code.medical-imaging.CopyImageSet]

    # snippet-start:[python.example_code.medical-imaging.DeleteImageSet]
    def delete_image_set(self, image_set_id, datastore_id):
        """
        Delete an image set.

        :param image_set_id: The ID of the image set.
        :param datastore_id: The ID of the data store the image set is stored in.
        :return: The delete results.
        """
        try:
            delete_results = self.health_imaging_client.delete_image_set(
                imageSetId=image_set_id, datastoreId=datastore_id)
        except ClientError as err:
            logger.error(
                "Couldn't delete image set. Here's why: %s: %s", err.response['Error']['Code'],
                err.response['Error']['Message'])
            raise
        else:
            return delete_results

    # snippet-end:[python.example_code.medical-imaging.DeleteImageSet]

    # snippet-start:[python.example_code.medical-imaging.TagResource]
    def tag_resource(self, resource_arn, tags):
        """
        Tag a resource.

        :param resource_arn: The ARN of the resource.
        :param tags: The tags to apply.
        :return: The tag results.
        """
        try:
            tag_results = self.health_imaging_client.tag_resource(resourceArn=resource_arn, tags=tags)
        except ClientError as err:
            logger.error(
                "Couldn't tag resource. Here's why: %s: %s", err.response['Error']['Code'],
                err.response['Error']['Message'])
            raise
        else:
            return tag_results

    # snippet-end:[python.example_code.medical-imaging.TagResource]

    # snippet-start:[python.example_code.medical-imaging.UntagResource]
    def untag_resource(self, resource_arn, tag_keys):
        """
        Untag a resource.

        :param resource_arn: The ARN of the resource.
        :param tag_keys: The tag keys to remove.
        """
        try:
            self.health_imaging_client.untag_resource(resourceArn=resource_arn, tagKeys=tag_keys)
        except ClientError as err:
            logger.error(
                "Couldn't untag resource. Here's why: %s: %s", err.response['Error']['Code'],
                err.response['Error']['Message'])
            raise

    # snippet end:[python.example_code.medical-imaging.UntagResource]

    # snippet-start:[python.example_code.medical-imaging.ListTagsForResource]
    def list_tags_for_resource(self, resource_arn):
        """
        List the tags for a resource.

        :param resource_arn: The ARN of the resource.
        :return: The list of tags.
        """
        try:
            tags = self.health_imaging_client.list_tags_for_resource(resourceArn=resource_arn)
        except ClientError as err:
            logger.error(
                "Couldn't list tags for resource. Here's why: %s: %s", err.response['Error']['Code'],
                err.response['Error']['Message'])
            raise
        else:
            return tags
    # snippet-end:[python.example_code.medical-imaging.ListTagsForResource]


if __name__ == '__main__':
    source_s3_uri = "s3://healthimaging-source-37eyet88/CRStudy/"
    dest_s3_uri = "s3://health-imaging-dest-ier9e86w/ouput_cr/"
    data_store_id = "728f13a131f748bf8d87a55d5ef6c5af"
    data_access_role_arn = "arn:aws:iam::123502194722:role/dicom_import"
    job_name = "job_1"

    client = boto3.client('medical-imaging')
    medical_imaging_wrapper = MedicalImagingWrapper(client)

    job_id = medical_imaging_wrapper.start_dicom_import_job(job_name, data_store_id,
                                                                 data_access_role_arn,
                                                                 source_s3_uri, dest_s3_uri)


    while True:
        job = medical_imaging_wrapper.get_dicom_import_job(job_id, data_store_id)
        job_status = job['jobStatus']
        print(f"job : {job}")
        if job_status == "COMPLETED":
            break
        elif job_status == "FAILED":
            raise Exception("DICOM import job failed")
        time.sleep(1)
