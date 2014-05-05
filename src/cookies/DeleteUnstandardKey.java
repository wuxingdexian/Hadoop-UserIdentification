package cookies;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import EncodeDecode.*;

import extraTextHandle.KeysInCookie;
/**
 * 删除不合格的key，进一步提取用户的uid
 * 思路：map阶段，读取每一行
 * 		reduce阶段，使用多种规则去除value。
 * @author dhu
 *
 */
public class DeleteUnstandardKey {
	

	public static class deleteKeyMapper extends Mapper<Object, Text, Text, Text> {
		//private static Map<String, String> keyvalMap = new HashMap<String, String>();
		Configuration conf;
		private static Map<String, String> keyvalMap = new HashMap<String, String>();
		public void setup(Context context) {
			conf = context.getConfiguration();
			String kl = conf.get("keylist");
			if (kl != null) {
				String[] keyvals = kl.split(", ");
				for (String keyval : keyvals) {
					// System.out.println(keyval);
					keyvalMap.put(keyval.split("	")[0],
							keyval.split("	").length > 1 ? keyval.split("	")[1]
									: "");
				}
			}
		}
		
		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			
			String[] records = value.toString().split("	");
			if (records.length ==2) {
				context.write(new Text(records[0]), new Text(records[1]));
			}
			else {
				context.write(new Text(records[0]), new Text(""));
			}
			
		}
	}
	
	public static class deleteKeyReducer extends Reducer<Text, Text, Text, Text>{
		
		//private static Map<String, String> keyvalMap = new HashMap<String, String>();
		Configuration conf;
		private static Map<String, String> keyvalMap = new HashMap<String, String>();
		public void setup(Context context) {
			conf = context.getConfiguration();
			String kl = conf.get("keylist");
			if (kl != null) {
				String[] keyvals = kl.split(", ");
				for (String keyval : keyvals) {
					// System.out.println(keyval);
					keyvalMap.put(keyval.split("	")[0],
							keyval.split("	").length > 1 ? keyval.split("	")[1]
									: "");
				}
			}
		}
		
		public void reduce(Text key, Iterable<Text> values, Context context) 
				throws IOException, InterruptedException{
			
			boolean isStandand = true;
			List<String> vaList = new ArrayList<String>();
			int averageLenth = 0;
			
			for (Text valText : values) {
				vaList.add(valText.toString());
				// 之前抓取到的cookie中含有该字段key
				if (keyvalMap.containsKey(key.toString())) {
					if (!keyvalMap.get(key.toString()).equals(valText.toString())) {
						
						isStandand = rules(key.toString(), valText.toString());
						if (!isStandand) {
							break;
						}
					}
				}
				// 之前抓取到的cookie中不包含该字段key
				else {
					
					isStandand = rules(key.toString(), valText.toString());
					if (!isStandand) {
						break;
					}
				}
				
				// 限制value的平均长度
				averageLenth += valText.toString().length();
			}
			
			if ((averageLenth / vaList.size()) < 3 || (averageLenth / vaList.size()) > 40) {
				isStandand = false;
			}
			
			if (isStandand) {
				for (String valString : vaList) {
					if (keyvalMap.containsKey(key.toString())) {
						if (!keyvalMap.get(key.toString()).equals(valString)) {
							context.write(key, new Text(valString));
						}
					}
					else {
						context.write(key, new Text(valString));
					}
				}
			}
		}
	}
	// 规则函数
	private static boolean rules(String key, String value) {
		// 限制长度
		if (value.length() > 50) {
			return false;
		}
		// 排除Google Analysis字段 __utma __utmb __utmc __utmz
		if (key.equals("__utma") || key.equals("__utmb")
				|| key.equals("__utmc") || key.equals("__utmz")) {
			return false;
		}
		// 限制key的长度
		if (key.length() > 10) {
			return false;
		}
		
		
		
		return true;
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws InterruptedException 
	 */
	public void UnstandarKey(String[] input_output, String keysPath) throws IOException, InterruptedException, ClassNotFoundException {
		// TODO Auto-generated method stub
		Configuration conf = new Configuration(); ///dianxin //inPutDerectory/someDianxin
		
		//keyvalMap = KeysInCookie.Getkeys(conf, keysPath, "value出现重复值，暂不排除");

		String[] otherArgs = new GenericOptionsParser(conf, input_output)
				.getRemainingArgs();
		
		List<String> keyList = KeysInCookie.Getkeys(conf, keysPath, "value出现重复值，暂不排除");
		StringBuffer keyBuffer = new StringBuffer(keyList.toString());
		//System.out.println(keyBuffer);
		keyBuffer.delete(0, 1);
		keyBuffer.delete(keyBuffer.length() - 1, keyBuffer.length());
		conf.set("keylist", keyBuffer.toString());

		Job job = new Job(conf, "deleteKey");
		job.setJarByClass(DeleteUnstandardKey.class);
		job.setMapperClass(deleteKeyMapper.class);
		job.setReducerClass(deleteKeyReducer.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

}
