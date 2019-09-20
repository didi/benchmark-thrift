package com.pressir.utils;


import com.alibaba.fastjson.JSON;
import com.pressir.idl.Shop;
import com.pressir.idl.ShopStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ReflectUtilsTest {
    private static Shop shop = new Shop("shop", ShopStatus.OPEN);
    private static List<Shop> aList = Arrays.asList(shop);
    private static Set<Shop> aSet = new HashSet<>(Arrays.asList(shop));
    private static Map<Integer, Shop> aMap = Collections.singletonMap(7, shop);
    private final ThreadLocal<Type> paramType = new ThreadLocal<Type>();
    private Object paramValue;
    private String stringValue;

    public ReflectUtilsTest(Type paramType, Object paramValue, String stringValue) {
        this.paramType.set(paramType);
        this.paramValue = paramValue;
        this.stringValue = stringValue;
    }

    @Parameterized.Parameters
    public static Collection paramTypeValues() {
        return Arrays.asList(new Object[][]{
                {byte.class, (byte) 1, Byte.toString((byte) 1)},
                {boolean.class, true, "true"},
                {short.class, (short) 2, Short.toString((short) 2)},
                {char.class, 'c', String.valueOf('c')},
                {int.class, 3, String.valueOf(3)},
                {long.class, (long) 4, String.valueOf(4)},
                {float.class, (float) 5.0, String.valueOf(5.0)},
                {double.class, 6.0, String.valueOf(6.0)},
                {String.class, "string", "string"},
                {getGenericTypeOf("shop"), shop, "" + JSON.toJSONString(shop)},
                {getGenericTypeOf("aList"), aList, "" + JSON.toJSONString(aList)},
                {getGenericTypeOf("aSet"), aSet, "" + JSON.toJSONString(aSet)},
                {getGenericTypeOf("aMap"), aMap, JSON.toJSONString(aMap)},
                {BigDecimal.class, new BigDecimal(13), JSON.toJSONString(new BigDecimal(13))},
                {BigInteger.class, new BigInteger("14"), JSON.toJSONString(new BigInteger("14"))},
        });
    }

    private static Type getGenericTypeOf(String fieldName) {
        try {
            Type genericType = ReflectUtilsTest.class.getDeclaredField(fieldName).getGenericType();
            return genericType;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Test
    public void testPrimeNumberChecker() {
        System.out.println("Parameterized value is : " + stringValue);
        assertEquals(paramValue, ReflectUtils.cast(stringValue, paramType.get()));
    }

}
