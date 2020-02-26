# ChangeLog

All notable changes are documented in this file using the [Keep a CHANGELOG](https://keepachangelog.com/) principles.

## [3.0.0] - 2020-02-07

### Removed

* This component is no longer supported on PHP 7.1 and PHP 7.2

## [2.0.1] - 2018-10-04

### Fixed

* Functions and methods with nullable parameters of type `resource` are now also considered

## [2.0.0] - 2018-09-27

### Changed

* [FunctionSignatureMap.php](https://raw.githubusercontent.com/phan/phan/master/src/Phan/Language/Internal/FunctionSignatureMap.php) from `phan/phan` is now used instead of [arginfo.php](https://raw.githubusercontent.com/rlerdorf/phan/master/includes/arginfo.php) from `rlerdorf/phan`

### Removed

* This component is no longer supported on PHP 5.6 and PHP 7.0

## 1.0.0 - 2015-07-28

* Initial release

[3.0.0]: https://github.com/sebastianbergmann/comparator/compare/2.0.1...master
[2.0.1]: https://github.com/sebastianbergmann/comparator/compare/2.0.0...2.0.1
[2.0.0]: https://github.com/sebastianbergmann/comparator/compare/1.0.0...2.0.0
