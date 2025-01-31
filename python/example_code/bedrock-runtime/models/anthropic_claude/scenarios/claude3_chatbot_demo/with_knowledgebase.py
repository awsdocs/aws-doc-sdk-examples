# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
import argparse
import boto3
import os
import yaml
from langchain_community.chat_models import BedrockChat

from utils.custom_logging import setup_custom_logger
from utils.timeit import timeit


class Claude3WithKnowledgeBase:
    """
    A conversational QA application simulating responses using a
    RetrievalQA model integrated with a knowledge base and a large language model.
    It processes user inputs in an interactive session, utilizing a contextual doc and
    user queries to generate contextually relevant answers.

    This module relies on the existence of an S3 bucket with user-provided document
    and a Knowledge Base (created using these instructions:
    https://docs.aws.amazon.com/bedrock/latest/userguide/knowledge-base-create.html)
    """

    def __init__(self, knowledge_base_id):
        """
        Initializes the QA application with necessary components, including a custom logger,
        the BedrockChat model for interaction with a large language model, and identifiers for
        knowledge base and document retrieval.
        """
        with open("config.yaml", "r") as file:
            self.data = yaml.safe_load(file)
        self.logger = setup_custom_logger(os.path.basename(__file__))
        self.boto3_bedrock = boto3.client(service_name="bedrock-runtime")
        self.llm = BedrockChat(
            model_id=self.data["model"],
            client=self.boto3_bedrock,
        )
        self.knowledge_base_id = knowledge_base_id

    @timeit
    def get_kb_answer(self, prompt, qa):
        """
        Retrieves an answer from the RetrievalQA model based on the provided prompt.

        Args:
            prompt (str): The user's prompt or question.
            qa (RetrievalQA): An instance of the RetrievalQA model for answering queries.

        Returns:
            str: The generated response based on the model and knowledge base retrieval.
        """
        try:
            response = qa.invoke(prompt)
            return response["result"]
        except Exception as e:
            self.logger.error(f"Failed to retrieve knowledge base answer: {e}")
            return "Sorry, I encountered an issue processing your request."

    def run_app(self):
        """
        Executes the main application loop, initiating a conversational interface that
        continuously processes user inputs until "bye" is detected, using Ford Prior's
        persona and resume for context.
        """
        from langchain.chains import RetrievalQA
        from langchain_community.retrievers import AmazonKnowledgeBasesRetriever
        from langchain_core.prompts import PromptTemplate

        try:
            retriever = AmazonKnowledgeBasesRetriever(
                knowledge_base_id=self.knowledge_base_id,
                region_name="us-east-1",
                retrieval_config={"vectorSearchConfiguration": {"numberOfResults": 1}},
            )
            qa = RetrievalQA.from_chain_type(
                llm=self.llm, retriever=retriever, return_source_documents=False
            )

            template = PromptTemplate(
                input_variables=["input"],
                template="""
                         {input}
                     """,
            )

            input_text = input("You: ")
            prompt = template.format(input=input_text)
            initial_response = self.get_kb_answer(f"{prompt}\nIntroduce yourself.", qa)
            self.logger.warning(initial_response)
            reply = input("\nYou: ")

            while "bye" not in reply.lower():
                response = self.get_kb_answer(reply, qa)
                self.logger.warning(response)
                reply = input("\nYou: ")
        except Exception as e:
            self.logger.error(f"Application failed to run: {e}")
            raise


if __name__ == "__main__":
    # Argument parsing setup
    parser = argparse.ArgumentParser(
        description="Run Claude3WithKnowledgeBase with a specified knowledge base ID."
    )
    parser.add_argument(
        "--knowledge_base_id",
        type=str,
        required=True,
        help="The ID of the knowledge base to use.",
    )
    args = parser.parse_args()

    # Create an instance of the app with the provided knowledge base ID and run it
    app = Claude3WithKnowledgeBase(knowledge_base_id=args.knowledge_base_id)
    app.run_app()
