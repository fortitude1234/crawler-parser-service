package com.dianping.ssp.crawler.parser.service.crawler.subprocessor;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;

import com.alibaba.fastjson.JSON;
import com.dianping.ed.logger.EDLogger;
import com.dianping.ed.logger.LoggerManager;
import com.dianping.lion.client.Lion;
import com.dianping.ssp.article.constant.OriginalStatusCode;
import com.dianping.ssp.crawler.common.contants.CrawlerCommonConstants;
import com.dianping.ssp.crawler.common.contants.DuplicateType;
import com.dianping.ssp.crawler.common.entity.ProStatus;
import com.dianping.ssp.crawler.common.entity.ProcessorContext;
import com.dianping.ssp.crawler.common.enums.ProMessageCode;
import com.dianping.ssp.crawler.common.pageprocessor.subprocess.CrawlerSubProcessTag;
import com.dianping.ssp.crawler.common.pageprocessor.subprocess.ICrawlerSubProcess;
import com.dianping.ssp.crawler.common.scheduler.RedisRepository;
import com.dianping.ssp.crawler.common.util.JsonUtil;
import com.dianping.ssp.crawler.parser.service.dao.Crawler58ResultDao;
import com.dianping.ssp.crawler.parser.service.entity.Crawler58ResultEntity;
import com.dianping.ssp.crawler.parser.service.log.CrawlerParserServiceLogEnum;
import com.dianping.ssp.file.download.SSPDownload;
import com.dianping.ssp.file.upload.SSPUpload;
import com.dianping.ssp.file.upload.client.mss.MssS3UploadClient;
import com.dianping.ssp.file.upload.client.mss.result.MssS3UploadResult;
import com.dianping.ssp.similarity.simhash.Fingerprint;
import com.dianping.ssp.similarity.simhash.FingerprintUtil;
import com.google.common.collect.Maps;

/**
 *
 * @author Mr.Bian
 *
 */
@CrawlerSubProcessTag(name="58ParserSubProcessor")
public class FiveEightParserSubProcessor implements ICrawlerSubProcess{

	private static final EDLogger LOGGER = LoggerManager.getLogger(CrawlerParserServiceLogEnum.PARSER_PROCESSOR.getValue());

    private static final Map<Long, String> ARTICLE_CONTENT_URL=new HashMap<Long, String>();

    private static final int RETRY_TIMES = 5;
    

    @Autowired
    private Crawler58ResultDao crawler58ResultDao;
    @Autowired
    private RedisRepository redisRepository;

    @Override
    public ProStatus process(Page page) {
        for (int i = 0; i < RETRY_TIMES; i++) {
            try {
                String content = page.getResultItems().get(CrawlerCommonConstants.PageFieldKeys.CONTENT);
                MssS3UploadClient uploadClient = SSPUpload.MssS3.getMssS3UploadClient();
                MssS3UploadResult uploadResult = uploadClient.uploadFile(content.getBytes(), "sixBing.html", null);
                if (uploadResult == null || !uploadResult.isSuccess() || StringUtils.isEmpty(uploadResult.getFilename())) {
                    continue;
                }
                String remoteFileName = uploadResult.getFilename();
                long contentFingerprint=FingerprintUtil.createDefaultNgramFingerprint(content);
                Date meetingBeginTime = page.getResultItems().get("meetingBeginTime");
                Fingerprint similarFingerprint=loadSimilar(contentFingerprint, content, meetingBeginTime);//获取相似文章的指纹
                Request request=page.getRequest();
                Integer duplicate=(Integer)request.getExtra("duplicate");
                Crawler58ResultEntity entity=buildLogEntity(page, remoteFileName,similarFingerprint == null? null : similarFingerprint.getArticleId(), meetingBeginTime,duplicate);
                int id=crawler58ResultDao.insert(entity);
                Fingerprint thisFingerprint=new Fingerprint(id, contentFingerprint, meetingBeginTime);
                if(similarFingerprint==null && (duplicate==null || duplicate!=DuplicateType.NEED_DUPLICATE)){//不存在相似文章
                    redisRepository.addToFingerprintSet(thisFingerprint);
                }else{
                    LOGGER.error("has a similer article, this: "+JsonUtil.toJson(thisFingerprint)+"; similar: "+JsonUtil.toJson(similarFingerprint));
                }
                return ProStatus.success();
            } catch (Exception e) {
                LOGGER.error("failed to parse detail page, url: " + (String) page.getRequest().getExtra(CrawlerCommonConstants.PageFieldKeys.ORIGIN_URL), e);
            }
        }
        return ProStatus.fail(ProMessageCode.SUB_PROCESSOR_ERROR.getCode(), "落库下载的详细页失败");
    }

    private Crawler58ResultEntity buildLogEntity(Page page, String remoteFileUrl,Long similarArticleId, Date meetingBeginTime,Integer duplicate) {
    	Crawler58ResultEntity entity = new Crawler58ResultEntity();
        entity.setStatus(OriginalStatusCode.INITIAL);
        Map<String, Object> extraMap = Maps.newHashMap();
        entity.setContentUrl(remoteFileUrl);
        //from resultItems
        entity.setSource((String) page.getResultItems().get(CrawlerCommonConstants.PageFieldKeys.SOURCE));
        entity.setTitle((String) page.getResultItems().get(CrawlerCommonConstants.PageFieldKeys.TITLE));
        entity.setOriginalTime((Date) page.getResultItems().get(CrawlerCommonConstants.PageFieldKeys.ORIGINAL_TIME));
        entity.setDomainTag((String) ProcessorContext.getContext(page).getParam(CrawlerCommonConstants.ProcessorContextConstant.DOMAIN_TAG));
        List<String> serviceArea = page.getResultItems().get("serviceArea");
        entity.setServiceArea(JSON.toJSONString(serviceArea));
        String category = page.getResultItems().get("category");
        entity.setCategory(category);
        String childCategory = page.getResultItems().get("childCategory");
        entity.setChildCategory(childCategory);
        String phone = page.getResultItems().get("phone");
        entity.setPhone(phone);
        String contact = page.getResultItems().get("contact");
        entity.setContact(contact);
        String vipYear = page.getResultItems().get("vipYear");
        entity.setVipYear(Integer.parseInt(vipYear));
        String readCount = page.getResultItems().get("readCount");
        entity.setReadCount(Integer.parseInt(readCount));
        entity.setCityId(1);
        entity.setUrl((String) page.getRequest().getExtra(CrawlerCommonConstants.PageFieldKeys.ORIGIN_URL));
        if(entity.getUrl() == null){
        	entity.setUrl("");
        }
        entity.setCrawlTime((Date) page.getRequest().getExtra(CrawlerCommonConstants.PageFieldKeys.CRAWL_TIME));
        if(entity.getCrawlTime() == null){
        	entity.setCrawlTime(new Date());
        }
        entity.setThumbnail((String) page.getRequest().getExtra(CrawlerCommonConstants.PageFieldKeys.THUMBNAIL));
        if(similarArticleId != null && similarArticleId > 0){
        	entity.setStatus(OriginalStatusCode.REPEATED);
        	entity.setSimilarId(similarArticleId);
        }
        if (duplicate!=null && duplicate==DuplicateType.NEED_DUPLICATE ){
        	entity.setStatus(OriginalStatusCode.REPAT_CRAWL);
        }
        entity.setExtraMessage(JsonUtil.toJson(extraMap));
        return entity;
    }
    private Fingerprint loadSimilar(long contentFingerprint,String content, Date meetingTime){
        if (Lion.getBooleanValue("ssp-crawler-parser-service.article.similar.switch.off", false)) {
            return null;
        }
    	Set<Fingerprint> fingerprints=redisRepository.loadAllFingerprintSet();
    	List<Long> thisHashcodes=null;
    	int similer_simhash_threshold = Lion.getIntValue("ssp-crawler-parser-service.article.similer.simhash.threshold", 7);
    	double similer_percent = Lion.getDoubleValue("ssp-crawler-parser-service.article.similer.percent.threshold", 0.65);
    	for (Fingerprint fingerprint2 : fingerprints) {
            if (meetingTime != null && fingerprint2.getMeetingTime() != null && meetingTime.compareTo(fingerprint2.getMeetingTime()) != 0) {
                continue;
            }
    		if(similer_simhash_threshold >= FingerprintUtil.hammingDistance(contentFingerprint, fingerprint2.getContentFingerprint())){
    			if(thisHashcodes==null){
    				thisHashcodes=FingerprintUtil.createNgramHashcodeList(content);
    			}
    			String url=ARTICLE_CONTENT_URL.get(fingerprint2.getArticleId());
    			if(url==null){
    				Crawler58ResultEntity entity = crawler58ResultDao.findById(fingerprint2.getArticleId());
    				if(entity!=null && StringUtils.isNotBlank(entity.getContentUrl())){
    					url=entity.getContentUrl();
    					ARTICLE_CONTENT_URL.put(entity.getId(), url);
    				}
    			}
    			if(StringUtils.isBlank(url)){
    				continue;
    			}
				try {
					String content2 = SSPDownload.MssS3.downloadPlainFileContent(url);
					List<Long> thisHashcodes2=FingerprintUtil.createNgramHashcodeList(content2);
					double similerPercent=FingerprintUtil.listSimilerPercent(thisHashcodes, thisHashcodes2);
					if(similerPercent >= similer_percent){
						return fingerprint2;
					}
				} catch (IOException e) {
					LOGGER.error("failed to SSPDownload.MssS3.downloadPlainFileContent, url: " + url, e);
				}
    		}
		}
    	return null;
    }

}
