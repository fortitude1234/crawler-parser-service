package com.dianping.ssp.crawler.parser.service.crawler.subprocessor;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dianping.ed.logger.EDLogger;
import com.dianping.ed.logger.LoggerManager;
import com.dianping.ssp.crawler.parser.service.log.CrawlerParserServiceLogEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import us.codecraft.webmagic.Page;

import com.dianping.ssp.article.dto.CityBaseDto;
import com.dianping.ssp.article.dto.ProvinceBaseDto;
import com.dianping.ssp.article.service.ProvinceRedisBaseService;
import com.dianping.ssp.crawler.common.contants.CrawlerCommonConstants;
import com.dianping.ssp.crawler.common.entity.ProStatus;
import com.dianping.ssp.crawler.common.pageprocessor.subprocess.CrawlerSubProcessTag;
import com.dianping.ssp.crawler.common.pageprocessor.subprocess.ICrawlerSubProcess;
import com.google.common.collect.Maps;

/**
 *
 * @author Mr.Bian
 *
 */
@CrawlerSubProcessTag(name = "cityId")
public class CityIdSubProcessor implements ICrawlerSubProcess{

	private static final EDLogger LOGGER = LoggerManager.getLogger(CrawlerParserServiceLogEnum.MEETING_CITYID_PROCESSOR.getValue());

	@Autowired
	private ProvinceRedisBaseService provinceRedisBaseService;
	
	@Override
	public ProStatus process(Page page) {
		try {
			String cityName = page.getResultItems().get(CrawlerCommonConstants.PageFieldKeys.CITY_NAME);
			if (!StringUtils.isEmpty(cityName)) {
				Integer cityId = getCityId(cityName);
				if (cityId != null) {
					page.getResultItems().put(CrawlerCommonConstants.PageFieldKeys.CITY_ID, cityId);
				}
			}
		} catch (Exception e) {
			LOGGER.error("failed to parse cityId: " + page.getResultItems().get(CrawlerCommonConstants.PageFieldKeys.CITY_NAME), e);
		}
		return ProStatus.success();
	}
	
	private Integer getCityId(String cityName){
		Map<String,Integer> cityMap=Maps.newHashMap();
		List<ProvinceBaseDto> provinceList=provinceRedisBaseService.queryProvinceInfos();
		for (ProvinceBaseDto province:provinceList){
			List<CityBaseDto> cityList=province.getCitys();
			if (CollectionUtils.isEmpty(cityList)){
				continue;
			}
			for (CityBaseDto city:cityList){
				cityMap.put(city.getCityName(), city.getCityId());
			}
		}
		if (StringUtils.isEmpty(cityName)){
			return 0;
		}
		Set<String> cityNameSet=cityMap.keySet();
		for(String targetCityName:cityNameSet){
			if (cityName.contains(targetCityName)){
				return cityMap.get(targetCityName);
			}
		}
		return 0;
	}

}
