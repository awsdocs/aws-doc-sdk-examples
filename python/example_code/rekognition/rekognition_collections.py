# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Rekognition to
create a collection that contains faces indexed from a series of images. The
collection is then searched for faces that match a reference face.

The usage demo in this file uses images in the .media folder. If you run this code
without cloning the GitHub repository, you must first download the image files from
    https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/python/example_code/rekognition/.media
"""

# snippet-start:[python.example_code.rekognition.collection.imports]
import logging
from pprint import pprint
import boto3
from botocore.exceptions import ClientError
from rekognition_objects import RekognitionFace
from rekognition_image_detection import RekognitionImage

logger = logging.getLogger(__name__)

# snippet-end:[python.example_code.rekognition.collection.imports]


# snippet-start:[python.example_code.rekognition.RekognitionCollection]
class RekognitionCollection:
    """
    Encapsulates an Amazon Rekognition collection. This class is a thin wrapper
    around parts of the Boto3 Amazon Rekognition API.
    """
    def __init__(self, collection, rekognition_client):
        """
        Initializes a collection object.

        :param collection: Collection data in the format returned by a call to
                           create_collection.
        :param rekognition_client: A Boto3 Rekognition client.
        """
        self.collection_id = collection['CollectionId']
        self.collection_arn, self.face_count, self.created = self._unpack_collection(
            collection)
        self.rekognition_client = rekognition_client

    @staticmethod
    def _unpack_collection(collection):
        """
        Unpacks optional parts of a collection that can be returned by
        describe_collection.

        :param collection: The collection data.
        :return: A tuple of the data in the collection.
        """
        return (
            collection.get('CollectionArn'),
            collection.get('FaceCount', 0),
            collection.get('CreationTimestamp'))
# snippet-end:[python.example_code.rekognition.RekognitionCollection]

# snippet-start:[python.example_code.rekognition.helper.to_dict]
    def to_dict(self):
        """
        Renders parts of the collection data to a dict.

        :return: The collection data as a dict.
        """
        rendering = {
            'collection_id': self.collection_id,
            'collection_arn': self.collection_arn,
            'face_count': self.face_count,
            'created': self.created
        }
        return rendering
# snippet-end:[python.example_code.rekognition.helper.to_dict]

# snippet-start:[python.example_code.rekognition.DescribeCollection]
    def describe_collection(self):
        """
        Gets data about the collection from the Amazon Rekognition service.

        :return: The collection rendered as a dict.
        """
        try:
            response = self.rekognition_client.describe_collection(
                CollectionId=self.collection_id)
            # Work around capitalization of Arn vs. ARN
            response['CollectionArn'] = response.get('CollectionARN')
            (self.collection_arn, self.face_count,
             self.created) = self._unpack_collection(response)
            logger.info("Got data for collection %s.", self.collection_id)
        except ClientError:
            logger.exception("Couldn't get data for collection %s.", self.collection_id)
            raise
        else:
            return self.to_dict()
# snippet-end:[python.example_code.rekognition.DescribeCollection]

# snippet-start:[python.example_code.rekognition.DeleteCollection]
    def delete_collection(self):
        """
        Deletes the collection.
        """
        try:
            self.rekognition_client.delete_collection(CollectionId=self.collection_id)
            logger.info("Deleted collection %s.", self.collection_id)
            self.collection_id = None
        except ClientError:
            logger.exception("Couldn't delete collection %s.", self.collection_id)
            raise
# snippet-end:[python.example_code.rekognition.DeleteCollection]

# snippet-start:[python.example_code.rekognition.IndexFaces]
    def index_faces(self, image, max_faces):
        """
        Finds faces in the specified image, indexes them, and stores them in the
        collection.

        :param image: The image to index.
        :param max_faces: The maximum number of faces to index.
        :return: A tuple. The first element is a list of indexed faces.
                 The second element is a list of faces that couldn't be indexed.
        """
        try:
            response = self.rekognition_client.index_faces(
                CollectionId=self.collection_id, Image=image.image,
                ExternalImageId=image.image_name, MaxFaces=max_faces,
                DetectionAttributes=['ALL'])
            indexed_faces = [
                RekognitionFace({**face['Face'], **face['FaceDetail']})
                for face in response['FaceRecords']]
            unindexed_faces = [
                RekognitionFace(face['FaceDetail'])
                for face in response['UnindexedFaces']]
            logger.info(
                "Indexed %s faces in %s. Could not index %s faces.", len(indexed_faces),
                image.image_name, len(unindexed_faces))
        except ClientError:
            logger.exception("Couldn't index faces in image %s.", image.image_name)
            raise
        else:
            return indexed_faces, unindexed_faces
# snippet-end:[python.example_code.rekognition.IndexFaces]

# snippet-start:[python.example_code.rekognition.ListFaces]
    def list_faces(self, max_results):
        """
        Lists the faces currently indexed in the collection.

        :param max_results: The maximum number of faces to return.
        :return: The list of faces in the collection.
        """
        try:
            response = self.rekognition_client.list_faces(
                CollectionId=self.collection_id, MaxResults=max_results)
            faces = [RekognitionFace(face) for face in response['Faces']]
            logger.info(
                "Found %s faces in collection %s.", len(faces), self.collection_id)
        except ClientError:
            logger.exception(
                "Couldn't list faces in collection %s.", self.collection_id)
            raise
        else:
            return faces
# snippet-end:[python.example_code.rekognition.ListFaces]

# snippet-start:[python.example_code.rekognition.SearchFacesByImage]
    def search_faces_by_image(self, image, threshold, max_faces):
        """
        Searches for faces in the collection that match the largest face in the
        reference image.

        :param image: The image that contains the reference face to search for.
        :param threshold: The match confidence must be greater than this value
                          for a face to be included in the results.
        :param max_faces: The maximum number of faces to return.
        :return: A tuple. The first element is the face found in the reference image.
                 The second element is the list of matching faces found in the
                 collection.
        """
        try:
            response = self.rekognition_client.search_faces_by_image(
                CollectionId=self.collection_id, Image=image.image,
                FaceMatchThreshold=threshold, MaxFaces=max_faces)
            image_face = RekognitionFace({
                'BoundingBox': response['SearchedFaceBoundingBox'],
                'Confidence': response['SearchedFaceConfidence']
            })
            collection_faces = [
                RekognitionFace(face['Face']) for face in response['FaceMatches']]
            logger.info("Found %s faces in the collection that match the largest "
                        "face in %s.", len(collection_faces), image.image_name)
        except ClientError:
            logger.exception(
                "Couldn't search for faces in %s that match %s.", self.collection_id,
                image.image_name)
            raise
        else:
            return image_face, collection_faces
# snippet-end:[python.example_code.rekognition.SearchFacesByImage]

# snippet-start:[python.example_code.rekognition.SearchFaces]
    def search_faces(self, face_id, threshold, max_faces):
        """
        Searches for faces in the collection that match another face from the
        collection.

        :param face_id: The ID of the face in the collection to search for.
        :param threshold: The match confidence must be greater than this value
                          for a face to be included in the results.
        :param max_faces: The maximum number of faces to return.
        :return: The list of matching faces found in the collection. This list does
                 not contain the face specified by `face_id`.
        """
        try:
            response = self.rekognition_client.search_faces(
                CollectionId=self.collection_id, FaceId=face_id,
                FaceMatchThreshold=threshold, MaxFaces=max_faces)
            faces = [RekognitionFace(face['Face']) for face in response['FaceMatches']]
            logger.info(
                "Found %s faces in %s that match %s.", len(faces), self.collection_id,
                face_id)
        except ClientError:
            logger.exception(
                "Couldn't search for faces in %s that match %s.", self.collection_id,
                face_id)
            raise
        else:
            return faces
# snippet-end:[python.example_code.rekognition.SearchFaces]

# snippet-start:[python.example_code.rekognition.DeleteFaces]
    def delete_faces(self, face_ids):
        """
        Deletes faces from the collection.

        :param face_ids: The list of IDs of faces to delete.
        :return: The list of IDs of faces that were deleted.
        """
        try:
            response = self.rekognition_client.delete_faces(
                CollectionId=self.collection_id, FaceIds=face_ids)
            deleted_ids = response['DeletedFaces']
            logger.info(
                "Deleted %s faces from %s.", len(deleted_ids), self.collection_id)
        except ClientError:
            logger.exception("Couldn't delete faces from %s.", self.collection_id)
            raise
        else:
            return deleted_ids
# snippet-end:[python.example_code.rekognition.DeleteFaces]


# snippet-start:[python.example_code.rekognition.RekognitionCollectionManager]
class RekognitionCollectionManager:
    """
    Encapsulates Amazon Rekognition collection management functions.
    This class is a thin wrapper around parts of the Boto3 Amazon Rekognition API.
    """
    def __init__(self, rekognition_client):
        """
        Initializes the collection manager object.

        :param rekognition_client: A Boto3 Rekognition client.
        """
        self.rekognition_client = rekognition_client
# snippet-end:[python.example_code.rekognition.RekognitionCollectionManager]

# snippet-start:[python.example_code.rekognition.CreateCollection]
    def create_collection(self, collection_id):
        """
        Creates an empty collection.

        :param collection_id: Text that identifies the collection.
        :return: The newly created collection.
        """
        try:
            response = self.rekognition_client.create_collection(
                CollectionId=collection_id)
            response['CollectionId'] = collection_id
            collection = RekognitionCollection(response, self.rekognition_client)
            logger.info("Created collection %s.", collection_id)
        except ClientError:
            logger.exception("Couldn't create collection %s.", collection_id)
            raise
        else:
            return collection
# snippet-end:[python.example_code.rekognition.CreateCollection]

# snippet-start:[python.example_code.rekognition.ListCollections]
    def list_collections(self, max_results):
        """
        Lists collections for the current account.

        :param max_results: The maximum number of collections to return.
        :return: The list of collections for the current account.
        """
        try:
            response = self.rekognition_client.list_collections(MaxResults=max_results)
            collections = [
                RekognitionCollection({'CollectionId': col_id}, self.rekognition_client)
                for col_id in response['CollectionIds']]
        except ClientError:
            logger.exception("Couldn't list collections.")
            raise
        else:
            return collections
# snippet-end:[python.example_code.rekognition.ListCollections]


# snippet-start:[python.example_code.rekognition.Usage_FindFacesInCollection]
def usage_demo():
    print('-'*88)
    print("Welcome to the Amazon Rekognition face collection demo!")
    print('-'*88)

    logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')

    rekognition_client = boto3.client('rekognition')
    images = [
        RekognitionImage.from_file(
            '.media/pexels-agung-pandit-wiguna-1128316.jpg', rekognition_client,
            image_name='sitting'),
        RekognitionImage.from_file(
            '.media/pexels-agung-pandit-wiguna-1128317.jpg', rekognition_client,
            image_name='hopping'),
        RekognitionImage.from_file(
            '.media/pexels-agung-pandit-wiguna-1128318.jpg', rekognition_client,
            image_name='biking')]

    collection_mgr = RekognitionCollectionManager(rekognition_client)
    collection = collection_mgr.create_collection('doc-example-collection-demo')
    print(f"Created collection {collection.collection_id}:")
    pprint(collection.describe_collection())

    print("Indexing faces from three images:")
    for image in images:
        collection.index_faces(image, 10)
    print("Listing faces in collection:")
    faces = collection.list_faces(10)
    for face in faces:
        pprint(face.to_dict())
    input("Press Enter to continue.")

    print(f"Searching for faces in the collection that match the first face in the "
          f"list (Face ID: {faces[0].face_id}.")
    found_faces = collection.search_faces(faces[0].face_id, 80, 10)
    print(f"Found {len(found_faces)} matching faces.")
    for face in found_faces:
        pprint(face.to_dict())
    input("Press Enter to continue.")

    print(f"Searching for faces in the collection that match the largest face in "
          f"{images[0].image_name}.")
    image_face, match_faces = collection.search_faces_by_image(images[0], 80, 10)
    print(f"The largest face in {images[0].image_name} is:")
    pprint(image_face.to_dict())
    print(f"Found {len(match_faces)} matching faces.")
    for face in match_faces:
        pprint(face.to_dict())
    input("Press Enter to continue.")

    collection.delete_collection()
    print('Thanks for watching!')
    print('-'*88)
# snippet-end:[python.example_code.rekognition.Usage_FindFacesInCollection]


if __name__ == '__main__':
    usage_demo()
