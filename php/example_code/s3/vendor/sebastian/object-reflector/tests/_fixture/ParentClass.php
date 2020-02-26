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

class ParentClass
{
    private $privateInParent = 'private';
    private $protectedInParent = 'protected';
    private $publicInParent = 'public';
}
