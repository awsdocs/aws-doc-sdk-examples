# Change Log

All notable changes to `sebastianbergmann/object-reflector` are documented in this file using the [Keep a CHANGELOG](http://keepachangelog.com/) principles.

## [2.0.0] - 2020-02-07

### Removed

* This component is no longer supported on PHP 7.0, PHP 7.1, and PHP 7.2

## [1.1.1] - 2017-03-29

* Fixed [#1](https://github.com/sebastianbergmann/object-reflector/issues/1): Attributes with non-string names are not handled correctly

## [1.1.0] - 2017-03-16

### Changed

* Changed implementation of `ObjectReflector::getattributes()` to use `(array)` cast instead of `ReflectionObject`

## 1.0.0 - 2017-03-12

* Initial release

[2.0.0]: https://github.com/sebastianbergmann/object-reflector/compare/1.1.1...master
[1.1.1]: https://github.com/sebastianbergmann/object-reflector/compare/1.1.0...1.1.1
[1.1.0]: https://github.com/sebastianbergmann/object-reflector/compare/1.0.0...1.1.0
