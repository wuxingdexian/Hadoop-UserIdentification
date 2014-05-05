package cookies;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import EncodeDecode.*;

public class FindRepeatedValue {
	
	public static class RepeatedValueMapper extends Mapper<Object, Text, Text, Text>{
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			String[] keyval = value.toString().split("	");
			context.write(new Text(keyval[0]), new Text(keyval.length == 2? keyval[1]:""));
		}
	}
	
	public static class RepeatedValueReducer extends Reducer<Text, Text, Text, DoubleWritable> {
		public void reduce(Text key, Iterable<Text> values, Context context)  throws IOException, InterruptedException {
			
			int numOfValues = 0;
			Map<String, Integer> valMap = new HashMap<String, Integer>();
			for (Text val : values) {
				if (valMap.containsKey(val.toString())) {
					valMap.put(val.toString(), valMap.get(val.toString()) + 1);
				}
				else {
					valMap.put(val.toString(), 1);
				}
				numOfValues ++;
			}
			Set<Entry<String, Integer>> ValSet = valMap.entrySet();
			Iterator<Entry<String, Integer>> keyValIterator = ValSet.iterator();
			while (keyValIterator.hasNext()) {
				Entry<String, Integer> ValEntry = keyValIterator.next();
				if (ValEntry.getValue() > 1) { // 重复条件
					context.write(new Text(key + "	" + ValEntry.getKey()), new DoubleWritable(ValEntry.getValue() * 1.0 / numOfValues));
				}
				
			}
			
		}
	}
	
	/*
	public static class RepeatedValueReducer extends Reducer<Text, Text, Text, Text> {
		public void reduce(Text key, Iterable<Text> values, Context context)  throws IOException, InterruptedException {
			List<String> vaList = new ArrayList<String>(); 
			for (Text val : values) {
				vaList.add(val.toString());
			}
			
			Collections.sort(vaList);
			
			Map<String, String> keyvalMap = new HashMap<String, String>();
			String valTemp = vaList.get(0);
			// 先找到重复的值
			for (int i = 1; i < vaList.size(); i++) {
				if (vaList.size() > 1 && vaList.get(i).equals(valTemp)) {
					//context.write(key, new Text(vaList.get(i)));
					keyvalMap.put(valTemp, "");
				}
				valTemp = vaList.get(i);
			}
			// 输出重复的值
			Set<Entry<String, String>> keyValSet = keyvalMap.entrySet();
			Iterator<Entry<String, String>> keyValIterator = keyValSet.iterator();
			while (keyValIterator.hasNext()) {
				Entry<String, String> keyValEntry = keyValIterator.next();
				//System.out.println(keyValEntry.getKey());

				// utf-8 或unicode解码
				String tmp = keyValEntry.getKey();
				new URLEncodeTest();
				for (int i = 0; i < 5; i++) {
					System.out.println(i + "tmp:"  + tmp);
					tmp = URLEncodeTest.decode(tmp);
				}
				
				
				if (tmp != null && URLEncodeTest.RegAccount(tmp)?URLEncodeTest.RegAccount(tmp):URLEncodeTest.RegEmail(tmp)) {
					context.write(key,new Text(tmp));
				}
				//context.write(key, new Text(new URLEncodeTest().decode(keyValEntry.getKey())));
				//context.write(key, new Text(URLDecoder.decode(keyValEntry.getKey())));
				//context.write(key, new Text(keyValEntry.getKey()));
			
			}
		}
	}*/
	
	public void RepeatedValue(String[] input_output) throws IOException, InterruptedException, ClassNotFoundException {
		Configuration conf = new Configuration(); // /dianxin
		String[] otherArgs = new GenericOptionsParser(conf, input_output)
				.getRemainingArgs();
		Job job = new Job(conf, "RepeatedValue");
		
		job.setJarByClass(FindRepeatedValue.class);
		job.setMapperClass(RepeatedValueMapper.class);
		job.setReducerClass(RepeatedValueReducer.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
	

}
