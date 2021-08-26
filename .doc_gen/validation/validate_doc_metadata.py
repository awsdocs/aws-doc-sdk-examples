"""
Validator for mapping and example metadata used to generate code example documentation.
This validator uses Yamale (https://github.com/23andMe/Yamale) to compare a schema
against YAML files stored in the metadata folder and runs as a GitHub action.
"""

import argparse
import glob
import os
import re
import yaml
import yamale
from yamale import YamaleError
from yamale.validators import DefaultValidators, Validator, String


class ServiceName(Validator):
    """ Validate that service names appear in mapping.yaml. """
    tag = 'service_name'
    services = {}

    def get_name(self):
        return 'service name found in mapping.yaml'

    def _is_valid(self, value):
        return value in self.services


class ExampleId(Validator):
    """
    Validate an example ID starts with a service ID and has underscore-separated
    operation and specializations (like sns_Subscribe_Email).
    """
    tag = 'example_id'
    services = {}

    def get_name(self):
        return "valid example ID"

    def _is_valid(self, value):
        if not re.fullmatch('^[\\da-z-]+(_[\\da-zA-Z]+)+$', value):
            return False
        else:
            svc = value.split('_')[0]
            return svc == 'cross' or svc in self.services


class BlockContent(Validator):
    """ Validate that block content refers to an existing file. """
    tag = 'block_content'
    block_names = []

    def get_name(self):
        return "file found in the cross-content folder"

    def _is_valid(self, value):
        return value in self.block_names


class StringExtension(String):
    """ Validate that strings don't contain non-entity AWS usage. """
    def get_name(self):
        return 'string or contains a non-entity usage of AWS'

    def _is_valid(self, value):
        # All occurrences of AWS must be entities or within a word.
        if len(re.findall('(?<![&\\da-zA-Z])AWS|AWS(?![;\\da-zA-Z])', value)) > 0:
            return False
        else:
            return super()._is_valid(value)


def validate_files(schema_name, meta_names, validators):
    """ Iterate a list of files and validate each one against a schema. """
    success = True

    schema = yamale.make_schema(schema_name, validators=validators)
    for meta_name in meta_names:
        try:
            data = yamale.make_data(meta_name)
            yamale.validate(schema, data)
            print(f'{meta_name} validation success! 👍')
        except YamaleError as e:
            print(e.message)
            success = False
    return success


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "doc_gen", help="The folder that contains schema and metadata files.")
    args = parser.parse_args()

    with open(os.path.join(args.doc_gen, 'metadata/mapping.yaml')) as mapping_file:
        mapping_yaml = yaml.safe_load(mapping_file)
    validators = DefaultValidators.copy()
    ServiceName.services = mapping_yaml['services']
    ExampleId.services = mapping_yaml['services']
    BlockContent.block_names = os.listdir(os.path.join(args.doc_gen, 'cross-content'))
    validators[ServiceName.tag] = ServiceName
    validators[ExampleId.tag] = ExampleId
    validators[BlockContent.tag] = BlockContent
    validators[String.tag] = StringExtension

    # Validate mapping.yaml file.
    schema_name = os.path.join(args.doc_gen, 'validation/mapping_schema.yaml')
    meta_names = glob.glob(os.path.join(args.doc_gen, 'metadata/mapping.yaml'))
    success = validate_files(schema_name, meta_names, validators)

    # Validate example (*_metadata.yaml) files.
    schema_name = os.path.join(args.doc_gen, 'validation/example_schema.yaml')
    meta_names = glob.glob(os.path.join(args.doc_gen, 'metadata/*_metadata.yaml'))
    success &= validate_files(schema_name, meta_names, validators)

    if success:
        print("Validation succeeded! 👍👍👍")
    else:
        print("\n********************************************")
        print("* Validation failed, please check the log! *")
        print("********************************************")
        exit(1)


if __name__ == '__main__':
    main()
