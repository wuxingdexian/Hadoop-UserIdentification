package cookies;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import extraTextHandle.ExtractHost;
import extraTextHandle.ExtractHost_Key;



public class newDataADSLUAHostValue {
	
	public static class NewDataMapper extends Mapper<Object, Text, Text, Text> {
		Configuration conf;
		private static String hostString;
		private static String keyOfHost;
		public void setup(Context context) {
			String host_keyString;
			conf = context.getConfiguration();
			keyOfHost = conf.get("keyOfHost");
			hostString = conf.get("host");

		}
		
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException{
			/*
			 *  recordsInCDR 字段顺序：
			 *  ts,
			 *  ad,
			 *  url<url,site,domain,scheme,user,host,port,path,query<>,fragment,charset>,
			 *  ref<url,site,domain,scheme,user,host,port,path,query<>,fragment,charset>,
			 *  cookie<>,
			 *  ua,
			 *  srcip<s,i,l>,
			 *  dstip<s,i,l>
			 */

			String[] recordsInCDR = value.toString().split("")[3].split("");
			String host_IPString = (null != ExtractHost.extractHost(recordsInCDR[2].split("")[0])? ExtractHost
					.extractHost(recordsInCDR[2].split("")[0]) : ExtractHost.extractIP(recordsInCDR[2].split("")[0]));
			
			if (hostString.equals(host_IPString)) {
				Map<String, String> keyValueMap = new HashMap<String, String>();
				String[] cookies = recordsInCDR[4].split("");
				for (int i = 0; i < cookies.length; i++) {
					keyValueMap.put(-1 != cookies[i].indexOf("")?cookies[i].substring(0, cookies[i].indexOf("")):cookies[i], 
							-1 != cookies[i].indexOf("")?cookies[i].substring(cookies[i].indexOf("") + 1):"");
				}
				if (keyValueMap.containsKey(keyOfHost)) {
					for (String cookie : cookies) {
						context.write(new Text(recordsInCDR[1] + "	" + recordsInCDR[5] 
							+ "	" + host_IPString + "	" + keyValueMap.get(keyOfHost)), 
							new Text(cookie.replaceAll("", "=")));
					}
				}
			}
		}
	}
	
	public void newData(String[] input_output, String host, String hdfsPath) throws IOException, InterruptedException, ClassNotFoundException {

		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, input_output)
				.getRemainingArgs();
		if (otherArgs.length != 2) {
			System.err.println("Usage: wordcount <in> <out>");
			System.exit(2);
		}
		conf.set("host", host);
		conf.set("keyOfHost", new ExtractHost_Key(hdfsPath).keyOfHost(conf, host));

		Job job = new Job(conf, "new data");
		job.setJarByClass(newDataADSLUAHostValue.class);
		job.setMapperClass(NewDataMapper.class);
		// job.setReducerClass(MyReducer.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

}
