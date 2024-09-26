package com.xmzs.system.domain.bo;

import com.xmzs.common.core.validate.AddGroup;
import com.xmzs.common.core.validate.EditGroup;
import com.xmzs.common.mybatis.core.domain.BaseEntity;
import com.xmzs.system.domain.SysModel;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统模型业务对象 sys_model
 *
 * @author Lion Li
 * @date 2024-04-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysModel.class, reverseConvertGenerate = false)
public class SysModelBo extends BaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 模型名称
     */
    @NotBlank(message = "模型名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String modelName;


    /**
     * 模型描述
     */
    @NotBlank(message = "模型描述不能为空", groups = { AddGroup.class, EditGroup.class })
    private String modelDescribe;

    /**
     * 模型价格
     */
    @NotNull(message = "模型价格不能为空", groups = { AddGroup.class, EditGroup.class })
    private double modelPrice;

    /**
     * 计费类型 (1 token扣费; 2 次数扣费 )
     */
    @NotBlank(message = "计费类型不能为空", groups = { AddGroup.class, EditGroup.class })
    private String modelType;

    /**
     * 模型状态  (0 显示; 1 隐藏 )
     */
    private String modelShow;


    /**
     * 系统提示词
     */
    private String systemPrompt;

    /**
     * 备注
     */
    @NotBlank(message = "备注不能为空", groups = { AddGroup.class, EditGroup.class })
    private String remark;


}
