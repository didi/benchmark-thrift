package com.didiglobal.pressir.thrift.generator;

import java.util.List;

/**
 * @InterfaceName Generator
 * @Description 任务生成器
 * @Author pressir
 * @Date 2019-09-03 09:50
 */
public interface Generator {
    /**
     * 根据输入的数字生成相应数量的Runnable
     * @param num a num of tasks
     * @return a runnable list
     */
    List<Runnable> generate(int num);
}
