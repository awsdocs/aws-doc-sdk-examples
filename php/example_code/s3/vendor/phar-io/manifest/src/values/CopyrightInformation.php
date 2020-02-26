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

class CopyrightInformation {
    /**
     * @var AuthorCollection
     */
    private $authors;

    /**
     * @var License
     */
    private $license;

    public function __construct(AuthorCollection $authors, License $license) {
        $this->authors = $authors;
        $this->license = $license;
    }

    /**
     * @return AuthorCollection
     */
    public function getAuthors() {
        return $this->authors;
    }

    /**
     * @return License
     */
    public function getLicense() {
        return $this->license;
    }
}
