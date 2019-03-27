package org.talend.dataquality.datamasking.functions.text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.talend.dataquality.common.pattern.TextPatternUtil;
import org.talend.dataquality.datamasking.FunctionMode;
import org.talend.dataquality.datamasking.functions.CharactersOperationUtils;
import org.talend.dataquality.datamasking.functions.Function;
import org.talend.dataquality.datamasking.generic.Alphabet;
import org.talend.dataquality.datamasking.generic.GenerateFromAlphabet;

public class KeepLastDigitsAndReplaceOtherDigits extends Function<String> {

    private static final long serialVersionUID = 948008740278615816L;

    private int integerParam = 0;

    private GenerateFromAlphabet ff1Cipher;

    private transient Alphabet alphabet = Alphabet.DIGITS;

    @Override
    public void setSecret(FunctionMode method, String password) {
        ff1Cipher = new GenerateFromAlphabet(alphabet, method, password);
    }

    @Override
    public void parse(String extraParameter, boolean keepNullValues) {
        super.parse(extraParameter, keepNullValues);
        if (CharactersOperationUtils.validParameters1Number(parameters))
            integerParam = Integer.parseInt(parameters[0]);
        else
            throw new IllegalArgumentException("The parameter is not a positive integer.");
    }

    @Override
    protected String doGenerateMaskedField(String str) {
        return doGenerateMaskedField(str, FunctionMode.RANDOM);
    }

    @Override
    protected String doGenerateMaskedField(String str, FunctionMode mode) {
        if (integerParam < 0)
            return EMPTY_STRING;

        if (str == null || integerParam >= str.trim().length()) {
            return mode == FunctionMode.BIJECTIVE_BASIC ? null : str;
        }

        StringBuilder sb = new StringBuilder(str.trim());

        switch (mode) {
        case CONSISTENT:
            generateConsistentDigits(sb);
            break;
        case BIJECTIVE_BASIC:
            generateBijectiveDigits(sb);
            break;
        default:
            generateRandomDigits(sb);
            break;
        }
        if (sb.length() == 0) {
            return null;
        }

        return sb.toString();
    }

    private void generateRandomDigits(StringBuilder sb) {
        int totalDigit = 0;
        for (int i = sb.length() - 1; i >= 0; i--) {
            if (Character.isDigit(sb.charAt(i))) {
                if (integerParam > totalDigit)
                    totalDigit++;
                else
                    sb.setCharAt(i, Character.forDigit(nextRandomDigit(), 10));
            }
        }
    }

    private void generateConsistentDigits(StringBuilder sb) {
        int totalDigit = 0;
        String toBeReplaced = findDigits(sb);
        Random random = getRandomForObject(toBeReplaced);
        List<Integer> replacedCodePoints = TextPatternUtil.replaceStringCodePoints(toBeReplaced, random);
        int counter = 0;
        for (int i = sb.length() - 1; i >= 0; i--) {
            if (Character.isDigit(sb.charAt(i))) {
                if (integerParam > totalDigit)
                    totalDigit++;
                else {
                    sb.setCharAt(i, Character.toChars(replacedCodePoints.get(counter))[0]);
                    counter++;
                }
            }
        }
    }

    private void generateBijectiveDigits(StringBuilder sb) {
        int totalDigit = 0;
        List<Integer> digitsToReplace = new ArrayList<>();
        List<Integer> indexesToReplace = new ArrayList<>();
        for (int i = sb.length() - 1; i >= 0; i--) {
            if (Character.isDigit(sb.charAt(i))) {
                if (integerParam > totalDigit)
                    totalDigit++;
                else {
                    indexesToReplace.add(i);
                    digitsToReplace.add(Character.digit(sb.charAt(i), 10));
                }
            }
        }
        List<Integer> replacedDigits = ff1Cipher.generateUniqueDigits(digitsToReplace);

        if (replacedDigits.isEmpty()) {
            sb.delete(0, sb.length());
        } else {
            Iterator<Integer> it = replacedDigits.iterator();
            for (int i = 0; i < indexesToReplace.size() && it.hasNext(); i++) {
                sb.replace(indexesToReplace.get(i), indexesToReplace.get(i) + 1, it.next().toString());
            }
        }
    }

    private String findDigits(StringBuilder sb) {
        StringBuilder stringBuilder = new StringBuilder();
        int totalDigit = 0;
        for (int i = sb.length() - 1; i >= 0; i--) {
            if (Character.isDigit(sb.charAt(i))) {
                if (integerParam > totalDigit)
                    totalDigit++;
                else
                    stringBuilder.append(sb.charAt(i));
            }
        }
        return stringBuilder.toString();
    }

}
