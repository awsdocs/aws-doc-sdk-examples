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

class Url {
    /**
     * @var string
     */
    private $url;

    /**
     * @param string $url
     *
     * @throws InvalidUrlException
     */
    public function __construct($url) {
        $this->ensureUrlIsValid($url);

        $this->url = $url;
    }

    /**
     * @return string
     */
    public function __toString() {
        return $this->url;
    }

    /**
     * @param string $url
     *
     * @throws InvalidUrlException
     */
    private function ensureUrlIsValid($url) {
        if (filter_var($url, \FILTER_VALIDATE_URL) === false) {
            throw new InvalidUrlException;
        }
    }
}
