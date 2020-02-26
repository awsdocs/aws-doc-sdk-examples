# Change Log

All notable changes to this project will be documented in this file. This project adheres to [Semantic Versioning](http://semver.org/).

## [3.0.0] - 2020-02-07

### Removed

* This component is no longer supported on PHP 7.1 and PHP 7.2

## [2.0.2] - 2018-09-13

### Fixed

* Fixed [#48](https://github.com/sebastianbergmann/php-file-iterator/issues/48): Excluding an array that contains false ends up excluding the current working directory

## [2.0.1] - 2018-06-11

### Fixed

* Fixed [#46](https://github.com/sebastianbergmann/php-file-iterator/issues/46): Regression with hidden parent directory

## [2.0.0] - 2018-05-28

### Fixed

* Fixed [#30](https://github.com/sebastianbergmann/php-file-iterator/issues/30): Exclude is not considered if it is a parent of the base path

### Changed

* This component now uses namespaces

### Removed

* This component is no longer supported on PHP 5.3, PHP 5.4, PHP 5.5, PHP 5.6, and PHP 7.0

## [1.4.5] - 2017-11-27

### Fixed

* Fixed [#37](https://github.com/sebastianbergmann/php-file-iterator/issues/37): Regression caused by fix for [#30](https://github.com/sebastianbergmann/php-file-iterator/issues/30)

## [1.4.4] - 2017-11-27

### Fixed

* Fixed [#30](https://github.com/sebastianbergmann/php-file-iterator/issues/30): Exclude is not considered if it is a parent of the base path

## [1.4.3] - 2017-11-25

### Fixed

* Fixed [#34](https://github.com/sebastianbergmann/php-file-iterator/issues/34): Factory should use canonical directory names

## [1.4.2] - 2016-11-26

No changes

## [1.4.1] - 2015-07-26

No changes

## 1.4.0 - 2015-04-02

### Added

* [Added support for wildcards (glob) in exclude](https://github.com/sebastianbergmann/php-file-iterator/pull/23)

[3.0.0]: https://github.com/sebastianbergmann/php-file-iterator/compare/2.0.2...master
[2.0.2]: https://github.com/sebastianbergmann/php-file-iterator/compare/2.0.1...2.0.2
[2.0.1]: https://github.com/sebastianbergmann/php-file-iterator/compare/2.0.0...2.0.1
[2.0.0]: https://github.com/sebastianbergmann/php-file-iterator/compare/1.4...2.0.0
[1.4.5]: https://github.com/sebastianbergmann/php-file-iterator/compare/1.4.4...1.4.5
[1.4.4]: https://github.com/sebastianbergmann/php-file-iterator/compare/1.4.3...1.4.4
[1.4.3]: https://github.com/sebastianbergmann/php-file-iterator/compare/1.4.2...1.4.3
[1.4.2]: https://github.com/sebastianbergmann/php-file-iterator/compare/1.4.1...1.4.2
[1.4.1]: https://github.com/sebastianbergmann/php-file-iterator/compare/1.4.0...1.4.1
