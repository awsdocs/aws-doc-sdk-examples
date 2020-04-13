# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Tests for the Amazon S3 file transfer demo.

These tests use monkeypatch instead of the botocore Stubber, because the transfer
manager hooks into the request system at a level lower than the Stubber, so
the Stubber caused an unwanted short circuit that bypassed some of the transfer logic.
"""

import hashlib
import os
import shutil
import threading
import pytest

from botocore.exceptions import ClientError

import file_transfer

_demo_sse_key = hashlib.sha256('demo_passphrase'.encode('utf-8')).digest()

def make_mock_upload(expect_filename, expect_bucket, expect_key,
                     expect_extra_args=None, expect_config=None):
    """Make a mock upload function that asserts against expected arguments and
    calls the Callback with the upload file size."""
    def mock_upload_file(Filename=None, Bucket=None, Key=None, Callback=None,
                         ExtraArgs=None, Config=None):
        assert Filename == expect_filename
        assert Bucket == expect_bucket
        assert expect_key == Key
        assert expect_extra_args == ExtraArgs
        if expect_config or Config:
            for key, val in expect_config.items():
                assert getattr(Config, key) == val
        Callback(os.path.getsize(Filename))
    return mock_upload_file


def make_mock_download(expect_filename, expect_bucket, expect_key,
                     expect_extra_args=None, expect_config=None):
    """Make a mock download function that asserts against expected arguments and
    calls the Callback with the download file size."""
    def mock_download_file(Filename=None, Bucket=None, Key=None, Callback=None,
                           ExtraArgs=None, Config=None):
        assert Filename == expect_filename
        assert Bucket == expect_bucket
        assert expect_key == Key
        assert expect_extra_args == ExtraArgs
        if expect_config or Config:
            for key, val in expect_config.items():
                assert getattr(Config, key) == val
        shutil.copyfile(__file__, Filename)
        Callback(os.path.getsize(Filename))
    return mock_download_file


def test_transfer_callback():
    """Test the class that receives progress callbacks."""
    thread = threading.current_thread()
    callback = file_transfer.TransferCallback(100)
    callback(10)
    for key, value in callback.thread_info.items():
        assert key == thread.ident
        assert value == 10
    callback(50)
    for key, value in callback.thread_info.items():
        assert key == thread.ident
        assert value == 60


@pytest.mark.parametrize("upload_func,upload_kwargs,expected_upload_kwargs,"
                         "download_func,download_kwargs,expected_download_kwargs", [
    (
        file_transfer.upload_with_default_configuration, {}, {},
        file_transfer.download_with_default_configuration, {}, {}
    ), (
        file_transfer.upload_with_chunksize_and_meta,
        {'metadata': {'favorite_color': 'aqua'}},
        {
            'expect_extra_args': {'Metadata': {'favorite_color': 'aqua'}},
            'expect_config': {'multipart_chunksize': 1 * file_transfer.MB}
        },
        file_transfer.download_with_single_thread,
        {},
        {'expect_config': {'use_threads': False}}
    ), (
            file_transfer.upload_with_high_threshold,
            {},
            {'expect_config': {
                'multipart_threshold': os.path.getsize(__file__) * 2
            }},
            file_transfer.download_with_high_threshold,
            {},
            {'expect_config': {
                'multipart_threshold': os.path.getsize(__file__) * 2
            }}
    ), (
            file_transfer.upload_with_sse,
            {'sse_key': _demo_sse_key},
            {'expect_extra_args': {
                'SSECustomerAlgorithm': 'AES256',
                'SSECustomerKey': _demo_sse_key}
            },
            file_transfer.download_with_sse,
            {'sse_key': _demo_sse_key},
            {'expect_extra_args': {
                'SSECustomerAlgorithm': 'AES256',
                'SSECustomerKey': _demo_sse_key}
            },
    )]
)
def test_upload_download_mega_test(
        use_real_aws, make_unique_name, make_bucket, monkeypatch,
        upload_func, upload_kwargs, expected_upload_kwargs,
        download_func, download_kwargs, expected_download_kwargs
):
    """
    Test upload and download scenarios with various arguments and configurations.

    :param use_real_aws: Indicates the tests run against AWS services and not mocks.
    :param make_unique_name: Makes a unique name with a specified prefix.
    :param make_bucket: Makes a test bucket that is emptied and deleted after the
                        test completes.
    :param monkeypatch: Pytest monkeypatch object.
    :param upload_func: The upload function to test.
    :param upload_kwargs: Arguments passed to the upload function under test.
    :param expected_upload_kwargs: Arguments expected to be received by the mocked
                                   upload function.
    :param download_func: The download function to test.
    :param download_kwargs: Arguments passed to the download function under test.
    :param expected_download_kwargs: Arguments expected to be received by the mocked
                                     download function.
    """
    bucket_name = make_unique_name('bucket')
    object_key = make_unique_name('object')
    download_file = f'{object_key}.txt'
    file_size = os.path.getsize(__file__)

    if use_real_aws:
        make_bucket(file_transfer.s3, bucket_name)
    else:
        monkeypatch.setattr(
            file_transfer.s3.meta.client, 'upload_file',
            make_mock_upload(
                __file__, bucket_name, object_key, **expected_upload_kwargs
            ))
        monkeypatch.setattr(
            file_transfer.s3.meta.client, 'download_file',
            make_mock_download(
                download_file, bucket_name, object_key, **expected_download_kwargs
            ))

    upload_thread_info = upload_func(
        __file__, bucket_name, object_key, file_size/file_transfer.MB,
        **upload_kwargs)
    download_thread_info = download_func(
        bucket_name, object_key, download_file, file_size/file_transfer.MB,
        **download_kwargs)

    assert sum(upload_thread_info.values()) == file_size
    if use_real_aws:
        if upload_func == file_transfer.upload_with_sse:
            # If SSE is used, the SSE algorithm and key must be included.
            with pytest.raises(ClientError) as exc_info:
                file_transfer.s3.meta.client.head_object(
                    Bucket=bucket_name, Key=object_key)
            assert exc_info.value.response['Error']['Code'] == '400'
            file_transfer.s3.meta.client.head_object(
                Bucket=bucket_name, Key=object_key,
                SSECustomerAlgorithm='AES256',
                SSECustomerKey=_demo_sse_key)
        else:
            file_transfer.s3.meta.client.head_object(Bucket=bucket_name, Key=object_key)

    assert sum(download_thread_info.values()) == file_size
    assert os.path.exists(download_file)
    os.remove(download_file)
