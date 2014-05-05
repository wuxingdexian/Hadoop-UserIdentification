package cookies;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import EncodeDecode.URLEncodeTest;
/**
 * 网站用户账户识别第三步：提取用户的uid
 * 思路：map阶段，每一行取cookie中的c_key-c_value pair，
 * 		c_key直接作为map的key输出，c_value直接作为map的value输出
 * 		reduce阶段，取value中存在重复的值输出。
 * @author dhu
 *
 */
public class ExtractKeyValue {

	public static class KeyValueMapper extends Mapper<Object, Text, Text, Text> {

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			String[] records = value.toString().split("	");
			if (records.length != 5) {
				System.out.println(value.toString());
			}
			if (records.length == 5 && records[4].length() > 0) {
				//System.out.println(records[4]);
				context.write(
						new Text(-1 != records[4].indexOf("=") ? records[4]
								.substring(0, records[4].indexOf("="))
								: records[4]),
						new Text(-1 != records[4].indexOf("=") ? records[4]
								.substring(records[4].indexOf("=") + 1) : ""));
			}
		}
	}

	public static class KeyValueReduce extends Reducer<Text, Text, Text, Text>{
		public void reduce(Text key, Iterable<Text> values, Context context) 
				throws IOException, InterruptedException {
			List<String> valList = new ArrayList<String>();
			new URLEncodeTest();
			boolean IsIllegal = false;
			
			// 判断是否包含有特殊字符
			for (Text valText : values) {
				valList.add(valText.toString());
				String tmp = valText.toString();
				// 解码
				for (int j = 0; j < 5; j++) {
					
					tmp = URLEncodeTest.decode(tmp);
				}
				// 判断特殊字符
				if (tmp != null) {
					IsIllegal = URLEncodeTest.RegIllegalCharacter(tmp);
					if (IsIllegal) {
						System.out.println(key.toString() + "-包含特殊字符：" + tmp);
						break;
					}
					
				}
				
				
			}
			
			// 正则匹配用户
			if (!IsIllegal) {
				Collections.sort(valList);// 排序 便于下面查找重复值
				for (int i = 0; i < valList.size(); i++) {
					if (key.toString().length() > 0 && valList.get(i) != null) {
						// 进行utf-8 或 unicode解码 并正则匹配账户 邮箱
						String tmp = valList.get(i);

						for (int j = 0; j < 5; j++) {

							tmp = URLEncodeTest.decode(tmp);
						}
						if (tmp != null && URLEncodeTest.RegAccount(tmp) ? URLEncodeTest
								.RegAccount(tmp) : URLEncodeTest.RegEmail(tmp)) {
							context.write(key, new Text(tmp));
						}
					}
					// context.write(key, new Text(valList.get(i)));
				}
			}

		}
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws InterruptedException 
	 */
	public void KeyValue(String[] input_output) throws IOException, InterruptedException, ClassNotFoundException {
		// TODO Auto-generated method stub
		Configuration conf = new Configuration(); // /dianxin
		String[] otherArgs = new GenericOptionsParser(conf, input_output)
				.getRemainingArgs();
		Job job = new Job(conf, "KeyValue");
		job.setJarByClass(ExtractKeyValue.class);
		job.setMapperClass(KeyValueMapper.class);
		job.setReducerClass(KeyValueReduce.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

}
