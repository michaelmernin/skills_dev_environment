package com.perficient.etm.web.deserializer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public abstract class EnumDeserializer<E> extends StdDeserializer<E> {
    
    private static final long serialVersionUID = 7568144951035706157L;
    
    private static final TypeReference<HashMap<String, Object>> MAP_TYPE = new TypeReference<HashMap<String, Object>>() {};

    protected EnumDeserializer(Class<?> vc) {
        super(vc);
    }
    
    protected EnumDeserializer(JavaType valueType) {
        super(valueType);
    }

    @Override
    public E deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (jp.getCurrentToken() == JsonToken.START_OBJECT) {
            return getById(parseId(jp));
        } else {
            try {
                return getById(_parseInteger(jp, ctxt));
            } catch (JsonProcessingException e) {
                return getByName( _parseString(jp, ctxt));
            }
        }
    }

    private Integer parseId(JsonParser jp) throws IOException {
        Map<String, Object> map = jp.readValueAs(MAP_TYPE);
        if (map != null) {
            Object obj =  map.get("id");
            if (obj != null && Integer.class.isAssignableFrom(obj.getClass())) {
                return (Integer) obj;
            }
        }
        return null;
    }
    
    private E getById(Integer id) {
        return castToEnumClass(invokeStaticMethod("getById", Integer.class, id));
    }
    
    private E getByName(String name) {
        return castToEnumClass(invokeStaticMethod("valueOf", String.class, name));
    }

    private Object invokeStaticMethod(String methodName, Class<?> parameterType, Object parameter) {
        if (parameter != null) {
            try {
                return handledType().getMethod(methodName, parameterType).invoke(null, parameter);
            } catch (IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | NoSuchMethodException
                    | SecurityException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    private E castToEnumClass(Object value) {
        if (value != null && handledType().isAssignableFrom(value.getClass())) {
            return (E) value;
        }
        return null;
    }
}
