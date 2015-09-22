package com.perficient.etm.utils;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hamcrest.Matchers;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Utility class for testing REST controllers.
 */
public class ResourceTestUtils {

    /** Fields of the User entity without a JsonView or with the Public JsonView **/
    public static final String[] PUBLIC_USER_FIELDS = {"id", "login", "firstName", "lastName", "email"};

    /** Fields of the User entity with the Peer JsonView **/
    private static final String[] PEER_ONLY_USER_FIELDS = {"counselor", "generalManager"};
    public static final String[] PEER_USER_FIELDS = Stream.of(PUBLIC_USER_FIELDS, PEER_ONLY_USER_FIELDS).flatMap(Stream::of).toArray(String[]::new);

    /** Fields of the User entity with the Counselee JsonView **/
    private static final String[] COUNSELEE_ONLY_USER_FIELDS = {"startDate", "title", "targetTitle"};
    public static final String[] COUNSELEE_USER_FIELDS = Stream.of(PEER_USER_FIELDS, COUNSELEE_ONLY_USER_FIELDS).flatMap(Stream::of).toArray(String[]::new);

    /** Fields of the User entity with the Private JsonView **/
    private static final String[] PRIVATE_ONLY_USER_FIELDS = {"employeeId", "langKey", "authorities", "createdBy", "createdDate", "lastModifiedBy", "lastModifiedDate"};
    public static final String[] PRIVATE_USER_FIELDS = Stream.of(COUNSELEE_USER_FIELDS, PRIVATE_ONLY_USER_FIELDS).flatMap(Stream::of).toArray(String[]::new);

    /** MediaType for JSON UTF8 */
    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(
            MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    /**
     * Convert an object to JSON byte array.
     *
     * @param object
     *            the object to convert
     * @return the JSON byte array
     * @throws IOException
     */
    public static byte[] convertObjectToJsonBytes(Object object)
            throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }

    /**
     * Convert an object to JSON byte array.
     *
     * @param object
     *            the object to convert
     * @param mapper
     *            the configured object mapper to use for conversion
     * @return the JSON byte array
     * @throws IOException
     */
    public static byte[] convertObjectToJsonBytes(Object object, ObjectMapper mapper)
            throws IOException {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }

    public static void assertJsonCount(ResultActions result, int count) throws Exception {
        result.andExpect(jsonPath("$").isArray());
        IntStream.range(0, count).forEach(i -> {
            try {
                result.andExpect(jsonPath("$[" + i + "]").exists());
            } catch (RuntimeException re) {
                throw re;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        result.andExpect(jsonPath("$[" + count + "]").doesNotExist());
    }

    public static void assertJsonKeys(ResultActions result, String path, String... fields) throws RuntimeException {
        Arrays.stream(fields).forEach(f -> {
            try {
                result.andExpect(jsonPath(path).value(Matchers.hasKey(f)));
            } catch (RuntimeException re) {
                throw re;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void assertJsonArrayItemKeys(ResultActions result, int count, String... fields) {
        IntStream.range(0, count).forEach(i -> {
            assertJsonKeys(result, "$[" + i + "]", fields);
        });
    }
}
