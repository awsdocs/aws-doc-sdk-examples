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

class PhpElement extends ManifestElement {
    public function getVersion() {
        return $this->getAttributeValue('version');
    }

    public function hasExtElements() {
        return $this->hasChild('ext');
    }

    public function getExtElements() {
        return new ExtElementCollection(
            $this->getChildrenByName('ext')
        );
    }
}
