<?php
/*
 * This file is part of PharIo\Manifest.
 *
 * (c) Arne Blankerts <arne@blankerts.de>, Sebastian Heuer <sebastian@phpeople.de>, Sebastian Bergmann <sebastian@phpunit.de>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

namespace PharIo\Manifest;

use PharIo\Version\VersionConstraint;

abstract class Type {
    /**
     * @return Application
     */
    public static function application() {
        return new Application;
    }

    /**
     * @return Library
     */
    public static function library() {
        return new Library;
    }

    /**
     * @param ApplicationName   $application
     * @param VersionConstraint $versionConstraint
     *
     * @return Extension
     */
    public static function extension(ApplicationName $application, VersionConstraint $versionConstraint) {
        return new Extension($application, $versionConstraint);
    }

    /**
     * @return bool
     */
    public function isApplication() {
        return false;
    }

    /**
     * @return bool
     */
    public function isLibrary() {
        return false;
    }

    /**
     * @return bool
     */
    public function isExtension() {
        return false;
    }
}
