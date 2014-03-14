package com.googlecode.httpfilter.domain;

import java.util.HashMap;
import java.util.Map;

import com.googlecode.httpfilter.util.FeaturesUtils;

/**
 * Features支撑类
 * @author vlinux
 *
 */
public class FeaturesSupportDO extends BaseDO {

	private static final long serialVersionUID = -1784291210413090815L;

	private final Map<String,String> featuresMap = new HashMap<String,String>();

	/**
	 * 获取features-map
	 * @return
	 */
	public Map<String,String> getFeaturesMap() {
		return featuresMap;
	}
	
	public String getFeatures() {
		return FeaturesUtils.toString(featuresMap);
	}

	public void setFeatures(String features) {
		synchronized (this) {
			featuresMap.clear();
			featuresMap.putAll(FeaturesUtils.toMap(features));
		}
	}
	
}
