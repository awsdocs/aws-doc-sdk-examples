# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for revise_stanza.py functions.
"""

import pytest

import revise_stanza


@pytest.mark.parametrize("revision", ['lower', 'upper', 'reverse', 'delete'])
def test_revise_stanza(make_stubber, make_unique_name, make_event, make_result,
                       revision):
    s3_stubber = make_stubber(revise_stanza.s3.meta.client)
    bucket_name = make_unique_name('bucket')
    obj_key = make_unique_name('object')
    obj_data = 'Test data!'

    event = make_event(bucket_name, obj_key, revision)

    revised_data = None
    if revision == 'lower':
        revised_data = obj_data.lower()
    elif revision == 'upper':
        revised_data = obj_data.upper()
    elif revision == 'reverse':
        revised_data = obj_data[::-1]

    s3_stubber.stub_get_object(bucket_name, obj_key,
                               object_data=bytes(obj_data, 'utf-8'))
    if revision == 'delete':
        s3_stubber.stub_delete_object(bucket_name, obj_key)
    else:
        s3_stubber.stub_put_object(bucket_name, obj_key,
                                   body=bytes(revised_data, 'utf-8'))

    result = revise_stanza.lambda_handler(event, None)
    assert result == make_result('Succeeded')


def test_revise_stanza_object_not_exist(make_stubber, make_event, make_result):
    s3_stubber = make_stubber(revise_stanza.s3.meta.client)

    event = make_event('test-bucket', 'test-object', 'lower')

    s3_stubber.stub_get_object('test-bucket', 'test-object', error_code='NoSuchKey')

    result = revise_stanza.lambda_handler(event, None)
    assert result == make_result('Succeeded')


def test_revise_stanza_not_authorized(make_stubber, make_event, make_result):
    s3_stubber = make_stubber(revise_stanza.s3.meta.client)

    event = make_event('test-bucket', 'test-object', 'lower')

    s3_stubber.stub_get_object('test-bucket', 'test-object', error_code='403')

    result = revise_stanza.lambda_handler(event, None)
    assert result == make_result('PermanentFailure')


def test_revise_stanza_bad_revision(make_stubber, make_event):
    s3_stubber = make_stubber(revise_stanza.s3.meta.client)

    event = make_event('test-bucket', 'test-object', 'garbage')

    s3_stubber.stub_get_object('test-bucket', 'test-object', object_data=b'Test data')

    with pytest.raises(TypeError):
        revise_stanza.lambda_handler(event, None)
