package com.hhit.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.ImageHelper;

public class HttpUtils {
	private String uid = "2014122855";
	private String upd = "ajc19960130";

	public void downloadFile() throws Exception {
		int Status=0;
		HttpResponse httpResponse;
		String userAgent = "Mozilla/5.0 (iPhone; CPU iPhone OS 7_1_2 like Mac OS X) AppleWebKit/537.51.2 (KHTML, like Gecko) Mobile/11D257 QQ/5.2.1.302 NetType/WIFI Mem/28";
		do {
			String url = "http://zf.hhit.edu.cn/CheckCode.aspx";
			File dir = new File("./captcha");
			if (!dir.exists()) {
				dir.mkdirs();
			}
			String destfilename = "./captcha/captcha" + UUID.randomUUID().toString() + ".gif";
			HttpClient httpclient = HttpClients.createDefault();
			HttpGet httpget = new HttpGet(url);
			httpget.addHeader("User-Agent", userAgent);
			File file = new File(destfilename);
			if (file.exists()) {
				file.delete();
			}

			HttpResponse response = httpclient.execute(httpget);
			String setCookie = response.getFirstHeader("Set-Cookie").getValue();
			HttpEntity entity = response.getEntity();
			InputStream in = entity.getContent();
			try {
				FileOutputStream fout = new FileOutputStream(file);
				byte[] tmp = new byte[4096];
				while ((in.read(tmp)) != -1) {
					fout.write(tmp);
				}
				fout.close();
			} finally {
				// 在用InputStream处理HttpEntity时，切记要关闭低层流。
				in.close();
			}
			httpget.releaseConnection();
			String location = System.getProperty("user.dir") + "\\tessdata";
			String location1 = location.replace("\\", "/");
			Tesseract tessreact = new Tesseract();
			tessreact.setDatapath(location1);
			tessreact.setTessVariable("tessedit_char_whitelist",
					"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");

			String result = tessreact.doOCR(file);
			HttpPost httpPost = new HttpPost("http://zf.hhit.edu.cn/default2.aspx");
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("__VIEWSTATE", "dDwtNTE2MjI4MTQ7Oz7z8JUNn7l8ZiQEAX3eOkkLv/hQdQ=="));
			nvps.add(new BasicNameValuePair("txtUserName", uid));
			nvps.add(new BasicNameValuePair("Textbox1", uid));
			nvps.add(new BasicNameValuePair("TextBox2", upd));
			nvps.add(new BasicNameValuePair("txtSecretCode", result.trim()));
			nvps.add(new BasicNameValuePair("RadioButtonList1", "%D1%A7%C9%FA"));
			nvps.add(new BasicNameValuePair("Button1", ""));
			nvps.add(new BasicNameValuePair("lbLanguage", ""));
			nvps.add(new BasicNameValuePair("hidPdrs", ""));
			nvps.add(new BasicNameValuePair("hidsc", ""));
			httpPost.addHeader("User-Agent", userAgent);
			httpPost.setEntity(new UrlEncodedFormEntity(nvps));
			httpPost.addHeader("Cookie", setCookie.split(";")[0]);
		    httpResponse = httpclient.execute(httpPost);
			Status = response.getStatusLine().getStatusCode();
			System.out.println(Status);
		} while (Status != 302);
		HttpEntity entity1 = httpResponse.getEntity();
		entity1.getContent();
		String response1 = EntityUtils.toString(entity1, "UTF-8");
		EntityUtils.consume(entity1);
		System.out.println(response1);

	}

	@Test
	public void test() {
		for (int i = 0; i < 1; i++) {
			try {
				downloadFile();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
