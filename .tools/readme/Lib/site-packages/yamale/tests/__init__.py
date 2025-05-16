import os


def get_fixture(relative):
    script_dir = os.path.dirname(__file__)
    return os.path.join(script_dir, 'fixtures/', relative)
