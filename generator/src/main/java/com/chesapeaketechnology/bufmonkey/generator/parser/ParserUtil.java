package com.chesapeaketechnology.bufmonkey.generator.parser;

import com.google.protobuf.compiler.PluginProtos.CodeGeneratorRequest;
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Florian Enner
 * @since 06 Aug 2019
 */
public class ParserUtil {

    public static Map<String, String> getGeneratorParameters(CodeGeneratorRequest request) {
        if (!request.hasParameter())
            return Collections.emptyMap();
        return parseGeneratorParameters(request.getParameter());
    }

    /**
     * Returns a map of input arguments added before the proto path, e.g.,
     * <p>
     * PROTOC INPUT: "--GEN_out=option1=value1,option2=value2,optionFlag3:./my-output-directory"
     * PARAMETER STRING: "option1=value1,option2=value2,optionFlag3"
     *
     * @param parameter parameter string input into protoc
     * @return map
     */
    public static Map<String, String> parseGeneratorParameters(String parameter) {
        if (parameter == null || parameter.isEmpty())
            return Collections.emptyMap();

        HashMap<String, String> map = new HashMap<>();
        String[] parts = parameter.split(",");
        for (String part : parts) {

            int equalsIndex = part.indexOf("=");
            if (equalsIndex == -1) {
                map.put(part, "");
            } else {
                String key = part.substring(0, equalsIndex);
                String value = part.substring(equalsIndex + 1);
                map.put(key, value);
            }

        }
        return map;
    }

    public static CodeGeneratorResponse asErrorWithStackTrace(Exception e) {
        // Print error with StackTrace
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintStream ps = new PrintStream(baos, true, "UTF-8")) {
            e.printStackTrace(ps);
        } catch (UnsupportedEncodingException ex) {
            throw new AssertionError("UTF-8 encoding not supported");
        }
        String errorWithStackTrace = new String(baos.toByteArray(), StandardCharsets.UTF_8);
        return CodeGeneratorResponse.newBuilder().setError(errorWithStackTrace).build();
    }

    public static CodeGeneratorResponse asError(String errorMessage) {
        return CodeGeneratorResponse.newBuilder().setError(errorMessage).build();
    }

}
