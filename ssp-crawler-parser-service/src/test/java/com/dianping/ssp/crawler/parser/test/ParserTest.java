package com.dianping.ssp.crawler.parser.test;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.plexus.util.IOUtil;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.processor.PageProcessor;

import com.alibaba.fastjson.JSON;
import com.dianping.ssp.crawler.common.config.dto.CrawlerConfig;
import com.dianping.ssp.crawler.common.contants.CrawlerCommonConstants;
import com.dianping.ssp.crawler.common.pageprocessor.CommonPageProcessorImpl;
import com.google.common.collect.Maps;

/**
 *
 * @author Mr.Bian
 *
 */
public class ParserTest {
	
	@Test
	public void parserTest(){
		ApplicationContext application = new ClassPathXmlApplicationContext("classpath*:config/spring/local/appcontext-*.xml","classpath*:config/spring/common/appcontext-*.xml");
		String url="http://sh.58.com/gongzhuang/28156234845775x.shtml";
		String jsonPath="classpath*:json/58-parser.json";
		Request r = new Request();
		r.setUrl(url);
		Map<String,Object> extra=Maps.newHashMap();
		extra.put(CrawlerCommonConstants.PageFieldKeys.ORIGIN_URL, url);
		r.setExtras(extra);
		Downloader d = new HttpClientDownloader();
		Task t =new Task(){

			@Override
			public String getUUID() {
				return null;
			}

			@Override
			public Site getSite() {
				return Site.me();
			}
			
		};
		
		Page p = d.download(r,t );
		System.out.println(p.getHtml().get());
		PageProcessor processor = new CommonPageProcessorImpl(getConfig(jsonPath));
		processor.process(p);
	}
	
	private CrawlerConfig getConfig(String path){
		String jsonData = "";
		String domainTag = "";
        try{
			ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
	        Resource[] resources = patternResolver.getResources(path);
	        if (null != resources && resources.length > 0) {
	            for (Resource resource : resources) {
	                String fileName = resource.getFilename();
	                domainTag = StringUtils.substringBefore(fileName, ".json");
	                jsonData = IOUtil.toString(resource.getInputStream());
	            }
	        }
        }catch(Exception e){
        	
        }
        CrawlerConfig config=JSON.parseObject(jsonData,CrawlerConfig.class);
        config.setDomainTag(domainTag);
		return config;
	}
}
