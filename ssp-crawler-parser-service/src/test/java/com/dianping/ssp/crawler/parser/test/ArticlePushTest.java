package com.dianping.ssp.crawler.parser.test;

import com.dianping.combiz.spring.context.SpringLocator;
import com.dianping.ssp.crawler.common.config.dto.CrawlerConfig;
import com.dianping.ssp.crawler.common.config.impl.JsonCrawlerConfigParser;
import com.dianping.ssp.crawler.common.downloader.impl.HttpClientDownloaderImpl;
import com.dianping.ssp.crawler.common.pageprocessor.CommonPageProcessorImpl;
import com.dianping.ssp.crawler.common.pageprocessor.subprocess.CrawlerSubProcessTag;
import com.dianping.ssp.crawler.common.pageprocessor.subprocess.ICrawlerSubProcess;
import com.dianping.ssp.crawler.common.scheduler.DpRedisScheduler;
import com.dianping.ssp.crawler.common.spider.DpSpider;
import com.dianping.ssp.crawler.common.util.DomainTagUtils;
import com.dianping.ssp.crawler.parser.base.AbstractTest;

import com.dianping.ssp.crawler.parser.service.crawler.subprocessor.ParserSubProcessor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.downloader.Downloader;

import javax.annotation.Resource;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ArticlePushTest extends AbstractTest{

    @Test
    public void test(){
    	List<String> allDomainTags = DomainTagUtils.getAllDomainTags();
        
        ApplicationContext c = SpringLocator.getApplicationContext();
        Downloader downloader = new HttpClientDownloaderImpl();

        CrawlerConfig config2 = new JsonCrawlerConfigParser().parser("wechat-parser");
        CommonPageProcessorImpl processor2 = new CommonPageProcessorImpl(config2);
        DpRedisScheduler scheduler2 = new DpRedisScheduler("wechat-parser", config2.getSchedulerConfig());
        DpSpider dpSpider2 = DpSpider.create(processor2).setScheduler(scheduler2).startUrls(config2.getCrawlerBaseInfo().getBaseUrls());

		Map<String, ICrawlerSubProcess> result = c.getBeansOfType(ICrawlerSubProcess.class);
		ICrawlerSubProcess subProcess = result.get("parserSubProcessor");

		try {
			Request request = new Request();
			request.setUrl("http://mp.weixin.qq.com/s?timestamp=1482811015&src=3&ver=1&signature=6HU-CkqttfS1kojVvgiu4ZGKIhUiYj*h7fML5VE9kSCyv548AIHiNXH3TB5TKvi-dAXrINe7jEE-cpFJwen7k1fnmF9vo5cwWQCsmrbc1Vh1wkqDhJUgtIbk-OmOIPa8kDlWzDpxYMGsPhwMrYa4AR4kz7icLPYEcRQSMdVwFqU=");
			Page page = downloader.download(request, dpSpider2);
			processor2.process(page);

		}catch(Exception ex){
			System.out.println(ex);
		}finally {
			
		}
	}
}