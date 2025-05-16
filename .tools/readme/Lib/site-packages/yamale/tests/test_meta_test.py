import re
import os
from yamale import YamaleTestCase
from yamale.validators import DefaultValidators, Validator


data_folder = os.path.dirname(os.path.realpath(__file__))


class TestAllYaml(YamaleTestCase):
    base_dir = data_folder
    schema = 'meta_test_fixtures/schema.yaml'
    yaml = 'meta_test_fixtures/data1.yaml'

    def runTest(self):
        self.assertTrue(self.validate())


class TestBadYaml(YamaleTestCase):
    base_dir = data_folder
    schema = 'meta_test_fixtures/schema_bad.yaml'
    yaml = 'meta_test_fixtures/data*.yaml'

    def runTest(self):
        self.assertRaises(ValueError, self.validate)


class TestMapYaml(YamaleTestCase):
    base_dir = data_folder
    schema = 'meta_test_fixtures/schema.yaml'
    yaml = ['meta_test_fixtures/data1.yaml',
            'meta_test_fixtures/some_data.yaml',
            # Make sure  schema doesn't validate itself
            'meta_test_fixtures/schema.yaml']

    def runTest(self):
        self.assertTrue(self.validate())


# class TestListYaml(YamaleTestCase):
#     base_dir = data_folder
#     schema = 'meta_test_fixtures/schema_include_list.yaml'
#     yaml = ['meta_test_fixtures/data_include_list.yaml']

#     def runTest(self):
#         self.assertTrue(self.validate())


class Card(Validator):
    """ Custom validator for testing purpose """
    tag = 'card'
    card_regex = re.compile(r'^(10|[2-9JQKA])[SHDC]$')

    def _is_valid(self, value):
        return re.match(self.card_regex, value)


class TestCustomValidator(YamaleTestCase):
    base_dir = data_folder
    schema = 'meta_test_fixtures/schema_custom.yaml'
    yaml = 'meta_test_fixtures/data_custom.yaml'

    def runTest(self):
        validators = DefaultValidators.copy()
        validators['card'] = Card
        self.assertTrue(self.validate(validators))


class TestCustomValidatorWithIncludes(YamaleTestCase):
    base_dir = data_folder
    schema = 'meta_test_fixtures/schema_custom_with_include.yaml'
    yaml = 'meta_test_fixtures/data_custom_with_include.yaml'

    def runTest(self):
        validators = DefaultValidators.copy()
        validators['card'] = Card
        self.assertTrue(self.validate(validators))


class TestBadRequiredYaml(YamaleTestCase):
    base_dir = data_folder
    schema = 'meta_test_fixtures/schema_required_bad.yaml'
    yaml = 'meta_test_fixtures/data_required_bad.yaml'

    def runTest(self):
        self.assertRaises(ValueError, self.validate)


class TestGoodRequiredYaml(YamaleTestCase):
    base_dir = data_folder
    schema = 'meta_test_fixtures/schema_required_good.yaml'
    yaml = 'meta_test_fixtures/data_required_good.yaml'

    def runTest(self):
        self.assertTrue(self.validate())
