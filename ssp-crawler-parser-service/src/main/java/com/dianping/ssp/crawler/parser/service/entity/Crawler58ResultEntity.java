package com.dianping.ssp.crawler.parser.service.entity;

import java.util.Date;
import com.google.common.base.MoreObjects;

/**
 *
 * @author Mr.Bian
 *
 */
public class Crawler58ResultEntity {
	
	private long id;
	private String title;
	private String url;
	private String thumbnail;
	private String source;
	private String serviceArea;
	private String category;
	private String childCategory;
	private String phone;
	private String contact;
	private int vipYear;
	private int readCount;
	private int cityId;
	private Date originalTime;
	private Date crawlTime;
	private String contentUrl;
	private int status;
	private String extraMessage;
	private String domainTag;
	private long similarId;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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

	public Date getOriginalTime() {
		return originalTime;
	}

	public void setOriginalTime(Date originalTime) {
		this.originalTime = originalTime;
	}

	public Date getCrawlTime() {
		return crawlTime;
	}

	public void setCrawlTime(Date crawlTime) {
		this.crawlTime = crawlTime;
	}


	public String getServiceArea() {
		return serviceArea;
	}

	public void setServiceArea(String serviceArea) {
		this.serviceArea = serviceArea;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getChildCategory() {
		return childCategory;
	}

	public void setChildCategory(String childCategory) {
		this.childCategory = childCategory;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public int getVipYear() {
		return vipYear;
	}

	public void setVipYear(int vipYear) {
		this.vipYear = vipYear;
	}

	public String getContentUrl() {
		return contentUrl;
	}

	public void setContentUrl(String contentUrl) {
		this.contentUrl = contentUrl;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getExtraMessage() {
		return extraMessage;
	}

	public void setExtraMessage(String extraMessage) {
		this.extraMessage = extraMessage;
	}

	public String getDomainTag() {
		return domainTag;
	}

	public void setDomainTag(String domainTag) {
		this.domainTag = domainTag;
	}

	public long getSimilarId() {
		return similarId;
	}

	public void setSimilarId(long similarId) {
		this.similarId = similarId;
	}
	
	public int getReadCount() {
		return readCount;
	}

	public void setReadCount(int readCount) {
		this.readCount = readCount;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("id", id)
				.add("title", title).add("url", url)
				.add("thumbnail", thumbnail).add("source", source)
				.add("serviceArea", serviceArea).add("category", category)
				.add("childCategory", childCategory).add("phone", phone)
				.add("contact", contact).add("vipYear", vipYear)
				.add("originalTime", originalTime).add("crawlTime", crawlTime)
				.add("contentUrl", contentUrl).add("status", status)
				.add("extraMessage", extraMessage).add("domainTag", domainTag)
				.add("similarId", similarId).toString();
	}
	
	
}
