package com.dianping.ssp.crawler.parser.service.crawler.util;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;

import com.google.common.collect.Lists;

/**
 * Created by iClaod on 11/16/16.
 */
public class CleanDomUtil {
	private static List<String> ISOLATE_ELEMENT = Lists.newArrayList("img", "iframe");

	private static final String WHITESPACE_PATTERN = "((&nbsp;)|\\s|\\u00A0|\\u0000|\\u0007|\\u0009|\\u000A|\\u000B|\\u000C|\\u000D|\\u0020|\\u3000)*";

	public static String getTansFormHtml (String html){
		return new NodeCleanTraversor().getTransFormDom(html);
	}
	
	public static void filterEmptyBlock(Element element) {
		if (CollectionUtils.isNotEmpty(element.children())) {
			for (Element child: element.children()) {
				filterEmptyBlock(child);
			}
		}
		if (element.children().size()==0 && (StringUtils.isEmpty(element.text()) || element.text().matches(WHITESPACE_PATTERN))) {
			String tagName=element.tagName();
			if (!ISOLATE_ELEMENT.contains(tagName)){
				element.remove();
			}
		}
	}
    
}
