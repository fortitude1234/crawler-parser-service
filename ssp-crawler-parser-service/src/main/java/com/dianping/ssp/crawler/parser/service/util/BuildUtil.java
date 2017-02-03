package com.dianping.ssp.crawler.parser.service.util;

import com.dianping.ssp.crawler.parser.service.entity.ArticleOriginEntity;
import com.dianping.ssp.crawler.parser.service.mq.entity.ArticlePublishMessage;

public class BuildUtil {

	public static ArticlePublishMessage buildArticlePublishMessage(long originArticleId, int operateType, ArticleOriginEntity entity){
		if(originArticleId == 0 || entity == null){
			return null;
		}
		ArticlePublishMessage message = new ArticlePublishMessage();
		message.setAuthor(entity.getAuthor());
		message.setContentUrl(entity.getContentUrl());
		message.setCrawl_time(entity.getCrawlTime());
		message.setDomain_tag(entity.getDomainTag());
		message.setOperateType(operateType);
		message.setOrigin_article_id(originArticleId);
		message.setOriginal_time(entity.getOriginalTime());
		message.setSource(entity.getSource());
		message.setThumbnail(entity.getThumbnail());
		message.setTitle(entity.getTitle());
		message.setUrl(entity.getUrl());
		
		return message;
	}
}
