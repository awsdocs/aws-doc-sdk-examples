import * as readline from "readline";

export const selectModel = async (models) => {
    return new Promise(resolve => {
        const rl = readline.createInterface({
            input: process.stdin,
            output: process.stdout
        });

        const printOptions = () => {
            models.forEach((model, index) => {
                console.log(`${index + 1}. ${model.modelName}`);
            });

        };

        let currentQuestion = "Enter a number: ";

        const askQuestion = (question) => {
            rl.question(question, answer => {
                const selectedIndex = parseInt(answer, 10) - 1;
                if (selectedIndex >= 0 && selectedIndex < models.length) {
                    rl.close();
                    resolve(models[selectedIndex]);
                } else {
                    // Move the cursor up one line
                    readline.moveCursor(process.stdout, 0, -1);
                    // Clear the line above
                    readline.clearLine(process.stdout, 0);
                    // Move the cursor back to the beginning of the current line
                    readline.cursorTo(process.stdout, 0);
                    currentQuestion = "Invalid input. Please enter a valid number: ";
                    askQuestion(currentQuestion);
                }
            });
        };

        printOptions();
        askQuestion(currentQuestion);
    });
}

export const askForPrompt = async (models) => {
    return new Promise(resolve => {
        const rl = readline.createInterface({
            input: process.stdin,
            output: process.stdout
        });

        let currentQuestion = "Now, enter your prompt: ";
        const askQuestion = (question) => {
            rl.question(question, answer => {
                if (answer.trim() === "") {
                    // Move the cursor up one line
                    readline.moveCursor(process.stdout, 0, -1);
                    // Clear the line above
                    readline.clearLine(process.stdout, 0);
                    // Move the cursor back to the beginning of the current line
                    readline.cursorTo(process.stdout, 0);
                    currentQuestion = "Invalid input. Please enter a prompt: ";
                    askQuestion(currentQuestion);
                } else {
                    rl.close();
                    resolve(answer);
                }
            });
        };
        askQuestion(currentQuestion);
    });
}
