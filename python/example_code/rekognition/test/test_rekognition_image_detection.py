# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Unit tests for rekognition_image_detection.py.
"""

import boto3
from botocore.exceptions import ClientError
import pytest

from rekognition_image_detection import RekognitionImage
from rekognition_objects import (
    RekognitionFace, RekognitionCelebrity, RekognitionLabel,
    RekognitionModerationLabel, RekognitionText)

TEST_IMAGE = {'Bytes': b'just some bytes'}


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_detect_faces(make_stubber, make_faces, error_code):
    rekognition_client = boto3.client('rekognition')
    rekognition_stubber = make_stubber(rekognition_client)
    image = RekognitionImage(TEST_IMAGE, 'test-image', rekognition_client)
    faces = [RekognitionFace(face) for face in make_faces(3, True)]

    rekognition_stubber.stub_detect_faces(image.image, faces, error_code=error_code)

    if error_code is None:
        got_faces = image.detect_faces()
        assert (
            [face.to_dict() for face in faces] ==
            [face.to_dict() for face in got_faces]
        )
    else:
        with pytest.raises(ClientError) as exc_info:
            image.detect_faces()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_compare_faces(make_stubber, make_faces, error_code):
    rekognition_client = boto3.client('rekognition')
    rekognition_stubber = make_stubber(rekognition_client)
    source_image = RekognitionImage(TEST_IMAGE, 'source-image', rekognition_client)
    target_image = RekognitionImage(TEST_IMAGE, 'target-image', rekognition_client)
    matches = [RekognitionFace(face) for face in make_faces(1)]
    unmatches = [RekognitionFace(face) for face in make_faces(2)]
    similarity = 80

    rekognition_stubber.stub_compare_faces(
        source_image.image, target_image.image, similarity, matches, unmatches,
        error_code=error_code)

    if error_code is None:
        got_matches, got_unmatches = source_image.compare_faces(
            target_image, similarity)
        assert (
            [face.to_dict() for face in matches] ==
            [face.to_dict() for face in got_matches]
        )
        assert (
            [face.to_dict() for face in unmatches] ==
            [face.to_dict() for face in got_unmatches]
        )
    else:
        with pytest.raises(ClientError) as exc_info:
            source_image.compare_faces(target_image, similarity)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_detect_labels(make_stubber, make_labels, error_code):
    rekognition_client = boto3.client('rekognition')
    rekognition_stubber = make_stubber(rekognition_client)
    image = RekognitionImage(TEST_IMAGE, 'test-image', rekognition_client)
    labels = [RekognitionLabel(label) for label in make_labels(3)]
    max_labels = 3

    rekognition_stubber.stub_detect_labels(
        image.image, max_labels, labels, error_code=error_code)

    if error_code is None:
        got_labels = image.detect_labels(max_labels)
        assert (
            [label.to_dict() for label in labels] ==
            [label.to_dict() for label in got_labels])
    else:
        with pytest.raises(ClientError) as exc_info:
            image.detect_labels(max_labels)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_detect_moderation_labels(make_stubber, make_moderation_labels, error_code):
    rekognition_client = boto3.client('rekognition')
    rekognition_stubber = make_stubber(rekognition_client)
    image = RekognitionImage(TEST_IMAGE, 'test-image', rekognition_client)
    labels = [
        RekognitionModerationLabel(label) for label in make_moderation_labels(3)]

    rekognition_stubber.stub_detect_moderation_labels(
        image.image, labels, error_code=error_code)

    if error_code is None:
        got_labels = image.detect_moderation_labels()
        assert (
            [label.to_dict() for label in labels] ==
            [label.to_dict() for label in got_labels])
    else:
        with pytest.raises(ClientError) as exc_info:
            image.detect_moderation_labels()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_detect_text(make_stubber, make_texts, error_code):
    rekognition_client = boto3.client('rekognition')
    rekognition_stubber = make_stubber(rekognition_client)
    image = RekognitionImage(TEST_IMAGE, 'test-image', rekognition_client)
    texts = [RekognitionText(text) for text in make_texts(3)]

    rekognition_stubber.stub_detect_text(image.image, texts, error_code=error_code)

    if error_code is None:
        got_texts = image.detect_text()
        assert (
            [text.to_dict() for text in texts] ==
            [text.to_dict() for text in got_texts])
    else:
        with pytest.raises(ClientError) as exc_info:
            image.detect_text()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_recognize_celebrities(make_stubber, make_faces, error_code):
    rekognition_client = boto3.client('rekognition')
    rekognition_stubber = make_stubber(rekognition_client)
    image = RekognitionImage(TEST_IMAGE, 'test-image', rekognition_client)
    celebrities = [RekognitionCelebrity(face)
                   for face in make_faces(3, is_celebrity=True)]
    normals = [RekognitionFace(face) for face in make_faces(2)]

    rekognition_stubber.stub_recognize_celebrities(
        image.image, celebrities, normals, error_code=error_code)

    if error_code is None:
        got_celebrities, got_normals = image.recognize_celebrities()
        assert (
            [celeb.to_dict() for celeb in celebrities] ==
            [celeb.to_dict() for celeb in got_celebrities])
        assert (
            [normal.to_dict() for normal in normals] ==
            [normal.to_dict() for normal in got_normals])
    else:
        with pytest.raises(ClientError) as exc_info:
            image.recognize_celebrities()
        assert exc_info.value.response['Error']['Code'] == error_code
