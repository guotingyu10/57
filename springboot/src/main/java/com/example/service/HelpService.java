package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Help;
import com.example.entity.Records;
import com.example.mapper.HelpMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 救援信息业务处理
 **/
@Service
public class HelpService {

    @Resource
    private HelpMapper helpMapper;
    @Resource
    RecordsService recordsService;

    /**
     * 新增
     */
    public void add(Help help) {
        Account currentUser = TokenUtils.getCurrentUser();
        help.setUserId(currentUser.getId());
        help.setTime(DateUtil.now());
        help.setStatus("待处理");
        helpMapper.insert(help);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        helpMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            helpMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Help help) {
        helpMapper.updateById(help);
    }

    /**
     * 根据ID查询
     */
    public Help selectById(Integer id) {
        return helpMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Help> selectAll(Help help) {
        return helpMapper.selectAll(help);
    }

    /**
     * 分页查询
     */
    public PageInfo<Help> selectPage(Help help, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            help.setUserId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Help> list = helpMapper.selectAll(help);
        for (Help h : list) {
            Records records = recordsService.selectById(h.getRecordId());
            h.setRecords(records);
        }
        return PageInfo.of(list);
    }

    public void updateStatus(String status, Integer id) {
        helpMapper.updateStatus(status, id);
    }
}