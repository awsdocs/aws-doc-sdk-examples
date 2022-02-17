# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Simple Email Service
(Amazon SES) and Amazon Route 53 to copy email and domain identity configuration
from one AWS Region to another.
"""

# snippet-start:[ses.python.ses_replicateidentities.complete]
import argparse
import json
import logging
from pprint import pprint
import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


def get_identities(ses_client):
    """
    Gets the identities for the current Region. The Region is specified in the
    Boto3 Amazon SES client object.

    :param ses_client: A Boto3 Amazon SES client.
    :return: The list of email identities and the list of domain identities.
    """
    email_identities = []
    domain_identities = []
    try:
        identity_paginator = ses_client.get_paginator('list_identities')
        identity_iterator = identity_paginator.paginate(
            PaginationConfig={'PageSize': 20})
        for identity_page in identity_iterator:
            for identity in identity_page['Identities']:
                if '@' in identity:
                    email_identities.append(identity)
                else:
                    domain_identities.append(identity)
        logger.info(
            "Found %s email and %s domain identities.", len(email_identities),
            len(domain_identities))
    except ClientError:
        logger.exception("Couldn't get identities.")
        raise
    else:
        return email_identities, domain_identities


def verify_emails(email_list, ses_client):
    """
    Starts verification of a list of email addresses. Verification causes an email
    to be sent to each address. To complete verification, the recipient must follow
    the instructions in the email.

    :param email_list: The list of email addresses to verify.
    :param ses_client: A Boto3 Amazon SES client.
    :return: The list of emails that were successfully submitted for verification.
    """
    verified_emails = []
    for email in email_list:
        try:
            ses_client.verify_email_identity(EmailAddress=email)
            verified_emails.append(email)
            logger.info("Started verification of %s.", email)
        except ClientError:
            logger.warning("Couldn't start verification of %s.", email)
    return verified_emails


def verify_domains(domain_list, ses_client):
    """
    Starts verification for a list of domain identities. This returns a token for
    each domain, which must be registered as a TXT record with the DNS provider for
    the domain.

    :param domain_list: The list of domains to verify.
    :param ses_client: A Boto3 Amazon SES client.
    :return: The generated domain tokens to use to completed verification.
    """
    domain_tokens = {}
    for domain in domain_list:
        try:
            response = ses_client.verify_domain_identity(Domain=domain)
            token = response['VerificationToken']
            domain_tokens[domain] = token
            logger.info("Got verification token %s for domain %s.", token, domain)
        except ClientError:
            logger.warning("Couldn't get verification token for domain %s.", domain)
    return domain_tokens


def get_hosted_zones(route53_client):
    """
    Gets the Amazon Route 53 hosted zones for the current account.

    :param route53_client: A Boto3 Route 53 client.
    :return: The list of hosted zones.
    """
    zones = []
    try:
        zone_paginator = route53_client.get_paginator('list_hosted_zones')
        zone_iterator = zone_paginator.paginate(PaginationConfig={'PageSize': 20})
        zones = [
            zone for zone_page in zone_iterator for zone in zone_page['HostedZones']]
        logger.info("Found %s hosted zones.", len(zones))
    except ClientError:
        logger.warning("Couldn't get hosted zones.")
    return zones


def find_domain_zone_matches(domains, zones):
    """
    Finds matches between Amazon SES verified domains and Route 53 hosted zones.
    Subdomain matches are taken when found, otherwise root domain matches are taken.

    :param domains: The list of domains to match.
    :param zones: The list of hosted zones to match.
    :return: The set of matched domain-zone pairs. When a match is not found, the
             domain is included in the set with a zone value of None.
    """
    domain_zones = {}
    for domain in domains:
        domain_zones[domain] = None
        # Start at the most specific sub-domain and walk up to the root domain until a
        # zone match is found.
        domain_split = domain.split('.')
        for index in range(0, len(domain_split) - 1):
            sub_domain = '.'.join(domain_split[index:])
            for zone in zones:
                # Normalize the zone name from Route 53 by removing the trailing '.'.
                zone_name = zone['Name'][:-1]
                if sub_domain == zone_name:
                    domain_zones[domain] = zone
                    break
            if domain_zones[domain] is not None:
                break
    return domain_zones


def add_route53_verification_record(domain, token, zone, route53_client):
    """
    Adds a domain verification TXT record to the specified Route 53 hosted zone.
    When a TXT record already exists in the hosted zone for the specified domain,
    the existing values are preserved and the new token is added to the list.

    :param domain: The domain to add.
    :param token: The verification token for the domain.
    :param zone: The hosted zone where the domain verification record is added.
    :param route53_client: A Boto3 Route 53 client.
    """
    domain_token_record_set_name = f'_amazonses.{domain}'
    record_set_paginator = route53_client.get_paginator(
        'list_resource_record_sets')
    record_set_iterator = record_set_paginator.paginate(
       HostedZoneId=zone['Id'], PaginationConfig={'PageSize': 20})
    records = []
    for record_set_page in record_set_iterator:
        try:
            txt_record_set = next(
                record_set for record_set
                in record_set_page['ResourceRecordSets']
                if record_set['Name'][:-1] == domain_token_record_set_name and
                record_set['Type'] == 'TXT')
            records = txt_record_set['ResourceRecords']
            logger.info(
                "Existing TXT record found in set %s for zone %s.",
                domain_token_record_set_name, zone['Name'])
            break
        except StopIteration:
            pass
    records.append({'Value': json.dumps(token)})
    changes = [{
        'Action': 'UPSERT',
        'ResourceRecordSet': {
           'Name': domain_token_record_set_name,
           'Type': 'TXT',
           'TTL': 1800,
           'ResourceRecords': records}}]
    try:
        route53_client.change_resource_record_sets(
          HostedZoneId=zone['Id'], ChangeBatch={'Changes': changes})
        logger.info(
            "Created or updated the TXT record in set %s for zone %s.",
            domain_token_record_set_name, zone['Name'])
    except ClientError as err:
        logger.warning(
            "Got error %s. Couldn't create or update the TXT record for zone %s.",
            err.response['Error']['Code'], zone['Name'])


def generate_dkim_tokens(domain, ses_client):
    """
    Generates DKIM tokens for a domain. These must be added as CNAME records to the
    DNS provider for the domain.

    :param domain: The domain to generate tokens for.
    :param ses_client: A Boto3 Amazon SES client.
    :return: The list of generated DKIM tokens.
    """
    dkim_tokens = []
    try:
        dkim_tokens = ses_client.verify_domain_dkim(Domain=domain)['DkimTokens']
        logger.info("Generated %s DKIM tokens for domain %s.", len(dkim_tokens), domain)
    except ClientError:
        logger.warning("Couldn't generate DKIM tokens for domain %s.", domain)
    return dkim_tokens


def add_dkim_domain_tokens(hosted_zone, domain, tokens, route53_client):
    """
    Adds DKIM domain token CNAME records to a Route 53 hosted zone.

    :param hosted_zone: The hosted zone where the records are added.
    :param domain: The domain to add.
    :param tokens: The DKIM tokens for the domain to add.
    :param route53_client: A Boto3 Route 53 client.
    """
    try:
        changes = [{
            'Action': 'UPSERT',
            'ResourceRecordSet': {
                'Name': f'{token}._domainkey.{domain}',
                'Type': 'CNAME',
                'TTL': 1800,
                'ResourceRecords': [{'Value': f'{token}.dkim.amazonses.com'}]
            }} for token in tokens]
        route53_client.change_resource_record_sets(
            HostedZoneId=hosted_zone['Id'], ChangeBatch={'Changes': changes})
        logger.info(
            "Added %s DKIM CNAME records to %s in zone %s.", len(tokens),
            domain, hosted_zone['Name'])
    except ClientError:
        logger.warning(
            "Couldn't add DKIM CNAME records for %s to zone %s.", domain,
            hosted_zone['Name'])


def configure_sns_topics(identity, topics, ses_client):
    """
    Configures Amazon Simple Notification Service (Amazon SNS) notifications for
    an identity. The Amazon SNS topics must already exist.

    :param identity: The identity to configure.
    :param topics: The list of topics to configure. The choices are Bounce, Delivery,
                   or Complaint.
    :param ses_client: A Boto3 Amazon SES client.
    """
    for topic in topics:
        topic_arn = input(
            f"Enter the Amazon Resource Name (ARN) of the {topic} topic or press "
            f"Enter to skip: ")
        if topic_arn != '':
            try:
                ses_client.set_identity_notification_topic(
                    Identity=identity, NotificationType=topic, SnsTopic=topic_arn)
                logger.info("Configured %s for %s notifications.", identity, topic)
            except ClientError:
                logger.warning(
                    "Couldn't configure %s for %s notifications.", identity, topic)


def replicate(source_client, destination_client, route53_client):
    logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')

    print('-'*88)
    print(f"Replicating Amazon SES identities and other configuration from "
          f"{source_client.meta.region_name} to {destination_client.meta.region_name}.")
    print('-'*88)

    print(f"Retrieving identities from {source_client.meta.region_name}.")
    source_emails, source_domains = get_identities(source_client)
    print("Email addresses found:")
    print(*source_emails)
    print("Domains found:")
    print(*source_domains)

    print("Starting verification for email identities.")
    dest_emails = verify_emails(source_emails, destination_client)
    print("Getting domain tokens for domain identities.")
    dest_domain_tokens = verify_domains(source_domains, destination_client)

    # Get Route 53 hosted zones and match them with Amazon SES domains.
    answer = input(
        "Is the DNS configuration for your domains managed by Amazon Route 53 (y/n)? ")
    use_route53 = answer.lower() == 'y'
    hosted_zones = get_hosted_zones(route53_client) if use_route53 else []
    if use_route53:
        print("Adding or updating Route 53 TXT records for your domains.")
        domain_zones = find_domain_zone_matches(dest_domain_tokens.keys(), hosted_zones)
        for domain in domain_zones:
            add_route53_verification_record(
                domain, dest_domain_tokens[domain], domain_zones[domain],
                route53_client)
    else:
        print("Use these verification tokens to create TXT records through your DNS "
              "provider:")
        pprint(dest_domain_tokens)

    answer = input("Do you want to configure DKIM signing for your identities (y/n)? ")
    if answer.lower() == 'y':
        # Build a set of unique domains from email and domain identities.
        domains = {email.split('@')[1] for email in dest_emails}
        domains.update(dest_domain_tokens)
        domain_zones = find_domain_zone_matches(domains, hosted_zones)
        for domain, zone in domain_zones.items():
            answer = input(
                f"Do you want to configure DKIM signing for {domain} (y/n)? ")
            if answer.lower() == 'y':
                dkim_tokens = generate_dkim_tokens(domain, destination_client)
                if use_route53 and zone is not None:
                    add_dkim_domain_tokens(zone, domain, dkim_tokens, route53_client)
                else:
                    print(
                        "Add the following DKIM tokens as CNAME records through your "
                        "DNS provider:")
                    print(*dkim_tokens, sep='\n')

    answer = input(
        "Do you want to configure Amazon SNS notifications for your identities (y/n)? ")
    if answer.lower() == 'y':
        for identity in dest_emails + list(dest_domain_tokens.keys()):
            answer = input(
                f"Do you want to configure Amazon SNS topics for {identity} (y/n)? ")
            if answer.lower() == 'y':
                configure_sns_topics(
                    identity, ['Bounce', 'Delivery', 'Complaint'], destination_client)

    print(f"Replication complete for {destination_client.meta.region_name}.")
    print('-'*88)


def main():
    boto3_session = boto3.Session()
    ses_regions = boto3_session.get_available_regions('ses')
    parser = argparse.ArgumentParser(
        description="Copies email address and domain identities from one AWS Region to "
                    "another. Optionally adds records for domain verification and DKIM "
                    "signing to domains that are managed by Amazon Route 53, "
                    "and sets up Amazon SNS notifications for events of interest.")
    parser.add_argument(
        'source_region', choices=ses_regions, help="The region to copy from.")
    parser.add_argument(
        'destination_region', choices=ses_regions, help="The region to copy to.")
    args = parser.parse_args()
    source_client = boto3.client('ses', region_name=args.source_region)
    destination_client = boto3.client('ses', region_name=args.destination_region)
    route53_client = boto3.client('route53')
    replicate(source_client, destination_client, route53_client)


if __name__ == '__main__':
    main()
# snippet-end:[ses.python.ses_replicateidentities.complete]
