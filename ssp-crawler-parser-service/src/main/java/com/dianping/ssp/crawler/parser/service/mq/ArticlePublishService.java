package com.dianping.ssp.crawler.parser.service.mq;

import com.alibaba.fastjson.JSON;
import com.dianping.ed.logger.EDLogger;
import com.dianping.ed.logger.LoggerManager;
import com.dianping.lion.client.Lion;
import com.dianping.ssp.article.constant.OriginArticleOperateCode;
import com.dianping.ssp.article.constant.OriginalStatusCode;
import com.dianping.ssp.article.utils.JsonUtil;
import com.dianping.ssp.crawler.parser.service.constant.ArticleVideoType;
import com.dianping.ssp.crawler.parser.service.dao.ArticleOriginDao;
import com.dianping.ssp.crawler.parser.service.entity.ArticleOriginEntity;
import com.dianping.ssp.crawler.parser.service.entity.OriginArticleExtraMessageEntity;
import com.dianping.ssp.crawler.parser.service.log.CrawlerParserServiceLogEnum;
import com.dianping.ssp.crawler.parser.service.util.BuildUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ArticlePublishService {

	private static final EDLogger LOGGER = LoggerManager.getLogger(CrawlerParserServiceLogEnum.ARTICLE_PUBLISH_SERVICE_ERROR.getValue());
	private static final String DOMAIN_TAG_LIST = "hongcan-parser";
	private static final String SOURCE_LIST = "我们加餐";
	@Autowired
	private ArticlePublishProducer producer;
	@Autowired
	private ArticleOriginDao articleOriginDao;

	public void publish(long id, ArticleOriginEntity articleOriginEntity){
		String logStr = String.format("origin articleId=%d", id);
		try {
			if(!Lion.getBooleanValue("ssp-crawler-parser-service.publishSwitch")) {
				LOGGER.info("ArticlePublishService.sendMessage switch is off. " + logStr);
				return;
			}

			if(id < 0 || articleOriginEntity == null) {
				return;
			}
			
			int isVideo = ArticleVideoType.NOT_VIDEO;
			if(StringUtils.isNotBlank(articleOriginEntity.getExtraMessage())){
				OriginArticleExtraMessageEntity entity = JsonUtil.fromJson(articleOriginEntity.getExtraMessage(), OriginArticleExtraMessageEntity.class);
				if(entity != null){
					isVideo = entity.getIsVideo();
				}
			}

			//自动分发
			if((Lists.newArrayList(SOURCE_LIST.split(",")).contains(articleOriginEntity.getSource())
					|| Lists.newArrayList(DOMAIN_TAG_LIST.split(",")).contains(articleOriginEntity.getDomainTag())) && isVideo == ArticleVideoType.IS_VIDEO){
				boolean result = producer.sendMessage(JSON.toJSONString(BuildUtil.buildArticlePublishMessage(id, OriginArticleOperateCode.DISTRIBUTE, articleOriginEntity)));
				if (result) {
					articleOriginDao.updateStatus(id, OriginalStatusCode.DISTRIBUTED);
				}
			}
		} catch (Exception e) {
			LOGGER.error("ArticlePublishService.sendMessage exception. " + logStr, e);
		}
	}
	
}
