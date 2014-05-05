package EncodeDecode;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
/**
 * 用于解码cookie中的经过utf-8编码的数据，解码后的数据可用户寻找用户账户
 * @author SH
 *
 */
public class URLEncodeTest {

	public static void main(String[] args) throws UnsupportedEncodingException {
		
		String name=java.net.URLEncoder.encode("清水泡泡19700", "UTF-8"); //清水泡泡19700
		decode(name);
		
		String str = "中_国人sss_0民123";
		System.out.println(RegAccount(str));
		
		String email = "dhshenchanggan@163.com";
		System.out.println(RegEmail(email));
		
		String characterString = "!|@asdfafd|#|asdfa$|%|\\^";// !|@|#|\\$|%|\\^|&|\\*|(|)|\\+|=|_|\\-|~|`|?|>|<|/|.|,|\|:|;
		System.out.println(RegIllegalCharacter(characterString));
	}
	/**
	 * 解码 其中包括 utf-8 或unicode 的解码
	 * @param encodedString
	 * @return
	 */
	public static String decode(String encodedString) {
		String decodedString = null;
		try {
			if (encodedString.contains("\\u")) {
				//System.out.println("unicode" + StringEscapeUtils.unescapeJava(encodedString));
				decodedString = StringEscapeUtils.unescapeJava(encodedString);
			}
            else {
				//System.out.println("utf-8" + URLDecoder.decode(encodedString,"UTF-8"));
				decodedString = URLDecoder.decode(encodedString, "utf-8");
			}
			
		} catch (Exception  e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		return decodedString;
	}
	
	/**
	 * 用户名的匹配，包含中文 英文 数字 下划线
	 * @param str
	 * @return
	 */
	public static boolean RegAccount(String str) {
		boolean mark = false;
		//System.out.println(str.length());
		try{// "^(\\w[\u4E00-\u9FA5]_)${3,15}"
		Pattern pattern = Pattern.compile("^[\\w|[\u4E00-\u9FA5]|_]{3,15}"); // 注意 {n,m}出现n到m此，前面需要中括号包围[]
		Matcher matc = pattern.matcher(str); // "^(([a-z]*)([A-Z]*)([0-9]*)([\u4E00-\u9FA5]*_*))+(([a-z]*)([A-Z]*)([0-9]*)([\u4E00-\u9FA5]*_*))${3,15}"
		/*
		StringBuffer stb = new StringBuffer();
		while (matc.find()) {
			mark = true;
			stb.append(matc.group());
		}
		
		if (mark) {
			System.out.println("匹配的字符串为：" + stb.toString());
		}
		if (stb.toString().length() != str.length()) {
			mark = false;
			System.out.println(stb.toString().length() + " " + str.length());
		}  */
		
		mark = matc.matches();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return mark;
	}
	
	/**
	 * 匹配包含邮箱形式的串，因为邮箱可能包含于一长串数据中
	 * @param
	 * @return 包含则true 否则false
	 */
	public static boolean RegEmail(String str) {
		boolean isMatched = false;
		try{
			   
			String check = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(str);
			isMatched = matcher.matches();
			 
		}
		 catch(Exception e){
			 System.out.println("error"+e.getMessage());
		 }
		return isMatched;
	}
	
	/**
	 * 匹配是否包含有特殊字符，如
	 * [!|#|\\$|%|\\^|&|\\*|(|)|\\+|=|~|`|?|>|<|/|.|,|\\\\|;]
	 * 根据value有特殊字符，来排除一个字段key为账户字段
	 * 注意：取消对“@”和“_”的单独判断，因为其可用于账户正则
	 * @param str
	 * @return 包含则true  否则false
	 */
	public static boolean RegIllegalCharacter(String str) {
		boolean isMatched = false;
		
		try{
			   
			String check = "[!|\\$|%|\\^|&|\\*|\\+|~|`|?|>|<|+]+";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(str);
			while (matcher.find()) {
				isMatched = true;
				//System.out.println(matcher.group());
				//stb.append(matcher.group());
			}
			 
		}
		 catch(Exception e){
			 System.out.println("error"+e.getMessage());
		 }
		
		return isMatched;
	}
}