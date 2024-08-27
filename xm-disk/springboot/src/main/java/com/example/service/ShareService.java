package com.example.service;

import com.example.entity.Share;
import com.example.mapper.ShareMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 文件分享信息表业务处理
 **/
@Service
public class ShareService {

    @Resource
    private ShareMapper shareMapper;

    /**
     * 新增
     */
    public void add(Share share) {
        shareMapper.insert(share);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        shareMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            shareMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Share share) {
        shareMapper.updateById(share);
    }

    /**
     * 根据ID查询
     */
    public Share selectById(Integer id) {
        return shareMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Share> selectAll(Share share) {
        return shareMapper.selectAll(share);
    }

    /**
     * 分页查询
     */
    public PageInfo<Share> selectPage(Share share, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Share> list = shareMapper.selectAll(share);
        return PageInfo.of(list);
    }

}