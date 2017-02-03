package com.dianping.ssp.crawler.parser.service.mq;

import com.dianping.ed.logger.EDLogger;
import com.dianping.ed.logger.LoggerManager;
import com.dianping.lion.Environment;
import com.dianping.lion.client.Lion;
import com.dianping.ssp.crawler.parser.service.log.CrawlerParserServiceLogEnum;
import com.meituan.mafka.client.MafkaClient;
import com.meituan.mafka.client.consumer.ConsumerConstants;
import com.meituan.mafka.client.producer.IProducerProcessor;
import com.meituan.mafka.client.producer.ProducerResult;
import com.meituan.mafka.client.producer.ProducerStatus;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Properties;


@Service
public class ArticlePublishProducer {

	private static final EDLogger LOGGER = LoggerManager.getLogger(CrawlerParserServiceLogEnum.ARTICLE_PUBLISH_PRODUCER_ERROR.getValue());
	private static IProducerProcessor produceFactory = null;
	private static final String topic = "article_publish_topic";

	@PostConstruct
	public void init() {
		try {
			Properties properties = new Properties();
			// 设置生产者appkey
			properties.setProperty(ConsumerConstants.MafkaClientAppkey, "ssp-crawler-parser-service");

			// 设置业务所在BG的namespace
			properties.setProperty(ConsumerConstants.MafkaBGNamespace, "daocan");
			if ("product".equalsIgnoreCase(Environment.getEnv())) {
				properties.setProperty("mafka.config.resource.file", "config/mq/producer_pro.properties");
			} else if ("qa".equalsIgnoreCase(Environment.getEnv())) {
				properties.setProperty("mafka.config.resource.file", "config/mq/producer_qa.properties");
			} else if ("prelease".equalsIgnoreCase(Environment.getEnv())) {
				properties.setProperty("mafka.config.resource.file", "config/mq/producer_ppe.properties");
			}
			produceFactory = MafkaClient.buildProduceFactory(properties, topic);
		} catch (Exception e) {
			throw new RuntimeException("init makfa producer factory exception.", e);
		}
	}

	/**
	 * 发送字符串消息
	 *
	 * @param msg
	 */
	public boolean sendMessage(String msg) {
		try {
			ProducerResult producerResult = produceFactory.sendMessage(msg);
			if (producerResult.getProducerStatus() != ProducerStatus.SEND_OK) {
				LOGGER.error(String.format("send_mq_failed ,result=[%s] , msg=[%s]", producerResult.getProducerStatus(), msg));
				return false;
			}
			return true;
		} catch (Exception e) {
			LOGGER.error("send article publish message error, msg : " + msg, e);
			return false;
		}
	}
}
