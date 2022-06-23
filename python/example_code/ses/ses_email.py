# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Simple Email Service
(Amazon SES) to send email.
"""

import json
import logging
import smtplib
import ssl
import boto3
from botocore.exceptions import ClientError, WaiterError
from ses_identities import SesIdentity
from ses_templates import SesTemplate
from ses_generate_smtp_credentials import calculate_key

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.ses.SesDestination]
class SesDestination:
    """Contains data about an email destination."""
    def __init__(self, tos, ccs=None, bccs=None):
        """
        :param tos: The list of recipients on the 'To:' line.
        :param ccs: The list of recipients on the 'CC:' line.
        :param bccs: The list of recipients on the 'BCC:' line.
        """
        self.tos = tos
        self.ccs = ccs
        self.bccs = bccs

    def to_service_format(self):
        """
        :return: The destination data in the format expected by Amazon SES.
        """
        svc_format = {'ToAddresses': self.tos}
        if self.ccs is not None:
            svc_format['CcAddresses'] = self.ccs
        if self.bccs is not None:
            svc_format['BccAddresses'] = self.bccs
        return svc_format
# snippet-end:[python.example_code.ses.SesDestination]


# snippet-start:[python.example_code.ses.SesMailSender]
class SesMailSender:
    """Encapsulates functions to send emails with Amazon SES."""
    def __init__(self, ses_client):
        """
        :param ses_client: A Boto3 Amazon SES client.
        """
        self.ses_client = ses_client
# snippet-end:[python.example_code.ses.SesMailSender]

# snippet-start:[python.example_code.ses.SendEmail]
    def send_email(self, source, destination, subject, text, html, reply_tos=None):
        """
        Sends an email.

        Note: If your account is in the Amazon SES  sandbox, the source and
        destination email accounts must both be verified.

        :param source: The source email account.
        :param destination: The destination email account.
        :param subject: The subject of the email.
        :param text: The plain text version of the body of the email.
        :param html: The HTML version of the body of the email.
        :param reply_tos: Email accounts that will receive a reply if the recipient
                          replies to the message.
        :return: The ID of the message, assigned by Amazon SES.
        """
        send_args = {
            'Source': source,
            'Destination': destination.to_service_format(),
            'Message': {
                'Subject': {'Data': subject},
                'Body': {'Text': {'Data': text}, 'Html': {'Data': html}}}}
        if reply_tos is not None:
            send_args['ReplyToAddresses'] = reply_tos
        try:
            response = self.ses_client.send_email(**send_args)
            message_id = response['MessageId']
            logger.info(
                "Sent mail %s from %s to %s.", message_id, source, destination.tos)
        except ClientError:
            logger.exception(
                "Couldn't send mail from %s to %s.", source, destination.tos)
            raise
        else:
            return message_id
# snippet-end:[python.example_code.ses.SendEmail]

# snippet-start:[python.example_code.ses.SendTemplatedEmail]
    def send_templated_email(
            self, source, destination, template_name, template_data,
            reply_tos=None):
        """
        Sends an email based on a template. A template contains replaceable tags
        each enclosed in two curly braces, such as {{name}}. The template data passed
        in this function contains key-value pairs that define the values to insert
        in place of the template tags.

        Note: If your account is in the Amazon SES  sandbox, the source and
        destination email accounts must both be verified.

        :param source: The source email account.
        :param destination: The destination email account.
        :param template_name: The name of a previously created template.
        :param template_data: JSON-formatted key-value pairs of replacement values
                              that are inserted in the template before it is sent.
        :return: The ID of the message, assigned by Amazon SES.
        """
        send_args = {
            'Source': source,
            'Destination': destination.to_service_format(),
            'Template': template_name,
            'TemplateData': json.dumps(template_data)
        }
        if reply_tos is not None:
            send_args['ReplyToAddresses'] = reply_tos
        try:
            response = self.ses_client.send_templated_email(**send_args)
            message_id = response['MessageId']
            logger.info(
                "Sent templated mail %s from %s to %s.", message_id, source,
                destination.tos)
        except ClientError:
            logger.exception(
                "Couldn't send templated mail from %s to %s.", source, destination.tos)
            raise
        else:
            return message_id
# snippet-end:[python.example_code.ses.SendTemplatedEmail]


# snippet-start:[python.example_code.ses.Scenario_SendEmail]
def usage_demo():
    print('-'*88)
    print("Welcome to the Amazon Simple Email Service (Amazon SES) email demo!")
    print('-'*88)

    logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')

    ses_client = boto3.client('ses')
    ses_identity = SesIdentity(ses_client)
    ses_mail_sender = SesMailSender(ses_client)
    ses_template = SesTemplate(ses_client)
    email = input(
        "Enter an email address to send mail with Amazon SES: ")
    status = ses_identity.get_identity_status(email)
    verified = status == 'Success'
    if not verified:
        answer = input(
            f"The address '{email}' is not verified with Amazon SES. Unless your "
            f"Amazon SES account is out of sandbox, you can send mail only from "
            f"and to verified accounts. Do you want to verify this account for use "
            f"with Amazon SES? If yes, the address will receive a verification "
            f"email (y/n): ")
        if answer.lower() == 'y':
            ses_identity.verify_email_identity(email)
            print(f"Follow the steps in the email to {email} to complete verification.")
            print("Waiting for verification...")
            try:
                ses_identity.wait_until_identity_exists(email)
                print(f"Identity verified for {email}.")
                verified = True
            except WaiterError:
                print(f"Verification timeout exceeded. You must complete the "
                      f"steps in the email sent to {email} to verify the address.")

    if verified:
        test_message_text = "Hello from the Amazon SES mail demo!"
        test_message_html = "<p>Hello!</p><p>From the <b>Amazon SES</b> mail demo!</p>"

        print(f"Sending mail from {email} to {email}.")
        ses_mail_sender.send_email(
            email, SesDestination([email]), "Amazon SES demo",
            test_message_text, test_message_html)
        input("Mail sent. Check your inbox and press Enter to continue.")

        template = {
            'name': 'doc-example-template',
            'subject': 'Example of an email template.',
            'text': "This is what {{name}} will {{action}} if {{name}} can't display "
                    "HTML.",
            'html': "<p><i>This</i> is what {{name}} will {{action}} if {{name}} "
                    "<b>can</b> display HTML.</p>"}
        print("Creating a template and sending a templated email.")
        ses_template.create_template(**template)
        template_data = {'name': email.split('@')[0], 'action': 'read'}
        if ses_template.verify_tags(template_data):
            ses_mail_sender.send_templated_email(
                email, SesDestination([email]), ses_template.name(), template_data)
            input("Mail sent. Check your inbox and press Enter to continue.")

        print("Sending mail through the Amazon SES SMTP server.")
        boto3_session = boto3.Session()
        region = boto3_session.region_name
        credentials = boto3_session.get_credentials()
        port = 587
        smtp_server = f'email-smtp.{region}.amazonaws.com'
        password = calculate_key(credentials.secret_key, region)
        message = """
Subject: Hi there

This message is sent from the Amazon SES SMTP mail demo."""
        context = ssl.create_default_context()
        with smtplib.SMTP(smtp_server, port) as server:
            server.starttls(context=context)
            server.login(credentials.access_key, password)
            server.sendmail(email, email, message)
        print("Mail sent. Check your inbox!")

    if ses_template.template is not None:
        print("Deleting demo template.")
        ses_template.delete_template()
    if verified:
        answer = input(f"Do you want to remove {email} from Amazon SES (y/n)? ")
        if answer.lower() == 'y':
            ses_identity.delete_identity(email)
    print("Thanks for watching!")
    print('-'*88)
# snippet-end:[python.example_code.ses.Scenario_SendEmail]


if __name__ == '__main__':
    usage_demo()
