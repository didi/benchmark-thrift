package com.pressir.utils;

import com.alibaba.fastjson.JSON;
import com.pressir.idl.Shop;
import com.pressir.idl.ShopStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClassCastUtilsTest {

    private Shop shop = new Shop("shop", ShopStatus.OPEN);
    private List<String> strings = new ArrayList<>();
    private List<String> aList = new ArrayList<>();
    private Set<Shop> aSet = new HashSet<>();
    private HashMap<Integer, Shop> aMap = new HashMap<>();


    public void method(
            byte p,
            boolean p0,
            short p1,
            char p2,
            int p3,
            long p4,
            float p5,
            double p6,
            String p7,
            Shop p8,
            List<String> p9,
            Set<Shop> p10,
            Map<Integer, Shop> p11,
            BigDecimal p12,
            BigInteger p13) {
    }

    @Before
    public void before() {
        strings.add(Byte.toString((byte) 1));
        strings.add("true");
        strings.add(Short.toString((short) 2));
        strings.add(String.valueOf('c'));
        strings.add(String.valueOf(3));
        strings.add(String.valueOf(4));
        strings.add(String.valueOf(5.0));
        strings.add(String.valueOf(6.0));
        strings.add("string");
        strings.add("" + JSON.toJSONString(shop));
        aList.add("list");
        strings.add("" + JSON.toJSONString(aList));
        aSet.add(shop);
        strings.add("" + JSON.toJSONString(aSet));
        aMap.put(7, shop);
        strings.add(JSON.toJSONString(aMap));
        strings.add(JSON.toJSONString(new BigDecimal(13)));
        strings.add(JSON.toJSONString(new BigInteger("14")));

    }

    @Test
    public void test_cast() {
        Method[] methods = ClassCastUtilsTest.class.getMethods();
        Object[] args = strings.toArray();

        for (Method method : methods) {
            if ("method".equals(method.getName())) {
                Type[] types = method.getGenericParameterTypes();
                Object[] arguments = new Object[types.length];
                for (int i = 0; i < types.length; i++) {
                    arguments[i] = ClassCastUtils.cast(args[i], types[i]);
                }
                Assert.assertEquals((byte) 1, arguments[0]);
                Assert.assertEquals(true, arguments[1]);
                Assert.assertEquals((short) 2, arguments[2]);
                Assert.assertEquals('c', arguments[3]);
                Assert.assertEquals(3, arguments[4]);
                Assert.assertEquals((long) 4, arguments[5]);
                Assert.assertEquals((float) 5.0, arguments[6]);
                Assert.assertEquals(6.0, arguments[7]);
                Assert.assertEquals("string", arguments[8]);
                Assert.assertEquals(shop, arguments[9]);
                Assert.assertEquals(aList, arguments[10]);
                Assert.assertEquals(aSet, arguments[11]);
                Assert.assertEquals(aMap, arguments[12]);
                Assert.assertEquals(new BigDecimal(13), arguments[13]);
                Assert.assertEquals(new BigInteger("14"), arguments[14]);
                Assert.assertNull(ClassCastUtils.cast(null, null));

            }
        }
    }
}
