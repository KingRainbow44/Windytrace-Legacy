package lol.magix.windtrace.utils;

import java.util.Base64;

public final class StringUtils {
    /**
     * Base64 encodes a string.
     * @param string The string to encode.
     * @return The encoded string.
     */
    public static String base64Encode(String string) {
        return Base64.getEncoder().encodeToString(string.getBytes());
    }

    /**
     * Base64 encodes an array of bytes.
     * @param buffer The byte buffer to encode.
     * @return The encoded string.
     */
    public static String base64Encode(byte[] buffer) {
        return Base64.getEncoder().encodeToString(buffer);
    }
    
    /**
     * Base64 decodes a string.
     * @param string The string to decode.
     * @return The decoded string.
     */
    public static String base64Decode(String string) {
        return new String(Base64.getDecoder().decode(string));
    }
}
