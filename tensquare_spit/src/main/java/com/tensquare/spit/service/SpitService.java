package com.tensquare.spit.service;

import com.tensquare.spit.dao.SpitDao;
import com.tensquare.spit.pojo.Spit;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import util.IdWorker;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SpitService {
    @Autowired
    private SpitDao spitDao;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private IdWorker idWorker;

    /**
     * 发布吐槽的业务逻辑
     * @param spit
     */
    public void addSpit(Spit spit) {

        //生成id
        String id = idWorker.nextId() + "";
        //设置默认值
        spit.set_id(id);
        //发布时间
        spit.setPublishtime(new Date());
        //访问次数
        spit.setVisits(0);
        //点赞数
        spit.setThumbup(0);
        //分享数
        spit.setShare(0);
        //回复数
        spit.setComment(0);
        //状态
        spit.setState("1");
        //判断是否是回复数据
        String parentid = spit.getParentid();
        if (parentid != null && !"".equals(parentid)) {
            //父吐槽的回复数加一
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(parentid));
            Update update = new Update();
            update.inc("comment", 1);
            mongoTemplate.updateFirst(query, update, "spit");
        }
        //写入mongodb
        spitDao.save(spit);

    }

    public void updateSpit(Spit spit) {
        spitDao.save(spit);
    }

    public void deleteSpit(String spitId) {
        spitDao.deleteById(spitId);
    }

    public List<Spit> findAll() {
        List<Spit> list = spitDao.findAll();
        return list;
    }

    public Spit getSpitById(String spitId) {
        Optional<Spit> optional = spitDao.findById(spitId);
        Spit spit = optional.get();
        return spit;
    }

    /**
     * 根据parentId查询吐槽列表
     */
    public PageResult getSpitListByParentId(String parentId, int page, int size) {
        //根据parentId查询吐槽列表
        Page<Spit> result = spitDao.findByParentid(parentId, PageRequest.of(page - 1, size));
        long totalElements = result.getTotalElements();
        List<Spit> list = result.getContent();
        return new PageResult(totalElements, list);
    }

    /**
     * 吐槽点赞
     * @param spitId
     */
    public void thumbUp(String spitId) {
        /*//根据id查询吐槽数据
        Optional<Spit> optional = spitDao.findById(spitId);
        Spit spit = optional.get();
        //更新点赞数
        spit.setThumbup(spit.getThumbup() + 1);
        //保存到数据库
        spitDao.save(spit);*/

        //直接在thumbup字段上加一即可
        //创建Query
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(spitId));
        //创建Update
        Update update = new Update();
        update.inc("thumbup", 1);
        //参数1：查询条件
        //参数2：更新的数据，更新条件
        //参数3：操作的集合名称
        mongoTemplate.updateFirst(query, update, "spit");
    }
}
