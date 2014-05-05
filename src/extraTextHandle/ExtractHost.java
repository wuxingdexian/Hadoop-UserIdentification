package extraTextHandle;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractHost {

	/**
	 * 从URL里面提取域名，去掉顶级域名
	 * @param URLString
	 * @return hostString
	 */
	public static String extractHost(String URLString) {
		//String url = "http://anotherbug.blog.chinajavaworld.edu.com.cn/entry/4545/0/";
		Pattern p = Pattern.compile("(?<=http://|\\.)[^.]*?\\.(xxx|jobs|cat|com|corp|net|org|gov|edu|mil|biz|name|info|mobi|pro|travel|museum|int|aero|post|rec|asia|ac|ad|ae|af|ag|ai|al|am|an|ao|aq|ar|as|at|au|aw|az|ba|bb|bd|be|bf|bg|bh|bi|bj|bm|bn|bo|br|bs|bt|bv|bw|by|bz|ca|cc|cf|cd|ch|ci|ck|cl|cm|cn|co|cq|cr|cu|cv|cx|cy|cz|de|dj|dk|dm|do|dz|ec|ee|eg|eh|er|es|et|eu|ev|fi|fj|fk|fm|fo|fr|ga|gd|ge|gf|gg|gh|gi|gl|gm|gn|gp|gr|gs|gt|gu|gw|gy|hk|hm|hn|hr|ht|hu|id|ie|il|im|in|io|iq|ir|is|it|jm|jo|jp|je|ke|kg|kh|ki|km|kn|kp|kr|kw|ky|kz|la|lb|lc|li|lk|lr|ls|lt|lu|lv|ly|ma|mc|md|me|mg|mh|mk|ml|mm|mn|mo|mp|mq|mr|ms|mt|mu|mv|mw|mx|my|mz|na|nc|ne|nf|ng|ni|nl|no|np|nr|nt|nu|nz|om|qa|pa|pe|pf|pg|ph|pk|pl|pm|pn|pr|ps|pt|pw|py|re|rs|ro|ru|rw|sa|sb|sc|sd|se|sg|sh|si|sj|sk|sl|sm|sn|so|sr|st|sv|su|sy|sz|sx|tc|td|tf|tg|th|tj|tk|tl|tm|tn|to|tr|tt|tv|tw|tz|ua|ug|uk|um|us|uy|uz|va|vc|ve|vg|vi|vn|vu|wf|ws|ye|yt|za|zm|zw)[.|/|:]",Pattern.CASE_INSENSITIVE);
		Matcher matcher = p.matcher(URLString);
		matcher.find();
		//System.out.println(matcher.group()); 
		String hostString =null;
		try {
			hostString = matcher.group().substring(0, matcher.group().indexOf("."));
		} catch (Exception e) {
			// TODO: handle exception
			//System.out.println("test.extractHost()" + e + URLString);
		}
		
		//System.out.println(hostString);
		return hostString;
	}
	
	/**
	 * URL里面没有域名的情况，提取IP
	 * @param URLString
	 * @return ipString
	 */
	public static String extractIP(String URLString) {
		Pattern p = Pattern.compile("(?<=http://|\\.)(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)[:|/]",Pattern.CASE_INSENSITIVE);
		Matcher matcher = p.matcher(URLString);
		matcher.find();
		String ipString =null;
		try {
			ipString = matcher.group().substring(0,(-1 != matcher.group().indexOf("/")?matcher.group().indexOf("/"):matcher.group().indexOf(":")));
		} catch (Exception e) {
			// TODO: handle exception
			//System.out.println("test.extractIP()" + e + URLString);
		}
		return ipString;
	}
}
