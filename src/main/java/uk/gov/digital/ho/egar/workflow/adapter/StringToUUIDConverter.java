package uk.gov.digital.ho.egar.workflow.adapter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * A conversion component that converts from a UUID to a string.
 */
@Component
public class StringToUUIDConverter implements Converter<String, UUID> {
    /**
     * Converts the provided string to a UUID.
     * @param source a string
     * @return a UUID
     */
    public UUID convert(final String source) {
        return UUID.fromString(source);
    }
}
