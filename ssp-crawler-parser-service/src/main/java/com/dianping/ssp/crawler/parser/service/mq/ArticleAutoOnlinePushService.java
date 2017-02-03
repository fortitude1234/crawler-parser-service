package com.dianping.ssp.crawler.parser.service.mq;

import com.alibaba.fastjson.JSON;
import com.dianping.ed.logger.EDLogger;
import com.dianping.ed.logger.LoggerManager;
import com.dianping.lion.client.Lion;
import com.dianping.ssp.article.constant.OriginalStatusCode;
import com.dianping.ssp.crawler.parser.service.dao.ArticleOriginDao;
import com.dianping.ssp.crawler.parser.service.log.CrawlerParserServiceLogEnum;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
@Service
public class ArticleAutoOnlinePushService {

	private static final EDLogger LOGGER = LoggerManager.getLogger(CrawlerParserServiceLogEnum.ARTICLE_AUTOONLINEPUSH_SERVICE.getValue());
	private static final String DOMAIN_TAG = "wechat-parser";
	@Autowired
	private ArticleAutoOnlineProducer producer;
	@Autowired
	private ArticleOriginDao articleOriginDao;
	
	public void sendMessage(long id, String source, String domainTag){
		String logStr = String.format("id=%d, source=%s, domainTag=%s", id, source, domainTag);
		try {
			if(!Lion.getBooleanValue("ssp-crawler-parser-service.autoOnlineSwitch")) {
				LOGGER.info("ArticleAutoOnlinePushService.sendMessage auto online switch is off. " + logStr);
				return;
			}

			if(id < 0 || !Lists.newArrayList(Lion.get("ssp-crawler-parser-service.autoOnline.source", "").split(",")).contains(source) || !DOMAIN_TAG.equals(domainTag)) {
				LOGGER.info("ArticleAutoOnlinePushService.sendMessage msg drop." + logStr);
				return;
			}
			Map<String,Long> msg = new HashMap<String, Long>(1);
			msg.put("originArticleId", id);
			boolean result = producer.sendMessage(JSON.toJSONString(msg));
			if (result) {
				articleOriginDao.updateStatus(id, OriginalStatusCode.DISTRIBUTED);
			}
			LOGGER.info(String.format("ArticleAutoOnlinePushService.sendMessage result=%s, originArticleId=%s.",result,id) + logStr);
		} catch (Exception e) {
			LOGGER.error("ArticleAutoOnlinePushService.sendMessage exception. " + logStr, e);
		}
	}
	
}
