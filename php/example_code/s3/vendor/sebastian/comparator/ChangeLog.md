# ChangeLog

All notable changes are documented in this file using the [Keep a CHANGELOG](http://keepachangelog.com/) principles.

## [4.0.0] - 2020-02-07

### Removed

* Removed support for PHP 7.1 and PHP 7.2

## [3.0.2] - 2018-07-12

### Changed

* By default, `MockObjectComparator` is now tried before all other (default) comparators

## [3.0.1] - 2018-06-14

### Fixed

* Fixed [#53](https://github.com/sebastianbergmann/comparator/pull/53): `DOMNodeComparator` ignores `$ignoreCase` parameter
* Fixed [#58](https://github.com/sebastianbergmann/comparator/pull/58): `ScalarComparator` does not handle extremely ugly string comparison edge cases

## [3.0.0] - 2018-04-18

### Fixed

* Fixed [#48](https://github.com/sebastianbergmann/comparator/issues/48): `DateTimeComparator` does not support fractional second deltas

### Removed

* Removed support for PHP 7.0

## [2.1.3] - 2018-02-01

### Changed

* This component is now compatible with version 3 of `sebastian/diff`

## [2.1.2] - 2018-01-12

### Fixed

* Fix comparison of `DateTimeImmutable` objects

## [2.1.1] - 2017-12-22

### Fixed

* Fixed [phpunit/#2923](https://github.com/sebastianbergmann/phpunit/issues/2923): Unexpected failed date matching

## [2.1.0] - 2017-11-03

### Added

* Added `SebastianBergmann\Comparator\Factory::reset()` to unregister all non-default comparators
* Added support for `phpunit/phpunit-mock-objects` version `^5.0`

[4.0.0]: https://github.com/sebastianbergmann/comparator/compare/3.0.2...master
[3.0.2]: https://github.com/sebastianbergmann/comparator/compare/3.0.1...3.0.2
[3.0.1]: https://github.com/sebastianbergmann/comparator/compare/3.0.0...3.0.1
[3.0.0]: https://github.com/sebastianbergmann/comparator/compare/2.1.3...3.0.0
[2.1.3]: https://github.com/sebastianbergmann/comparator/compare/2.1.2...2.1.3
[2.1.2]: https://github.com/sebastianbergmann/comparator/compare/2.1.1...2.1.2
[2.1.1]: https://github.com/sebastianbergmann/comparator/compare/2.1.0...2.1.1
[2.1.0]: https://github.com/sebastianbergmann/comparator/compare/2.0.2...2.1.0
