package com.milaboratory.mitcr.cli;

import com.milaboratory.mitcr.pipeline.ParameterPresets;
import com.milaboratory.mitcr.pipeline.Parameters;
import org.junit.Assert;
import org.junit.Test;

public class ParametersIOTest {
    @Test
    public void test1() throws Exception {
        Parameters params = ParameterPresets.
                getFlex();
        String paramsString = ParametersIO.
                exportParametersToString(params);
        Assert.assertEquals(-1,
                paramsString.indexOf('\n'));
        Assert.assertEquals(params,
                ParametersIO.importParametersFromString(paramsString));

    }
}
