package org.loguno.processor.configuration;

import lombok.AccessLevel;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ConfigurationKeys {

    public final ConfigurationKey<String> METHOD_MESSAGE_PATTERN_DEFAULT =
            ConfigurationKey.<String>of("loguno.method.message.default", "Default message for method invocation logging");

    public final ConfigurationKey<String> LOCVAR_MESSAGE_PARAMS_PATTERN_DEFAULT =
            ConfigurationKey.<String>of("loguno.localvar.message.default", "Default message for logging one local variable");
}
