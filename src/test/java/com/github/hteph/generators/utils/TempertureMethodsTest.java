package com.github.hteph.generators.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TempertureMethodsTest {

    @Test
    void getDayTempCurve() {



        var test = TempertureMethods.getDayTempCurve(24,1, 15);

        assertNotNull(test);


    }
}