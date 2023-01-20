#include <memory>
#include <cstring>
#include <aws/lambda-runtime/runtime.h>
#include <aws/logging/logging.h>
#include <json/json.h>


using namespace aws::lambda_runtime;

char const ACTION_KEY[] = "action";
char const NUMBER_X_KEY[] = "x";
char const NUMBER_Y_KEY[] = "y";
char const RESULT_KEY[] = "result";
char const PLUS_ACTION[] = "plus";
char const MINUS_ACTION[] = "minus";
char const TIMES_ACTION[] = "times";
char const DIVIDED_BY_ACTION[] = "divided-by";
char const TAG[] = "LAMBDA_LOG";

static aws::logging::verbosity gLogLevel = aws::logging::verbosity::error;

static void myLog(aws::logging::verbosity v, char const* tag, char const* msg, va_list args);
[[gnu::format(printf, 2, 3)]] static void log_error(char const* tag, char const* msg, ...);
[[gnu::format(printf, 2, 3)]] static void log_info(char const* tag, char const* msg, ...);
[[gnu::format(printf, 2, 3)]] static void log_debug(char const* tag, char const* msg, ...);

invocation_response my_handler(invocation_request const& request)
{
    log_debug(TAG, "my_handler called.");
    Json::Value root;
    Json::CharReaderBuilder charReaderBuilder;
    const std::unique_ptr<Json::CharReader> reader(charReaderBuilder.newCharReader());
    JSONCPP_STRING err;
    if (!reader->parse(request.payload.c_str(), request.payload.c_str() + request.payload.length(), &root,
                       &err)) {
        return invocation_response::failure(std::string("Failed to parse input JSON. ") + err, "InvalidJSON");
    }

    if (!root.isMember(ACTION_KEY) || !root[ACTION_KEY].isString())
    {
        return invocation_response::failure("Missing valid 'action'.", "InvalidInput");
    }

    if (!root.isMember(NUMBER_X_KEY) || !root[NUMBER_X_KEY].isNumeric())
    {
        return invocation_response::failure("Missing valid 'x'.", "InvalidInput");
    }

    if (!root.isMember(NUMBER_Y_KEY) || !root[NUMBER_Y_KEY].isNumeric())
    {
        return invocation_response::failure("Missing valid 'y'.", "InvalidInput");
    }
    log_debug(TAG, "Json input is correct");

    std::string action = root[ACTION_KEY].asString();
    double x_number = root[NUMBER_X_KEY].asDouble();
    double y_number = root[NUMBER_Y_KEY].asDouble();
    if (action == PLUS_ACTION)
    {
        log_info(TAG, "operation %f + %f = %f", x_number, y_number, x_number + y_number);
        Json::Value result;
        result[RESULT_KEY] = x_number + y_number;
        Json::StreamWriterBuilder builder;
        return invocation_response::success(Json::writeString(builder, result), "application/json");
    }
    else if (action == MINUS_ACTION)
    {
        log_info(TAG, "operation %f - %f = %f", x_number, y_number, x_number - y_number);
        Json::Value result;
        result[RESULT_KEY] = x_number - y_number;
        Json::StreamWriterBuilder builder;
        return invocation_response::success(Json::writeString(builder, result), "application/json");
    }
    else if (action == TIMES_ACTION)
    {
        log_info(TAG, "operation %f * %f = %f", x_number, y_number, x_number * y_number);
        Json::Value result;
        result[RESULT_KEY] = x_number * y_number;
        Json::StreamWriterBuilder builder;
        return invocation_response::success(Json::writeString(builder, result), "application/json");
    }
    else if (action == DIVIDED_BY_ACTION)
    {
        if (y_number== 0)
        {
            log_error (TAG, "Divide by zero error");
            return invocation_response::failure("Divide by zero error", "InvalidOperand");
        }

        log_info(TAG, "operation %f / %f = %f", x_number, y_number, x_number / y_number);
        Json::Value result;
        result[RESULT_KEY] = x_number / y_number;
        Json::StreamWriterBuilder builder;
        return invocation_response::success(Json::writeString(builder, result), "application/json");
    }
    else
    {
        log_error (TAG, "Unimplemented action %s", action.c_str());
        return invocation_response::failure(std::string("Invalid action. ") + action, "InvalidAction");
    }
}

int main()
{
    const char* log_level = std::getenv("LOG_LEVEL");
    if (log_level)
    {
        if (strcmp("DEBUG", log_level) == 0)
        {
            gLogLevel = aws::logging::verbosity::debug;
        }
        else  if (strcmp("INFO", log_level) == 0)
        {
            gLogLevel = aws::logging::verbosity::info;
        }
    }

    run_handler(my_handler);
    return 0;
}

void myLog(aws::logging::verbosity v, char const* tag, char const* msg, va_list args)
{
    if (v <= gLogLevel)
    {
        aws::logging::log(v, tag, msg, args);
    }
}

[[gnu::format(printf, 2, 3)]] inline void log_error(char const* tag, char const* msg, ...)
{
    va_list args;
    va_start(args, msg);
    myLog(aws::logging::verbosity::error, tag, msg, args);
    va_end(args);
    (void)tag;
    (void)msg;
}

[[gnu::format(printf, 2, 3)]] inline void log_info(char const* tag, char const* msg, ...)
{
    va_list args;
    va_start(args, msg);
    myLog(aws::logging::verbosity::info, tag, msg, args);
    va_end(args);
}

[[gnu::format(printf, 2, 3)]] inline void log_debug(char const* tag, char const* msg, ...)
{
    va_list args;
    va_start(args, msg);
    myLog(aws::logging::verbosity::debug, tag, msg, args);
    va_end(args);
}
