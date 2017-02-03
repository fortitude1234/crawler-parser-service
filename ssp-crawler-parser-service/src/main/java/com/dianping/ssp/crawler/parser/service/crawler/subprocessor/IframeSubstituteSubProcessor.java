package com.dianping.ssp.crawler.parser.service.crawler.subprocessor;

import com.dianping.ed.logger.EDLogger;
import com.dianping.ed.logger.LoggerManager;
import com.dianping.ssp.crawler.common.entity.ProStatus;
import com.dianping.ssp.crawler.common.pageprocessor.subprocess.CrawlerSubProcessTag;
import com.dianping.ssp.crawler.common.pageprocessor.subprocess.ICrawlerSubProcess;
import com.dianping.ssp.crawler.common.util.ImgUploadUtil;
import com.dianping.ssp.crawler.parser.service.constant.ArticleVideoType;
import com.dianping.ssp.crawler.parser.service.crawler.util.UrlParamUtil;
import com.dianping.ssp.crawler.parser.service.log.CrawlerParserServiceLogEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Created by iClaod on 10/17/16.
 */
@CrawlerSubProcessTag(name = "iframeSubstitute")
public class IframeSubstituteSubProcessor implements ICrawlerSubProcess {

    private static final EDLogger LOGGER = LoggerManager.getLogger(CrawlerParserServiceLogEnum.IFRAME_SUBSTITUTE_PROCESSOR.getValue());

    @Override
    public ProStatus process(Page page) {
        Elements detailElements = page.getHtml().getDocument().getElementsByClass("details-content");
        Elements elements = detailElements.get(0).getElementsByTag("iframe");
        if(CollectionUtils.isNotEmpty(elements)) {
            page.putField("isVideo", ArticleVideoType.NOT_VIDEO);
        }
        for (Element element: elements) {
            String src = element.attr("src");
            if (StringUtils.isNotBlank(src)) {
                page.putField("isVideo", ArticleVideoType.IS_VIDEO);
                String widthString = element.attr("width");
                String heightString = element.attr("height");
                if(widthString != null && heightString != null) {
                    try{
                        float realWidth = Float.parseFloat(widthString);
                        float realHeight = Float.parseFloat(heightString);
                        element.attr("data-ratio", String.format("%.2f", realHeight/realWidth));
                    }catch(Exception ex){
                        LOGGER.error("can not get iframe ratio of height and width", ex);
                    }
                }
                element.attributes().remove("height");
                element.attributes().remove("width");
            } else {
                element.remove();
            }
        }
        return ProStatus.success();
    }

}
