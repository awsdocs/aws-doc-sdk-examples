# ChangeLog

All notable changes are documented in this file using the [Keep a CHANGELOG](https://keepachangelog.com/) principles.

## [3.0.0] - 2020-02-07

### Added

* Added `canInvokeWithTimeout()` method to check requirements for the functionality provided by this component to work

### Changed

* Moved `"ext-pcntl": "*"` requirement from `require` to `suggest` so that this component can be installed even if `ext/pcntl` is not available
* `invoke()` now raises an exception when the requirements for the functionality provided by this component to work are not met

### Removed

* This component is no longer supported on PHP 7.1 and PHP 7.2

[3.0.0]: https://github.com/sebastianbergmann/php-invoker/compare/2.0.0...master
