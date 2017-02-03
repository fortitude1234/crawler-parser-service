package com.dianping.ssp.crawler.parser.service.dao;

import com.dianping.avatar.dao.GenericDao;
import com.dianping.avatar.dao.annotation.DAOAction;
import com.dianping.avatar.dao.annotation.DAOActionType;
import com.dianping.avatar.dao.annotation.DAOParam;
import com.dianping.ssp.crawler.parser.service.entity.ArticleOriginEntity;

public interface ArticleOriginDao extends GenericDao {

	@DAOAction(action = DAOActionType.INSERT)
	public int addNewArticle(@DAOParam("entity") ArticleOriginEntity entity);
	@DAOAction(action = DAOActionType.LOAD)
	ArticleOriginEntity loadById(@DAOParam("id") long id);
	@DAOAction(action = DAOActionType.UPDATE)
	public int updateStatus(@DAOParam("id") long id, @DAOParam("status") int status);
}
