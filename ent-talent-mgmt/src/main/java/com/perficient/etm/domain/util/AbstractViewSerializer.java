package com.perficient.etm.domain.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializerProvider;

public abstract class AbstractViewSerializer extends JsonSerializer<Object> {

    protected abstract Class<?> getView();

    @Override
    public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writerWithView(getView());
        String json = writer.writeValueAsString(value);
        jgen.writeRawValue(json);
    }
}
