package com.dianping.ssp.crawler.parser.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.ed.logger.EDLogger;
import com.dianping.ed.logger.LoggerManager;
import com.dianping.lion.client.Lion;
import com.dianping.ssp.crawler.common.scheduler.RedisRepository;
import com.dianping.ssp.crawler.parser.base.AbstractTest;
import com.dianping.ssp.crawler.parser.service.dao.ArticleOriginDao;
import com.dianping.ssp.crawler.parser.service.entity.ArticleOriginEntity;
import com.dianping.ssp.crawler.parser.service.log.CrawlerParserServiceLogEnum;
import com.dianping.ssp.file.download.SSPDownload;
import com.dianping.ssp.similarity.simhash.Fingerprint;
import com.dianping.ssp.similarity.simhash.FingerprintUtil;

public class LoadSimilerTest extends AbstractTest {
	private static final EDLogger LOGGER = LoggerManager.getLogger(CrawlerParserServiceLogEnum.PARSER_PROCESSOR.getValue());

	private static final Map<Long, String> ARTICLE_CONTENT_URL=new HashMap<Long, String>();
	@Autowired
    private ArticleOriginDao articleOriginDao;
    @Autowired
    private RedisRepository redisRepository;
	@Test
	public void test() throws Exception {
		String content = SSPDownload.MssS3.downloadPlainFileContent("2016/10/21/4b537552c9f34756b2ca4a13fcf2b79e.html");
		long contentFingerprint=FingerprintUtil.createDefaultNgramFingerprint(content);
		Set<Fingerprint> fingerprints=redisRepository.loadAllFingerprintSet();
    	List<Long> thisHashcodes=null;
    	int similer_simhash_threshold = Lion.getIntValue("ssp-crawler-parser-service.article.similer.simhash.threshold", 7);
    	double similer_percent = Lion.getDoubleValue("ssp-crawler-parser-service.article.similer.percent.threshold", 0.65);
    	for (Fingerprint fingerprint2 : fingerprints) {
    		int similer_simhash=FingerprintUtil.hammingDistance(contentFingerprint, fingerprint2.getContentFingerprint());
    		if(similer_simhash_threshold >= similer_simhash ){
    			if(thisHashcodes==null){
    				thisHashcodes=FingerprintUtil.createNgramHashcodeList(content);
    			}
    			String url=ARTICLE_CONTENT_URL.get(fingerprint2.getArticleId());
    			if(url==null){
    				ArticleOriginEntity entity = articleOriginDao.loadById(fingerprint2.getArticleId());
    				if(entity!=null && StringUtils.isNotBlank(entity.getContentUrl())){
    					url=entity.getContentUrl();
    					ARTICLE_CONTENT_URL.put(entity.getId(), url);
    				}
    			}
    			if(StringUtils.isBlank(url)){
    				continue;
    			}
				try {
					String content2 = SSPDownload.MssS3.downloadPlainFileContent(url);
					List<Long> thisHashcodes2=FingerprintUtil.createNgramHashcodeList(content2);
					double similerPercent=FingerprintUtil.listSimilerPercent(thisHashcodes, thisHashcodes2);
					System.out.println("similer_simhash_threshold = "+similer_simhash+"; similer_percent = " + similerPercent);
					if(similerPercent >= similer_percent){
						System.out.println("++++++similer_simhash_threshold = "+similer_simhash+"; similer_percent = " + similerPercent);
					}
				} catch (IOException e) {
					LOGGER.error("failed to SSPDownload.MssS3.downloadPlainFileContent, url: " + url, e);
				}
    		}
		}
	}
}
