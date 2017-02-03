package com.dianping.ssp.crawler.parser.service.crawler.converter;

import com.dianping.avatar.log.AvatarLogger;
import com.dianping.avatar.log.AvatarLoggerFactory;
import com.dianping.combiz.util.DateUtils;
import com.dianping.ssp.crawler.common.pageprocessor.fieldhandler.converter.CrawlerConverterTag;
import com.dianping.ssp.crawler.common.pageprocessor.fieldhandler.converter.ICrawlerConverter;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@CrawlerConverterTag(name = "hdbDateConverter")
public class HdbDateCrawlerConverterImpl implements ICrawlerConverter {
    private static final AvatarLogger LOGGER = AvatarLoggerFactory.getLogger(HdbDateCrawlerConverterImpl.class);

    private static final String DEFAULT_DATEFORMAT = "yyyy-MM-dd HH:mm";

    @Override
    public Object converter(Object sourceData, Object params) {
        String dateFormat = DEFAULT_DATEFORMAT;
        if (null != params) {
            dateFormat = (String) params;
        }
        DateFormat dateFormater = new SimpleDateFormat(dateFormat);
        if (null != sourceData) {
            String dateStr = (String) sourceData;
            if (StringUtils.isNotBlank(dateStr)) {
                try {
                    Date date = dateFormater.parse(Calendar.getInstance().get(Calendar.YEAR) + "-" + sourceData);
                    return date;
                } catch (Exception e) {
                    try {
                        LOGGER.error("parse date error", e);
                        Date date = dateFormater.parse((String) sourceData);
                        return date;
                    } catch (Exception e1) {
                        LOGGER.error("parse date error", e1);
                    }
                } finally {
                    LOGGER.info("to parse dateStr:" + dateStr);
                }
            }
        }
        return null;
    }

}
