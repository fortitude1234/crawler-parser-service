package com.dianping.ssp.crawler.parser.service.crawler.converter;

import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.dianping.ed.logger.EDLogger;
import com.dianping.ed.logger.LoggerManager;
import com.dianping.ssp.crawler.common.pageprocessor.fieldhandler.converter.CrawlerConverterTag;
import com.dianping.ssp.crawler.common.pageprocessor.fieldhandler.converter.ICrawlerConverter;
import com.dianping.ssp.crawler.parser.service.log.CrawlerParserServiceLogEnum;

/**
 * 删除某个标记位之后的所有内容
 * <p>
 * Created by lin.zhao on 11/18/16.
 */
@CrawlerConverterTag(name = "removeTailConverter")
public class RemoveTailConverterImpl implements ICrawlerConverter {

    private static final EDLogger LOGGER = LoggerManager.getLogger(CrawlerParserServiceLogEnum.CONTENT_CONVERTER_PROCESSOR.getValue());

    @Override
    public Object converter(Object sourceData, Object params) {
        try {
            LOGGER.info("RemoveTailConverterImpl.convert begin....");
            if (null == sourceData || !(sourceData instanceof String)) {
                LOGGER.error("RemoveTailConverterImpl.convert sourceData is null or not String. sourceData =" + sourceData);
                return null;
            }
            Document document = Jsoup.parse((String) sourceData);

            Element terminalElement = findTerminalElement(document, (Map) params);      // 找到 标记位 结束的节点
            removeElementAfterTerminalElement(terminalElement); // 开始删除
            selfRemove(terminalElement);
            LOGGER.info("RemoveTailConverterImpl.convert success....");
            return document.body().html();
        } catch (Exception e) {
            LOGGER.error("RemoveTailConverterImpl.convert failed! content="+sourceData , e);
        }
        return sourceData;
    }

    private void selfRemove(Element terminalElement) {
        if(terminalElement!=null) {
            LOGGER.info("begin selfRemove.");
            terminalElement.remove();
        }
    }


    private static void removeElementAfterTerminalElement(Element terminalElement) {
        if (terminalElement == null) return;
        LOGGER.info("begin removeElementAfterTerminalElement...");

        Element parent = terminalElement.parent();  // 找到该节点的父节点，用于递归后面的元素

        Element siblingElement = terminalElement.nextElementSibling();
        while (siblingElement != null) {
            Element toRemoveElement = siblingElement;
            siblingElement = toRemoveElement.nextElementSibling();
            if (toRemoveElement != null)
                toRemoveElement.remove();
        }

        if (parent != null) {
            removeElementAfterTerminalElement(parent);
        }
    }

    private static Element findTerminalElement(Element element, Map<String, String> params) {
        if(element instanceof Document){
        String deleteIds = (String)params.get("deleteIds");
        if(deleteIds != null){
            String [] idArray = deleteIds.split(",");
            for(String id :idArray){
                    Document document =(Document)element;
                    Element  toRemove = document.getElementById(id);
                    if(toRemove != null){
                        toRemove.remove();
                    }
                }
            }
        }
        String terminalClass =(String)params.get("terminalClass");
        if(terminalClass != null)
            if(element instanceof Document ){
                Document document = (Document) element;
                Elements elements = document.getElementsByClass(terminalClass);
                if(CollectionUtils.isEmpty(elements)){
                    return null ;
            }
                return elements.get(0);
         }



        Elements children = element.children();
        if (CollectionUtils.isEmpty(children)) {  // 叶子节点
            String terminalKeywords = (String) params.get("terminalKeywords");
            String text = element.text();
            if(terminalKeywords != null)
                if (StringUtils.isNotBlank(text) &&  Pattern.matches(terminalKeywords, text) ) {
                    LOGGER.info("findTerminalElement . " + element);
                    return element;
                }
            return null;
        } else {
            for (Element child : children) {
                Element terminalElement = findTerminalElement(child, params);
                if (terminalElement != null) {
                    return terminalElement;
                }
            }
        }
        return null;
    }
}
