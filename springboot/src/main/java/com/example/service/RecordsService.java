package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Help;
import com.example.entity.Records;
import com.example.entity.Volunteer;
import com.example.mapper.RecordsMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 出警信息业务处理
 **/
@Service
public class RecordsService {

    @Resource
    private RecordsMapper recordsMapper;
    @Resource
    private VolunteerService volunteerService;
    @Resource
    HelpService helpService;

    /**
     * 新增
     */
    @Transactional
    public void add(Records records) {
//        records.setTime(DateUtil.now());  // 出警时间
        records.setStatus("待救援");
        recordsMapper.insert(records);

        // 更新救助员的状态
        Volunteer volunteer = volunteerService.selectById(records.getVolId());
        volunteer.setFree("否");
        volunteerService.updateById(volunteer);
        // 更新救助的信息
        Help help = helpService.selectById(records.getHelpId());
        help.setStatus("进行中");
        help.setRecordId(records.getId());
        helpService.updateById(help);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        recordsMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            recordsMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Records records) {
        if ("救援中".equals(records.getStatus())) {
            records.setTime(DateUtil.now());
        }
        if ("已救援".equals(records.getStatus())) {
            Integer volId = records.getVolId();
            Volunteer volunteer = volunteerService.selectById(volId);
            volunteer.setFree("是");
            volunteerService.updateById(volunteer);
            // 更新救助状态

            helpService.updateStatus("已解决", records.getHelpId());
        }
        recordsMapper.updateById(records);
    }

    /**
     * 根据ID查询
     */
    public Records selectById(Integer id) {
        return recordsMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Records> selectAll(Records records) {
        return recordsMapper.selectAll(records);
    }

    /**
     * 分页查询
     */
    public PageInfo<Records> selectPage(Records records, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.VOLUNTEER.name().equals(currentUser.getRole())) {
            records.setVolId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Records> list = recordsMapper.selectAll(records);
        return PageInfo.of(list);
    }

}