class DataPath(object):

    def __init__(self, *path):
        self._path = path

    def __add__(self, other):
        dp = DataPath()
        dp._path = self._path + other._path
        return dp

    def __str__(self):
        return '.'.join(map(str, (self._path)))

    def __repr__(self):
        return 'DataPath({})'.format(repr(self._path))
