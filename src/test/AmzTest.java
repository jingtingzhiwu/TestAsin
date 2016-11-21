package test;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class AmzTest {

	public static int pagenum = 1;
	public void run(String nexturl) throws Exception {
		try {
			Map<String, String> cookies = new HashMap<String, String>();
			cookies.put("x-amz-captcha-1", "1468922036613901");
			cookies.put("x-amz-captcha-2", "WLCoUKW19QqewRUQEL/vrg==");
			cookies.put("x-wl-uid", "1NLL0E9izM1ooVKa3RK6aPjbFgeaJFUUJg+6zEuktIshm/+g/zihELePWT9IMuXKrxepxKhqFhrYHPeuNOmxGtw==");
			cookies.put("at-main", "Atza|IwEBICbQOHnvkGI4fBsHSfhyIphUNb1kiL1wxPJOwbdYsuPzkJhWlF2mQIkEM7G6OZ6XQJ0BHK33s8ZvRTZ-TyRXwBn9R6LUsRSaQuREhpK72xnT0JQF-OGg3rSJ9ko6XDnvR6TPG8qdtAteChdEJ4OX6jVJO8_0Tg6IZ1yxHWnUilWugWfF1C-cT5dy50BwwJFgoptSbkR8Okyw3pWeFO0qxVyFj3L5wa_hhmAVHRt6myWgelESZPzbSZ9UpUjSAEulDg6d5pbyei3eX_xhz8nhYtrY5aB-55S-VvD7WyHpiOurUhPhidUWM23wbLXBEgVgBoFtoLg5E6cszE-ODlHXZMD_");
			cookies.put("session-token", "m3eO7WYohf31640KXCiF3ufFMc7IMu8CeF5CJBQpzPFf2fOkxtyMiV+3XEkhE8zAD6aNzhLdQNGgN6FJrPLGsiu/HoprpqPjw3KuMg++nFSk+5BgVNJbYvUy//eTS2uJGttMQ3BxzD2Y38xTXu36j8oTOT1u5pHUZo3VmydgYhhvf3LiGgBdOu6z0XSaxEcLtQ5QPvFWkLar9A+BsIVXLqFCon/18OMiqFHbH99jjYmwxGvdTO+FYWgQIhyRiQE8UYKUCPO1/wg=");
			cookies.put("csm-hit", "TSNYFZ19KMV3ZZ2Q5W61+s-63PJV2X0V1R6TDPGDHBS|1476957969147");
			cookies.put("session-id", "156-6607184-2496611");
			cookies.put("ubid-main", "181-3426044-6010000");
//			updateCookie();
			Parser parser = new Parser();

			while (true) {
				System.err.println("begin collection, page: " + pagenum);
				String fileContent = "";
				String url = null;
				String asin = null;
				String title = null;
				Document doc = parser.parseURL(nexturl, cookies);

				Elements eles = doc.select("ul#s-results-list-atf li[id*=result]");
				for (Element element : eles) {
					int offerNum = 0;
					asin = element.attr("data-asin");
					title = element.select("a.s-access-detail-page").attr("title");
					url = element.select("a.s-access-detail-page").attr("href");
					Elements offersDiv = element.select("div.a-span7 > div.a-spacing-mini div");
					for (Element eles2 : offersDiv) {
						if(eles2.text().toUpperCase().indexOf("OFFER") == -1) continue;
						String tmp = eles2.text().substring(eles2.text().indexOf("(")).replaceAll("[^\\d.]", "");
						offerNum += (tmp != null && tmp.length() > 0) ? Integer.valueOf(tmp) : 0;
					}
					if(offerNum == 0 || offerNum > 10) continue;

					System.err.println("ASIN: " + asin + "\tTITLE: " + title + "\tURL: " + url + "\tOFFERS: " + offerNum + "\n");
					fileContent += "ASIN: " + asin + "\tTITLE: " + title + "\tURL: " + url + "\tOFFERS: " + offerNum + "\n";
				}
				FileUtils.writeStringToFile(new File("/asintest.txt"), fileContent, "utf-8", true);

				nexturl = parseNextPage(doc);
				if (nexturl == null) {
					break;
				}else {
					if(!nexturl.startsWith("https://www.amazon.com"))
					nexturl = "https://www.amazon.com/" + nexturl;
				}
				pagenum++;
				Thread.sleep(30*1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String parseNextPage(Document doc) {
		Elements pages = doc.select("a#pagnNextLink");
		return pages == null ? null : pages.attr("href");
	}

	public static void main(String[] args) throws Exception {
//		String e = "$42.49used(28 offers)";
//		System.err.println(e.substring(e.indexOf("(")).replaceAll("[^\\d.]", ""));

		InputStream is = AmzTest.class.getResourceAsStream("/url.cfg");
		BufferedReader reader = null;
		String result = "";
		try {
			reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
			String tempString = null;
			int line = 1;
			while ((tempString = reader.readLine()) != null) {
				result += tempString;
				line++;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		new AmzTest().run(result);
	}
	

	/**
	 * 随机IP + cookie 
	 */
//	public void updateCookie(){
//		System.out.println("get amazon cookie.....");
//
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				for (String string : site) {
//					WebClient webClient = new WebClient();
//					webClient.getCookieManager().setCookiesEnabled(true);
//					webClient.getOptions().setTimeout(10000);
//					webClient.getCookieManager().clearCookies();
//					webClient.addRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36");
//					webClient.addRequestHeader("Accept", "application/json, text/javascript, */*; q=0.01");
//					webClient.addRequestHeader("Accept-Encoding", "gzip, deflate");
//					webClient.addRequestHeader("Accept-Language", "zh-CN,zh;q=0.8");
//					try {
//						Connection conn = DBUtil.openConnection();
//						conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
//
//						List<String> checkList = new ArrayList<String>();
//						List<String> list = new ArrayList<String>();
//						do {
//							checkList.clear();
//							list.clear();
//							HtmlPage page = webClient.getPage("https://" + string + "/");
//							webClient.waitForBackgroundJavaScript(30000);
//							Set<Cookie> set = webClient.getCookieManager().getCookies();
//							for (Iterator<Cookie> iterator = set.iterator(); iterator.hasNext();) {
//								Cookie cookie = iterator.next();
//								list.add("insert into amz_cookie (`key`,`value`,`site`,`status`) values ('" + cookie.getName() + "', '" + cookie.getValue() + "', '" + string + "', 1)");
//								checkList.add(cookie.getName());
//							}
//						} while (!checkList.contains("session-token") && !checkList.contains("session-id"));
//
//						DBUtil.execute(conn, "update amz_cookie set status = 0 where site='" + string + "'");
//						DBUtil.executeAsBatch(conn, list);
//					} catch (Exception e) {
//						e.printStackTrace();
//					} finally {
//						webClient.close();
//						System.out.println("got amazon cookie.....");
//					}
//				}
//			}
//		}).start();
//	}
}
