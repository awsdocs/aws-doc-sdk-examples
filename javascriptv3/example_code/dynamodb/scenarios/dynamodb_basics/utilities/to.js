// Utility function to wrap our async code and format the resolving and rejecting arguments to an array of two elements.
export function to(promise, improved){
    return promise
        .then((data) => [null, data])
        .catch((err) => {
            if (improved) {
                Object.assign(err, improved);
            }

            return [err]; // which is same as [err, undefined];
        });
}