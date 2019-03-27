package org.talend.dataquality.datamasking;

public enum FunctionMode {
    RANDOM,
    CONSISTENT,
    BIJECTIVE_BASIC,
    BIJECTIVE_AES_CBC_PRF,
    BIJECTIVE_SHA2_HMAC_PRF
}
