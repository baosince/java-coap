package com.arm.mbed.commons.lwm2m;

import static org.junit.Assert.*;
import com.arm.mbed.commons.lwm2m.LWM2MID;
import com.arm.mbed.commons.lwm2m.LWM2MResourceInstance;
import org.junit.Test;

public class LWM2MResourceInstanceTest {

    @Test
    public void createByString() {
        LWM2MResourceInstance instance = new LWM2MResourceInstance(LWM2MID.from(1), "instance");
        assertEquals(1, instance.getId().intValue() );
        assertTrue(instance.hasValue() );
        assertArrayEquals("instance".getBytes(), instance.getValue() );
    }
    
    @Test
    public void createByInteger() throws Exception {
        LWM2MResourceInstance instance = new LWM2MResourceInstance(LWM2MID.from(1), 42);
        assertEquals(1, instance.getId().intValue() );
        assertTrue(instance.hasValue() );
        assertArrayEquals(new byte[] { 42 }, instance.getValue() );
    }
    
    @Test
    public void createBy2ByteInteger() throws Exception {
        LWM2MResourceInstance instance = new LWM2MResourceInstance(LWM2MID.from(1), 554);
        assertEquals(1, instance.getId().intValue() );
        assertTrue(instance.hasValue() );
        assertEquals ("554", instance.getStringValue() );
        assertArrayEquals(new byte[] { 2, 42 }, instance.getValue() );
    }
    
    @Test
    public void createBy3ByteInteger() throws Exception {
        LWM2MResourceInstance instance = new LWM2MResourceInstance(LWM2MID.from(1), 65538);
        assertEquals(1, instance.getId().intValue() );
        assertTrue(instance.hasValue() );
        assertEquals ("65538", instance.getStringValue() );
        assertArrayEquals(new byte[] { 1, 0, 2 }, instance.getValue() );
    }
    
    @Test
    public void createBy4ByteInteger() throws Exception {
        LWM2MResourceInstance instance = new LWM2MResourceInstance(LWM2MID.from(1), 16777730);
        assertEquals(1, instance.getId().intValue() );
        assertTrue(instance.hasValue() );
        assertEquals ("16777730", instance.getStringValue() );
        assertArrayEquals(new byte[] { 1, 0, 2, 2 }, instance.getValue() );
    }
    
    @Test
    public void createByOpaque() throws Exception {
        byte[] opaque = "opaque".getBytes();
        LWM2MResourceInstance instance = new LWM2MResourceInstance(LWM2MID.from(1), opaque);
        assertEquals(1, instance.getId().intValue() );
        assertTrue(instance.hasValue() );
        assertArrayEquals(opaque, instance.getValue() );
    }
    
    @SuppressWarnings("unused")
    @Test(expected=NullPointerException.class)
    public void createWithNullID() throws Exception {
        new LWM2MResourceInstance(null);
    }
    
    @SuppressWarnings("unused")
    @Test(expected=IllegalArgumentException.class)
    public void createWithNullValue() throws Exception {
        new LWM2MResourceInstance(LWM2MID.from(1), (byte[]) null);
    }
    
    @SuppressWarnings("unused")
    @Test(expected=IllegalArgumentException.class)
    public void createWithEmptyValue() throws Exception {
        new LWM2MResourceInstance(LWM2MID.from(1), new byte[0]);
    }
    
    @Test
    public void createWithNegativeID() throws Exception {
        LWM2MResourceInstance instance = new LWM2MResourceInstance(LWM2MID.create(), 42);
        assertEquals(-1, instance.getId().intValue() );
    }

}