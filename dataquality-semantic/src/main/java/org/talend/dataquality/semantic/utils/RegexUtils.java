package org.talend.dataquality.semantic.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class RegexUtils {

    private static Pattern startPattern = Pattern.compile("[\\^]*+", Pattern.CASE_INSENSITIVE); //$NON-NLS-1$

    private static Pattern endPattern = Pattern.compile("[\\$]*$", Pattern.CASE_INSENSITIVE); //$NON-NLS-1$

    private RegexUtils() {
    }

    /**
     * Keep only one '$'at the end and remove other no need character '$'
     *
     * @param extraParameter
     * @return valid pattern string
     */
    public static String removeInvalidCharacter(String extraParameter) {
        String patternStr = stringStartTrim(extraParameter, "\\^"); //$NON-NLS-1$
        patternStr = stringEndTrim(patternStr, "\\$"); //$NON-NLS-1$
        patternStr = patternStr + "$"; //$NON-NLS-1$
        return patternStr;
    }

    /**
     * Remove special character from start
     *
     * @param stream original string
     * @param trim The character which you want to remove
     * @return
     */
    private static String stringStartTrim(String stream, String trim) {
        if (StringUtils.isEmpty(stream) || StringUtils.isEmpty(trim)) {
            return stream;
        }
        // The end location which need to remove str
        int end;
        String result = stream;

        // remove characters
        Matcher matcher = startPattern.matcher(stream);
        if (matcher.lookingAt()) {
            end = matcher.end();
            result = result.substring(end);
        }
        // return result after deal
        return result;
    }

    /**
     * Remove special character from tail
     *
     * @param stream original string
     * @param trim The character which you want to remove
     * @return
     */
    private static String stringEndTrim(String stream, String trim) {
        if (StringUtils.isEmpty(stream) || StringUtils.isEmpty(trim)) {
            return stream;
        }
        // The start location which need to remove str
        int start;
        String result = stream;
        // remove characters from tail
        Matcher matcher = endPattern.matcher(stream);
        if (matcher.find()) {
            start = matcher.start();
            result = result.substring(0, start);
        }
        // return result after deal
        return result;
    }
}
