package com.dianping.ssp.crawler.parser.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.dianping.ssp.crawler.parser.service.crawler.util.CleanDomUtil;

/**
 *
 * @author Mr.Bian
 *
 */
public class CleanDomUtilTest {

	@Test
	public void cleanDomTest() throws IOException {
		File file = new File("/Users/bianwenlong/coding/meeting.html");
		String html = new String(IOUtils.toByteArray(new FileInputStream(file)), "utf-8");
		System.out.println(CleanDomUtil.getTansFormHtml(html));
	}

}
