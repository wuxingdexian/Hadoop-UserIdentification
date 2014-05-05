package cookies;

import java.io.IOException;

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
 * 网站用户账户识别第二步：分组后的数据，进入该mapreduce过程，去掉重复记录值
 * 思路：一整行作为map输出的key，空值作为map输出的value
 * 		reduce直接输出key和value
 * @author dhu
 *
 */
public class QuChongFu {

	public static class QuChongFuMapper extends Mapper<Object, Text, Text, Text> {

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			context.write(value, new Text(""));
		}

	}

	public static class QuChongFuReducer extends Reducer<Text, Text, Text, Text>{
		public void reduce(Text key, Iterable<Text> values, Context context) 
				throws IOException, InterruptedException {
			context.write(new Text(key), new Text(""));
		}
	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InterruptedException
	 */
	public void QuChong(String[] input_output) throws IOException,
			InterruptedException, ClassNotFoundException {
		// TODO Auto-generated method stub
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, input_output)
				.getRemainingArgs();
		
		Job job = new Job(conf, "QuChong");
		job.setJarByClass(QuChongFu.class);
		job.setMapperClass(QuChongFuMapper.class);
		job.setReducerClass(QuChongFuReducer.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

}
