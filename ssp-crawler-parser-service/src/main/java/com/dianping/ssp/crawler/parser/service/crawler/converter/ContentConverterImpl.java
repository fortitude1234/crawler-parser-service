package com.dianping.ssp.crawler.parser.service.crawler.converter;

import com.dianping.ed.logger.EDLogger;
import com.dianping.ed.logger.LoggerManager;
import com.dianping.ssp.crawler.common.pageprocessor.fieldhandler.converter.CrawlerConverterTag;
import com.dianping.ssp.crawler.common.pageprocessor.fieldhandler.converter.ICrawlerConverter;
import com.dianping.ssp.crawler.common.pageprocessor.fieldhandler.converter.impl.HtmlTagFilterConverterImpl;
import com.dianping.ssp.crawler.parser.service.crawler.util.CleanDomUtil;
import com.dianping.ssp.crawler.parser.service.log.CrawlerParserServiceLogEnum;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import us.codecraft.webmagic.selector.Html;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by iClaod on 11/9/16.
 */
@CrawlerConverterTag(name = "contentConverter")
public class ContentConverterImpl implements ICrawlerConverter {

    private static final EDLogger LOGGER = LoggerManager.getLogger(CrawlerParserServiceLogEnum.CONTENT_CONVERTER_PROCESSOR.getValue());

    private static final String LINEFEED_PATTERN = "((&nbsp;)|\\s|\\u00A0|\\u0000|\\u0007|\\u0009|\\u000A|\\u000B|\\u000C|\\u000D|\\u0020|\\u3000)*";

    private static final Set<String> notFilterTagNames = Sets.newHashSet("img", "iframe");

    @Override
    public Object converter(Object sourceData, Object params) {
        try {
            if (null == sourceData || !(sourceData instanceof String)) {
                return null;
            }
            Document document = Jsoup.parse((String) sourceData);
            //CleanDomUtil.cleanDom(document);
            filterEmptyElement(document, (Map) params);
            return document.body().html();
        } catch (Exception e) {
            LOGGER.error("failed to convert content", e);
        }
        return null;
    }

    private static void filterEmptyElement(Element element, Map params) {
        if(null != params ) {
            String classFilterPattern = (String) params.get("classFilterPattern");
            if (classFilterPattern != null && StringUtils.isNotBlank(classFilterPattern)) {
                String text = element.attr("class");
                if (StringUtils.isNotBlank(text) && text.matches(classFilterPattern)) {
                    LOGGER.info("filtered pattern: " + element.outerHtml());
                    element.remove();
                    return;
                }
            }
        }
        if (CollectionUtils.isNotEmpty(element.children())) {
            for (Element childElement : element.children()) {
                filterEmptyElement(childElement, params);
            }
        }
        if (element.tagName().equals("a")) {
            element.unwrap();
            return;
        }
        if (CollectionUtils.isNotEmpty(element.children())) {
            return;
        }
        if (element.tagName().equals("br")) {
            element.remove();
            return;
        }

        if (element.tagName().equals("img") && StringUtils.isBlank(element.attr("src"))) {
            LOGGER.info("filtered pattern: " + element.outerHtml());
            element.remove();
            return;
        }
        if(null != params ) {
            String contentFilterPattern = (String) params.get("contentFilterPattern");
            if (contentFilterPattern != null && StringUtils.isNotBlank(contentFilterPattern)) {
                String text = element.text();
                if (StringUtils.isNotBlank(text) && text.matches(contentFilterPattern)) {
                    LOGGER.info("filtered content pattern: " + element.outerHtml());
                    element.remove();
                    return;
                }
            }
            String textRemover = (String) params.get("textRemover");
            if (textRemover != null && StringUtils.isNotBlank(textRemover)) {
                String text = element.text();
                if (StringUtils.isNotBlank(text)) {
                    LOGGER.info("filtered pattern: " + element.outerHtml());
                    element.text(text.replace(textRemover, ""));
                    return;
                }
            }
        }

        if (notFilterTagNames.contains(element.tagName().toLowerCase())) {
            return;
        }
        String text = element.text();
        if (StringUtils.isEmpty(text) || element.text().matches(LINEFEED_PATTERN)) {
            LOGGER.info("filtered pattern: " + element.outerHtml());
            element.remove();
        }
    }

}
