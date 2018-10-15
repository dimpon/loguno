package org.loguno.processor.configuration;


import org.loguno.processor.handlers.SupportedLoggers;

public class ConfigurationKeys {

    public static final String CLASS_PATTERN = "{class}";
    public static final String METHOD_PATTERN = "{method}";

    public static final ConfigurationKey<Boolean> ENABLE = ConfigurationKey.<Boolean>of("loguno.enable",
            "is loguno processing enabled", Boolean::valueOf);

    public static final ConfigurationKey<String> METHOD_MESSAGE_PATTERN_DEFAULT = ConfigurationKey.<String>of("loguno.method.message.default",
            "Default message for method invocation logging");

    public static final ConfigurationKey<String> LOCVAR_MESSAGE_PATTERN_DEFAULT = ConfigurationKey.<String>of("loguno.localvar.message.default",
            "Default message for logging one local variable");

    public static final ConfigurationKey<String> METHODPARAM_MESSAGE_PATTERN_DEFAULT = ConfigurationKey.<String>of("loguno.methodparam.message.default",
            "Default message for logging one method parameter");

    public static final ConfigurationKey<String> LOG_METHOD_DEFAULT = ConfigurationKey.<String>of("loguno.log.method.default",
            "Default log method");

    public static final ConfigurationKey<String> ERR_METHOD_DEFAULT = ConfigurationKey.<String>of("loguno.err.method.default",
            "Default err method");

    public static final ConfigurationKey<String> CATCH_MESSAGE_PATTERN_DEFAULT = ConfigurationKey.<String>of("loguno.catch.message.default",
            "Default message for exception catching");

    public static final ConfigurationKey<SupportedLoggers> LOGGING_FRAMEWORK_DEFAULT = ConfigurationKey.<SupportedLoggers>of("loguno.logging.framework.default",
            "Default logging framework", SupportedLoggers::valueOf);

}
