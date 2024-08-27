package com.example.mapper;

import com.example.entity.DiskFiles;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 操作diskFiles相关数据接口
 */
public interface DiskFilesMapper {

    /**
     * 新增
     */
    int insert(DiskFiles diskFiles);

    /**
     * 删除
     */
    int deleteById(Integer id);

    /**
     * 修改
     */
    int updateById(DiskFiles diskFiles);

    /**
     * 根据ID查询
     */
    DiskFiles selectById(Integer id);

    /**
     * 查询所有
     */
    List<DiskFiles> selectAll(DiskFiles diskFiles);

    @Update("update disk_files set  `delete` = 1 where id = #{id}")
    void trashById(Integer id);
}