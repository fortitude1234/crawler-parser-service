package com.dianping.ssp.crawler.parser.service.crawler.converter;

import com.dianping.ed.logger.EDLogger;
import com.dianping.ed.logger.LoggerManager;
import com.dianping.ssp.crawler.common.pageprocessor.fieldhandler.converter.CrawlerConverterTag;
import com.dianping.ssp.crawler.common.pageprocessor.fieldhandler.converter.ICrawlerConverter;
import com.dianping.ssp.crawler.parser.service.crawler.util.CleanDomUtil;
import com.dianping.ssp.crawler.parser.service.log.CrawlerParserServiceLogEnum;

/**
 *
 * @author Mr.Bian
 *
 */
@CrawlerConverterTag(name = "htmlCleanConverter")
public class HtmlCleanConvert implements ICrawlerConverter{
	private static final EDLogger LOGGER = LoggerManager.getLogger(CrawlerParserServiceLogEnum.CONTENT_CONVERTER_PROCESSOR.getValue());
	
	@Override
	public Object converter(Object sourceData, Object params) {
		if (sourceData instanceof String){
			String content=(String)sourceData;
			String newContent=null;
			try{
				newContent=CleanDomUtil.getTansFormHtml(content);
			}catch(Exception e){
				LOGGER.error("Html clean fail! html="+content, e);
			}
			return newContent;
		}
		return null;
	}

}
