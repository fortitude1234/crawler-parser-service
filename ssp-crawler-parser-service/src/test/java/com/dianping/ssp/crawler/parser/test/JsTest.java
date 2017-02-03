package com.dianping.ssp.crawler.parser.test;

import java.util.List;
import java.util.Map;

import com.dianping.ssp.crawler.common.spider.SpiderFactory;
import com.dianping.ssp.crawler.common.util.DomainTagUtils;
import com.dianping.ssp.crawler.parser.base.AbstractTest;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

import com.dianping.ssp.crawler.common.util.JavaScriptExecutorUtils;
import com.google.common.collect.Maps;

/**
 *
 * @author Mr.Bian
 *
 */
public class JsTest extends AbstractTest {
	
	@Test
	public void jsTest() throws Exception{
		List<String> allDomainTags = DomainTagUtils.getAllDomainTags();
		if (CollectionUtils.isNotEmpty(allDomainTags)) {
			for (String domainTag : allDomainTags) {
				SpiderFactory.initSpider(domainTag);
			}
		}
		Thread.currentThread().join();
	}
}
