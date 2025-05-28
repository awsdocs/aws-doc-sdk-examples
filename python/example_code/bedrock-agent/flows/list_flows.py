# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Amazon Bedrock Flow lists

Shows how to list Amazon Bedrock flows, flow versions in a flow,
and flow aliases in a flow.

"""

import logging
import boto3

from botocore.exceptions import ClientError


from flow import list_flows
from flow_version import list_flow_versions
from flow_alias import  list_flow_aliases


def main():
    """
    List the Amazon Bedrock flows, flow versions in a flow,
    and flow aliases in a flow. The call to List_flows shows
    the IDs that you can enter for the flow ID.

    Note:
        Requires valid AWS credentials in the default profile.
    """

    try:

        session = boto3.Session(profile_name='default')
        bedrock_agent_client = session.client('bedrock-agent')

        print("Listing flows")
        list_flows(bedrock_agent_client)
        flow_id = input("Enter Flow ID: ")
        print(f"Listing flow versions for flow {flow_id}")
        list_flow_versions(bedrock_agent_client, flow_id)
        print(f"Listing flow aliases for flow {flow_id}")
        list_flow_aliases(bedrock_agent_client, flow_id)

    except ClientError as e:
        logging.exception("Client error running example: %s", str(e))
   
    except Exception as e:
        print(f"Fatal error: {str(e)}")
    
    finally:
        print ("Done")
 
if __name__ == "__main__":
    main()

