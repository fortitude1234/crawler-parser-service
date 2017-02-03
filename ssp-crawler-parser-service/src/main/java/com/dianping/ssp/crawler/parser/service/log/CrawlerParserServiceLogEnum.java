package com.dianping.ssp.crawler.parser.service.log;


import com.dianping.ed.logger.LoggerConfig;
import com.dianping.ed.logger.LoggerLevel;

/**
 * Description
 * Created by yuxiang.cao on 16/5/17.
 */
public enum CrawlerParserServiceLogEnum {
	PARSER_PROCESSOR("parser_processor", "subprocessor", false, LoggerLevel.INFO),
	LINEFEED_FILTER_PROCESSOR("linefeed_filter_processor", "subprocessor", false, LoggerLevel.INFO),
	WECHAT_READ_COUNT("wechat_readcount_ajax_processor", "subprocessor", false, LoggerLevel.INFO),
	IMAGE_SUBSTITUTE_PROCESSOR("image_substitute_processor", "subprocessor", false, LoggerLevel.INFO),
	IFRAME_SUBSTITUTE_PROCESSOR("iframe_substitute_processor", "subprocessor", false, LoggerLevel.INFO),
	MEETING_CITYID_PROCESSOR("meeting_cityid_processor","subprocessor",false,LoggerLevel.INFO),
	CONTENT_CONVERTER_PROCESSOR("content_converter_processor", "converter", false, LoggerLevel.INFO),
	ARTICLE_AUTOONLINE_PRODUCER("article_autoonline_producer", "mq", false, LoggerLevel.INFO),
	ARTICLE_AUTOONLINEPUSH_SERVICE("article_autoonlinepush_pushproducer", "mq", false, LoggerLevel.INFO),
	ARTICLE_PUBLISH_PRODUCER_ERROR("article_publish_producer_error", "mq", true, LoggerLevel.ERROR),
	ARTICLE_PUBLISH_SERVICE_ERROR("article_publish_error", "mq", true, LoggerLevel.ERROR),
	;

	CrawlerParserServiceLogEnum(String name, String category, boolean isError, LoggerLevel level) {
		loggerConfig = new LoggerConfig();
		loggerConfig.setApp(APP_NAME);
		loggerConfig.setCategory(category);
		loggerConfig.setName(name);
		loggerConfig.setLevel(level);
		loggerConfig.setDaily(true);
		loggerConfig.setPerm(false);
		loggerConfig.setError(isError);
	}

	private static final String APP_NAME = "ssp-crawler-parser-service";

	private LoggerConfig loggerConfig;

	public LoggerConfig getValue() {
		return loggerConfig;
	}
}
