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

class VersionNumber {
    /**
     * @var int
     */
    private $value;

    /**
     * @param mixed $value
     */
    public function __construct($value) {
        if (is_numeric($value)) {
            $this->value = $value;
        }
    }

    /**
     * @return bool
     */
    public function isAny() {
        return $this->value === null;
    }

    /**
     * @return int
     */
    public function getValue() {
        return $this->value;
    }
}
