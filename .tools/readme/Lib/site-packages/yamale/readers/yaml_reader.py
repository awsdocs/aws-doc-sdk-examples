from __future__ import absolute_import
from io import StringIO


def _pyyaml(f):
    import yaml
    try:
        Loader = yaml.CSafeLoader
    except AttributeError:  # System does not have libyaml
        Loader = yaml.SafeLoader
    return list(yaml.load_all(f, Loader=Loader))


def _ruamel(f):
    from ruamel.yaml import YAML
    yaml = YAML(typ='safe')
    return list(yaml.load_all(f))


_parsers = {
    'pyyaml': _pyyaml,
    'ruamel': _ruamel
}


def parse_yaml(path=None, parser='pyyaml', content=None):
    try:
        parse = _parsers[parser.lower()]
    except KeyError:
        raise NameError('Parser "' + parser + '" is not supported\nAvailable parsers are listed below:\nPyYAML\nruamel')
    if (path is None and content is None) or (path is not None and content is not None):
        raise TypeError("Pass either path= or content=, not both")
    if path is not None:
        with open(path) as f:
            return parse(f)
    else:
        return parse(StringIO(content))
