package extraTextHandle;


import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.LineRecordReader.LineReader;

public class ExtractHost_Key {
	private static String HDFS = "hdfs://master:9000";
	
	public ExtractHost_Key(String hdfs) {
		// TODO Auto-generated constructor stub
		HDFS = hdfs;
	}
	
	public String keyOfHost(Configuration conf, String host) throws IOException {	
		//hdfs://10.9.18.143/
		String top_1Path = HDFS + "/inPutDerectory/TOP-1Key/HostNameNumberTOP-1.txt";
		FileSystem hdfs = FileSystem.get(URI.create(top_1Path),conf);
		FSDataInputStream hdfsStream = hdfs.open(new Path(top_1Path));
		LineReader lineReader = new LineReader(hdfsStream, conf);
		Text lineText = new Text();
		
		String key = null;
		while(lineReader.readLine(lineText) > 0){
			String[] hnnStrings = lineText.toString().split("	");
			for (int i = 0; i < hnnStrings.length; i++) {
				hnnStrings[i] = hnnStrings[i].replaceAll("\"", "");
			}
			if (host.equals(hnnStrings[0])) {
				key = hnnStrings[1];
				break;
			}
		}
		lineReader.close();
		hdfsStream.close();
		hdfs.close();
		return key;
		
		
	}
	
}