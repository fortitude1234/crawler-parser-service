package com.dianping.ssp.crawler.parser.service.crawler.subprocessor;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import us.codecraft.webmagic.Page;

import com.dianping.ed.logger.EDLogger;
import com.dianping.ed.logger.LoggerManager;
import com.dianping.ssp.crawler.common.entity.ProStatus;
import com.dianping.ssp.crawler.common.enums.ProMessageCode;
import com.dianping.ssp.crawler.common.pageprocessor.subprocess.CrawlerSubProcessTag;
import com.dianping.ssp.crawler.common.pageprocessor.subprocess.ICrawlerSubProcess;
import com.dianping.ssp.crawler.common.util.ImgUploadUtil;
import com.dianping.ssp.crawler.parser.service.log.CrawlerParserServiceLogEnum;

/**
 * Created by iClaod on 10/17/16.
 */
@CrawlerSubProcessTag(name = "wechatImgSubstitute")
public class WechatImageSubstituteSubProcessor implements ICrawlerSubProcess {

    private static final EDLogger LOGGER = LoggerManager.getLogger(CrawlerParserServiceLogEnum.IMAGE_SUBSTITUTE_PROCESSOR.getValue());

    @Override
    public ProStatus process(Page page) {
    	if ( page.getHtml().getDocument().getElementById("js_content")==null){
    		return ProStatus.fail(ProMessageCode.SUB_PROCESSOR_ERROR.getCode());
    	}
        Elements elements = page.getHtml().getDocument().getElementById("js_content").getElementsByTag("img");
        for (Element element: elements) {
            String dataSrc = element.attr("data-src");
            dataSrc = dataSrc.split("\\?")[0];
            String dataType = element.attr("data-type");
            byte[] remoteFileStream = ImgUploadUtil.downloadImg(dataSrc);
            String newDataSrc = ImgUploadUtil.upload(remoteFileStream, "sixBing." + dataType);
            if ("gif".equals(dataType)) {
                newDataSrc = ImgUploadUtil.getOriginalFilePath(newDataSrc);
            } else {
                try {
                    BufferedImage sourceImg = ImageIO.read(new ByteArrayInputStream(remoteFileStream));
                    if (sourceImg != null && (sourceImg.getWidth() > 720)) {
                        newDataSrc = ImgUploadUtil.addSizeToImgUrl(newDataSrc, 720);
                    }
                } catch (Exception e) {
                    LOGGER.error("failed to load size", e);
                }
            }
            if (StringUtils.isNotEmpty(dataSrc)) {
                element.attr("src", newDataSrc);
                element.attributes().remove("data-src");
            }
        }
        return ProStatus.success();
    }


}
