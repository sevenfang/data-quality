package org.talend.dataquality.datamasking.functions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jteuladedenantes on 22/09/16.
 */
public class GenerateUniquePhoneNumberUk extends AbstractGenerateUniquePhoneNumber {

    @Override
    protected int getDigitsNumberToMask() {
        return 7;
    }
}
