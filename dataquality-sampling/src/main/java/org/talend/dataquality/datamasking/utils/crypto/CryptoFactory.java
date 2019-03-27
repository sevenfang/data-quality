package org.talend.dataquality.datamasking.utils.crypto;

import javax.crypto.SecretKey;

import org.talend.dataquality.datamasking.FunctionMode;

/**
 * Factory for constructing the {@link AbstractPrf} and {@link AbstractCryptoSpec}
 * according to the context given by the {@link FunctionMode}.
 *
 * @author afournier
 * @see AbstractCryptoSpec
 * @see AbstractPrf
 * @see org.talend.dataquality.datamasking.SecretManager
 */
public class CryptoFactory {

    /**
     * Returns the correct instance of {@link AbstractCryptoSpec}
     * according to the context given by the {@link FunctionMode}.
     */
    public AbstractCryptoSpec getPrfSpec(FunctionMode method) {
        AbstractCryptoSpec cryptoSpec = null;

        switch (method) {
        case BIJECTIVE_AES_CBC_PRF:
            cryptoSpec = new AesCbcCryptoSpec();
            break;
        case BIJECTIVE_SHA2_HMAC_PRF:
            cryptoSpec = new HmacSha2CryptoSpec();
            break;
        default:
            break;
        }

        return cryptoSpec;
    }

    /**
     * Returns the correct keyed instance of {@link AbstractPrf)
     * according to the instance of {@link AbstractCryptoSpec}
     */
    public AbstractPrf getPrf(AbstractCryptoSpec spec, SecretKey secret) {
        AbstractPrf prf = null;

        if (spec instanceof AesCbcCryptoSpec) {
            prf = new AesPrf(spec, secret);
        } else if (spec instanceof HmacSha2CryptoSpec) {
            prf = new HmacPrf(spec, secret);
        }

        return prf;
    }
}
