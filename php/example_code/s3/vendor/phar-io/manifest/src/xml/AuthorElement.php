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

class AuthorElement extends ManifestElement {
    public function getName() {
        return $this->getAttributeValue('name');
    }

    public function getEmail() {
        return $this->getAttributeValue('email');
    }
}
