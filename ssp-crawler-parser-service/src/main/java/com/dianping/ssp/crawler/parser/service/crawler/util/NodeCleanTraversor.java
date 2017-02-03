package com.dianping.ssp.crawler.parser.service.crawler.util;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.parser.Tag;

import com.google.common.collect.Lists;

/**
 * Created by iClaod on 11/18/16.
 */
public class NodeCleanTraversor {
	private static List<String> TAG_CHANGE_TO_H2 = Lists.newArrayList("h1","h2", "h3", "h4", "h5");
	private static List<String> TAG_AS_TABLE = Lists.newArrayList("table","td", "th", "tr", "thead", "tbody", "tfoot");

	private Element targetBlock;
	private Element originRoot;
	
	public NodeCleanTraversor() {
		originRoot = new Document("");
		targetBlock = new Element(Tag.valueOf("p"), "");
		originRoot.appendChild(targetBlock);
	}
	
	public String getTransFormDom(String html){
		Element root=Jsoup.parse(html).body();
		transformDom(originRoot,root);
		CleanDomUtil.filterEmptyBlock(originRoot);
		return originRoot.outerHtml();
	}
	
	private boolean transformDom(Element targetRootNode,Node currentVisitNode) {
		if (currentVisitNode instanceof Element) {
			Element element = (Element) currentVisitNode;
			if ((element.tag().isBlock() || element.tagName().equals("br"))
					&& !"body".equals(element.tagName())) {
				if (TAG_AS_TABLE.contains(element.tagName())) {
					targetBlock = new Element(Tag.valueOf(element.tagName()),"");
					targetRootNode.appendChild(targetBlock);
					if (CollectionUtils.isNotEmpty(currentVisitNode.childNodes())) {
						Element oldTargetBlock=targetBlock;
						for (Node childNode : currentVisitNode.childNodes()) {
							Element newElementP = new Element(Tag.valueOf("p"),"");
							oldTargetBlock.appendChild(newElementP);
							targetBlock = newElementP;
							transformDom(oldTargetBlock,childNode);
						}
					}
					return true;
				} else if (TAG_CHANGE_TO_H2.contains(element.tagName())) {
					targetBlock = new Element(Tag.valueOf("h2"), "");
				} else if ("h6".equals(element.tagName())) {
					targetBlock = new Element(Tag.valueOf("h6"), "");
				} else if ("pre".equals(element.tagName())) {
					targetBlock = new Element(Tag.valueOf("pre"), "");
				} else {
					targetBlock = new Element(Tag.valueOf("p"), "");
				}
				targetRootNode.appendChild(targetBlock);
			} else if ("img".equals(element.tagName())) {
				// 对于独立元素img或者iframe, 单独处理
				String imgHref = element.attr("src");
				if (isHrefInImgTagValid(imgHref)) {
					Element newImgElement = new Element(Tag.valueOf("img"), "");
					newImgElement.attributes().put(new Attribute("src", imgHref));
					targetBlock.appendChild(newImgElement);
				}
				element.empty();
			} else if ("iframe".equals(element.tagName())) {
				String iframeHref = element.attr("src");
				if (isHrefInIframeTagValid(iframeHref)) {
					Element newIframeElement = new Element(Tag.valueOf("iframe"), "");
					newIframeElement.attributes().put(new Attribute("src", iframeHref));
					if (StringUtils.isNotBlank(element.attr("data-ratio"))) {
						newIframeElement.attributes().put(new Attribute("data-ratio", element.attr("data-ratio")));
					}
					targetBlock.appendChild(newIframeElement);
				}
				element.empty();
			} else if ("a".equals(element.tagName())) {
				String href = element.attr("href");
				if (isHrefInATagValid(href)) {
					// a标签直接复制href属性, 并且把元素内所有text复制到标签内, 子标签无脑丢弃
					Attribute attrHref = new Attribute("href", href);
					Attribute attrRel = new Attribute("rel", "nofollow");
					Attributes attributes = new Attributes();
					attributes.put(attrHref);
					attributes.put(attrRel);
					Element linkElement = new Element(Tag.valueOf("a"), "", attributes);
					linkElement.text(element.text());
					targetBlock.appendChild(linkElement);
				} else {
					// 如果链接不合法, 过滤链接, 只保留文字
					TextNode textNode = new TextNode(element.text(), "");
					targetBlock.appendChild(textNode);
				}
				element.empty();
			} else {
				// ignore
			}
			if (CollectionUtils.isNotEmpty(currentVisitNode.childNodes())) {
				for (Node childNode : currentVisitNode.childNodes()) {
					transformDom(targetRootNode,childNode);
				}
			}
		} else if (currentVisitNode instanceof TextNode) {
			targetBlock.appendChild(currentVisitNode.clone());
		}
		return true;
	}

	private static boolean isHrefInATagValid(String href) {
		return href != null && href.matches("[h|H][t|T][t|T][p|P].*");
	}

	private static boolean isHrefInImgTagValid(String href) {
		return href != null && href.matches("[h|H][t|T][t|T][p|P].*");
	}

	private static boolean isHrefInIframeTagValid(String href) {
		return href != null && href.matches("http[s]?://v.qq.com/.*");
	}

}
