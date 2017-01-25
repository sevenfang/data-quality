package org.talend.dataquality.semantic.recognizer;

import java.io.IOException;

/**
 * Created by jteuladedenantes on 24/01/17.
 */
public interface CategoryRecognizerBuilderInterface {

    void initIndex();

    CategoryRecognizer build() throws IOException;
}
