@ECHO OFF
setlocal DISABLEDELAYEDEXPANSION
SET BIN_TARGET=%~dp0/../phpunit/phpunit/phpunit
php "%BIN_TARGET%" %*
