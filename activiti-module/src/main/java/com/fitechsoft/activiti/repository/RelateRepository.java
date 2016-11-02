package com.fitechsoft.activiti.repository;

import java.util.Collection;

import com.fitechsoft.activiti.domain.RelationActiviti;

public interface RelateRepository<R extends RelationActiviti> extends SubjectRepository<R> {
	
	
	public Collection<RelationActiviti> findByProcInstId(String procInstId);
}
