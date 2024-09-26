package com.xmzs.system.sandbox.bo;

import lombok.Data;

import java.util.List;

/**
 * @Description
 * @Author lkp
 * @Date 2024/7/11 18:15
 */
@Data
public class SnekboxResult {

    private String stdout;

    private int returncode;

    private List<SnekboxFile> files;


}
