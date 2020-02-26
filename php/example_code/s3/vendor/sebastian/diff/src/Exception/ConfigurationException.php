<?php declare(strict_types=1);
/*
 * This file is part of sebastian/diff.
 *
 * (c) Sebastian Bergmann <sebastian@phpunit.de>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

namespace SebastianBergmann\Diff;

final class ConfigurationException extends InvalidArgumentException
{
    /**
     * @param string          $option
     * @param string          $expected
     * @param mixed           $value
     * @param int             $code
     * @param null|\Exception $previous
     */
    public function __construct(
        string $option,
        string $expected,
        $value,
        int $code = 0,
        \Exception $previous = null
    ) {
        parent::__construct(
            \sprintf(
                'Option "%s" must be %s, got "%s".',
                $option,
                $expected,
                \is_object($value) ? \get_class($value) : (null === $value ? '<null>' : \gettype($value) . '#' . $value)
            ),
            $code,
            $previous
        );
    }
}
