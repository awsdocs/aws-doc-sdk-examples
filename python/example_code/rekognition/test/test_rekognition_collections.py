# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Unit tests for rekognition_collections.py.
"""

import datetime
import random
import boto3
from botocore.exceptions import ClientError
import pytest

from rekognition_collections import RekognitionCollectionManager, RekognitionCollection
from rekognition_image_detection import RekognitionImage
from rekognition_objects import RekognitionFace

TEST_IMAGE = {'Bytes': b'just some bytes'}


def make_collection(rekognition_client):
    return RekognitionCollection({
        'CollectionId': 'test-collection-id',
        'CollectionArn': 'arn:aws:rekognition::collection/test-collection-id',
        'FaceCount': random.randint(1, 100),
        'CreationTimestamp': datetime.datetime.now()
    }, rekognition_client)


@pytest.mark.parametrize("error_code", [None, 'TestException'])
def test_describe_collection(make_stubber, error_code):
    rekognition_client = boto3.client('rekognition')
    rekognition_stubber = make_stubber(rekognition_client)
    in_collection = RekognitionCollection(
        {'CollectionId': 'test-collection-id'}, rekognition_client)
    out_collection = make_collection(rekognition_client)

    rekognition_stubber.stub_describe_collection(
        in_collection.collection_id, out_collection, error_code=error_code)

    if error_code is None:
        got_collection_dict = in_collection.describe_collection()
        assert out_collection.to_dict() == got_collection_dict
    else:
        with pytest.raises(ClientError) as exc_info:
            in_collection.describe_collection()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, 'TestException'])
def test_delete_collection(make_stubber, error_code):
    rekognition_client = boto3.client('rekognition')
    rekognition_stubber = make_stubber(rekognition_client)
    collection = make_collection(rekognition_client)

    rekognition_stubber.stub_delete_collection(
        collection.collection_id, error_code=error_code)

    if error_code is None:
        collection.delete_collection()
    else:
        with pytest.raises(ClientError) as exc_info:
            collection.delete_collection()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, 'TestException'])
def test_index_faces(make_stubber, make_faces, error_code):
    rekognition_client = boto3.client('rekognition')
    rekognition_stubber = make_stubber(rekognition_client)
    image = RekognitionImage(TEST_IMAGE, 'test-image', rekognition_client)
    max_faces = 3
    indexed_faces = [
        RekognitionFace(face) for face in
        make_faces(3, has_details=True, is_index=True)]
    unindexed_faces = [RekognitionFace(face) for face in make_faces(4)]
    collection = make_collection(rekognition_client)

    rekognition_stubber.stub_index_faces(
        collection.collection_id, image, max_faces, indexed_faces, unindexed_faces,
        error_code=error_code)

    if error_code is None:
        got_indexed_faces, got_unindexed_faces = collection.index_faces(
            image, max_faces)
        assert (
            [face.to_dict() for face in indexed_faces] ==
            [face.to_dict() for face in got_indexed_faces])
        assert (
            [face.to_dict() for face in unindexed_faces] ==
            [face.to_dict() for face in got_unindexed_faces])
    else:
        with pytest.raises(ClientError) as exc_info:
            collection.index_faces(image, max_faces)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, 'TestException'])
def test_list_faces(make_stubber, error_code):
    rekognition_client = boto3.client('rekognition')
    rekognition_stubber = make_stubber(rekognition_client)
    max_faces = 3
    faces = [
        RekognitionFace({'FaceIndex': f'face-{index}', 'ImageIndex': f'image-{index}'})
        for index in range(0, 3)]
    collection = make_collection(rekognition_client)

    rekognition_stubber.stub_list_faces(
        collection.collection_id, max_faces, faces, error_code=error_code)

    if error_code is None:
        got_faces = collection.list_faces(max_faces)
        assert (
            [face.to_dict() for face in faces] ==
            [face.to_dict() for face in got_faces])
    else:
        with pytest.raises(ClientError) as exc_info:
            collection.list_faces(max_faces)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, 'TestException'])
def test_search_face_by_image(make_stubber, make_faces, error_code):
    rekognition_client = boto3.client('rekognition')
    rekognition_stubber = make_stubber(rekognition_client)
    collection = make_collection(rekognition_client)
    image = RekognitionImage(TEST_IMAGE, 'test-image', rekognition_client)
    threshold = 80
    max_faces = 3
    image_face = RekognitionFace(make_faces(1)[0])
    faces = [
        RekognitionFace({'FaceIndex': f'face-{index}', 'ImageIndex': f'image-{index}'})
        for index in range(0, 3)]

    rekognition_stubber.stub_search_faces_by_image(
        collection.collection_id, image, threshold, max_faces, image_face,
        faces, error_code=error_code)

    if error_code is None:
        got_image_face, got_faces = collection.search_faces_by_image(
            image, threshold, max_faces)
        assert image_face.to_dict() == got_image_face.to_dict()
        assert (
            [face.to_dict() for face in faces] ==
            [face.to_dict() for face in got_faces])
    else:
        with pytest.raises(ClientError) as exc_info:
            collection.search_faces_by_image(image, threshold, max_faces)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, 'TestException'])
def test_search_faces(make_stubber, make_faces, error_code):
    rekognition_client = boto3.client('rekognition')
    rekognition_stubber = make_stubber(rekognition_client)
    collection = make_collection(rekognition_client)
    face_id = 'test-face-id'
    threshold = 80
    max_faces = 3
    faces = [
        RekognitionFace({'FaceIndex': f'face-{index}', 'ImageIndex': f'image-{index}'})
        for index in range(0, 3)]

    rekognition_stubber.stub_search_faces(
        collection.collection_id, face_id, threshold, max_faces, faces,
        error_code=error_code)

    if error_code is None:
        got_faces = collection.search_faces(face_id, threshold, max_faces)
        assert (
            [face.to_dict() for face in faces] ==
            [face.to_dict() for face in got_faces])
    else:
        with pytest.raises(ClientError) as exc_info:
            collection.search_faces(face_id, threshold, max_faces)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, 'TestException'])
def test_delete_faces(make_stubber, error_code):
    rekognition_client = boto3.client('rekognition')
    rekognition_stubber = make_stubber(rekognition_client)
    collection = make_collection(rekognition_client)
    face_ids = [f'test-face-id-{index}' for index in range(0, 3)]

    rekognition_stubber.stub_delete_faces(
        collection.collection_id, face_ids, error_code=error_code)

    if error_code is None:
        got_faces_ids = collection.delete_faces(face_ids)
        assert got_faces_ids == face_ids
    else:
        with pytest.raises(ClientError) as exc_info:
            collection.delete_faces(face_ids)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, 'TestException'])
def test_create_collection(make_stubber, error_code):
    rekognition_client = boto3.client('rekognition')
    rekognition_stubber = make_stubber(rekognition_client)
    collection_mgr = RekognitionCollectionManager(rekognition_client)
    collection = RekognitionCollection({
        'CollectionId': 'test-collection-id',
        'CollectionArn': 'arn:aws:rekognition::collection/test-collection-id'},
        rekognition_client)

    rekognition_stubber.stub_create_collection(
        collection.collection_id, collection, error_code=error_code)

    if error_code is None:
        got_collection = collection_mgr.create_collection(collection.collection_id)
        assert collection.to_dict() == got_collection.to_dict()
    else:
        with pytest.raises(ClientError) as exc_info:
            collection_mgr.create_collection(collection.collection_id)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, 'TestException'])
def test_list_collections(make_stubber, error_code):
    rekognition_client = boto3.client('rekognition')
    rekognition_stubber = make_stubber(rekognition_client)
    collection_mgr = RekognitionCollectionManager(rekognition_client)
    max_results = 3
    collections = [RekognitionCollection(
        {'CollectionId': f'test-collection-id-{index}'}, rekognition_client)
        for index in range(0, 3)]

    rekognition_stubber.stub_list_collections(
        max_results, [col.collection_id for col in collections], error_code=error_code)

    if error_code is None:
        got_collections = collection_mgr.list_collections(max_results)
        assert [col.to_dict() for col in collections] == \
               [col.to_dict() for col in got_collections]
    else:
        with pytest.raises(ClientError) as exc_info:
            collection_mgr.list_collections(max_results)
        assert exc_info.value.response['Error']['Code'] == error_code
