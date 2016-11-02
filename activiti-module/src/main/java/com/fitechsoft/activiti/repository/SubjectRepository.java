package com.fitechsoft.activiti.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fitechsoft.activiti.domain.FDObject;

/**
 * Created by zhangsuyun on 2016/9/19.
 */
public interface SubjectRepository<O extends FDObject> extends JpaRepository<O, Long> {

    O findById(String id);
}
