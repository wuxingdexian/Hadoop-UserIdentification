package cookies;

import java.io.IOException;
import java.util.ArrayList;
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


/**
 * 最后制定一些规则,进一步删除候选用户字段
 * @author dhu
 *
 */
public class DeleteUnUserKey {

	public static class delKeyMapper extends Mapper<Object, Text, Text, Text> {
		
		
		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			
			String[] records = value.toString().split("	");
			if (records.length == 3 ) {
				context.write(new Text(records[0]), new Text(records[1] + "	" + records[2]));
			}
		}
	}
	
	public static class delKeyReducer extends Reducer<Text, Text, Text, Text>{
		
		
		public void reduce(Text key, Iterable<Text> values, Context context) 
				throws IOException, InterruptedException{
			
			boolean flag = true;
			List<String> valList = new ArrayList<String>();
			for (Text valText : values) {
				String[] valStrings = valText.toString().split("	");
				// 判断字段值占比
				if (valStrings.length == 2 && Double.valueOf(valStrings[1]) > 0.01) {
					flag = false;
					break;
				}
				// 判断字段长度 15
				if (key.getLength() > 15) {
					flag = false;
					break;
				}
				
				// 判断字段值长度分布
				valList.add(valText.toString());
			}
			
			// 输出最后用户字段
			if (flag) {
				for (String valString : valList) {
					context.write(key, new Text(valString));
				}
			}
			
		}
	}
	
	public void UnstandarKey(String[] input_output) throws IOException, InterruptedException, ClassNotFoundException {
		// TODO Auto-generated method stub
		Configuration conf = new Configuration(); // /dianxin
		String[] otherArgs = new GenericOptionsParser(conf, input_output)
				.getRemainingArgs();
		Job job = new Job(conf, "DeleteUnUserKey");
		job.setJarByClass(ExtractKeyValue.class);
		job.setMapperClass(delKeyMapper.class);
		job.setReducerClass(delKeyReducer.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
