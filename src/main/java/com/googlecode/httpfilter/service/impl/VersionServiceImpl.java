package com.googlecode.httpfilter.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.googlecode.httpfilter.constant.ErrorCodeConstants;
import com.googlecode.httpfilter.domain.MultiResultDO;
import com.googlecode.httpfilter.domain.SingleResultDO;
import com.googlecode.httpfilter.domain.VersionDO;
import com.googlecode.httpfilter.manager.BizException;
import com.googlecode.httpfilter.manager.impl.VersionManagerImpl;
import com.googlecode.httpfilter.service.VersionService;

@Service
public class VersionServiceImpl implements VersionService {

	private static final Logger logger = LoggerFactory.getLogger("httpfilter-biz");
	
	@Autowired
	VersionManagerImpl versionManager;
	@Override
	public SingleResultDO<VersionDO> createVersionDO(VersionDO version) {
		SingleResultDO<VersionDO> result = new SingleResultDO<VersionDO>();
		try {
			result.setModel(versionManager.createVersionDO(version));
		} catch (BizException e) {
			result.setSuccess(false);
			logger.warn("VersionDO create version failed.", e);
		}
		return result;
	}

	@Override
	public SingleResultDO<VersionDO> getVersionDOById(long id) {
		SingleResultDO<VersionDO> result = new SingleResultDO<VersionDO>();
		
		try {
			result.setModel(versionManager.getVersionDO(id));
		} catch (BizException e) {
			result.setSuccess(false);
			logger.warn("VersionDO create version failed.", e);
		}
		return null;
	}

	@Override
	public MultiResultDO<Long, VersionDO> getVersionDOByIds(List<Long> ids) {
		final MultiResultDO<Long, VersionDO> result = new MultiResultDO<Long, VersionDO>();
		boolean isSuccess = false;
		for( long id : ids ){
			try {
				result.getModels().put(id, versionManager.getVersionDO(id));
				isSuccess = true;
			} catch (BizException e) {
				logger.warn("VersionDO get version failed. id = " + id, e);
				result.putError(id, ErrorCodeConstants.GET_VERSION_BY_ID_ERROR);
			}
		}
		result.setSuccess(isSuccess);
		return result;
	}

}
