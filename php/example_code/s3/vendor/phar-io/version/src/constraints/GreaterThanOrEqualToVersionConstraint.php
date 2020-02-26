<?php
/*
 * This file is part of PharIo\Version.
 *
 * (c) Arne Blankerts <arne@blankerts.de>, Sebastian Heuer <sebastian@phpeople.de>, Sebastian Bergmann <sebastian@phpunit.de>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

namespace PharIo\Version;

class GreaterThanOrEqualToVersionConstraint extends AbstractVersionConstraint {
    /**
     * @var Version
     */
    private $minimalVersion;

    /**
     * @param string $originalValue
     * @param Version $minimalVersion
     */
    public function __construct($originalValue, Version $minimalVersion) {
        parent::__construct($originalValue);

        $this->minimalVersion = $minimalVersion;
    }

    /**
     * @param Version $version
     *
     * @return bool
     */
    public function complies(Version $version) {
        return $version->getVersionString() == $this->minimalVersion->getVersionString()
            || $version->isGreaterThan($this->minimalVersion);
    }
}
