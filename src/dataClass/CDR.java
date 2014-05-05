package dataClass;

//需要再还原为原始的CDR数据
public	class CDR{
		private String srcIP;
		private String adsl;
		private String timeStamp;
		private String URL;
		private String referer;
		private String UA;
		private String desIP;
		private String cookie;
		
		public void setAdsl(String adsl) {
			this.adsl = adsl;
		}
		
		public void setCookie(String cookie) {
			this.cookie = cookie;
		}
		
		public void setDesIP(String desIP) {
			this.desIP = desIP;
		}
		
		public void setReferer(String referer) {
			this.referer = referer;
		}
		
		public void setSrcIP(String srcIP) {
			this.srcIP = srcIP;
		}
		
		public void setTimeStamp(String timeStamp) {
			this.timeStamp = timeStamp;
		}
		
		public void setUA(String uA) {
			UA = uA;
		}
		
		public void setURL(String uRL) {
			URL = uRL;
		}
		
		public String getAdsl() {
			return adsl;
		}
		
		public String getCookie() {
			return cookie;
		}
		
		public String getDesIP() {
			return desIP;
		}
		
		public String getReferer() {
			return referer;
		}
		
		public String getSrcIP() {
			return srcIP;
		}
		
		public String getTimeStamp() {
			return timeStamp;
		}
		
		public String getUA() {
			return UA;
		}
		
		public String getURL() {
			return URL;
		}
	}