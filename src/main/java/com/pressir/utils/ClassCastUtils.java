package com.pressir.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.util.TypeUtils;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @ClassName ClassCastUtils
 * @Description TODO
 * @Author didi
 * @Date 2019-09-03 11:32
 */
public class ClassCastUtils {

    public static <T> T cast(Object obj, Type type) {
        if (obj == null) {
            return null;
        }
        if (type == String.class) {
            return (T) obj.toString();
        }
        if (type == byte.class || type == Byte.class) {
            return (T) TypeUtils.castToByte(obj);
        }
        if (type == short.class || type == Short.class) {
            return (T) TypeUtils.castToShort(obj);
        }
        if (type == int.class || type == Integer.class) {
            return (T) TypeUtils.castToInt(obj);
        }
        if (type == long.class || type == Long.class) {
            return (T) TypeUtils.castToLong(obj);
        }
        if (type == float.class || type == Float.class) {
            return (T) TypeUtils.castToFloat(obj);
        }
        if (type == double.class || type == Double.class) {
            return (T) TypeUtils.castToDouble(obj);
        }
        if (type == char.class || type == Character.class) {
            return (T) TypeUtils.castToChar(obj);
        }
        if (type == boolean.class || type == Boolean.class) {
            return (T) TypeUtils.castToBoolean(obj);
        }
        if (type == BigDecimal.class) {
            return (T) TypeUtils.castToBigDecimal(obj);
        }
        if (type == BigInteger.class) {
            return (T) TypeUtils.castToBigInteger(obj);
        }
        return JSON.parseObject(obj.toString(), type);
    }
}
