# ABCs for containers were moved to their own module
try:
    from collections.abc import Mapping, Set, Sequence
except ImportError:
    from collections import Mapping, Set, Sequence


# Python 3 has no basestring, lets test it.
try:
    basestring  # attempt to evaluate basestring

    def isstr(s):
        return isinstance(s, basestring)

    def to_unicode(s):
        return unicode(s)

except NameError:
    def isstr(s):
        return isinstance(s, str)

    def to_unicode(s):
        return s


def is_list(obj):
    return isinstance(obj, Sequence) and not isstr(obj)


def is_map(obj):
    return isinstance(obj, Mapping)


def get_keys(obj):
    if is_map(obj):
        return obj.keys()
    elif is_list(obj):
        return range(len(obj))


def get_iter(iterable):
    if isinstance(iterable, Mapping):
        return iterable.items()
    else:
        return enumerate(iterable)


def get_subclasses(cls, _subclasses_yielded=None):
    """
    Generator recursively yielding all subclasses of the passed class (in
    depth-first order).

    Parameters
    ----------
    cls : type
        Class to find all subclasses of.
    _subclasses_yielded : set
        Private parameter intended to be passed only by recursive invocations of
        this function, containing all previously yielded classes.
    """

    if _subclasses_yielded is None:
        _subclasses_yielded = set()

    # If the passed class is old- rather than new-style, raise an exception.
    if not hasattr(cls, '__subclasses__'):
        raise TypeError('Old-style class "%s" unsupported.' % cls.__name__)

    # For each direct subclass of this class
    for subclass in cls.__subclasses__():
        # If this subclass has already been yielded, skip to the next.
        if subclass in _subclasses_yielded:
            continue

        # Yield this subclass and record having done so before recursing.
        yield subclass
        _subclasses_yielded.add(subclass)

        # Yield all direct subclasses of this class as well.
        for subclass_subclass in get_subclasses(subclass, _subclasses_yielded):
            yield subclass_subclass
