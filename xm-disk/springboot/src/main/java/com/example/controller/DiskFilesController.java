package com.example.controller;

import com.example.common.Result;
import com.example.entity.DiskFiles;
import com.example.service.DiskFilesService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 网盘文件前端操作接口
 **/
@RestController
@RequestMapping("/diskFiles")
public class DiskFilesController {

    @Resource
    private DiskFilesService diskFilesService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(MultipartFile file, String name, String folder, Integer folderId) {
        diskFilesService.add(file, name, folder, folderId);
        return Result.success();
    }

    @GetMapping("/download/{flag}")
    public void download(@PathVariable String flag, HttpServletResponse response) {
        diskFilesService.download(flag, response);
    }

    /**
     * 移入回收站
     */
    @DeleteMapping("/trash/{id}")
    public Result trash(@PathVariable Integer id) {
        diskFilesService.trashById(id);
        return Result.success();
    }

    /**
     * 批量移入回收站
     */
    @DeleteMapping("/trash/batch")
    public Result trashBatch(@RequestBody List<Integer> ids) {
        diskFilesService.trashBatch(ids);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        diskFilesService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        diskFilesService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody DiskFiles diskFiles) {
        diskFilesService.updateById(diskFiles);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        DiskFiles diskFiles = diskFilesService.selectById(id);
        return Result.success(diskFiles);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(DiskFiles diskFiles ) {
        List<DiskFiles> list = diskFilesService.selectAll(diskFiles);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(DiskFiles diskFiles,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<DiskFiles> page = diskFilesService.selectPage(diskFiles, pageNum, pageSize);
        return Result.success(page);
    }
}