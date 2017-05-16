package io.springboard.framework.utils.encode;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 * 各种格式的编码解码工具类.
 * 
 * 集成Commons-Codec,Commons-Lang及JDK提供的编解码方法.
 * 集成Xss攻击字符串的编码方法
 * 
 * @author fanjun
 */
public class EncodeUtils {

	private static final String DEFAULT_URL_ENCODING = "UTF-8";

	/**
	 * Hex编码.
	 */
	public static String hexEncode(byte[] input) {
		return Hex.encodeHexString(input);
	}
	/**
	 * Hex解码.
	 */
	public static byte[] hexDecode(String input) {
		try {
			return Hex.decodeHex(input.toCharArray());
		} catch (DecoderException e) {
			throw new IllegalStateException("Hex Decoder exception", e);
		}
	}
	/**
	 * Base64编码.
	 */
	public static String base64Encode(byte[] input) {
		return new String(Base64.encodeBase64(input));
	}

	/**
	 * Base64编码, URL安全(将Base64中的URL非法字符如+,/=转为其他字符, 见RFC3548).
	 */
	public static String base64UrlSafeEncode(byte[] input) {
		return Base64.encodeBase64URLSafeString(input);
	}
	/**
	 * Base64解码.
	 */
	public static byte[] base64Decode(String input) {
		return Base64.decodeBase64(input);
	}

	/**
	 * URL 编码, Encode默认为UTF-8. 
	 */
	public static String urlEncode(String input) {
		try {
			return URLEncoder.encode(input, DEFAULT_URL_ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Unsupported Encoding Exception", e);
		}
	}
	/**
	 * URL 解码, Encode默认为UTF-8. 
	 */
	public static String urlDecode(String input) {
		try {
			return URLDecoder.decode(input, DEFAULT_URL_ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Unsupported Encoding Exception", e);
		}
	}

	/**
	 * Html 转码,中文也会被转码
	 */
	public static String htmlEscape(String html) {
		return StringEscapeUtils.escapeHtml4(html);
	}
	/**
	 * Html 解码.
	 */
	public static String htmlUnescape(String htmlEscaped) {
		return StringEscapeUtils.unescapeHtml4(htmlEscaped);
	}

	/**
	 * Xml 转码.
	 */
	public static String xmlEscape(String xml) {
		return StringEscapeUtils.escapeXml(xml);
	}
	/**
	 * Xml 解码.
	 */
	public static String xmlUnescape(String xmlEscaped) {
		return StringEscapeUtils.unescapeXml(xmlEscaped);
	}
	
	/**
	 * JavaScript 转码.
	 */
	public static String javaScriptEscape(String javaScript){
		return StringEscapeUtils.escapeEcmaScript(javaScript);
	}
	/**
	 * JavaScript 解码
	 */
	public static String javaScriptUnescape(String javaScriptEscaped){
		return StringEscapeUtils.unescapeEcmaScript(javaScriptEscaped);
	}
	
	/**
	 * JavaScript Html 同时转码,中文也会被转码
	 */
	public static String htmljsEscape(String htmljs) {
		return StringEscapeUtils.escapeEcmaScript(StringEscapeUtils.escapeHtml4(htmljs));
	}
	/**
	 * JavaScript Html 同时解码
	 */
	public static String htmljsUnescape(String htmljs) {
		return StringEscapeUtils.unescapeEcmaScript(StringEscapeUtils.unescapeHtml4(htmljs));
	}
	
	/**
	 * 全角编码
	 * 将容易引起xss漏洞的半角字符直接替换成全角字符
	 * 
	 * @param s
	 * @return
	 */
	public static String fullwidthEncode(String s) {
		if (s == null || "".equals(s)) {
			return s;
		}
		StringBuilder sb = new StringBuilder(s.length() + 16);
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
			case '>':
				sb.append('＞');// 全角大于号
				break;
			case '<':
				sb.append('＜');// 全角小于号
				break;
			case '\'':
				sb.append('‘');// 全角单引号
				break;
//			case '\"':
//				sb.append('“');// 全角双引号
//				break;
			case '&':
				sb.append('＆');// 全角
				break;
//			case '\\':
//				sb.append('＼');// 全角斜线
//				break;
			case '#':
				sb.append('＃');// 全角井号
				break;
			default:
				sb.append(c);
				break;
			}
		}
		return sb.toString();
	}
	/**
	 * 全角解码
	 * 对应@EncodeUtils.fullwidthEncode(String s)
	 * 
	 * @param s
	 * @return
	 */
	public static String fullwidthDecode(String s) {
		if (s == null || "".equals(s)) {
			return s;
		}
		StringBuilder sb = new StringBuilder(s.length() + 16);
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
			case '＞':
				sb.append('>');// 全角大于号
				break;
			case '＜':
				sb.append('<');// 全角小于号
				break;
			case '‘':
				sb.append('\'');// 全角单引号
				break;
//			case '“':
//				sb.append('\"');// 全角双引号
//				break;
			case '＆':
				sb.append('&');// 全角
				break;
//			case '＼':
//				sb.append('\\');// 全角斜线
//				break;
			case '＃':
				sb.append('#');// 全角井号
				break;
			default:
				sb.append(c);
				break;
			}
		}
		return sb.toString();
	}
	
	/**
	 * xss转码，具体转码实现使用EncodeUtils中的方法
	 * @param s
	 * @return
	 */
	public static String xssEncode(String s) {
		return fullwidthEncode(s);
	}
	/**
	 * xss解码，具体解码实现使用EncodeUtils中的方法
	 * @param s
	 * @return
	 */
	public static String xssDecode(String s) {
		return fullwidthDecode(s);
	}
}
