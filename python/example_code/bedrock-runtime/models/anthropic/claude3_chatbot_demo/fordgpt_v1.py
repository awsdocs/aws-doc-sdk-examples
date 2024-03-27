"""
This example is part of a larger live demo.
"""
import os

import boto3

from utils.custom_logging import setup_custom_logger
from utils.timeit import timeit


class FordGPTv1:
    """
    A class that simulates conversational responses from Ford Prior, a fictional character
    known for his background in cloud engineering and DevOps practices. It utilizes the
    LangChain library for dynamic prompt construction and AWS Boto3 for accessing the
    Bedrock runtime AI model. The conversation continues until the user says 'bye'.
    """

    def __init__(self):
        """
        Initializes the FordPriorConversationalResponder with a Bedrock runtime client and a custom logger.
        """
        self.bedrock_runtime_client = boto3.client(service_name="bedrock-runtime")
        self.logger = setup_custom_logger(os.path.basename(__file__))

    @timeit
    def reply_no_rag(self):
        """
        Generates and logs replies based on user input using the Ford Prior persona in a conversational context.
        The conversation continues until the user inputs 'bye'. Each user input is processed to generate
        a contextually relevant and snarky reply that reflects Ford's persona.

        Exception Handling:
            Captures and logs any exceptions, terminating the conversation on error.

        User Interaction:
            Maintains a loop for conversational interaction with the user until 'bye' is mentioned.
        """
        import json

        from langchain_core.prompts import PromptTemplate

        multi_var_prompt = PromptTemplate(
            input_variables=["input"],
            template="""
                 Imagine you are Ford Prior, a seasoned cloud and DevOps engineer with a
                 rich background in cloud engineering, DevOps practices, and software development.

                 Your response should reflect Ford's style: extremely concise, insightful, and
                 very direct with a side of snark. Only help if asked; otherwise be cheeky
                 but truthful.

                 If you are asked a question directly, ask 1 question to gain context,
                 then dive in confidently with a local tip for getting into
                 the Richmond tech scene based on your context and knowledge.

                 Do not self refer or admit you are a bot. You are Ford Prior.

                 User: {input}

                 How would you reply? No intro; just reply as you would:
             """,
        )

        input_text = input("You: ")
        while "bye" not in input_text.lower():
            try:
                prompt = multi_var_prompt.format(input=input_text)
                response = self.bedrock_runtime_client.invoke_model(
                    modelId="anthropic.claude-3-sonnet-20240229-v1:0",
                    body=json.dumps(
                        {
                            "anthropic_version": "bedrock-2023-05-31",
                            "max_tokens": 500,
                            "messages": [
                                {
                                    "role": "user",
                                    "content": [{"type": "text", "text": prompt}],
                                }
                            ],
                        }
                    ),
                )
                bytes_content = response["body"].read()
                data = json.loads(bytes_content.decode("utf-8"))
                text_contents = [
                    item["text"] for item in data["content"] if item["type"] == "text"
                ]
                self.logger.warning(text_contents[0])
                input_text = input("You: ")
            except Exception as e:
                self.logger.error(f"Error during conversation: {e}")
                break


bot = FordGPTv1()
bot.reply_no_rag()
