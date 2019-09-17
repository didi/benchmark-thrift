package com.pressir.client;

import com.google.common.net.HostAndPort;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.*;

public class OneHostAndPortTest {

    @Before
    public void before(){
        OneHostAndPort.hostAndPortList = Collections.singletonList(HostAndPort.fromString("127.0.0.1:8090"));
    }
    @Test
    public void should_return_one_address() {
        Assert.assertEquals(HostAndPort.fromString("127.0.0.1:8090"), OneHostAndPort.getOne());
    }
}
