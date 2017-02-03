package com.dianping.ssp.crawler.parser.service.mq.entity;

import java.util.Date;

public class ArticlePublishMessage {

	private long origin_article_id;
	private int operateType;
	private String url;
	private String title;
	private String thumbnail;
	private String source;
	private String author;
	private Date original_time;
	private Date crawl_time;
	private String contentUrl;
	private String domain_tag;
	
	
	public long getOrigin_article_id() {
		return origin_article_id;
	}
	public void setOrigin_article_id(long origin_article_id) {
		this.origin_article_id = origin_article_id;
	}
	public int getOperateType() {
		return operateType;
	}
	public void setOperateType(int operateType) {
		this.operateType = operateType;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public Date getOriginal_time() {
		return original_time;
	}
	public void setOriginal_time(Date original_time) {
		this.original_time = original_time;
	}
	public Date getCrawl_time() {
		return crawl_time;
	}
	public void setCrawl_time(Date crawl_time) {
		this.crawl_time = crawl_time;
	}
	public String getContentUrl() {
		return contentUrl;
	}
	public void setContentUrl(String contentUrl) {
		this.contentUrl = contentUrl;
	}
	public String getDomain_tag() {
		return domain_tag;
	}
	public void setDomain_tag(String domain_tag) {
		this.domain_tag = domain_tag;
	}
	
}
