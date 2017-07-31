package com.molbase.dts.server.dto;

import com.molbase.dts.server.cache.CacheObjMetaData.ObjectMetaType;

/**
 * Tuple
 * @author changgen.xu
 */
public class TupleObjectMetaType {

	private ObjectMetaType metaType1;
	private ObjectMetaType metaType2;
	public TupleObjectMetaType(ObjectMetaType type1, ObjectMetaType type2){
		this.metaType1 = type1;
		this.metaType2 = type2;
	}
	public ObjectMetaType getMetaType1() {
		return metaType1;
	}
	public ObjectMetaType getMetaType2() {
		return metaType2;
	}
	
}
