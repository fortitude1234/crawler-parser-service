package com.dianping.ssp.crawler.parser.service.crawler.subprocessor;

import com.dianping.ed.logger.EDLogger;
import com.dianping.ed.logger.LoggerManager;
import com.dianping.ssp.crawler.common.entity.ProStatus;
import com.dianping.ssp.crawler.common.pageprocessor.subprocess.CrawlerSubProcessTag;
import com.dianping.ssp.crawler.common.pageprocessor.subprocess.ICrawlerSubProcess;
import com.dianping.ssp.crawler.common.util.ImgUploadUtil;
import com.dianping.ssp.crawler.parser.service.log.CrawlerParserServiceLogEnum;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;

/**
 * Created by iClaod on 10/17/16.
 */
@CrawlerSubProcessTag(name = "linkRemove")
public class LinkRemoveSubProcessor implements ICrawlerSubProcess {

    private static final EDLogger LOGGER = LoggerManager.getLogger(CrawlerParserServiceLogEnum.IMAGE_SUBSTITUTE_PROCESSOR.getValue());

    private static final String urlPattern = "[h|H][t|T][t|T][p|P].*";

    @Override
    public ProStatus process(Page page) {

        Elements elements = page.getHtml().getDocument().getElementsByTag("a");
        for (Element element: elements) {
            String href = element.attr("href");
            if (href != null && href.matches(urlPattern)) {
                element.remove();
                continue;
            }
            href = element.attr("data_ue_src");
            if (href != null && href.matches(urlPattern)) {
                element.remove();
                continue;
            }
        }
        return ProStatus.success();
    }


}
