import sys
import yaml
import boto3
import json
from langchain_core.prompts import PromptTemplate

class PostDrafter:
    """
    A class that writes a draft blog post for AWS LinkedIn.
    """

    def __init__(self, config_path="config.yml"):
        """
        Initializes with configuration loaded from a YAML file.
        """
        self.config = self.load_config(config_path)
        self.bedrock_runtime_client = boto3.client(service_name="bedrock-runtime")

    def load_config(self, config_path):
        """
        Loads configuration from a YAML file.
        """
        try:
            with open(config_path, "r") as file:
                return yaml.safe_load(file)
        except FileNotFoundError:
            print(f"Configuration file not found: {config_path}")
            raise
        except Exception as e:
            print(f"Error loading configuration: {e}")
            raise

    def load_readme_content(self, readme_path):
        """
        Loads the content of the README file given its path.
        """
        try:
            with open(readme_path, "r", encoding="utf-8") as file:
                return file.read()
        except FileNotFoundError:
            print(f"File not found: {readme_path}")
            raise
        except Exception as e:
            print(f"Error loading file: {e}")
            raise

    def draft_post(self, readme_path):
        """
        Generates post using GitHub path.
        """
        try:
            readme_content = self.load_readme_content(readme_path)
            multi_var_prompt = PromptTemplate(
                input_variables=["prompt_text", "response_length"],
                template=self.config["prompt_template"].format(readme=readme_content, response_length=self.config['response_length']),
            )

            prompt = multi_var_prompt.format(input=readme_content)
            response = self.bedrock_runtime_client.invoke_model(
                modelId="anthropic.claude-3-sonnet-20240229-v1:0",
                body=json.dumps(
                    {
                        "anthropic_version": "bedrock-2023-05-31",
                        "max_tokens": 1000,
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
            print(text_contents[0])
            return text_contents[0]
        except Exception as e:
            print(f"Error during post generation: {e}")
            raise


if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: script.py <path_to_readme>")
        sys.exit(1)
    readme_path = sys.argv[1]
    bot = PostDrafter()
    bot.draft_post(readme_path)
