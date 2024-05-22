# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
import boto3
import os
import yaml
from langchain_community.chat_models import BedrockChat

from utils.custom_logging import setup_custom_logger
from utils.timeit import timeit


class Claude3WithDocumentRAG:
    """
    A class encapsulating the functionality to run a conversational application
    simulating interactions as Ford Prior, using context from a resume loaded from
    Amazon Simple Storage Service (Amazon S3).

    It leverages the LangChain library for managing conversation flows and the AWS Boto3
    client for interacting with the Amazon Bedrock runtime.
    """

    def __init__(self):
        """
        Initializes the conversational app with a custom logger, an Amazon Bedrock runtime client,
        and sets up the LangChain's BedrockChat model for conversation handling.
        """
        with open("config.yaml", "r") as file:
            self.data = yaml.safe_load(file)
        self.logger = setup_custom_logger(os.path.basename(__file__))
        self.boto3_bedrock = boto3.client(service_name="bedrock-runtime")
        self.llm = BedrockChat(
            model_id=self.data["model"],
            client=self.boto3_bedrock,
        )

    @timeit
    def run_app(self):
        """
        Starts the conversational application, loading contextual data from a document
        in Amazon S3, and engages in a conversation based on dynamically formatted prompts and
        user inputs.

        The conversation continues until "bye" is included in a user's reply, integrating
        context from the user-provided document throughout the interaction.

        Exception Handling:
            Captures and logs any exceptions, indicating failure to run the application.
        """
        from langchain.chains import ConversationChain
        from langchain.memory import ConversationBufferMemory
        from langchain_community.document_loaders import S3FileLoader
        from langchain_core.prompts import PromptTemplate

        try:
            loader = S3FileLoader(self.data["bucket_name"], self.data["file_name"])
            document = loader.load()

            template = PromptTemplate(
                input_variables=["document", "input_text"],
                template="""
                    Take into account the following document: {document}
                    {input_text}
                """,
            )

            prompt = template.format(
                document=document, input_text="Introduce yourself to start the chat."
            )

            conversation = ConversationChain(
                llm=self.llm,
                verbose=False,
                memory=ConversationBufferMemory(return_messages=True),
            )
            initial_prediction = conversation.predict(input=prompt)
            self.logger.warning(initial_prediction)

            self.reply_with_document(template, "User: ", conversation, document)
        except Exception as e:
            self.logger.error(f"Application failed to run: {e}")
            raise

    def reply_with_document(self, template, reply_text, conversation, document):
        """
        Facilitates a conversation using dynamically generated prompts, incorporating user input
        and document context. Logs responses until "bye" is detected in user input.

        Args:
            template (PromptTemplate): The template for generating conversation prompts.
            reply_text (str): Initial text to prompt user input.
            conversation (ConversationChain): The conversation handler.
            document (str): Contextual document content to include in prompts.
        """
        reply = input(reply_text)
        while "bye" not in reply.lower():
            try:
                prompt = template.format(document=document, input_text=reply)
                breakpoint()
                prediction = conversation.predict(input=prompt)
                self.logger.warning(prediction)
                reply = input("User: ")
            except Exception as e:
                self.logger.error(f"Error during conversation: {e}")
                break


app = Claude3WithDocumentRAG()
app.run_app()
