import os

from langchain_community.chat_models import BedrockChat

from utils.custom_logging import setup_custom_logger
from utils.timeit import timeit


class FordGPTv3:
    """
    A conversational QA application simulating responses as Ford Prior, using a
    RetrievalQA model integrated with a knowledge base and a large language model.
    It processes user inputs in an interactive session, utilizing Ford's resume and
    user queries to generate contextually relevant answers.
    """

    def __init__(self):
        """
        Initializes the QA application with necessary components, including a custom logger,
        the BedrockChat model for interaction with a large language model, and identifiers for
        knowledge base and document retrieval.
        """
        self.logger = setup_custom_logger(os.path.basename(__file__))
        self.model_id = "anthropic.claude-3-sonnet-20240229-v1:0"
        self.knowledge_base_id = "TB1WKZHMYK"
        self.llm = BedrockChat(
            model_id=self.model_id,
            region_name="us-east-1",
        )

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

            # loader = S3FileLoader("bedrock-training-ford-gpt", "Profile.pdf")
            # resume = loader.load()
            resume = "See file in context."

            multi_var_prompt = PromptTemplate(
                input_variables=["input", "resume"],
                template="""
                         Imagine you are Ford Prior, a seasoned cloud and DevOps engineer with a
                         rich background in cloud engineering, DevOps practices, and software development.

                         His work experience is here: {resume}

                         Your response should reflect Ford's style: extremely concise, insightful, and
                         very direct with a side of snark. Only help if asked; otherwise, be cheeky
                         but truthful.

                         User: {input}

                         How would you reply? No intro; just reply as you would:
                     """,
            )

            input_text = input("You: ")
            prompt = multi_var_prompt.format(input=input_text, resume=resume)

            initial_response = self.get_kb_answer(
                f"{prompt}\nIntroduce yourself in a sentence.", qa
            )
            self.logger.warning(initial_response)
            reply = input("\nYou: ")

            while "bye" not in reply.lower():
                response = self.get_kb_answer(reply, qa)
                self.logger.warning(response)
                reply = input("\nYou: ")
        except Exception as e:
            self.logger.error(f"Application failed to run: {e}")


app = FordGPTv3()
app.run_app()
