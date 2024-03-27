import os

import boto3

from utils.custom_logging import setup_custom_logger
from utils.timeit import timeit


class BedrockModelService:
    """
    A class to interact with the Bedrock AI service, specifically for operations
    related to foundation models.
    """

    def __init__(self):
        """
        Initializes the BedrockModelService with a boto3 client for Bedrock and a custom logger.
        """
        self.bedrock_client = boto3.client(service_name="bedrock")
        self.logger = setup_custom_logger(os.path.basename(__file__))

    @timeit
    def get_models(self):
        """
        Fetches a list of foundation models from the Bedrock AI service and logs their IDs.

        This method uses the `boto3` library to interact with the Bedrock AI service,
        retrieving a list of foundation models. Each model's ID is then logged using
        a custom logger. The method is decorated with `@timeit` to measure its execution time.

        Exception Handling:
            Catches and logs exceptions that may occur during the interaction with the Bedrock service.

        Logging:
            Logs the total number of models found and each model's ID at DEBUG level. If no models are found
            or an exception occurs, appropriate warnings or errors are logged.
        """
        try:
            # Request a list of foundation models from Bedrock
            model_list = self.bedrock_client.list_foundation_models()

            # Extract model summaries from the response
            model_summaries = model_list.get("modelSummaries")
            if model_summaries is not None:
                self.logger.info(f"Found models: {len(model_summaries)}")
                # Log each model's ID
                for model in model_summaries:
                    self.logger.debug(model["modelId"])
            else:
                self.logger.warning("No model summaries found in the Bedrock response.")
        except Exception as e:
            # Log any exceptions that occur during the process
            self.logger.error(
                f"Failed to retrieve models from Bedrock: {e}", exc_info=True
            )


bedrock_service = BedrockModelService()
bedrock_service.get_models()
