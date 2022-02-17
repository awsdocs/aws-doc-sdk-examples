# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Contains common test fixtures used to run unit tests.
"""

import random
import sys
import pytest
# This is needed so Python can find test_tools on the path.
sys.path.append('../..')
from test_tools.fixtures.common import *


@pytest.fixture()
def make_faces():
    def _make_faces(count, has_details=False, is_celebrity=False, is_index=False):
        faces = []
        for _ in range(0, count):
            face_dict = {
                'BoundingBox': {'Left': 0, 'Top': .5, 'Width': .3, 'Height': .7},
                'Confidence': random.randint(0, 100),
                'Landmarks': [{'Type': 'test'}],
                'Pose': {'Roll': random.randint(0, 100)},
                'Quality': {'Brightness': random.randint(0, 100)}}
            if has_details:
                face_dict.update({
                    'AgeRange': {
                        'Low': random.randint(0, 30), 'High': random.randint(30, 100)},
                    'Smile': {'Value': random.choice([True, False])},
                    'Eyeglasses': {'Value': random.choice([True, False])},
                    'Sunglasses': {'Value': random.choice([True, False])},
                    'Gender': {'Value': random.choice(['Male', 'Female'])},
                    'Beard': {'Value': random.choice([True, False])},
                    'Mustache': {'Value': random.choice([True, False])},
                    'EyesOpen': {'Value': random.choice([True, False])},
                    'MouthOpen': {'Value': random.choice([True, False])},
                    'Emotions': [{'Type': emotion, 'Confidence': random.randint(0, 100)}
                                 for emotion in ['BEMUSED', 'CAUTIOUSLY OPTIMISTIC']]})
            if is_index:
                face_dict.update({
                    'FaceId': 'test-face-id',
                    'ImageId': 'test-image-id'
                })
            if is_celebrity:
                celeb = {
                    'Urls': ['http://example.com'],
                    'Name': 'test-name',
                    'Id': 'test-celeb-id',
                    'MatchConfidence': random.randint(0, 100),
                    'Face': face_dict}
                faces.append(celeb)
            else:
                faces.append(face_dict)
        return faces
    return _make_faces


@pytest.fixture()
def make_labels():
    def _make_labels(count):
        return [{
            'Name': 'test-name',
            'Confidence': random.randint(0, 100),
            'Instances': [
                {'BoundingBox': {'Left': 0, 'Top': .5, 'Width': .3, 'Height': .7}}],
            'Parents': [{'Name': 'test-parent'}]
        } for _ in range(0, count)]
    return _make_labels


@pytest.fixture()
def make_persons(make_faces):
    def _make_persons(count):
        return [{
            'Index': random.randint(1000, 10000),
            'BoundingBox': {'Left': 0, 'Top': .5, 'Width': .3, 'Height': .7},
            'Face': make_faces(1, has_details=True)[0]
        } for _ in range(0, count)]
    return _make_persons


@pytest.fixture()
def make_moderation_labels():
    def _make_moderation_labels(count):
        return [{
            'Name': 'test-name',
            'Confidence': random.randint(0, 100),
            'ParentName': 'test-parent'
        } for _ in range(0, count)]
    return _make_moderation_labels


@pytest.fixture()
def make_texts():
    def _make_texts(count):
        return [{
            'DetectedText': 'test-text',
            'Confidence': random.randint(0, 100),
            'Type': random.choice(['WORD', 'LINE']),
            'Id': random.randint(1000, 10000),
            'ParentId': random.randint(1000, 10000),
            'Geometry': {
                'BoundingBox': {'Left': 0, 'Top': .5, 'Width': .3, 'Height': .7},
                'Polygon': [
                    {'X': random.randint(0, 100) / 100,
                     'Y': random.randint(0, 100) / 100},
                    {'X': random.randint(0, 100) / 100,
                     'Y': random.randint(0, 100) / 100},
                    {'X': random.randint(0, 100) / 100,
                     'Y': random.randint(0, 100) / 100}
                ]}
        } for _ in range(0, count)]
    return _make_texts
