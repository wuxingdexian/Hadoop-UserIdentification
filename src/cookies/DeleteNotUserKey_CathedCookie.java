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

import extraTextHandle.KeysInCookie;

public class DeleteNotUserKey_CathedCookie {

	
	
	public static class NoteUserMapper extends Mapper<Object, Text, Text, Text>{
		
		Configuration conf;
		private static Map<String, String> keysMap = new HashMap<String, String>();
		public void setup(Context context) {
			conf = context.getConfiguration();
			String kl = conf.get("keylist");
			if (kl != null) {
				String[] keyvals = kl.split(", ");
				for (String keyval : keyvals) {
					// System.out.println(keyval);
					keysMap.put(keyval.split("	")[0],
							keyval.split("	").length > 1 ? keyval.split("	")[1]
									: "");
				}
			}
		}
		
		public void map(Object key, Text value, Context context) throws IOException,InterruptedException {
			String[] keyval = value.toString().split("	");
			if (!keysMap.containsKey(keyval[0])) {
				context.write(new Text(keyval[0]), keyval.length == 2?new Text(keyval[1]):new Text(""));
			}
		}
	}
	
	public static class NotUserReducer extends Reducer<Text, Text, Text, Text>{
		
		public void reduce(Text key,Iterable<Text> values, Context context)  throws IOException,InterruptedException {
			List<String> valList = new ArrayList<String>();
			
			for (Text valText : values) {
				valList.add(valText.toString());
			}
			
			Collections.sort(valList);
			
			for (String valString : valList) {
				context.write(key, new Text(valString));
			}
		}
	}
	
	public void NotUserKey(String[] input_output, String keysPath) throws IOException, InterruptedException, ClassNotFoundException {
		// TODO Auto-generated method stub
		
		Configuration conf = new Configuration(); // /dianxin
		String[] otherArgs = new GenericOptionsParser(conf, input_output)
				.getRemainingArgs();
		
		List<String> keyList = KeysInCookie.Getkeys(conf, keysPath, "value不出现重复值，排除");
		StringBuffer keyBuffer = new StringBuffer(keyList.toString());
		keyBuffer.delete(0, 1);
		keyBuffer.delete(keyBuffer.length() - 1, keyBuffer.length());
		conf.set("keylist", keyBuffer.toString());
		
		Job job = new Job(conf, "KeyValue");
		job.setJarByClass(DeleteNotUserKey_CathedCookie.class);
		job.setMapperClass(NoteUserMapper.class);
		job.setReducerClass(NotUserReducer.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

}
