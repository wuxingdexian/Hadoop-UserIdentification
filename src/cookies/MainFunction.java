package cookies;

import org.apache.hadoop.conf.Configuration;

public class MainFunction {

	/**
	 * @param args: 1 choice; 2 host; 3 master的HDFS地址; 4 选择输入的那个文件
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//dianxin //inPutDerectory/fullDianxin //outPutDerectory/taobao/cookies-full1
		if (args.length != 4) {
		      System.err.println("输入的字段个数不够");
		      System.exit(4);
		}
		// dt=20131201/000000_0
		String[] catchedCookie_exceptedCookie = {"/inPutDerectory/cookies/" + args[1],"/outPutDerectory/cookies/" + args[1]};
		String[] sourceNew_interminal1 = {"/inPutDerectory/" + args[3],"/outPutDerectory/" + args[1] + "/cookies1-new"};
		String[] sourceOld_interminal1 = {"/inPutDerectory/someDianxin","/outPutDerectory/taobao/cookies1"};
		String[] interminal1_interminal2 = {"/outPutDerectory/" + args[1] + "/cookies1-new","/outPutDerectory/" + args[1] + "/cookies2-new"};
		String[] interminal2_interminal3 = {"/outPutDerectory/" + args[1] + "/cookies2-new","/outPutDerectory/" + args[1] + "/cookies3-new"};
		String[] interminal3_interminal4 = {"/outPutDerectory/" + args[1] + "/cookies3-new","/outPutDerectory/" + args[1] + "/cookies4-new"};
		String[] interminal4_interminal5 = {"/outPutDerectory/" + args[1] + "/cookies4-new","/outPutDerectory/" + args[1] + "/cookies5-new"};
		String[] interminal5_interminal6 = {"/outPutDerectory/" + args[1] + "/cookies5-new","/outPutDerectory/" + args[1] + "/cookies6-new"};
		int choice = Integer.valueOf(args[0]);
		//choice = 7;
		Configuration conf = new Configuration();
		switch (choice) {
		case 0: // 从抓取得到的cookies里获得排除的字段
			new DealCathedCookies().GetExceptedCookieList(catchedCookie_exceptedCookie, conf);
			break;
		case 1: // 分组:使用的是2013年的新数据，原始数据格式为保存在hive的数据
			//newDataADSLUAHostValue nd = new newDataADSLUAHostValue(args[1], args[2], conf);
			//newDataADSLUAHostValue nd = new newDataADSLUAHostValue();
			new newDataADSLUAHostValue().newData(sourceNew_interminal1, args[1], args[2]);
			//new newDataADSLUAHostValue(args[1], args[2], conf).newData(sourceNew_interminal1);
			break;
		case 2: // 分组:使用的是2012年的旧数据，原始数据格式为八个字段
			new ADSLUAHostValue().FenZuByADSLUAHostValue(sourceOld_interminal1, args[1], args[2]);
			break;
		case 3: // web日志记录去重复
			new QuChongFu().QuChong(interminal1_interminal2);
			break;
		case 4: // 提取key-value 该步骤reducer添加（1）解码功能 实现对utf-8或unicode解码 （2）排除特殊字符字段 （3）账户字段正则匹配
			new ExtractKeyValue().KeyValue(interminal2_interminal3);
			break;
		case 5: // 在web日志记录中，去掉从抓取得到的cookies里获得排除的字段
			new DeleteNotUserKey_CathedCookie().NotUserKey(interminal3_interminal4, args[2] + "/outPutDerectory/cookies/" + args[1] + "/part-r-00000");
			break;
		case 6: // 取value出现重复的值(即cookie在不同分组情况下，还会出现相同的值，则很有可能为用户账户)
			new FindRepeatedValue().RepeatedValue(interminal4_interminal5);
			break;
		/*case 7: // 指定规则，删除不符合规则的字段key 这部分是旧的规则
			new DeleteUnstandardKey().UnstandarKey(interminal5_interminal6, args[2] + "/outPutDerectory/cookies/" + args[1] + "/part-r-00000");
		*/
		case 7: // 指定规则，删除不符合规则的字段key 这部分是新的规则
			new DeleteUnUserKey().UnstandarKey(interminal5_interminal6);
			break;
		default:
			break;
		}
	}
	
	
	/* back up
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//dianxin //inPutDerectory/fullDianxin //outPutDerectory/taobao/cookies-full1
		if (args.length != 4) {
		      System.err.println("输入的字段个数不够");
		      System.exit(4);
		}
		// dt=20131201/000000_0
		String[] catchedCookie_exceptedCookie = {"hdfs://hadoopbaoxin09:9000/inPutDerectory/cookies/" + args[1],"hdfs://hadoopbaoxin09:9000/outPutDerectory/cookies/" + args[1]};
		String[] sourceNew_interminal1 = {"hdfs://hadoopbaoxin09:9000/inPutDerectory/" + args[3],"hdfs://hadoopbaoxin09:9000/outPutDerectory/" + args[1] + "/cookies1"};
		String[] sourceOld_interminal1 = {"hdfs://hadoopbaoxin09:9000/inPutDerectory/someDianxin","hdfs://hadoopbaoxin09:9000/outPutDerectory/taobao/cookies1"};
		String[] interminal1_interminal2 = {"hdfs://hadoopbaoxin09:9000/outPutDerectory/" + args[1] + "/cookies1","hdfs://hadoopbaoxin09:9000/outPutDerectory/" + args[1] + "/cookies2"};
		String[] interminal2_interminal3 = {"hdfs://hadoopbaoxin09:9000/outPutDerectory/" + args[1] + "/cookies2","hdfs://hadoopbaoxin09:9000/outPutDerectory/" + args[1] + "/cookies3"};
		String[] interminal3_interminal4 = {"hdfs://hadoopbaoxin09:9000/outPutDerectory/" + args[1] + "/cookies3","hdfs://hadoopbaoxin09:9000/outPutDerectory/" + args[1] + "/cookies4"};
		String[] interminal4_interminal5 = {"hdfs://hadoopbaoxin09:9000/outPutDerectory/" + args[1] + "/cookies4","hdfs://hadoopbaoxin09:9000/outPutDerectory/" + args[1] + "/cookies5"};
		String[] interminal5_interminal6 = {"hdfs://hadoopbaoxin09:9000/outPutDerectory/" + args[1] + "/cookies5","hdfs://hadoopbaoxin09:9000/outPutDerectory/" + args[1] + "/cookies6"};
		int choice = Integer.valueOf(args[0]);
		choice = 7;
		Configuration conf = new Configuration();
		switch (choice) {
		case 0: // 从抓取得到的cookies里获得排除的字段
			new DealCathedCookies().GetExceptedCookieList(catchedCookie_exceptedCookie, conf);
			break;
		case 1: // 分组:使用的是2013年的新数据，原始数据格式为保存在hive的数据
			//newDataADSLUAHostValue nd = new newDataADSLUAHostValue(args[1], args[2], conf);
			//newDataADSLUAHostValue nd = new newDataADSLUAHostValue();
			new newDataADSLUAHostValue().newData(sourceNew_interminal1, args[1], args[2]);
			//new newDataADSLUAHostValue(args[1], args[2], conf).newData(sourceNew_interminal1);
			break;
		case 2: // 分组:使用的是2012年的旧数据，原始数据格式为八个字段
			new ADSLUAHostValue().FenZuByADSLUAHostValue(sourceOld_interminal1, args[1], args[2]);
			break;
		case 3: // web日志记录去重复
			new QuChongFu().QuChong(interminal1_interminal2);
			break;
		case 4: // 提取key-value
			new ExtractKeyValue().KeyValue(interminal2_interminal3);
			break;
		case 5: // 在web日志记录中，去掉从抓取得到的cookies里获得排除的字段
			new DeleteNotUserKey_CathedCookie().NotUserKey(interminal3_interminal4, args[2] + "/outPutDerectory/cookies/" + args[1] + "/part-r-00000");
			break;
		case 6: // 取value出现重复的值(即cookie在不同分组情况下，还会出现相同的值，则很有可能为用户账户)
			new FindRepeatedValue().RepeatedValue(interminal4_interminal5);
			break;
		case 7: // 指定规则，删除不符合规则的字段key
			new DeleteUnstandardKey().UnstandarKey(interminal5_interminal6, args[2] + "/outPutDerectory/cookies/" + args[1] + "/part-r-00000");
		default:
			break;
		}
	}
	*/

}
