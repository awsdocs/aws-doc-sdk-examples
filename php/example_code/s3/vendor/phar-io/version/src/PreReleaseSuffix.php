<?php

namespace PharIo\Version;

class PreReleaseSuffix {
    private $valueScoreMap = [
        'dev' => 0,
        'a' => 1,
        'alpha' => 1,
        'b' => 2,
        'beta' => 2,
        'rc' => 3,
        'p' => 4,
        'patch' => 4,
    ];

    /**
     * @var string
     */
    private $value;

    /**
     * @var int
     */
    private $valueScore;

    /**
     * @var int
     */
    private $number = 0;

    /**
     * @param string $value
     */
    public function __construct($value) {
        $this->parseValue($value);
    }

    /**
     * @return string
     */
    public function getValue() {
        return $this->value;
    }

    /**
     * @return int|null
     */
    public function getNumber() {
        return $this->number;
    }

    /**
     * @param PreReleaseSuffix $suffix
     *
     * @return bool
     */
    public function isGreaterThan(PreReleaseSuffix $suffix) {
        if ($this->valueScore > $suffix->valueScore) {
            return true;
        }

        if ($this->valueScore < $suffix->valueScore) {
            return false;
        }

        return $this->getNumber() > $suffix->getNumber();
    }

    /**
     * @param $value
     *
     * @return int
     */
    private function mapValueToScore($value) {
        if (array_key_exists($value, $this->valueScoreMap)) {
            return $this->valueScoreMap[$value];
        }

        return 0;
    }

    private function parseValue($value) {
        $regex = '/-?(dev|beta|b|rc|alpha|a|patch|p)\.?(\d*).*$/i';
        if (preg_match($regex, $value, $matches) !== 1) {
            throw new InvalidPreReleaseSuffixException(sprintf('Invalid label %s', $value));
        }

        $this->value = $matches[1];
        if (isset($matches[2])) {
            $this->number = (int)$matches[2];
        }
        $this->valueScore = $this->mapValueToScore($this->value);
    }
}
