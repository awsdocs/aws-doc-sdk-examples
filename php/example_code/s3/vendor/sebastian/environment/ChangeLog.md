# Changes in sebastianbergmann/environment

All notable changes in `sebastianbergmann/environment` are documented in this file using the [Keep a CHANGELOG](http://keepachangelog.com/) principles.

## [5.0.1] - 2020-02-19

### Changed

* `Runtime::getNameWithVersionAndCodeCoverageDriver()` now prioritizes PCOV over Xdebug when both extensions are loaded (just like php-code-coverage does)

## [5.0.0] - 2020-02-07

### Removed

* This component is no longer supported on PHP 7.1 and PHP 7.2

## [4.2.3] - 2019-11-20

### Changed

* Implemented [#50](https://github.com/sebastianbergmann/environment/pull/50): Windows improvements to console capabilities

### Fixed

* Fixed [#49](https://github.com/sebastianbergmann/environment/issues/49): Detection how OpCache handles docblocks does not work correctly when PHPDBG is used

## [4.2.2] - 2019-05-05

### Fixed

* Fixed [#44](https://github.com/sebastianbergmann/environment/pull/44): `TypeError` in `Console::getNumberOfColumnsInteractive()`

## [4.2.1] - 2019-04-25

### Fixed

* Fixed an issue in `Runtime::getCurrentSettings()`

## [4.2.0] - 2019-04-25

### Added

* Implemented [#36](https://github.com/sebastianbergmann/environment/pull/36): `Runtime::getCurrentSettings()`

## [4.1.0] - 2019-02-01

### Added

* Implemented `Runtime::getNameWithVersionAndCodeCoverageDriver()` method
* Implemented [#34](https://github.com/sebastianbergmann/environment/pull/34): Support for PCOV extension

## [4.0.2] - 2019-01-28

### Fixed

* Fixed [#33](https://github.com/sebastianbergmann/environment/issues/33): `Runtime::discardsComments()` returns true too eagerly

### Removed

* Removed support for Zend Optimizer+ in `Runtime::discardsComments()`

## [4.0.1] - 2018-11-25

### Fixed

* Fixed [#31](https://github.com/sebastianbergmann/environment/issues/31): Regressions in `Console` class

## [4.0.0] - 2018-10-23 [YANKED]

### Fixed

* Fixed [#25](https://github.com/sebastianbergmann/environment/pull/25): `Console::hasColorSupport()` does not work on Windows

### Removed

* This component is no longer supported on PHP 7.0

## [3.1.0] - 2017-07-01

### Added

* Implemented [#21](https://github.com/sebastianbergmann/environment/issues/21): Equivalent of `PHP_OS_FAMILY` (for PHP < 7.2) 

## [3.0.4] - 2017-06-20

### Fixed

* Fixed [#20](https://github.com/sebastianbergmann/environment/pull/20): PHP 7 mode of HHVM not forced

## [3.0.3] - 2017-05-18

### Fixed

* Fixed [#18](https://github.com/sebastianbergmann/environment/issues/18): `Uncaught TypeError: preg_match() expects parameter 2 to be string, null given`

## [3.0.2] - 2017-04-21

### Fixed

* Fixed [#17](https://github.com/sebastianbergmann/environment/issues/17): `Uncaught TypeError: trim() expects parameter 1 to be string, boolean given`

## [3.0.1] - 2017-04-21

### Fixed

* Fixed inverted logic in `Runtime::discardsComments()`

## [3.0.0] - 2017-04-21

### Added

* Implemented `Runtime::discardsComments()` for querying whether the PHP runtime discards annotations

### Removed

* This component is no longer supported on PHP 5.6

[5.0.1]: https://github.com/sebastianbergmann/phpunit/compare/5.0.0...5.0.1
[5.0.0]: https://github.com/sebastianbergmann/phpunit/compare/4.2.3...5.0.0
[4.2.3]: https://github.com/sebastianbergmann/phpunit/compare/4.2.2...4.2.3
[4.2.2]: https://github.com/sebastianbergmann/phpunit/compare/4.2.1...4.2.2
[4.2.1]: https://github.com/sebastianbergmann/phpunit/compare/4.2.0...4.2.1
[4.2.0]: https://github.com/sebastianbergmann/phpunit/compare/4.1.0...4.2.0
[4.1.0]: https://github.com/sebastianbergmann/phpunit/compare/4.0.2...4.1.0
[4.0.2]: https://github.com/sebastianbergmann/phpunit/compare/4.0.1...4.0.2
[4.0.1]: https://github.com/sebastianbergmann/phpunit/compare/66691f8e2dc4641909166b275a9a4f45c0e89092...4.0.1
[4.0.0]: https://github.com/sebastianbergmann/phpunit/compare/3.1.0...66691f8e2dc4641909166b275a9a4f45c0e89092
[3.1.0]: https://github.com/sebastianbergmann/phpunit/compare/3.0...3.1.0
[3.0.4]: https://github.com/sebastianbergmann/phpunit/compare/3.0.3...3.0.4
[3.0.3]: https://github.com/sebastianbergmann/phpunit/compare/3.0.2...3.0.3
[3.0.2]: https://github.com/sebastianbergmann/phpunit/compare/3.0.1...3.0.2
[3.0.1]: https://github.com/sebastianbergmann/phpunit/compare/3.0.0...3.0.1
[3.0.0]: https://github.com/sebastianbergmann/phpunit/compare/2.0...3.0.0

