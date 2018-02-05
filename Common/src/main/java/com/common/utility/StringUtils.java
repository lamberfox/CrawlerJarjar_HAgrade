package com.common.utility;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils extends org.apache.commons.lang.StringUtils {

	/**
	 * 如果系统中存在旧版本的数据，则此值不能修改，否则在进行密码解析的时候出错
	 */
	private static final String PASSWORD_CRYPT_KEY = "__jDlog_";

	private final static String DES = "DES";

	public final static int GBCODE = 1111;

	public final static int UNICODE = 2222;

	public final static String CONFIG_BUNDLE_NAME = "com.ninetowns.resources.ApplicationConfig";

	private final static String[] strDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d",
			"e", "f" };

	// map转url格式工具
	public static String getMapString(Map<String, String> map) {
		String value = "";
		for (String key : map.keySet()) {
			value = value + key + "=" + map.get(key) + "&";
		}
		return value.substring(0, value.length() - 1);
	}

	// Unix时间戳转换工具
	public static String TimeStamp2Date(String timestampString) {
		String formats = "yyyyMMdd";
		Long timestamp = Long.parseLong(timestampString);
		String date = new SimpleDateFormat(formats).format(new Date(timestamp));
		return date;
	}

	// 替换表情
	public static String filterEmoji(String source, String slipStr) {

		return source.replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", slipStr);
	}

	// 替换字符串中包含的中文单双引号，英文单双引号
	public static String formatStr(String str) {
		return str.replace("“", "").replace("”", "").replace("‘", "").replace("’", "").replace("'", "")
				.replace("\"", "").replace("\\", "");
	}

	// 获得小写16位md5加密后的值
	public static String get16MD5Code(String string) {
		return get32MD5Code(string).substring(8, 24);
	}

	// 获得小写32位md5加密后的值
	public static String get32MD5Code(String strObj) {
		String resultString = null;

		if (!checkNullAndEmpty(strObj)) {
			return "";
		}
		try {
			resultString = new String(strObj);
			MessageDigest md = MessageDigest.getInstance("MD5");
			// md.digest() 该函数返回值为存放哈希值结果的byte数组
			resultString = byteToString(md.digest(strObj.getBytes()));
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		}
		return resultString;
	}

	public static boolean checkNullAndEmpty(String str) {
		if (str == null || str.isEmpty())
			return false;
		return true;
	}

	// 转换字节数组为16进制字串
	private static String byteToString(byte[] bByte) {
		StringBuffer sBuffer = new StringBuffer();
		for (int i = 0; i < bByte.length; i++) {
			sBuffer.append(byteToArrayString(bByte[i]));
		}
		return sBuffer.toString();
	}

	// 返回形式为数字跟字符串
	private static String byteToArrayString(byte bByte) {
		int iRet = bByte;
		// System.out.println("iRet="+iRet);
		if (iRet < 0) {
			iRet += 256;
		}
		int iD1 = iRet / 16;
		int iD2 = iRet % 16;
		return strDigits[iD1] + strDigits[iD2];
	}

	/**
	 * 判断是不是一个合法的电子邮件地址
	 *
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		if (email == null)
			return false;
		email = email.trim();
		if (email.indexOf(' ') != -1)
			return false;

		int idx = email.indexOf('@');
		if (idx == 0 || (idx + 1) == email.length())
			return false;
		if (email.indexOf('@', idx + 1) != -1)
			return false;
		return true;
		/*
		 * Pattern emailer; if(emailer==null){ String check =
		 * "^([a-z0-9A-Z]+[-|\\._]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		 * emailer = Pattern.compile(check); } Matcher matcher =
		 * emailer.matcher(email); return matcher.matches();
		 */
	}

	/**
	 * 加密
	 *
	 * @param src
	 *            数据源
	 * @param key
	 *            密钥，长度必须是8的倍数
	 * @return 返回加密后的数据
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] src, byte[] key) throws Exception {
		// DES算法要求有一个可信任的随机数源
		SecureRandom sr = new SecureRandom();
		// 从原始密匙数据创建DESKeySpec对象
		DESKeySpec dks = new DESKeySpec(key);
		// 创建一个密匙工厂，然后用它把DESKeySpec转换成
		// 一个SecretKey对象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey securekey = keyFactory.generateSecret(dks);
		// Cipher对象实际完成加密操作
		Cipher cipher = Cipher.getInstance(DES);
		// 用密匙初始化Cipher对象
		cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
		// 现在，获取数据并加密
		// 正式执行加密操作
		return cipher.doFinal(src);
	}

	/**
	 * 解密
	 *
	 * @param src
	 *            数据源
	 * @param key
	 *            密钥，长度必须是8的倍数
	 * @return 返回解密后的原始数据
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] src, byte[] key) throws Exception {
		// DES算法要求有一个可信任的随机数源
		SecureRandom sr = new SecureRandom();
		// 从原始密匙数据创建一个DESKeySpec对象
		DESKeySpec dks = new DESKeySpec(key);
		// 创建一个密匙工厂，然后用它把DESKeySpec对象转换成
		// 一个SecretKey对象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey securekey = keyFactory.generateSecret(dks);
		// Cipher对象实际完成解密操作
		Cipher cipher = Cipher.getInstance(DES);
		// 用密匙初始化Cipher对象
		cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
		// 现在，获取数据并解密
		// 正式执行解密操作
		return cipher.doFinal(src);
	}

	/**
	 * 密码解密
	 *
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public final static String decrypt(String data) {
		try {
			return new String(decrypt(hex2byte(data.getBytes()), PASSWORD_CRYPT_KEY.getBytes()));
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 密码加密
	 *
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public final static String encrypt(String password) {
		try {
			return byte2hex(encrypt(password.getBytes(), PASSWORD_CRYPT_KEY.getBytes()));
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 二行制转字符串
	 *
	 * @param b
	 * @return
	 */
	public static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
		}
		return hs.toUpperCase();
	}

	public static byte[] hex2byte(byte[] b) {
		if ((b.length % 2) != 0)
			throw new IllegalArgumentException("长度不是偶数");
		byte[] b2 = new byte[b.length / 2];
		for (int n = 0; n < b.length; n += 2) {
			String item = new String(b, n, 2);
			b2[n / 2] = (byte) Integer.parseInt(item, 16);
		}
		return b2;
	}

	/**
	 * 大小写无关的字符串替换策略
	 *
	 * @param str
	 * @param src
	 * @param obj
	 * @return
	 */
	public static String replaceIgnoreCase(String str, String src, String obj) {
		String l_str = str.toLowerCase();
		String l_src = src.toLowerCase();
		int fromIdx = 0;
		StringBuffer result = new StringBuffer();
		do {
			int idx = l_str.indexOf(l_src, fromIdx);
			if (idx == -1)
				break;
			result.append(str.substring(fromIdx, idx));
			result.append(obj);
			fromIdx = idx + src.length();
		} while (true);
		result.append(str.substring(fromIdx));
		return result.toString();
	}

	/**
	 * 描述: 判断是GB码还是UNICOE码<br>
	 * @ param String<br>
	 * 
	 * @return int <br>
	 */
	public static int isGB_Unicode(String sInput) {
		int i = 0;
		int iChar = 0;
		int iLength = sInput.length();
		while (i < iLength) {
			iChar = (int) sInput.charAt(i);
			if (iChar > 128 || iChar < 0) {
				break;
			}
			i++;
		}
		if (i == iLength) {
			return (GBCODE + UNICODE); // sInput is empty or only ASCII string.
		}
		if ((iChar & 0xff00) != 0xff00 && (iChar & 0xff00) != 0x0) {
			return (UNICODE);
		} else {
			iChar = iChar & 0x00ff;
			if (iChar > 160 && iChar < 248) {
				return (GBCODE);
			} else {
				return (UNICODE);
			}
		}
	}

	/**
	 * 描述: Unicode转为UTF-8码<br>
	 * @ param String<br>
	 * 
	 * @return String<br>
	 */
	public static String UnicodeToGB(String sInput) {
		int i;
		int iLength;
		if (sInput == null)
			return "";

		if (isGB_Unicode(sInput) == UNICODE) {
			return sInput;
		} else {
			iLength = sInput.length();
			byte[] bBytes = new byte[iLength];
			for (i = 0; i < iLength; i++) {
				bBytes[i] = (byte) sInput.charAt(i);
			}
			try {
				return (new String(bBytes, "gb2312"));
			} catch (Exception e) {
				System.out.println("characters convert failed!  " + e);
				return null;
			}
		}
	}

	/**
	 *
	 * 描述: to Trim the String(对字符串进行去空格操作）<br>
	 *
	 * @param string,(需要处理的字符串)
	 *            <br>
	 * @return String,(去掉空格以后的字符串)<br>
	 * @author Qinjunjun<br>
	 * @since 20001-3-30 16:00<br>
	 */
	public static String GS_TrimStr(String strTrim) {
		strTrim = (strTrim != null) ? strTrim.trim() : "";
		return strTrim;
	}

	/**
	 * 转换Null字符为空
	 *
	 * @param strNull
	 * @return
	 */
	public static String nvl(String strNull) {
		strNull = (strNull != null) ? strNull : "";
		return strNull;
	}

	public static String DateToStr(Date date) {
		SimpleDateFormat sdfTemp = new SimpleDateFormat("yyyy-MM-dd");
		String strDate = (date != null ? sdfTemp.format(date) : "");
		return strDate;
	}

	public static String DateTimeToStr(Date date) {
		SimpleDateFormat sdfTemp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String strDate = (date != null ? sdfTemp.format(date) : "");
		return strDate;
	}

	/**
	 * 字符串转为日期
	 *
	 * @param strDate
	 * @return
	 */
	public static Date StringToDate(String strDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		try {
			date = sdf.parse(strDate);
		} catch (Exception e) {
			return date;
		}
		return date;
	}

	/**
	 * 2013-10-1 12:23:24 <br/>
	 * 如果不给字符串则默认返回当前的时间戳
	 * 
	 * @param datatime
	 * @return
	 */
	public static Timestamp StringToTimeStamp(String datatime) {
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		if (datatime == null || "".equals(datatime))
			return ts;
		try {
			ts = Timestamp.valueOf(datatime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ts;
	}

	/**
	 * TimeStamp转换为 String
	 * 
	 * @param timestamp
	 * @return
	 */

	public static String TimeStampToString(Timestamp timestamp) {
		if (timestamp == null)
			return "";
		return timestamp.toLocaleString();
	}

	public static Date StringToDateTime(String strDateTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		try {
			date = sdf.parse(strDateTime);
		} catch (Exception e) {
			return date;
		}
		return date;
	}

	/**
	 * 计算之前日期时间
	 *
	 * @param date
	 * @return
	 */
	public static String beforeDate(String date) throws Exception {
		if (date == null)
			return null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		// ParsePosition pos = new ParsePosition(0);
		// 获得当前时间
		Calendar c = Calendar.getInstance();
		// 取出“日”数
		int d = c.get(Calendar.DAY_OF_MONTH);
		// 得到之前天数
		d -= Integer.parseInt(date);
		c.set(Calendar.DAY_OF_MONTH, d);// 将“日”数设置回去
		return df.format(c.getTime());
	}

	/**
	 * 返回当前星期
	 *
	 * @return
	 * @throws Exception
	 */
	public static String getWeek() throws Exception {
		String week = "";
		Date date = new Date();
		int wk = date.getDay();

		switch (wk) {
		case 0:
			week = "星期日";
			break;
		case 1:
			week = "星期一";
			break;
		case 2:
			week = "星期二";
			break;
		case 3:
			week = "星期三";
			break;
		case 4:
			week = "星期四";
			break;
		case 5:
			week = "星期五";
			break;
		case 6:
			week = "星期六";
			break;
		}
		return week;
	}

	/**
	 * 返回当前日期字符串
	 *
	 * @return日期字符
	 */
	public static String nowDate() throws Exception {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		return df.format(c.getTime());
	}

	/**
	 * 返回当前时间
	 *
	 * @return
	 */
	public static String nowTime() throws Exception {
		SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss");
		Calendar c = Calendar.getInstance();
		return df.format(c.getTime());
	}

	/**
	 * 返回当前日期时间字符串
	 *
	 * @return日期时间字符
	 */
	public static String nowDateTime() throws Exception {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar c = Calendar.getInstance();
		return df.format(c.getTime());
	}

	/**
	 * 返回当前日期
	 *
	 * @return日期
	 */
	public static Date currentDate() throws Exception {
		Calendar c = Calendar.getInstance();
		return c.getTime();
	}

	/**
	 * 将给出的字符串source使用delim划分为单词数组
	 *
	 * @param source
	 * @param delim
	 * @return 划分以后的数组,如果delim为null则使用逗号作为分隔字符串
	 */
	public static String[] stringToStringArray(String source, String delim) {
		if (source == null)
			return null;
		if (delim == null)
			delim = ",";
		// source = UnicodeToGB(source);
		String[] strArray = split(source, delim);
		return strArray;
	}

	public static String[] stringTokenToStringArray(String source, String delim) {
		StringTokenizer token = new StringTokenizer(source, delim);
		String[] array = new String[token.countTokens()];
		if (source == null)
			return null;
		if (delim == null)
			delim = ",";
		int i = 0;
		while (token.hasMoreTokens()) {
			array[i] = token.nextToken();
			i++;
		}
		return array;

	}

	/**
	 * 删除已经存在的文件
	 *
	 * @param str
	 */
	public static void delFile(String str) {
		File file = new File(str);
		if (file.exists()) {
			file.delete();
		}

	}

	/**
	 * 文件拷贝
	 *
	 * @param url1
	 *            源文件
	 * @param url2
	 *            目标文件
	 * @return
	 */
	public static boolean copyFile(String url1, String url2) {
		try {
			(new File(url2)).mkdirs();
			File file = new File(url1);
			FileInputStream input = new FileInputStream(file);
			FileOutputStream output = new FileOutputStream(url2 + "/" + file.getName());
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = input.read(b)) != -1) {
				output.write(b, 0, len);
			}
			output.flush();
			output.close();
			input.close();
		} catch (Exception e) {
			System.out.println("Error!---------------" + e.getMessage());
			return (false); // if fail then return false
		}
		return (true); // if success then return true
	}

	/**
	 * 根据配置文件读取配置值
	 *
	 * @param key
	 * @return
	 */
	public static String getValue(String key) {
		String value = "";
		PropertyResourceBundle configBundle = (PropertyResourceBundle) PropertyResourceBundle
				.getBundle(CONFIG_BUNDLE_NAME);
		value = configBundle.getString(key);
		return value;
	}

	/**
	 * 根据配置文件读取配置值
	 *
	 * @param key
	 * @return
	 */
	public static String getValue(String bundleName, String key) {
		String value = "";
		PropertyResourceBundle configBundle = (PropertyResourceBundle) PropertyResourceBundle.getBundle(bundleName);
		value = configBundle.getString(key);
		return value;
	}

	public static String FilterHTML(String HtmlString) {
		while (HtmlString.indexOf(">") > 0) {
			int begin = HtmlString.indexOf("<");
			int end = HtmlString.indexOf(">");
			HtmlString = HtmlString.substring(0, begin) + HtmlString.substring(end + 1);
		}
		HtmlString.replaceAll("v\\:* {behavior:url(#default#VML);}", "");
		HtmlString.replaceAll("o\\:* {behavior:url(#default#VML);}", "");
		HtmlString.replaceAll("w\\:* {behavior:url(#default#VML);}", "");
		HtmlString.replaceAll(".shape {behavior:url(#default#VML);}", "");
		return HtmlString;
	}

	/**
	 * 取出HTML代码
	 *
	 * @param Htmlstring
	 * @return
	 */
	public static String RMHTML(String Htmlstring)

	{

		// 删除脚本
		Htmlstring = RegexPattern("<\\s*?script[^>]*>[\\s\\S]*?<\\s*?/\\s*?script\\s*?>", "", Htmlstring);

		// 删除HTML

		Htmlstring = RegexPattern("<([^>]*)>", "", Htmlstring);

		Htmlstring = RegexPattern("([\r\n])[\\s]+", "", Htmlstring);

		Htmlstring = RegexPattern("-->", "", Htmlstring);

		Htmlstring = RegexPattern("<!--.*", "", Htmlstring);

		Htmlstring = RegexPattern("&(quot|#34);", "\"", Htmlstring);

		Htmlstring = RegexPattern("&(amp|#38);", "&", Htmlstring);

		Htmlstring = RegexPattern("&(lt|#60);", "<", Htmlstring);

		Htmlstring = RegexPattern("&(gt|#62);", ">", Htmlstring);

		Htmlstring = RegexPattern("&(nbsp|#160);", " ", Htmlstring);

		Htmlstring = RegexPattern("&(iexcl|#161);", "\\xa1", Htmlstring);

		Htmlstring = RegexPattern("&(cent|#162);", "\\xa2", Htmlstring);

		Htmlstring = RegexPattern("&(pound|#163);", "\\xa3", Htmlstring);

		Htmlstring = RegexPattern("&(copy|#169);", "\\xa9", Htmlstring);

		Htmlstring = RegexPattern("&#(\\d+);", "", Htmlstring);

		Htmlstring = RegexPattern("<", "", Htmlstring);

		Htmlstring = RegexPattern(">", "", Htmlstring);

		// Htmlstring.replace("\r\n", "",Htmlstring);

		return Htmlstring;

	}

	public static String RegexPattern(String pattern, String str, String content) {

		if (pattern != null && !pattern.equals("")) {

			Pattern p = Pattern.compile(pattern, 2); // 参数2表示大小写不区分

			Matcher m = p.matcher(content);
			content = m.replaceAll(str);

		}
		return content;
	}

	public static String getGetMonthGb2312(String str) {
		String month = "";
		if (str.equals("1") || str.equals("01"))
			month = "一月";
		if (str.equals("2") || str.equals("02"))
			month = "二月";
		if (str.equals("3") || str.equals("03"))
			month = "三月";
		if (str.equals("4") || str.equals("04"))
			month = "四月";
		if (str.equals("5") || str.equals("05"))
			month = "五月";
		if (str.equals("6") || str.equals("06"))
			month = "六月";
		if (str.equals("7") || str.equals("07"))
			month = "七月";
		if (str.equals("8") || str.equals("08"))
			month = "八月";
		if (str.equals("9") || str.equals("09"))
			month = "九月";
		if (str.equals("10"))
			month = "十月";
		if (str.equals("11"))
			month = "十一月";
		if (str.equals("12"))
			month = "十二月";
		return month;
	}

	/**
	 * 得到相减天数
	 *
	 * @param enddate
	 * @param begindate
	 * @return
	 */
	public static int getIntervalDays(Date enddate, Date begindate) {
		long millisecond = enddate.getTime() - begindate.getTime();
		int day = (int) (millisecond / 24L / 60L / 60L / 1000L);
		return day;
	}

	public static void main(String[] args) {
		String html = "";
		try {
			BufferedReader in = new BufferedReader(new FileReader("c:\\广州局内外网站方案.htm"));
			String s = "";
			while ((s = in.readLine()) != null) {
				html += s + "\n";

				if (html.length() > 10000) {
					StringUtils.RMHTML(html);
					break;
				}

			}
			in.close();

			// while ( html.indexOf(">")>0){
			// int begin=html.indexOf("<");
			// int end=html.indexOf(">");
			// html=html.substring(0,begin)+html.substring(end+1);
			// }
			// StringUtils.RMHTML(html);
			// html="<html>sdfsfsq<font
			// color='sss'>white</font><spanlang=EN-US>文档目的</span></html>";
			System.out.println(html);
			// System.out.println(StringUtils.RMHTML(html));
		} catch (Exception e) {

		}
		/*
		 * String pwd = "测试dasdfaaaaaaa"; String data = encrypt(pwd);
		 * System.out.println("data="+data); pwd = decrypt(data);
		 * System.out.println("pwd="+pwd);
		 * 
		 * System.out.println(replaceIgnoreCase("public class StringUtilsTest
		 * extends TestCase","clAss","inTerface"));
		 * 
		 * System.out.println(isEmail("liudong@mo168.com"));
		 * System.out.println(isEmail("liu@ong@mo168.com"));
		 */
		/*
		 * SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		 * ParsePosition par = new ParsePosition(0); Calendar c =
		 * Calendar.getInstance(); // 当时的日期和时间
		 * 
		 * Date date = df.parse(df.format(c.getTime()),par); System.out.println(
		 * "date = "+df.format(c.getTime()));
		 * System.out.println(df.format(c.getTime()));
		 * 
		 * int d = c.get(Calendar.DAY_OF_MONTH); // 取出“日”数
		 */
		// --d;
		// d -= 30;// 将“日”减一，即得到前一天
		// c.set(Calendar.DAY_OF_MONTH, d); // 将“日”数设置回去
		// System.out.println(df.format(c.getTime()));
		// c.set(Calendar.DAY_OF_MONTH, 0); // 如果当前日期是1日的情况会怎么样？
		// 这里演示了这种情况(这里情况下1-1=0所以直接赋0值了)
		// System.out.println(df.format(c.getTime())); // 这里打印了上个月的最后一天。
		/*
		 * String ss = "safdsadf sdfsdf sdfadsf"; String[] sarray =
		 * stringToStringArray(ss," "); for(int i=0;i<sarray.length;i++)
		 * System.out.println(sarray[i]);
		 */
		// System.out.print("http//127.0.0.1:80/NewsManage/UploadFile/200603010300080203.jpg".lastIndexOf("/"));
	}

	public static List getMustLength(List list, int length) {
		int i = 0;
		List mustList = new ArrayList();
		String strlength = "";
		if (list != null) {
			while (list.iterator().hasNext()) {
				strlength = list.get(i).toString();
				if (strlength.length() < length || strlength.length() == length) {
					mustList.add(strlength);
				} else {
					mustList.add(strlength.substring(0, length - 3) + "...");
				}

			}
			return mustList;
		}
		return list;

	}
}
