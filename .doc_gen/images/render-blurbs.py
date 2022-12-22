from ipaddress import v6_int_to_packed
import jinja2
import yaml
import os

env = jinja2.Environment(
    loader=jinja2.FileSystemLoader(os.path.dirname(__file__))
)

with open('../../metadata/sdks.yaml', 'r') as file:
    metadata = yaml.safe_load(file)
    for language in metadata.keys():
        metadata[language]
        print(f'\nName: {language}')
        short_description = f'This image provides a pre-built for SDK for {language} environment and is recommended for local testing of SDK for {language} example code.'
        print(f'Blurb: {short_description}')
        shortname = metadata[language]['property']
        usage = short_description + f' It is not intended for production usage. For detailed and up-to-date steps on running this image, please see https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/{shortname}/README.md#docker-image.'
        print(f'Usage: {usage}')
