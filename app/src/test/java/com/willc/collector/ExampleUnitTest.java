package com.willc.collector;

import org.junit.Test;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void format_decimal() throws Exception {
        NumberFormat numberFormat = NumberFormat.getInstance(new Locale("zh", "CN"));
        numberFormat.setRoundingMode(RoundingMode.HALF_UP);
        numberFormat.setMaximumFractionDigits(1);


        System.out.println(numberFormat.format(12.3695));
        System.out.println(numberFormat.format(12.2456));
    }
}