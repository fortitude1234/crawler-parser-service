package com.dianping.ssp.crawler.parser.service.crawler.util;

import org.apache.commons.collections.MapUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by iClaod on 12/14/16.
 */
public class UrlParamUtil {

    public static String buildUrl(String urlPath, Map<String, String> params) {
        if (MapUtils.isEmpty(params)) {
            return urlPath;
        }
        StringBuilder sb = new StringBuilder(urlPath);
        boolean firstParam = true;
        for (String key: params.keySet()) {
            if (firstParam) {
                sb.append("?");
            } else {
                sb.append("&");
            }
            sb.append(key).append("=").append(params.get(key) == null? "": params.get(key));
            firstParam = false;
        }
        return sb.toString();
    }

    /**
     * 去掉url中的路径，留下请求参数部分
     * @param url url地址
     * @return url请求参数部分
     */
    public static Map<String, String> extractParamFromUrl(String url) {
        Map<String, String> mapRequest = new HashMap<String, String>();

        String[] arrSplit=null;

        String strUrlParam= truncateUrlPage(url);
        if(strUrlParam==null)
        {
            return mapRequest;
        }
        //每个键值为一组
        arrSplit=strUrlParam.split("[&]");
        for(String strSplit:arrSplit)
        {
            String[] arrSplitEqual=null;
            arrSplitEqual= strSplit.split("[=]");

            //解析出键值
            if(arrSplitEqual.length>1)
            {
                //正确解析
                mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);

            }
            else
            {
                if(arrSplitEqual[0]!="")
                {
                    //只有参数没有值，不加入
                    mapRequest.put(arrSplitEqual[0], "");
                }
            }
        }
        return mapRequest;
    }

    /**
     * 去掉url中的路径，留下请求参数部分
     * @param strURL url地址
     * @return url请求参数部分
     */
    private static String truncateUrlPage(String strURL) {
        String strAllParam=null;
        String[] arrSplit=null;

        strURL=strURL.trim().toLowerCase();

        arrSplit=strURL.split("[?]");
        if(strURL.length()>1)
        {
            if(arrSplit.length>1)
            {
                if(arrSplit[1]!=null)
                {
                    strAllParam=arrSplit[1];
                }
            }
        }

        return strAllParam;
    }

    /**
     * 解析出url请求的路径，包括页面
     * @param url url地址
     * @return url路径
     */
    public static String extractUrlPath(String url) {
        String strPage=null;
        String[] arrSplit=null;

        url = url.trim().toLowerCase();

        arrSplit= url.split("[?]");
        if(url.length()>0)
        {
            if(arrSplit.length>1)
            {
                if(arrSplit[0]!=null)
                {
                    strPage=arrSplit[0];
                }
            }
        }

        return strPage;
    }
}
