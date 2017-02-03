package com.dianping.ssp.crawler.parser.test;

import com.dianping.ssp.crawler.common.spider.SpiderFactory;
import com.dianping.ssp.crawler.common.util.DomainTagUtils;
import com.dianping.ssp.crawler.parser.base.AbstractTest;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by iClaod on 10/12/16.
 */
public class ServiceTest  {
    public void test() {
        try{
            while(true){
                List<String> allDomainTags = DomainTagUtils.getAllDomainTags();
                if (CollectionUtils.isNotEmpty(allDomainTags)) {
                    for (String domainTag : allDomainTags) {
                        SpiderFactory.initSpider(domainTag);
                    }
                }
                Thread.sleep(20000000);
            }
        }catch(Exception e){

        }
    }

    public static void main(String args[]){
      System.out.println(Pattern.matches("^/w*(PlayTourSubmit)/w*$", "PlayTourSubmit"));
    }

}