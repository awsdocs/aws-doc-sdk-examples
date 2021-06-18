module.exports = {
    testEnvironment: 'node',
    moduleDirectories: ["node_modules"],
    testMatch: ["**/tests/*.test.js"],
    transform: {
        "^.+\\.(js|jsx)$": "babel-jest",
    }
};
