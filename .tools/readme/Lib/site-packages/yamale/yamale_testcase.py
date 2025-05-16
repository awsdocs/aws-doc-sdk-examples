import glob
import os
import itertools

from unittest import TestCase

import yamale


class YamaleTestCase(TestCase):
    """ TestCase for easily validating YAML in your own tests.
    `schema`: String of path to the schema file to use. One schema file per test case.
    `yaml`: String or list of yaml files to validate. Accepts globs.
    `base_dir`: String path to prepend to all other paths. This is optional.
    """

    schema = None
    yaml = None
    base_dir = None

    def validate(self, validators=None):
        schema = self.schema
        yaml = self.yaml
        base_dir = self.base_dir

        if schema is None:
            return

        if type(yaml) != list:
            yaml = [yaml]

        if base_dir is not None:
            schema = os.path.join(base_dir, schema)
            yaml = {os.path.join(base_dir, y) for y in yaml}

        # Run yaml through glob and flatten list
        yaml = set(itertools.chain(*map(glob.glob, yaml)))

        # Remove schema from set of data files
        yaml = yaml - {schema}

        yamale_schema = yamale.make_schema(schema, validators=validators)
        yamale_data = itertools.chain(*map(yamale.make_data, yaml))

        for result in yamale.validate(yamale_schema, yamale_data):
            if not result.isValid():
                raise ValueError(result)
        return True

