package com.xmzs.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xmzs.common.core.utils.MapstructUtils;
import com.xmzs.common.core.utils.StringUtils;
import com.xmzs.common.mybatis.core.page.PageQuery;
import com.xmzs.common.mybatis.core.page.TableDataInfo;
import com.xmzs.system.domain.SysModel;
import com.xmzs.system.domain.bo.SysModelBo;
import com.xmzs.system.domain.vo.SysModelVo;
import com.xmzs.system.mapper.SysModelMapper;
import com.xmzs.system.service.ISysModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 系统模型Service业务层处理
 *
 * @author Lion Li
 * @date 2024-04-04
 */
@RequiredArgsConstructor
@Service
public class SysModelServiceImpl implements ISysModelService {

    private final SysModelMapper baseMapper;

    /**
     * 查询系统模型
     */
    @Override
    public SysModelVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询系统模型列表
     */
    @Override
    public TableDataInfo<SysModelVo> queryPageList(SysModelBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SysModel> lqw = buildQueryWrapper(bo);
        Page<SysModelVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询系统模型列表
     */
    @Override
    public List<SysModelVo> queryList(SysModelBo bo) {
        LambdaQueryWrapper<SysModel> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<SysModel> buildQueryWrapper(SysModelBo bo) {
        LambdaQueryWrapper<SysModel> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getModelName()), SysModel::getModelName, bo.getModelName());
        lqw.like(StringUtils.isNotBlank(bo.getModelShow()), SysModel::getModelShow, bo.getModelShow());
        lqw.eq(StringUtils.isNotBlank(bo.getModelDescribe()), SysModel::getModelDescribe, bo.getModelDescribe());
        lqw.eq(StringUtils.isNotBlank(bo.getModelType()), SysModel::getModelType, bo.getModelType());
        return lqw;
    }

    /**
     * 新增系统模型
     */
    @Override
    public Boolean insertByBo(SysModelBo bo) {
        SysModel add = MapstructUtils.convert(bo, SysModel.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改系统模型
     */
    @Override
    public Boolean updateByBo(SysModelBo bo) {
        SysModel update = MapstructUtils.convert(bo, SysModel.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SysModel entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除系统模型
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
