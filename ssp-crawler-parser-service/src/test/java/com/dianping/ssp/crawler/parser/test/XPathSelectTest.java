package com.dianping.ssp.crawler.parser.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import us.codecraft.webmagic.selector.CssSelector;
import us.codecraft.xsoup.Xsoup;


/**
 *
 * @author Mr.Bian
 *
 */
public class XPathSelectTest {
	
	@Test
	public void xPathSelectTest() throws IOException{
		File file =new File("/Users/bianwenlong/coding/meeting.html");
		String html=new String(IOUtils.toByteArray(new FileInputStream(file)),"utf-8");
		Document document = Jsoup.parse(html);
        List<String> result = Xsoup.compile("//ul[@class='suUl']/allText()").evaluate(document).list();
        System.out.println(result);
	}
	
	@Test
	public void cssSelectTest() throws IOException{
		File file =new File("/Users/bianwenlong/coding/meeting.html");
		String html=new String(IOUtils.toByteArray(new FileInputStream(file)),"utf-8");
		Document document = Jsoup.parse(html);
		CssSelector s=new CssSelector(".zhgk:nth-child(1),.zhgk:nth-child(2),.zhgk:nth-child(3)");
		String result=s.select(document);
        System.out.println(result);
	}
}
