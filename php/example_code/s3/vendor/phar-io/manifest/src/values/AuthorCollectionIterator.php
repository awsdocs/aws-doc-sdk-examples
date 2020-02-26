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

class AuthorCollectionIterator implements \Iterator {
    /**
     * @var Author[]
     */
    private $authors = [];

    /**
     * @var int
     */
    private $position;

    public function __construct(AuthorCollection $authors) {
        $this->authors = $authors->getAuthors();
    }

    public function rewind() {
        $this->position = 0;
    }

    /**
     * @return bool
     */
    public function valid() {
        return $this->position < count($this->authors);
    }

    /**
     * @return int
     */
    public function key() {
        return $this->position;
    }

    /**
     * @return Author
     */
    public function current() {
        return $this->authors[$this->position];
    }

    public function next() {
        $this->position++;
    }
}
