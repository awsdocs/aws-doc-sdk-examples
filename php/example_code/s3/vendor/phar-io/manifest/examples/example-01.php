<?php
/*
 * This file is part of PharIo\Manifest.
 *
 * (c) Arne Blankerts <arne@blankerts.de>, Sebastian Heuer <sebastian@phpeople.de>, Sebastian Bergmann <sebastian@phpunit.de>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

use PharIo\Manifest\ManifestLoader;
use PharIo\Manifest\ManifestSerializer;

require __DIR__ . '/../vendor/autoload.php';

$manifest = ManifestLoader::fromFile(__DIR__ . '/../tests/_fixture/phpunit-5.6.5.xml');

echo sprintf(
    "Manifest for %s (%s):\n\n",
    $manifest->getName(),
    $manifest->getVersion()->getVersionString()
);
echo (new ManifestSerializer)->serializeToString($manifest);
