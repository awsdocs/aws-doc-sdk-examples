---
skip: true
prompt: Generate the rust implementation of this workflow.
---

<file name="newsletter.py">

```python
import boto3
from botocore.exceptions import ClientError
from time import sleep

# Constants
CONTACT_LIST_NAME = "weekly-coupons-newsletter"
TEMPLATE_NAME = "weekly-coupons"

INTRO = """
Welcome to the Amazon SES v2 Coupon Newsletter Workflow!

This workflow will help you:
1. Prepare a verified email identity and contact list for your newsletter.
2. Gather subscriber email addresses and send them a welcome email.
3. Send a weekly coupon newsletter to your subscribers using email templates.
4. Monitor your sending activity and metrics in the AWS console.

Let's get started!
"""


# Helper functions
def load_file_content(file_path):
    """
    Loads the content of a file.

    Args:
        file_path (str): The path to the file.

    Returns:
        str: The content of the file.
    """
    with open(file_path, "r") as file:
        content = file.read()
    return content


def print_error(error):
    """
    Prints the error message to the console.

    Args:
        error (Exception): The exception object.
    """
    print(f"Error: {error}")


def get_subaddress_variants(base_email, num_variants):
    """
    Generates subaddress variants of a base email address.

    Args:
        base_email (str): The base email address.
        num_variants (int): The number of variants to generate.

    Returns:
        list: A list of subaddress variants.
    """
    user_part, domain_part = base_email.split("@")
    variants = [
        f"{user_part}+ses-weekly-newsletter-{i}@{domain_part}"
        for i in range(1, num_variants + 1)
    ]
    return variants


class SESv2Workflow:
    """
    A class to manage the SES v2 Coupon Newsletter Workflow.
    """

    def __init__(self, ses_client, sleep=True):
        self.ses_client = ses_client
        self.sleep = sleep

    def prepare_application(self):
        """
        Prepares the application by creating an email identity and a contact list.
        """
        # Get the verified email address from the user
        self.verified_email = input("Enter the verified email address: ")

        # Create the email identity
        try:
            self.ses_client.create_email_identity(EmailIdentity=self.verified_email)
            print(f"Email identity '{self.verified_email}' created successfully.")
        except ClientError as e:
            # If the email identity already exists, skip and proceed
            if e.response["Error"]["Code"] == "AlreadyExistsException":
                print(f"Email identity '{self.verified_email}' already exists.")
            else:
                raise e

        # Create the contact list
        try:
            self.ses_client.create_contact_list(ContactListName=CONTACT_LIST_NAME)
            print(f"Contact list '{CONTACT_LIST_NAME}' created successfully.")
        except ClientError as e:
            # If the contact list already exists, skip and proceed
            if e.response["Error"]["Code"] == "AlreadyExistsException":
                print(f"Contact list '{CONTACT_LIST_NAME}' already exists.")
            else:
                raise e

        # Create the email template
        try:
            template_content = {
                "Subject": "Weekly Coupons Newsletter",
                "Html": load_file_content("coupon-newsletter.html"),
                "Text": load_file_content("coupon-newsletter.txt"),
            }
            self.ses_client.create_email_template(
                TemplateName=TEMPLATE_NAME, TemplateContent=template_content
            )
            print(f"Email template '{TEMPLATE_NAME}' created successfully.")
        except ClientError as e:
            # If the template already exists, skip and proceed
            if e.response["Error"]["Code"] == "AlreadyExistsException":
                print(f"Email template '{TEMPLATE_NAME}' already exists.")
            else:
                raise e

    def gather_subscriber_email_addresses(self):
        """
        Gathers subscriber email addresses and sends a welcome email to each subscriber.
        """
        # Get the base email address from the user
        base_email = input(
            "Enter a base email address for subscribing to the newsletter: "
        )

        # Generate subaddress variants
        email_variants = get_subaddress_variants(base_email, 3)

        # Load the welcome email content
        welcome_text = load_file_content("welcome.txt")
        welcome_html = load_file_content("welcome.html")

        # Create contacts and send welcome emails
        for email in email_variants:
            try:
                # Create a new contact
                self.ses_client.create_contact(
                    ContactListName=CONTACT_LIST_NAME, EmailAddress=email
                )
                print(f"Contact with email '{email}' created successfully.")

                # Send the welcome email
                self.ses_client.send_email(
                    FromEmailAddress=self.verified_email,
                    Destination={"ToAddresses": [email]},
                    Content={
                        "Simple": {
                            "Subject": {
                                "Data": "Welcome to the Weekly Coupons Newsletter"
                            },
                            "Body": {
                                "Text": {"Data": welcome_text},
                                "Html": {"Data": welcome_html},
                            },
                        }
                    },
                )
                print(f"Welcome email sent to '{email}'.")
                if self.sleep:
                    # 1 email per second in sandbox mode, remove in production.
                    sleep(1.1)
            except ClientError as e:
                # If the contact already exists, skip and proceed
                if e.response["Error"]["Code"] == "AlreadyExistsException":
                    print(f"Contact with email '{email}' already exists. Skipping...")
                else:
                    raise e

    def send_coupon_newsletter(self):
        """
        Sends the coupon newsletter to the subscribers.
        """
        # Get the list of contacts
        try:
            contacts_response = self.ses_client.list_contacts(
                ContactListName=CONTACT_LIST_NAME
            )
        except ClientError as e:
            if e.response["Error"]["Code"] == "NotFoundException":
                print(f"Contact list '{CONTACT_LIST_NAME}' does not exist.")
                return
            else:
                raise e

        # Send the coupon newsletter to each contact
        coupon_items = load_file_content("sample_coupons.json")

        for contact in contacts_response["Contacts"]:
            email_address = contact["EmailAddress"]
            try:
                send = self.ses_client.send_email(
                    FromEmailAddress=self.verified_email,
                    Destination={"ToAddresses": [email_address]},
                    Content={
                        "Template": {
                            "TemplateName": TEMPLATE_NAME,
                            "TemplateData": coupon_items,
                        }
                    },
                    ListManagementOptions={"ContactListName": CONTACT_LIST_NAME},
                )
                print(f"Newsletter sent to '{email_address}'.")
                print("Debug: ", send)
                if self.sleep:
                    # 1 email per second in sandbox mode, remove in production.
                    sleep(1.1)
            except ClientError as e:
                print_error(e)

    def monitor_and_review(self):
        """
        Provides instructions for monitoring sending activity in the AWS console.
        """
        print(
            "To monitor your sending activity, please visit the SES Homepage in the AWS console:"
        )
        print("https://console.aws.amazon.com/ses/home#/account")
        print(
            "From there, you can view various dashboards and metrics related to your newsletter campaign."
        )
        input("Press enter to continue.")

    def clean_up(self):
        """
        Cleans up the resources created during the workflow.
        """
        # Delete the contact list
        try:
            self.ses_client.delete_contact_list(ContactListName=CONTACT_LIST_NAME)
            print(f"Contact list '{CONTACT_LIST_NAME}' deleted successfully.")
        except ClientError as e:
            # If the contact list doesn't exist, skip and proceed
            if e.response["Error"]["Code"] == "NotFoundException":
                print(f"Contact list '{CONTACT_LIST_NAME}' does not exist.")
            else:
                print(e)

        # Delete the email template
        try:
            self.ses_client.delete_email_template(TemplateName=TEMPLATE_NAME)
            print(f"Email template '{TEMPLATE_NAME}' deleted successfully.")
        except ClientError as e:
            # If the email template doesn't exist, skip and proceed
            if e.response["Error"]["Code"] == "NotFoundException":
                print(f"Email template '{TEMPLATE_NAME}' does not exist.")
            else:
                print(e)

        # Ask the user if they want to delete the email identity
        delete_identity = input("Do you want to delete the email identity? (y/n) ")
        if delete_identity.lower() == "y":
            try:
                self.ses_client.delete_email_identity(EmailIdentity=self.verified_email)
                print(f"Email identity '{self.verified_email}' deleted successfully.")
            except ClientError as e:
                # If the email identity doesn't exist, skip and proceed
                if e.response["Error"]["Code"] == "NotFoundException":
                    print(f"Email identity '{self.verified_email}' does not exist.")
                else:
                    print(e)
        else:
            print("Skipping email identity deletion.")


# Main function
def main():
    """
    The main function that orchestrates the execution of the workflow.
    """
    print(INTRO)
    ses_client = boto3.client("sesv2")
    workflow = SESv2Workflow(ses_client)
    try:
        workflow.prepare_application()
        workflow.gather_subscriber_email_addresses()
        workflow.send_coupon_newsletter()
        workflow.monitor_and_review()
    except ClientError as e:
        print_error(e)
    workflow.clean_up()


if __name__ == "__main__":
    main()
```

</file>
