package com.dianping.ssp.crawler.parser.service.crawler.subprocessor;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.utils.UrlUtils;

import com.dianping.ed.logger.EDLogger;
import com.dianping.ed.logger.LoggerManager;
import com.dianping.ssp.crawler.common.contants.CrawlerCommonConstants;
import com.dianping.ssp.crawler.common.entity.ProStatus;
import com.dianping.ssp.crawler.common.pageprocessor.subprocess.CrawlerSubProcessTag;
import com.dianping.ssp.crawler.common.pageprocessor.subprocess.ICrawlerSubProcess;
import com.dianping.ssp.crawler.common.util.ImgUploadUtil;
import com.dianping.ssp.crawler.parser.service.log.CrawlerParserServiceLogEnum;

/**
 * Created by iClaod on 10/17/16.
 */
@CrawlerSubProcessTag(name = "imgSubstitute")
public class ImageSubstituteSubProcessor implements ICrawlerSubProcess {

    private static final EDLogger LOGGER = LoggerManager.getLogger(CrawlerParserServiceLogEnum.IMAGE_SUBSTITUTE_PROCESSOR.getValue());

    private static final String urlPattern = "[h|H][t|T][t|T][p|P].*";

    @Override
    public ProStatus process(Page page) {
        Elements elements = page.getHtml().getDocument().getElementsByTag("img");
		for (Element element : elements) {
			try{
				String dataSrc = element.attr("src");
			    if (StringUtils.isBlank(dataSrc)) {
			    	dataSrc = element.attr("data-original");
				}
			    if (StringUtils.isNotEmpty(dataSrc)) {
			    	if (!dataSrc.matches(urlPattern)) {
			    		String originUrl = (String) page.getRequest().getExtra(CrawlerCommonConstants.PageFieldKeys.ORIGIN_URL);
			    		if (StringUtils.isNotEmpty(originUrl)) {
			    			dataSrc = UrlUtils.getHost(originUrl) + dataSrc;
			    		}
			    	}
			    	dataSrc = dataSrc.replaceAll("https://", "http://");
			    	if (isLegalUrl(dataSrc)) {
			    		String newDataSrc = getLocalPath(dataSrc);
			    		element.attr("src", newDataSrc);
			    	}
			    }
			}catch(Exception e){
				LOGGER.error("ImageSubstituteSubProcessor error! element=" + element.html(),e);
			}
		}
        return ProStatus.success();
    }

    private static Pattern patternForWechat = Pattern.compile(".*?wx_fmt=(jpg|gif|png|bmp|jpeg).*", Pattern.CASE_INSENSITIVE);

    public static String getLocalPath(String uri) {
        byte[] bytes = ImgUploadUtil.downloadImg(uri);
        String newUrl = "";
        try{
	        if (bytes.length > 0) {
	            if (uri.matches("(http://mmbiz.qlogo.cn/.*)|(http://mmbiz.qpic.cn/.*)")) {
	                Matcher matcher = patternForWechat.matcher(uri);
	                if (matcher.find()) {
	                    newUrl = ImgUploadUtil.upload(bytes, "sixBing." + matcher.group(1));
	                }
	            } else {
	                newUrl = ImgUploadUtil.upload(bytes, uri);
	            }
	        } else {
	            LOGGER.info("img size is 0, url: " + uri);
	        }
			if (newUrl.contains("gif")) {
				newUrl = ImgUploadUtil.getOriginalFilePath(newUrl);
			} else {
				BufferedImage sourceImg = ImageIO.read(new ByteArrayInputStream(bytes));
				if (sourceImg != null && (sourceImg.getWidth() > 720 || sourceImg.getHeight() > 720)) {
					newUrl = ImgUploadUtil.addSizeToImgUrl(newUrl, 720);
				}
			}
        }catch(Exception e){
        	LOGGER.error("failed to load size", e);
        }
       
        return newUrl;
    }

    private boolean isLegalUrl(String url) {
        return url.matches("http://([^']+(?:jpg|gif|png|bmp|jpeg))(\\S+)?");
    }

}
