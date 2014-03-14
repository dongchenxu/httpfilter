package com.googlecode.httpfilter.dao.ibatis;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * Ibatis支撑类
 * @author vlinux
 *
 */
public abstract class AbstractSqlMapClientDaoSupport extends
		SqlMapClientDaoSupport {

	@Resource(name = "httpfilterSqlMapClient")
	private SqlMapClient sqlMapClient;

	/**
	  * 在方法上加上注解@PostConstruct，这个方法就会在Bean初始化之后被Spring容器执行
	  * （注：Bean初始化包括，实例化Bean，并 装配Bean的属性（依赖注入））。
	  * 它的一个典型的应用场景是，当你需要往Bean里注入一个其父类中定义的属性，
	  * 而你又无法复写父类的属性或属性的 setter方法时
	  */
	@PostConstruct
	public final void initSqlMapClient() {
		super.setSqlMapClient(sqlMapClient);
	}

}
