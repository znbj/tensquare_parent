package com.tensquare.qa.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.tensquare.qa.pojo.Problem;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 数据访问接口
 * @author Administrator
 *
 */
public interface ProblemDao extends JpaRepository<Problem,String>,JpaSpecificationExecutor<Problem>{

    //最新回答列表
    @Query("select p from Problem p WHERE p.id IN (SELECT problemid FROM PL WHERE labelid=?1) ORDER BY p.replytime DESC")
    public Page<Problem> findAllProblem(String id,Pageable pageable);

    //根据标签ID查询热门问题列表
    @Query("select p from  Problem p WHERE  p.id  IN (SELECT problemid FROM PL WHERE labelid=?1) ORDER BY p.reply DESC")
    public Page<Problem> findHotListByLabelId(String id,Pageable pageable);

    //根据标签ID查询等待回答列表
    @Query("select p from  Problem p WHERE  p.id  IN (SELECT problemid FROM PL WHERE labelid=?1) and p.reply=0 ORDER BY p.createtime DESC")
    public Page<Problem> findWaitListByLabelId(String id, Pageable pageable);
}
