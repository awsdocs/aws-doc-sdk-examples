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

class ApplicationName {
    /**
     * @var string
     */
    private $name;

    /**
     * ApplicationName constructor.
     *
     * @param string $name
     *
     * @throws InvalidApplicationNameException
     */
    public function __construct($name) {
        $this->ensureIsString($name);
        $this->ensureValidFormat($name);
        $this->name = $name;
    }

    /**
     * @return string
     */
    public function __toString() {
        return $this->name;
    }

    public function isEqual(ApplicationName $name) {
        return $this->name === $name->name;
    }

    /**
     * @param string $name
     *
     * @throws InvalidApplicationNameException
     */
    private function ensureValidFormat($name) {
        if (!preg_match('#\w/\w#', $name)) {
            throw new InvalidApplicationNameException(
                sprintf('Format of name "%s" is not valid - expected: vendor/packagename', $name),
                InvalidApplicationNameException::InvalidFormat
            );
        }
    }

    private function ensureIsString($name) {
        if (!is_string($name)) {
            throw new InvalidApplicationNameException(
                'Name must be a string',
                InvalidApplicationNameException::NotAString
            );
        }
    }
}
