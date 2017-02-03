package com.dianping.ssp.crawler.parser.service.crawler.subprocessor;

import com.dianping.ed.logger.EDLogger;
import com.dianping.ed.logger.LoggerManager;
import com.dianping.ssp.crawler.common.contants.CrawlerCommonConstants;
import com.dianping.ssp.crawler.common.entity.ProStatus;
import com.dianping.ssp.crawler.common.enums.ProMessageCode;
import com.dianping.ssp.crawler.common.pageprocessor.subprocess.CrawlerSubProcessTag;
import com.dianping.ssp.crawler.common.pageprocessor.subprocess.ICrawlerSubProcess;
import com.dianping.ssp.crawler.parser.service.log.CrawlerParserServiceLogEnum;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.HtmlNode;
import us.codecraft.webmagic.selector.Selectable;

import java.util.*;

/**
 * Created by iClaod on 10/17/16.
 */
@CrawlerSubProcessTag(name = "wechatFilter")
public class WechatFilterSubProcessor implements ICrawlerSubProcess {

    private static final EDLogger LOGGER = LoggerManager.getLogger(CrawlerParserServiceLogEnum.LINEFEED_FILTER_PROCESSOR.getValue());

    private static final String LINEFEED_PATTERN = "((&nbsp;)|\\s|\\u00A0|\\u0000|\\u0007|\\u0009|\\u000A|\\u000B|\\u000C|\\u000D|\\u0020|\\u3000)*";

    private static final Set<String> notFilterTagNames = Sets.newHashSet("img", "iframe");

    @Override
    public ProStatus process(Page page) {
        try {
            filterEmptyElement(page.getHtml().getDocument().getElementById("js_content"));
            return ProStatus.success();
        } catch (Exception e) {
            LOGGER.error("failed to parse detail page, url: " + (String) page.getRequest().getExtra(CrawlerCommonConstants.PageFieldKeys.ORIGIN_URL), e);
        }
        return ProStatus.fail(ProMessageCode.SUB_PROCESSOR_ERROR.getCode(), "过滤失败");
    }

    private void filterEmptyElement(Element element) {
        if (CollectionUtils.isNotEmpty(element.children())) {
            for (Element childElement : element.children()) {
                filterEmptyElement(childElement);
            }
        }
        if (CollectionUtils.isNotEmpty(element.children())) {
            return;
        }
        if (element.tagName().equals("br")) {
            element.remove();
            return;
        }
        if (notFilterTagNames.contains(element.tagName().toLowerCase())) {
            return;
        }
        String text = element.text();
        if (StringUtils.isEmpty(text) || element.text().matches(LINEFEED_PATTERN)) {
            element.remove();
        }
    }

    public static void main(String[] args) {
        char[] a = new char[24];
        a[0] = '0';
        a[1] = '\u00A0';
        a[2] = '1';
        a[3] = '\u0000';
        a[4] = '2';
        a[5] = '\u0000';
        a[6] = '3';
        a[7] = '\u0007';
        a[8] = '4';
        a[9] = '\u0009';
        a[10] = '5';
        a[11] = 10;
        a[12] = '6';
        a[13] = '\u000B';
        a[14] = '7';
        a[15] = '\u000C';
        a[16] = '8';
        a[17] = 13;
        a[18] = '9';
        a[19] = '\u0020';
        a[20] = 'A';
        a[21] = '\uA1A1';
        a[22] = 'B';
        a[23] = '\u3000';
        System.out.println(new String(a));
        System.out.println(new String(a));
        System.out.println(new String(a).replaceAll(LINEFEED_PATTERN, ""));

    }


}
