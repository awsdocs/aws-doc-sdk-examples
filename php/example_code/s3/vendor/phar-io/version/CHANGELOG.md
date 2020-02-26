# Changelog

All notable changes to phar-io/version are documented in this file using the [Keep a CHANGELOG](http://keepachangelog.com/) principles.

## [2.0.1] - 08.07.2018

### Fixed

- Versions without a pre-release suffix are now always considered greater 
than versions without a pre-release suffix. Example: `3.0.0 > 3.0.0-alpha.1`  

## [2.0.0] - 23.06.2018

Changes to public API:

- `PreReleaseSuffix::construct()`: optional parameter `$number` removed
- `PreReleaseSuffix::isGreaterThan()`: introduced
- `Version::hasPreReleaseSuffix()`: introduced

### Added

- [#11](https://github.com/phar-io/version/issues/11): Added support for pre-release version suffixes. Supported values are:
  - `dev`
  - `beta` (also abbreviated form `b`)
  - `rc`
  - `alpha` (also abbreviated form `a`)
  - `patch` (also abbreviated form `p`)

  All values can be followed by a number, e.g. `beta3`. 

  When comparing versions, the pre-release suffix is taken into account. Example:
`1.5.0 > 1.5.0-beta1 > 1.5.0-alpha3 > 1.5.0-alpha2 > 1.5.0-dev11`

### Changed

- reorganized the source directories

### Fixed

- [#10](https://github.com/phar-io/version/issues/10): Version numbers containing 
a numeric suffix as seen in Debian packages are now supported.  

[2.0.1]: https://github.com/phar-io/version/compare/2.0.0...2.0.1
[2.0.0]: https://github.com/phar-io/version/compare/1.0.1...2.0.0
