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

class License {
    /**
     * @var string
     */
    private $name;

    /**
     * @var Url
     */
    private $url;

    public function __construct($name, Url $url) {
        $this->name = $name;
        $this->url  = $url;
    }

    /**
     * @return string
     */
    public function getName() {
        return $this->name;
    }

    /**
     * @return Url
     */
    public function getUrl() {
        return $this->url;
    }
}
