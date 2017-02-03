package com.dianping.ssp.crawler.parser.service.crawler.converter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dianping.ssp.crawler.common.pageprocessor.fieldhandler.converter.CrawlerConverterTag;
import com.dianping.ssp.crawler.common.pageprocessor.fieldhandler.converter.ICrawlerConverter;

/**
 *
 * @author Mr.Bian
 *
 */
@CrawlerConverterTag(name = "regexConverter")
public class RegexConverterImpl implements ICrawlerConverter{

	@Override
	public Object converter(Object sourceData, Object params) {
		if (!(sourceData instanceof String) && ! (params instanceof String)){
			return null;
		}
		String source = (String) sourceData;
		String regex = (String) params;
		if (source==null || regex==null){
			return null;
		}
		Pattern p = Pattern.compile(regex);
		Matcher m=p.matcher(source);
		String result=null;
		if (m.find()){
			result=m.group();
		}
		return result;
	}

}
