package com.chenzj.myledger.utils;

import java.math.BigDecimal;

/**
 * @description: TODO
 * @author: chenzj
 * @date: 2022/3/23 18:37
 */
public class ArithUtils {
    /**
     * 取两位小数，四舍五入
     * @param deci
     * @return
     */
    public static double get2plase(double deci){
        BigDecimal bigDecimal = new BigDecimal(deci);
        // 四舍五入，保留两位小数
        return bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double add(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }

    /**
     * 提供精确的减法运算。
     *
     * @param v1
     *            被减数
     * @param v2
     *            减数
     * @return 两个参数的差
     */

    public static double sub(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }
}
