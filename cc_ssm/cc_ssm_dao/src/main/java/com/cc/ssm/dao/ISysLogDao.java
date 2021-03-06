package com.cc.ssm.dao;

import com.cc.ssm.domain.SysLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ISysLogDao {

    /**
     * 保存日志
     * @param sysLog
     * @throws Exception
     */
    @Insert("insert into syslog(visitTime,username,ip,url,executionTime,method) values(#{visitTime},#{username},#{ip},#{url},#{executionTime},#{method})")
    public void save(SysLog sysLog) throws Exception;

    /**
     * 查询所有日志
     * @return
     * @throws Exception
     */
    @Select("select * from sysLog")
    List<SysLog> findAll() throws Exception;
}
