<?php
/*
 * This file is part of object-reflector.
 *
 * (c) Sebastian Bergmann <sebastian@phpunit.de>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

declare(strict_types=1);

namespace SebastianBergmann\ObjectReflector\TestFixture;

class ChildClass extends ParentClass
{
    private $privateInChild = 'private';
    private $protectedInChild = 'protected';
    private $publicInChild = 'public';

    public function __construct()
    {
        $this->undeclared = 'undeclared';
    }
}
