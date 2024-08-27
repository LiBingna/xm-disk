package com.example.service;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.example.common.Constants;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.DiskFiles;
import com.example.entity.Trash;
import com.example.mapper.DiskFilesMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.util.List;

/**
 * 网盘文件信息表业务处理
 **/
@Service
public class DiskFilesService {

    private static final String filePath = System.getProperty("user.dir") + "/disk/";

    private static final Logger log = LoggerFactory.getLogger(DiskFilesService.class);

    @Value("${server.port:9090}")
    private String port;

    @Value("${ip:localhost}")
    private String ip;

    @Resource
    private DiskFilesMapper diskFilesMapper;

    @Resource
    private TrashService trashService;
    /**
     * 新增
     */
    public void add(MultipartFile file, String name, String folder, Integer folderId) {
        // 参数传  文件夹的名称 是否是文件夹  文件夹的ID
        DiskFiles diskFiles = new DiskFiles();
        String now = DateUtil.now();
        diskFiles.setCrateTime(now);
        diskFiles.setUpdateTime(now);
        diskFiles.setFolder(folder);  // 是否是目录
        diskFiles.setName(name);
        diskFiles.setType(Constants.FILE_TYPE_FOLDER);  // 默认是文件夹  后面如果是文件的话 会覆盖这个值
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            diskFiles.setUserId(currentUser.getId());
        }
        diskFiles.setFolderId(folderId); // 当前文件/文件夹 外传的目录ID
        if (Constants.NOT_FOLDER.equals(folder)) {   // 文件操作
            String originalFilename = file.getOriginalFilename();
            diskFiles.setName(originalFilename);
            String extName = FileUtil.extName(originalFilename);   // 获取文件的后缀
            diskFiles.setType(extName);
            long flag = System.currentTimeMillis();
            String fileName = flag + "-" + originalFilename;  // 文件存储在磁盘的文件名
            if (!FileUtil.exist(filePath)) {
                FileUtil.mkdir(filePath);
            }
            try {
                byte[] bytes = file.getBytes();  // byte
                double size = BigDecimal.valueOf(bytes.length).divide(BigDecimal.valueOf(1024), 3, RoundingMode.HALF_UP).doubleValue();
                diskFiles.setSize(size);
                // 文件上传
                file.transferTo(new File(filePath + fileName));
            } catch (Exception e) {
                log.error("网盘文件上传错误", e);
            }
            String url = "http://" + ip + ":" + port + "/diskFiles/download/" + fileName;
            diskFiles.setFile(url);
        }
        diskFilesMapper.insert(diskFiles);


        if (folderId != null) {  // 外层有目录
            DiskFiles parentFolder = this.selectById(folderId);  // 获取到外层的目录
            Integer rootFolderId = parentFolder.getRootFolderId();
            diskFiles.setRootFolderId(rootFolderId);
        } else {
            if (Constants.IS_FOLDER.equals(folder)) {  // 当前是新建目录操作
                Integer diskFilesId = diskFiles.getId();  // 刚才插入到数据库的文件的ID
                diskFiles.setRootFolderId(diskFilesId);
            }
        }
        this.updateById(diskFiles);  // 更新 root_folder_id 字段的值
    }

    /**
     * 网盘文件下载
     */
    public void download(String flag, HttpServletResponse response) {
        OutputStream os;
        try {
            if (StrUtil.isNotEmpty(flag)) {
                response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(flag, "UTF-8"));
                response.setContentType("application/octet-stream");
                byte[] bytes = FileUtil.readBytes(filePath + flag);
                os = response.getOutputStream();
                os.write(bytes);
                os.flush();
                os.close();
            }
        } catch (Exception e) {
            System.out.println("文件下载失败");
        }
    }

    /**
     * 移入垃圾箱
     */
    @Transactional
    public void trashById(Integer id) {
        DiskFiles diskFiles = this.selectById(id);
        diskFilesMapper.trashById(id);
        //移入垃圾箱
        Trash trash = new Trash();
        trash.setName(diskFiles.getName());
        trash.setTime(DateUtil.now());
        trash.setFileId(id);
        trash.setUserId(diskFiles.getUserId());
        trashService.add(trash);
    }

    /**
     * 批量移入垃圾箱
     */
    public void trashBatch(List<Integer> ids) {
        for(Integer id : ids){
            this.trashById(id);
        }
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        diskFilesMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            diskFilesMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(DiskFiles diskFiles) {
        diskFilesMapper.updateById(diskFiles);
    }

    /**
     * 根据ID查询
     */
    public DiskFiles selectById(Integer id) {
        return diskFilesMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<DiskFiles> selectAll(DiskFiles diskFiles) {
        return diskFilesMapper.selectAll(diskFiles);
    }

    /**
     * 分页查询
     */
    public PageInfo<DiskFiles> selectPage(DiskFiles diskFiles, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<DiskFiles> list = diskFilesMapper.selectAll(diskFiles);
        return PageInfo.of(list);
    }

}