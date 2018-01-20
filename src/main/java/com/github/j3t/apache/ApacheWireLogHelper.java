package com.github.j3t.apache;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class contains methods that helps with tasks related to ApacheHttpClient wire log.
 */
public class ApacheWireLogHelper {

    public static final Pattern PATTERN_ENCODED_CHARACTER = Pattern.compile("\\[0x([0-9a-f]{1,2})\\]");


    /**
     * Decodes a given ApacheHttpClient wire log message.<br>
     * <br>
     * <b>Why is this necessary?</b><br>
     * ApacheHttpClient wire log replaces character 10 with [\n], character 13 with [\r], characters < 32 and > 127
     * (non-printable) with [0xN] where N is char's hexadecimal representation.
     *
     * @param message That contains encoded characters
     * @return Given message but the encoded characters are decoded or null
     */
    public static String decodeMessage(String message) {
        if (message == null) {
            return null;
        }

        // replace [\n] and [\r]
        String result = message
                .replaceAll("\\[\\\\n\\]", String.valueOf((char) 10))
                .replaceAll("\\[\\\\r\\]", String.valueOf((char) 13));

        // create a matcher that finds all encoded characters (group is [0xN] and group(1) is N)
        Matcher matcher = PATTERN_ENCODED_CHARACTER.matcher(result);

        // iterate over all findings and replace [0xN] with the corresponding character
        while (matcher.find()) {
            char replacement = (char) Integer.parseInt(matcher.group(1), 16);
            result = result.replace(matcher.group(), String.valueOf(replacement));
        }

        return result;
    }

}
