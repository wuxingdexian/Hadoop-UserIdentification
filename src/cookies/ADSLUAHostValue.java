package cookies;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import extraTextHandle.ExtractHost;
import extraTextHandle.ExtractHost_Key;

/**
 * 网站用户账户识别第一步：(1)数据分组 (2)提取符合条件的5行记录 (3)提取5行记录前后增加的字段
 * 输入cookies
 * @author dhu
 *
 */
public class ADSLUAHostValue {
	/**
	 * 分组：(ADSL,UA,HOST,VALUE)  
	 * 
	 * @author dhu
	 *
	 */
	public static class ADSLUAHostValueMapper extends
			Mapper<Object, Text, Text, Text> {
		
		Configuration conf;
		private static String hostString;
		private static String keyOfHost;
		public void setup(Context context) {
			conf = context.getConfiguration();
			keyOfHost = conf.get("keyOfHost");
			hostString = conf.get("host");
		}

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			
			String[] keywords = { "login", "passwd", "password", "passport",
					"register", " account", "user", "signup", "forgot", "reg",
					"accounts", "safe", "findPwd", "callpwd", "findpass",
					"pass", "safe" };
			boolean condition = false;

			String[] record = value.toString().split("	");
			String host_IPString = (null != ExtractHost.extractHost(record[3])? ExtractHost
					.extractHost(record[3]) : ExtractHost.extractIP(record[3]));
			
			// 判断referer字段是否包含有上述的关键词
			for (String keyword : keywords) {
				if (!record[4].isEmpty() && record[4].contains(keyword.toLowerCase())) {
					condition = true;
					continue;
				}
			}
			
			if (record.length == 8 && condition) {
				String[] keyValues = record[7].split("; ");
				Map<String, String> keyValueMap = new HashMap<String, String>();
				for (String keyval : keyValues) {
					keyValueMap.put(
							-1 != keyval.indexOf("=") ? keyval.substring(0,
									keyval.indexOf("=")) : keyval,
							-1 != keyval.indexOf("=") ? keyval.substring(keyval
									.indexOf("=") + 1) : "");

				}

				if (host_IPString != null && hostString.equals(host_IPString)
						&& keyValueMap.containsKey(keyOfHost)) {
					
					context.write(
							new Text(record[1] + "	" + record[5] + "	"
									+ host_IPString + "	"
									+ keyValueMap.get(keyOfHost)), new Text(
									record[7]));
				}

			}
		}
	}
	/**
	 * 取出所有的符合指定网站的cookies
	 * @author dhu
	 *
	 */
	public static class ADSLUAHostValueReducer extends Reducer<Text, Text, Text, Text> {
		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			//List<String> vaList = new ArrayList();
			for (Text val : values) {
				String[] cookies = val.toString().split("; ");
				for (String keyvalue : cookies) {
					context.write(key, new Text(keyvalue));
				}
			}
			
			
		}
	}
	
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public void FenZuByADSLUAHostValue(String[] input_output,String host, String hdfsPath) throws Exception {
		Configuration conf = new Configuration(); ///dianxin //inPutDerectory/someDianxin
		//String[] argStrings = {"/dianxin","/outPutDerectory/taobao/cookies-full1"};
		String[] otherArgs = new GenericOptionsParser(conf, input_output)
				.getRemainingArgs();
		conf.set("host", host);
		conf.set("keyOfHost", new ExtractHost_Key(hdfsPath).keyOfHost(conf, host));
		
		Job job = new Job(conf, "FenZuByADSLUAHostValue");
		job.setJarByClass(ADSLUAHostValue.class);
		job.setMapperClass(ADSLUAHostValueMapper.class);
		job.setReducerClass(ADSLUAHostValueReducer.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
