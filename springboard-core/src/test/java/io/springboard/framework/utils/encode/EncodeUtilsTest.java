package io.springboard.framework.utils.encode;

import static org.junit.Assert.assertEquals;
import io.springboard.framework.utils.encode.EncodeUtils;

import org.junit.Test;

public class EncodeUtilsTest {
	
	@Test
	public void hexEncode() {
		String input = "haha,i am a very long message";
		String result = EncodeUtils.hexEncode(input.getBytes());
		assertEquals(input, new String(EncodeUtils.hexDecode(result)));
	}

	@Test
	public void base64Encode() {
		String input = "haha,i am a very long message";
		String result = EncodeUtils.base64Encode(input.getBytes());
		assertEquals(input, new String(EncodeUtils.base64Decode(result)));
	}

	@Test
	public void base64UrlSafeEncode() {
		String input = "haha,i am a very long message";
		String result = EncodeUtils.base64UrlSafeEncode(input.getBytes());
		assertEquals(input, new String(EncodeUtils.base64Decode(result)));
		System.out.println("base64UrlSafeEncode()");
		System.out.println("input:\t" + input);
		System.out.println("resutl:\t" + result);
	}

	@Test
	public void urlEncode() {
		String input = "http://locahost/?q=中文";
		String result = EncodeUtils.urlEncode(input);
		assertEquals(input, EncodeUtils.urlDecode(result));
		System.out.println("urlEncode()");
		System.out.println("input:\t" + input);
		System.out.println("resutl:\t" + result);
	}

	@Test
	public void xmlEncode() {
		String input = "1>2";
		String result = EncodeUtils.xmlEscape(input);
		assertEquals("1&gt;2", result);
		assertEquals(input, EncodeUtils.xmlUnescape(result));
	}

	@Test
	public void htmlEncode() {
		String input = "中文<script src=\"http://www.yunsec.net/template/pcgames/header_js.jsp?title=',alert(123),'\"></script>\\\\";
		String result = EncodeUtils.htmlEscape(input);
		assertEquals(input, EncodeUtils.htmlUnescape(result));
		System.out.println("htmlEncode()");
		System.out.println("input:\t" + input);
		System.out.println("resutl:\t" + result);
	}
	
	@Test
	public void jsEncode() {
		String input = "{a:'abc',b:'cde'};中文winId=jobDetailWin&id='+id+''%2Balert%2852241%29%\\\\;";
		String result = EncodeUtils.javaScriptEscape(input);
		assertEquals(input, EncodeUtils.javaScriptUnescape(result));
		System.out.println("jsEncode()");
		System.out.println("input:\t" + input);
		System.out.println("resutl:\t" + result);
	}
	
	@Test
	public void htmljs(){
		String input = "\"'+id+''%2Balert%2852241%29%2B'+ <>'\"&+中文";
		String result = EncodeUtils.htmljsEscape(input);
		assertEquals(input, EncodeUtils.htmljsUnescape(result));
		System.out.println("htmljs()");
		System.out.println("input:\t" + input);
		System.out.println("resutl:\t" + result);
	}
	
	@Test
	public void htmljs2(){
		String input = "|&;$%@'\"\\'\\\"<>()+,\\";
		String result = EncodeUtils.htmljsEscape(input);
		assertEquals(input, EncodeUtils.htmljsUnescape(result));
		System.out.println("htmljs2()");
		System.out.println("input:\t" + input);
		System.out.println("resutl:\t" + result);
	}
	
	@Test
	public void htmljs3(){
		String input = "[{\"kpiCode\":\"1101\",\"kpiDate\":\"2013-12-27\",\"sost\":10,\"tst\":30,\"uost\":20},{\"kpiCode\":\"2001001\",\"kpiDate\":\"2014-06-09\",\"nst\":\"100\",\"tnt\":\"100\"}]";
		String result = EncodeUtils.htmljsEscape(input);
		assertEquals(input, EncodeUtils.htmljsUnescape(result));
		System.out.println("htmljs3()");
		System.out.println("input:\t" + input);
		System.out.println("resutl:\t" + result);
	}

	@Test
	public void xss() {
		String input = "<>'\"&\\#" + "\n";
		input += "[{\"kpiCode\":\"1101\",\"kpiDate\":\"2013-12-27\",\"sost\":10,\"tst\":30,\"uost\":20},{\"kpiCode\":\"2001001\",\"kpiDate\":\"2014-06-09\",\"nst\":\"100\",\"tnt\":\"100\"}]";
		String result = EncodeUtils.xssEncode(input);
		System.out.println("xss()");
		System.out.println("input:\t" + input);
		System.out.println("resutl:\t" + result);
	}
}
