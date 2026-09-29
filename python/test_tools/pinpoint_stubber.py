# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon Pinpoint unit tests.

When tests are run against an actual AWS account, the stubber class does not
set up stubs and passes all calls through to the Boto 3 client.
"""

from test_tools.example_stubber import ExampleStubber


class PinpointStubber(ExampleStubber):
    """
    A class that implements a variety of stub functions that are used by the
    Amazon Pinpoint unit tests.

    The stubbed functions all expect certain parameters to be passed to them as
    part of the tests, and will raise errors when the actual parameters differ from
    the expected.
    """

    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto 3 Pinpoint client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_create_app(self, name):
        self.add_response(
            "create_app",
            expected_params={"CreateApplicationRequest": {"Name": name}},
            service_response={
                "ApplicationResponse": {
                    "Arn": "arn:aws:mobiletargeting:us-west-2:111122223333:apps/d41d8cd98f00b204e9800998ecf8427e",
                    "Id": "d41d8cd98f00b204e9800998ecf8427e",
                    "Name": name,
                }
            },
        )

    def stub_create_app_error(self, name, error_code):
        self.add_client_error(
            "create_app",
            expected_params={"CreateApplicationRequest": {"Name": name}},
            service_error_code=error_code,
        )

    def stub_get_apps(self, apps, page_size=25, error_code=None):
        expected_params = {"PageSize": str(page_size)}
        response = {"ApplicationsResponse": {"Item": apps}}
        self._stub_bifurcator(
            "get_apps", expected_params, response, error_code=error_code
        )

    def stub_get_apps_error(self, error_code, page_size=25):
        self.add_client_error(
            "get_apps",
            expected_params={"PageSize": str(page_size)},
            service_error_code=error_code,
        )

    def stub_update_email_channel(
        self, app_id, from_address, identity_arn, enabled=True, error_code=None
    ):
        expected_params = {
            "ApplicationId": app_id,
            "EmailChannelRequest": {
                "FromAddress": from_address,
                "Identity": identity_arn,
                "Enabled": enabled,
            },
        }
        response = {
            "EmailChannelResponse": {
                "ApplicationId": app_id,
                "FromAddress": from_address,
                "Identity": identity_arn,
                "Enabled": enabled,
                "Platform": "EMAIL",
            }
        }
        self._stub_bifurcator(
            "update_email_channel", expected_params, response, error_code=error_code
        )

    def stub_create_segment(self, app_id, segment_name, segment_id, error_code=None):
        expected_params = {
            "ApplicationId": app_id,
            "WriteSegmentRequest": {
                "Name": segment_name,
                "Dimensions": {
                    "Demographic": {
                        "Channel": {
                            "DimensionType": "INCLUSIVE",
                            "Values": ["EMAIL"],
                        }
                    }
                },
            },
        }
        response = {
            "SegmentResponse": {
                "Id": segment_id,
                "Name": segment_name,
                "ApplicationId": app_id,
                "SegmentType": "DIMENSIONAL",
                "CreationDate": "2024-01-01T00:00:00.000Z",
                "Arn": f"arn:aws:mobiletargeting:us-west-2:111122223333:apps/{app_id}/segments/{segment_id}",
            }
        }
        self._stub_bifurcator(
            "create_segment", expected_params, response, error_code=error_code
        )

    def stub_create_campaign(
        self,
        app_id,
        campaign_name,
        segment_id,
        from_address,
        subject,
        html_body,
        text_body,
        campaign_id,
        error_code=None,
    ):
        expected_params = {
            "ApplicationId": app_id,
            "WriteCampaignRequest": {
                "Name": campaign_name,
                "SegmentId": segment_id,
                "MessageConfiguration": {
                    "EmailMessage": {
                        "Title": subject,
                        "Body": text_body,
                        "HtmlBody": html_body,
                        "FromAddress": from_address,
                    }
                },
                "Schedule": {"StartTime": "IMMEDIATE"},
            },
        }
        response = {
            "CampaignResponse": {
                "Id": campaign_id,
                "Name": campaign_name,
                "ApplicationId": app_id,
                "SegmentId": segment_id,
                "SegmentVersion": 1,
                "CreationDate": "2024-01-01T00:00:00.000Z",
                "LastModifiedDate": "2024-01-01T00:00:00.000Z",
                "State": {"CampaignStatus": "SCHEDULED"},
                "Arn": f"arn:aws:mobiletargeting:us-west-2:111122223333:apps/{app_id}/campaigns/{campaign_id}",
            }
        }
        self._stub_bifurcator(
            "create_campaign", expected_params, response, error_code=error_code
        )

    def stub_get_campaign(
        self, app_id, campaign_id, segment_id="seg-456", error_code=None
    ):
        expected_params = {"ApplicationId": app_id, "CampaignId": campaign_id}
        response = {
            "CampaignResponse": {
                "Id": campaign_id,
                "Name": "WelcomeCampaign",
                "ApplicationId": app_id,
                "SegmentId": segment_id,
                "SegmentVersion": 1,
                "CreationDate": "2024-01-01T00:00:00.000Z",
                "LastModifiedDate": "2024-01-01T00:00:00.000Z",
                "State": {"CampaignStatus": "COMPLETED"},
                "Arn": f"arn:aws:mobiletargeting:us-west-2:111122223333:apps/{app_id}/campaigns/{campaign_id}",
            }
        }
        self._stub_bifurcator(
            "get_campaign", expected_params, response, error_code=error_code
        )

    def stub_get_campaign_activities(
        self, app_id, campaign_id, activities, error_code=None
    ):
        expected_params = {"ApplicationId": app_id, "CampaignId": campaign_id}
        response = {"ActivitiesResponse": {"Item": activities}}
        self._stub_bifurcator(
            "get_campaign_activities",
            expected_params,
            response,
            error_code=error_code,
        )

    def stub_delete_campaign(self, app_id, campaign_id, error_code=None):
        expected_params = {"ApplicationId": app_id, "CampaignId": campaign_id}
        response = {
            "CampaignResponse": {
                "Id": campaign_id,
                "Name": "WelcomeCampaign",
                "ApplicationId": app_id,
                "SegmentId": "seg-456",
                "SegmentVersion": 1,
                "CreationDate": "2024-01-01T00:00:00.000Z",
                "LastModifiedDate": "2024-01-01T00:00:00.000Z",
                "Arn": f"arn:aws:mobiletargeting:us-west-2:111122223333:apps/{app_id}/campaigns/{campaign_id}",
            }
        }
        self._stub_bifurcator(
            "delete_campaign", expected_params, response, error_code=error_code
        )

    def stub_delete_segment(self, app_id, segment_id, error_code=None):
        expected_params = {"ApplicationId": app_id, "SegmentId": segment_id}
        response = {
            "SegmentResponse": {
                "Id": segment_id,
                "Name": "EmailSubscribers",
                "ApplicationId": app_id,
                "SegmentType": "DIMENSIONAL",
                "CreationDate": "2024-01-01T00:00:00.000Z",
                "Arn": f"arn:aws:mobiletargeting:us-west-2:111122223333:apps/{app_id}/segments/{segment_id}",
            }
        }
        self._stub_bifurcator(
            "delete_segment", expected_params, response, error_code=error_code
        )

    def stub_delete_app(self, app):
        self.add_response(
            "delete_app",
            expected_params={"ApplicationId": app["Id"]},
            service_response={"ApplicationResponse": app},
        )

    def stub_delete_app_error(self, app, error_code):
        self.add_client_error(
            "delete_app",
            expected_params={"ApplicationId": app["Id"]},
            service_error_code=error_code,
        )

    def stub_send_email_messages(
        self,
        app_id,
        sender,
        to_addresses,
        char_set,
        subject,
        html_message,
        text_message,
        message_ids,
        error_code=None,
    ):
        expected_params = {
            "ApplicationId": app_id,
            "MessageRequest": {
                "Addresses": {
                    to_address: {"ChannelType": "EMAIL"} for to_address in to_addresses
                },
                "MessageConfiguration": {
                    "EmailMessage": {
                        "FromAddress": sender,
                        "SimpleEmail": {
                            "Subject": {"Charset": char_set, "Data": subject},
                            "HtmlPart": {"Charset": char_set, "Data": html_message},
                            "TextPart": {"Charset": char_set, "Data": text_message},
                        },
                    }
                },
            },
        }
        response = {
            "MessageResponse": {
                "ApplicationId": app_id,
                "Result": {
                    to_address: {
                        "MessageId": message_id,
                        "DeliveryStatus": "SUCCESSFUL",
                        "StatusCode": 200,
                    }
                    for to_address, message_id in zip(to_addresses, message_ids)
                },
            }
        }
        self._stub_bifurcator(
            "send_messages", expected_params, response, error_code=error_code
        )

    def stub_send_templated_email_messages(
        self,
        app_id,
        sender,
        to_addresses,
        template_name,
        template_version,
        message_ids,
        error_code=None,
    ):
        expected_params = {
            "ApplicationId": app_id,
            "MessageRequest": {
                "Addresses": {
                    to_address: {"ChannelType": "EMAIL"} for to_address in to_addresses
                },
                "MessageConfiguration": {"EmailMessage": {"FromAddress": sender}},
                "TemplateConfiguration": {
                    "EmailTemplate": {
                        "Name": template_name,
                        "Version": template_version,
                    }
                },
            },
        }
        response = {
            "MessageResponse": {
                "ApplicationId": app_id,
                "Result": {
                    to_address: {
                        "MessageId": message_id,
                        "DeliveryStatus": "SUCCESSFUL",
                        "StatusCode": 200,
                    }
                    for to_address, message_id in zip(to_addresses, message_ids)
                },
            }
        }
        self._stub_bifurcator(
            "send_messages", expected_params, response, error_code=error_code
        )

    def stub_send_sms_message(
        self,
        app_id,
        origination_number,
        destination_number,
        message,
        message_type,
        message_id,
        error_code=None,
    ):
        expected_params = {
            "ApplicationId": app_id,
            "MessageRequest": {
                "Addresses": {destination_number: {"ChannelType": "SMS"}},
                "MessageConfiguration": {
                    "SMSMessage": {
                        "Body": message,
                        "MessageType": message_type,
                        "OriginationNumber": origination_number,
                    }
                },
            },
        }
        response = {
            "MessageResponse": {
                "ApplicationId": app_id,
                "Result": {
                    destination_number: {
                        "DeliveryStatus": "SUCCESSFUL",
                        "StatusCode": 200,
                        "MessageId": message_id,
                    }
                },
            }
        }
        self._stub_bifurcator(
            "send_messages", expected_params, response, error_code=error_code
        )

    def stub_send_templated_sms_message(
        self,
        app_id,
        origination_number,
        destination_number,
        message_type,
        template_name,
        template_version,
        message_id,
        error_code=None,
    ):
        expected_params = {
            "ApplicationId": app_id,
            "MessageRequest": {
                "Addresses": {destination_number: {"ChannelType": "SMS"}},
                "MessageConfiguration": {
                    "SMSMessage": {
                        "MessageType": message_type,
                        "OriginationNumber": origination_number,
                    }
                },
                "TemplateConfiguration": {
                    "SMSTemplate": {"Name": template_name, "Version": template_version}
                },
            },
        }
        response = {
            "MessageResponse": {
                "ApplicationId": app_id,
                "Result": {
                    destination_number: {
                        "DeliveryStatus": "SUCCESSFUL",
                        "StatusCode": 200,
                        "MessageId": message_id,
                    }
                },
            }
        }
        self._stub_bifurcator(
            "send_messages", expected_params, response, error_code=error_code
        )
