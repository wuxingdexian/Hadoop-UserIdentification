package extraTextHandle;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.LineRecordReader.LineReader;

public class KeysInCookie {
	public static List<String> Getkeys(Configuration conf, String keysPath, String condition) throws IOException {
	//public static Map<String, String> Getkeys(Configuration conf, String keysPath, String condition) throws IOException {
		//hdfs://10.9.18.143/  // 一定要指定到hdfs里面的具体文件
		//String keysPath = "hdfs://10.9.18.143:9000/outPutDerectory/cookies/taobao/part-r-00000";
		FileSystem hdfs = FileSystem.get(URI.create(keysPath),conf);
		FSDataInputStream hdfsStream = hdfs.open(new Path(keysPath));
		LineReader lineReader = new LineReader(hdfsStream, conf);
		Text lineText = new Text();
		
		Map<String, String> KeysMap = new HashMap<String, String>();
		//List<String> keyList = new ArrayList<String>();
		List<String> keyList = new ArrayList<String>();
		
		while(lineReader.readLine(lineText) > 0){
			String[] keyval = lineText.toString().split("	");
			if (keyval[1].equals(condition)) {
				KeysMap.put(keyval[0], keyval.length > 2?keyval[2]:keyval[1]);
				keyList.add(keyval.length > 2?keyval[0] + "	" + keyval[2]:keyval[0]);
			}
			
		}
		lineReader.close();
		hdfsStream.close();
		hdfs.close();
		//return KeysMap;
		return keyList;
		/*
		BufferedReader bReader = new BufferedReader(new InputStreamReader(
				new FileInputStream("HostNameNumberTOP-1.txt")));
		Map<String, String> host_key = new HashMap<String, String>();
		
		for (String lines = bReader.readLine();lines != null; lines = bReader.readLine()) {
			String[] hnnStrings = lines.split("	");
			for (int i = 0; i < hnnStrings.length; i++) {
				hnnStrings[i] = hnnStrings[i].replaceAll("\"", "");
			}
			host_key.put(hnnStrings[0], hnnStrings[1]);
			//System.out.println(host_key);
		}
		bReader.close();
		return host_key;
		*/
	}
	
}