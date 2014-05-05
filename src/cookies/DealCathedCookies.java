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
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class DealCathedCookies {

	public static class CookieMapper extends Mapper<Object, Text, Text, Text> {

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			String[] cookies = value.toString().substring(8).split("; ");
			for (String cookie : cookies) {
				context.write(new Text(-1 != cookie.indexOf("=")?cookie.substring(0, cookie.indexOf("=")):cookie),
						new Text(-1 != cookie.indexOf("=")?cookie.substring(cookie.indexOf("=") + 1):""));
			}
		}
	}

	public static class CookieReduce extends Reducer<Text, Text, Text, Text>{
		public void reduce(Text key, Iterable<Text> values, Context context) 
				throws IOException, InterruptedException {
			List<String> valList = new ArrayList<String>();
			
			for (Text valText : values) {
				valList.add(valText.toString());
			}
			Collections.sort(valList);
			if (valList.get(0).equals(valList.get(valList.size() - 1))) {
				context.write(key, new Text("value出现重复值，暂不排除" + "	" + valList.get(0)));
			}
			else {
				context.write(key, new Text("value不出现重复值，排除"));
			}
			
		}
	}
	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws InterruptedException 
	 */
	public void GetExceptedCookieList(String[] input_output, Configuration conf) throws IOException, InterruptedException, ClassNotFoundException {
		// TODO Auto-generated method stub
		//Configuration conf = new Configuration(); // /dianxin
		String[] otherArgs = new GenericOptionsParser(conf, input_output)
				.getRemainingArgs();

		Job job = new Job(conf, "DealCathedCookies");
		job.setNumReduceTasks(1); // 设置一个reducer，为的是得到一个文件
		job.setJarByClass(DealCathedCookies.class);
		job.setMapperClass(CookieMapper.class);
		job.setReducerClass(CookieReduce.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

}
