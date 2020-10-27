# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Unit tests for rekognition_video_detection.py.
"""

import json
from unittest.mock import MagicMock
import uuid
import time
import boto3
from botocore.exceptions import ClientError
from botocore.stub import ANY
import pytest

from rekognition_video_detection import RekognitionVideo
from rekognition_objects import (
    RekognitionFace, RekognitionCelebrity, RekognitionLabel,
    RekognitionModerationLabel, RekognitionPerson)


def mock_video(monkeypatch, poll_status, rekognition_client):
    video_name = 'test-video'
    video = RekognitionVideo(
        {'S3Object': {'Bucket': 'doc-example-bucket', 'Name': video_name}},
        video_name, rekognition_client)
    video.role = MagicMock(arn='arn:aws:iam:::role/test-role-arn')
    video.topic = MagicMock(arn='arn:aws:sns:::test-topic-arn')

    monkeypatch.setattr(video, 'poll_notification', lambda jid: poll_status)
    return video

@pytest.mark.parametrize('error_code,stop_on_method', [
    (None, None),
    ('TestException', 'stub_create_topic'),
    ('TestException', 'stub_create_queue'),
    ('TestException', 'stub_get_queue_attributes'),
    ('TestException', 'stub_set_queue_attributes'),
    ('TestException', 'stub_subscribe'),
    ('TestException', 'stub_create_role'),
    ('TestException', 'stub_create_policy'),
    ('TestException', 'stub_attach_role_policy'),
])
def test_create_notification_channel(
        make_stubber, stub_runner, error_code, stop_on_method):
    rekognition_client = boto3.client('rekognition')
    make_stubber(rekognition_client)
    iam_resource = boto3.resource('iam')
    iam_stubber = make_stubber(iam_resource.meta.client)
    sns_resource = boto3.resource('sns')
    sns_stubber = make_stubber(sns_resource.meta.client)
    sqs_resource = boto3.resource('sqs')
    sqs_stubber = make_stubber(sqs_resource.meta.client)
    resource_name = 'test-resource'
    topic_arn = f'arn:aws:sns:::{resource_name}'
    queue_url = f'https://sqs.us-west-2.amazonaws.com/123456789012/{resource_name}'
    queue_arn = f'arn:aws:sqs:::{resource_name}'
    subscription_arn = f'{topic_arn}:{uuid.uuid4()}'
    policy_arn = f'arn:aws:iam:::policy/{resource_name}'

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(sns_stubber.stub_create_topic, resource_name, topic_arn)
        runner.add(sqs_stubber.stub_create_queue, resource_name, ANY, queue_url)
        runner.add(sqs_stubber.stub_get_queue_attributes, queue_url, queue_arn)
        runner.add(sqs_stubber.stub_set_queue_attributes, queue_url, {'Policy': ANY})
        runner.add(
            sns_stubber.stub_subscribe, topic_arn, 'sqs', queue_arn, subscription_arn)
        runner.add(iam_stubber.stub_create_role, resource_name)
        runner.add(iam_stubber.stub_create_policy, resource_name, policy_arn)
        runner.add(iam_stubber.stub_attach_role_policy, resource_name, policy_arn)

    video = RekognitionVideo({
        'S3Object': {'Bucket': 'doc-example-bucket', 'Name': 'doc-example-key'}},
        'Test Video',
        rekognition_client)

    if error_code is None:
        video.create_notification_channel(
            resource_name, iam_resource, sns_resource, sqs_resource)
        assert topic_arn == video.topic.arn
        assert queue_url == video.queue.url
        assert resource_name == video.role.name
    else:
        with pytest.raises(ClientError) as exc_info:
            video.create_notification_channel(
                resource_name, iam_resource, sns_resource, sqs_resource)
        assert exc_info.value.response['Error']['Code'] == error_code


def test_get_notification_channel():
    video = RekognitionVideo(None, None, None)
    video.role = MagicMock(arn='arn:aws:iam:::role/test-role-arn')
    video.topic = MagicMock(arn='arn:aws:sns:::test-topic-arn')

    channel = video.get_notification_channel()
    assert channel['RoleArn'] == video.role.arn
    assert channel['SNSTopicArn'] == video.topic.arn


@pytest.mark.parametrize('error_code,stop_on_method', [
    (None, None),
    ('TestException', 'stub_list_attached_role_policies'),
    ('TestException', 'stub_detach_role_policy'),
    ('TestException', 'stub_delete_policy'),
    ('TestException', 'stub_delete_role'),
    ('TestException', 'stub_delete_queue'),
    ('TestException', 'stub_delete_topic'),
])
def test_delete_notification_channel(
            make_stubber, stub_runner, error_code, stop_on_method):
    iam_resource = boto3.resource('iam')
    iam_stubber = make_stubber(iam_resource.meta.client)
    sns_resource = boto3.resource('sns')
    sns_stubber = make_stubber(sns_resource.meta.client)
    sqs_resource = boto3.resource('sqs')
    sqs_stubber = make_stubber(sqs_resource.meta.client)
    resource_name = 'test-resource'
    topic_arn = f'arn:aws:sns:::{resource_name}'
    queue_url = f'https://sqs.us-west-2.amazonaws.com/123456789012/{resource_name}'
    policy_arn = f'arn:aws:iam:::policy/{resource_name}'
    policies = {resource_name: policy_arn}

    video = RekognitionVideo(None, None, None)
    video.role = iam_resource.Role(resource_name)
    video.queue = sqs_resource.Queue(queue_url)
    video.topic = sns_resource.Topic(topic_arn)

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(
            iam_stubber.stub_list_attached_role_policies, resource_name, policies)
        runner.add(iam_stubber.stub_detach_role_policy, resource_name, policy_arn)
        runner.add(iam_stubber.stub_delete_policy, policy_arn)
        runner.add(iam_stubber.stub_delete_role, resource_name)
        runner.add(sqs_stubber.stub_delete_queue, queue_url)
        runner.add(sns_stubber.stub_delete_topic, topic_arn)

    if error_code is None:
        video.delete_notification_channel()
    else:
        with pytest.raises(ClientError) as exc_info:
            video.delete_notification_channel()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code,stop_on_method', [
    (None, None),
    ('TestException', 'stub_receive_messages'),
    ('TestException', 'stub_delete_message')])
def test_poll_notification(make_stubber, stub_runner, error_code, stop_on_method):
    sqs_resource = boto3.resource('sqs')
    sqs_stubber = make_stubber(sqs_resource.meta.client)
    queue_url = 'https://sqs.us-west-2.amazonaws.com/123456789012/test-queue'
    job_id = 'test-job-id'
    status = 'TESTING'
    message = {'body': json.dumps({
        'Message': json.dumps({'JobId': job_id, 'Status': status})})}
    message_count = 1

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(
            sqs_stubber.stub_receive_messages,
            queue_url, [message], message_count, message_attributes=None)
        runner.add(
            sqs_stubber.stub_delete_message,
            queue_url, MagicMock(receipt_handle='Receipt-0'))

    video = RekognitionVideo(None, None, None)
    video.queue = sqs_resource.Queue(queue_url)

    if error_code is None:
        got_status = video.poll_notification(job_id)
        assert got_status == status
    else:
        with pytest.raises(ClientError) as exc_info:
            video.poll_notification(job_id)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('poll_status,error_code,stop_on_method', [
    ('SUCCEEDED', None, None),
    ('FAILED', None, None),
    ('SUCCEEDED', 'TestException', 'stub_start_detection'),
    ('SUCCEEDED', 'TestException', 'stub_get_label_detection'),
])
def test_do_label_detection(
        make_stubber, stub_runner, make_labels, monkeypatch, poll_status, error_code,
        stop_on_method):
    rekognition_client = boto3.client('rekognition')
    rekognition_stubber = make_stubber(rekognition_client)
    job_id = 'test-job-id'
    job_status = 'TESTING'
    labels = [RekognitionLabel(label, time.time_ns()) for label in make_labels(3)]
    video = mock_video(monkeypatch, poll_status, rekognition_client)

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(
            rekognition_stubber.stub_start_detection, 'start_label_detection',
            video.video, video.get_notification_channel(), job_id)
        if poll_status == 'SUCCEEDED':
            runner.add(
                rekognition_stubber.stub_get_label_detection, job_id, job_status,
                labels)

    if error_code is None:
        got_labels = video.do_label_detection()
        if poll_status == 'SUCCEEDED':
            assert (
                [label.to_dict() for label in labels] ==
                [label.to_dict() for label in got_labels]
            )
        else:
            assert got_labels == []
    else:
        with pytest.raises(ClientError) as exc_info:
            video.do_label_detection()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code,stop_on_method', [
    (None, None),
    ('TestException', 'stub_start_detection'),
    ('TestException', 'stub_get_face_detection'),
])
def test_do_face_detection(
        make_stubber, stub_runner, make_faces, monkeypatch, error_code,
        stop_on_method):
    rekognition_client = boto3.client('rekognition')
    rekognition_stubber = make_stubber(rekognition_client)
    job_id = 'test-job-id'
    job_status = 'TESTING'
    video = mock_video(monkeypatch, 'SUCCEEDED', rekognition_client)
    faces = [RekognitionFace(face, time.time_ns()) for face in make_faces(3)]

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(
            rekognition_stubber.stub_start_detection, 'start_face_detection',
            video.video, video.get_notification_channel(), job_id)
        runner.add(
            rekognition_stubber.stub_get_face_detection, job_id, job_status,
            faces)

    if error_code is None:
        got_faces = video.do_face_detection()
        assert (
            [face.to_dict() for face in faces] ==
            [face.to_dict() for face in got_faces]
        )
    else:
        with pytest.raises(ClientError) as exc_info:
            video.do_face_detection()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code,stop_on_method', [
    (None, None),
    ('TestException', 'stub_start_detection'),
    ('TestException', 'stub_get_person_tracking'),
])
def test_do_person_tracking(
        make_stubber, stub_runner, make_persons, monkeypatch, error_code,
        stop_on_method):
    rekognition_client = boto3.client('rekognition')
    rekognition_stubber = make_stubber(rekognition_client)
    job_id = 'test-job-id'
    job_status = 'TESTING'
    video = mock_video(monkeypatch, 'SUCCEEDED', rekognition_client)
    persons = [RekognitionPerson(person, time.time_ns())
               for person in make_persons(3)]

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(
            rekognition_stubber.stub_start_detection, 'start_person_tracking',
            video.video, video.get_notification_channel(), job_id)
        runner.add(
            rekognition_stubber.stub_get_person_tracking, job_id, job_status,
            persons)

    if error_code is None:
        got_persons = video.do_person_tracking()
        assert (
            [person.to_dict() for person in persons] ==
            [person.to_dict() for person in got_persons]
        )
    else:
        with pytest.raises(ClientError) as exc_info:
            video.do_person_tracking()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code,stop_on_method', [
    (None, None),
    ('TestException', 'stub_start_detection'),
    ('TestException', 'stub_get_celebrity_recognition'),
])
def test_do_celebrity_recognition(
        make_stubber, stub_runner, make_faces, monkeypatch, error_code,
        stop_on_method):
    rekognition_client = boto3.client('rekognition')
    rekognition_stubber = make_stubber(rekognition_client)
    job_id = 'test-job-id'
    job_status = 'TESTING'
    video = mock_video(monkeypatch, 'SUCCEEDED', rekognition_client)
    celebrities = [RekognitionCelebrity(celebrity, time.time_ns())
               for celebrity in make_faces(3, is_celebrity=True)]

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(
            rekognition_stubber.stub_start_detection, 'start_celebrity_recognition',
            video.video, video.get_notification_channel(), job_id)
        runner.add(
            rekognition_stubber.stub_get_celebrity_recognition, job_id, job_status,
            celebrities)

    if error_code is None:
        got_celebrities = video.do_celebrity_recognition()
        assert (
            [celebrity.to_dict() for celebrity in celebrities] ==
            [celebrity.to_dict() for celebrity in got_celebrities]
        )
    else:
        with pytest.raises(ClientError) as exc_info:
            video.do_celebrity_recognition()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code,stop_on_method', [
    (None, None),
    ('TestException', 'stub_start_detection'),
    ('TestException', 'stub_get_content_moderation'),
])
def test_do_content_moderation(
        make_stubber, stub_runner, make_moderation_labels, monkeypatch, error_code,
        stop_on_method):
    rekognition_client = boto3.client('rekognition')
    rekognition_stubber = make_stubber(rekognition_client)
    job_id = 'test-job-id'
    job_status = 'TESTING'
    video = mock_video(monkeypatch, 'SUCCEEDED', rekognition_client)
    labels = [RekognitionModerationLabel(label, time.time_ns())
                   for label in make_moderation_labels(3)]

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(
            rekognition_stubber.stub_start_detection, 'start_content_moderation',
            video.video, video.get_notification_channel(), job_id)
        runner.add(
            rekognition_stubber.stub_get_content_moderation, job_id, job_status,
            labels)

    if error_code is None:
        got_labels = video.do_content_moderation()
        assert (
            [label.to_dict() for label in labels] ==
            [label.to_dict() for label in got_labels]
        )
    else:
        with pytest.raises(ClientError) as exc_info:
            video.do_content_moderation()
        assert exc_info.value.response['Error']['Code'] == error_code
