package com.dianping.ssp.crawler.parser.service.crawler.subprocessor;

import com.dianping.ed.logger.EDLogger;
import com.dianping.ed.logger.LoggerManager;
import com.dianping.ssp.crawler.common.contants.CrawlerCommonConstants;
import com.dianping.ssp.crawler.common.downloader.CrawlerDownloaderFactory;
import com.dianping.ssp.crawler.common.downloader.impl.MtDownloaderImpl;
import com.dianping.ssp.crawler.common.entity.ProStatus;
import com.dianping.ssp.crawler.common.pageprocessor.subprocess.CrawlerSubProcessTag;
import com.dianping.ssp.crawler.common.pageprocessor.subprocess.ICrawlerSubProcess;
import com.dianping.ssp.crawler.parser.service.log.CrawlerParserServiceLogEnum;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.downloader.Downloader;

/**
 * Created by iClaod on 11/24/16.
 */
@CrawlerSubProcessTag(name = "wechatReadCount")
public class WechatReadCountSubProcessor implements ICrawlerSubProcess{

    private static final EDLogger LOGGER = LoggerManager.getLogger(CrawlerParserServiceLogEnum.WECHAT_READ_COUNT.getValue());

    @Override
    public ProStatus process(Page page) {
        try {
            String url = (String) page.getRequest().getExtra(CrawlerCommonConstants.PageFieldKeys.ORIGIN_URL);
            if (url != null && url.contains("signature")) {
                String commentUrl = url.replace("mp.weixin.qq.com/s", "mp.weixin.qq.com/mp/getcomment");
                Request request = new Request(commentUrl);
                Page commentPage = MtDownloaderImpl.getUrl(request);
                String readCount = commentPage.getJson().jsonPath("$.read_num").get();
                page.putField("readCount", readCount);
                String likeCount = commentPage.getJson().jsonPath("$.like_num").get();
                page.putField("likeCount", likeCount);
                String commentCount = commentPage.getJson().jsonPath("$.elected_comment_total_cnt").get();
                page.putField("commentCount", commentCount);
            }
        } catch (Exception e) {
            LOGGER.error("error to get wechat readcount" + page.getRequest().getUrl(), e);
        }
        return ProStatus.success();
    }
}
