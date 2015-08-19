package com.perficient.etm.domain.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.perficient.etm.web.view.View;

/**
 * Custom Jackson serializer for serializing objects without every field.
 * Ignores field annotated with @JsonView(View.Full.class)
 */
public class PartialSerializer extends JsonSerializer<Object> {

    @Override
    public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writerWithView(View.Partial.class);
        String json = writer.writeValueAsString(value);
        jgen.writeRaw(":" + json);
    }

}
