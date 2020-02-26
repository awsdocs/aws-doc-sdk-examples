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

use PharIo\Version\Version;

class BundledComponent {
    /**
     * @var string
     */
    private $name;

    /**
     * @var Version
     */
    private $version;

    /**
     * @param string  $name
     * @param Version $version
     */
    public function __construct($name, Version $version) {
        $this->name    = $name;
        $this->version = $version;
    }

    /**
     * @return string
     */
    public function getName() {
        return $this->name;
    }

    /**
     * @return Version
     */
    public function getVersion() {
        return $this->version;
    }
}
