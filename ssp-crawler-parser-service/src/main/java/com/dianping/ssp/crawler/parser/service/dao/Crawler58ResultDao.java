package com.dianping.ssp.crawler.parser.service.dao;

import com.dianping.avatar.dao.GenericDao;
import com.dianping.avatar.dao.annotation.DAOAction;
import com.dianping.avatar.dao.annotation.DAOActionType;
import com.dianping.avatar.dao.annotation.DAOParam;
import com.dianping.ssp.crawler.parser.service.entity.Crawler58ResultEntity;

/**
 *
 * @author Mr.Bian
 *
 */
public interface Crawler58ResultDao extends GenericDao {
	
	@DAOAction(action = DAOActionType.INSERT)
	public int insert(@DAOParam("entity") Crawler58ResultEntity entity);
	@DAOAction(action = DAOActionType.LOAD)
	public Crawler58ResultEntity findById(@DAOParam("id") long id);
}
