package io.springboard.framework.utils;

import static org.junit.Assert.*;
import io.springboard.framework.utils.ByteUtils;

import java.util.Arrays;
import java.util.BitSet;

import org.junit.Test;

public class ByteUtilsTest {

	@Test
	public void testByte() {
		short s = 122;
		int i = 122;
		long l = 1222222;
		char c = 'a';
		float f = 122.22f;
		double d = 122.22;
		String string = "我是好孩子";

		assertEquals(s, ByteUtils.getShort(ByteUtils.getBytes(s)));
		assertEquals(i, ByteUtils.getInt(ByteUtils.getBytes(i)));
		assertEquals(l, ByteUtils.getLong(ByteUtils.getBytes(l)));
		assertEquals(c, ByteUtils.getChar(ByteUtils.getBytes(c)));
		assertTrue(f == ByteUtils.getFloat(ByteUtils.getBytes(f)));
		assertTrue(d == ByteUtils.getDouble(ByteUtils.getBytes(d)));
		assertEquals(string, ByteUtils.getString(ByteUtils.getBytes(string)));
	}

	@Test
	public void testBitSet() {
		BitSet bitSet = new BitSet();
        bitSet.set(0, true);
        bitSet.set(10, true);
        
        //将BitSet对象转成byte数组
        byte[] bytes = ByteUtils.bitSet2ByteArray(bitSet);
        System.out.println(Arrays.toString(bytes));
         
        //在将byte数组转回来
        BitSet bitSet2 = ByteUtils.byteArray2BitSet(bytes);
        System.out.println(bitSet2.toString());
        System.out.println(bitSet2.get(10));
        
        assertTrue(bitSet.equals(bitSet2));
	}
}
