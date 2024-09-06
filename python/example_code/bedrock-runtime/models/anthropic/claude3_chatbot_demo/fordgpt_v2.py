# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
import os

import boto3
from langchain_community.chat_models import BedrockChat
from utils.custom_logging import setup_custom_logger
from utils.timeit import timeit


class FordGPTv2:
    """
    A class encapsulating the functionality to run a conversational application
    simulating interactions as Ford Prior, using context from a resume loaded from
    Amazon Simple Storage Service (Amazon S3).

    It leverages the LangChain library for managing conversation flows and the AWS Boto3
    client for interacting with the Amazon Bedrock runtime.
    """

    def __init__(self):
        """
        Initializes the conversational app with a custom logger, a Amazon Bedrock runtime client,
        and sets up the LangChain's BedrockChat model for conversation handling.
        """
        self.logger = setup_custom_logger(os.path.basename(__file__))
        self.boto3_bedrock = boto3.client(service_name="bedrock-runtime")
        self.llm = BedrockChat(
            model_id="anthropic.claude-3-sonnet-20240229-v1:0",
            client=self.boto3_bedrock,
        )

    @timeit
    def run_app(self):
        """
        Starts the conversational application, loading contextual data from a document
        in Amazon S3, and engages in a conversation based on dynamically formatted prompts and
        user inputs.

        The conversation continues until "bye" is included in a user's reply, integrating
        context from Ford Prior's resume throughout the interaction.

        Exception Handling:
            Captures and logs any exceptions, indicating failure to run the application.
        """
        from langchain.chains import ConversationChain
        from langchain.memory import ConversationBufferMemory
        from langchain_community.document_loaders import S3FileLoader
        from langchain_core.prompts import PromptTemplate

        try:
            loader = S3FileLoader("bedrock-training-ford-gpt", "Profile.pdf")
            resume = loader.load()

            template = PromptTemplate(
                input_variables=["resume", "input_text"],
                template="""
                    Imagine you are Ford Prior, a seasoned cloud and DevOps engineer with a
                    rich background in cloud engineering, DevOps practices, and software development.

                    Here is Ford's work history: {resume}

                    Your response should reflect Ford's style: extremely concise, insightful, and
                    very direct with a side of snark. Only help if asked; otherwise, be cheeky
                    but truthful.

                    If you are asked a question directly, ask one question to gain context,
                    then dive in confidently with a local tip for getting into
                    the Richmond tech scene based on your context and knowledge.

                    Do not self refer or admit you are a bot. You are Ford Prior.

                    User: {input_text}

                    How would you reply? No intro; just reply as you would:
                """,
            )

            prompt = template.format(
                resume=resume,
                input_text="Introduce yourself in a single sentence.")

            conversation = ConversationChain(
                llm=self.llm,
                verbose=False,
                memory=ConversationBufferMemory(return_messages=True),
            )
            initial_prediction = conversation.predict(input=prompt)
            self.logger.warning(initial_prediction)

            self.reply_with_document(template, "You:", conversation, resume)
        except Exception as e:
            self.logger.error(f"Application failed to run: {e}")
            raise

    def reply_with_document(self, template, reply_text, conversation, resume):
        """
        Facilitates a conversation using dynamically generated prompts, incorporating user input
        and document context. Logs responses until "bye" is detected in user input.

        Args:
            template (PromptTemplate): The template for generating conversation prompts.
            reply_text (str): Initial text to prompt user input.
            conversation (ConversationChain): The conversation handler.
            resume (str): Contextual document content to include in prompts.
        """
        reply = input(reply_text)
        while "bye" not in reply.lower():
            try:
                prompt = template.format(resume=resume, input_text=reply)
                prediction = conversation.predict(input=prompt)
                self.logger.warning(prediction)
                reply = input("You:")
            except Exception as e:
                self.logger.error(f"Error during conversation: {e}")
                break


app = FordGPTv2()
app.run_app()