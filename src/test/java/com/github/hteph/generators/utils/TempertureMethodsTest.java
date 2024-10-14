package com.github.hteph.generators.utils;

import com.github.hteph.starsys.service.generators.utils.TempertureMethods;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TempertureMethodsTest {

    @Test
    void getDayTempCurve() {



        var test = TempertureMethods.getDayTempCurve(24, 1, 15);

        assertNotNull(test);


    }
}