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

class Email {
    /**
     * @var string
     */
    private $email;

    /**
     * @param string $email
     *
     * @throws InvalidEmailException
     */
    public function __construct($email) {
        $this->ensureEmailIsValid($email);

        $this->email = $email;
    }

    /**
     * @return string
     */
    public function __toString() {
        return $this->email;
    }

    /**
     * @param string $url
     *
     * @throws InvalidEmailException
     */
    private function ensureEmailIsValid($url) {
        if (filter_var($url, \FILTER_VALIDATE_EMAIL) === false) {
            throw new InvalidEmailException;
        }
    }
}
