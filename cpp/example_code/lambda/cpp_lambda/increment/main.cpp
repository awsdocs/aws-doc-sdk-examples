#include <memory>
#include <aws/lambda-runtime/runtime.h>
#include <aws/logging/logging.h>
#include <json/json.h>


using namespace aws::lambda_runtime;

char const ACTION_KEY[] = "action";
char const NUMBER_KEY[] = "number";
char const RESULT_KEY[] = "result";
char const INCREMENT_ACTION[] = "increment";
char const TAG[] = "LAMBDA_LOG";

invocation_response my_handler(invocation_request const& request)
{
    Json::Value root;
    Json::CharReaderBuilder builder;
    const std::unique_ptr<Json::CharReader> reader(builder.newCharReader());
    JSONCPP_STRING err;
    if (!reader->parse(request.payload.c_str(), request.payload.c_str() + request.payload.length(), &root,
                       &err)) {
        return invocation_response::failure(std::string("Failed to parse input JSON. ") + err, "InvalidJSON");
    }

    if (!root.isMember(ACTION_KEY) || !root[ACTION_KEY].isString())
    {
        return invocation_response::failure("Missing valid 'action'.", "InvalidInput");
    }

    if (!root.isMember(NUMBER_KEY) || !root[NUMBER_KEY].isNumeric())
    {
        return invocation_response::failure("Missing valid 'number'.", "InvalidInput");
    }

    std::string action = root[ACTION_KEY].asString();
    if (action == INCREMENT_ACTION)
    {
        Json::Value result;
        int number = root[NUMBER_KEY].asInt();
        aws::logging::log_info (TAG, "Incremented %d by 1", number);

        result[RESULT_KEY] = number + 1;
        Json::StreamWriterBuilder builder;
        return invocation_response::success(Json::writeString(builder, result), "application/json");
    }
    else
    {
        aws::logging::log_error (TAG, "Unimplemented action %s", action.c_str());
        return invocation_response::failure(std::string("Invalid action. ") + action, "InvalidAction");
    }
}

int main()
{
    run_handler(my_handler);
    return 0;
}