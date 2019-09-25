package com.didiglobal.pressir.thrift.generator;

import com.didiglobal.pressir.thrift.base.ServiceClientInvocation;
import org.apache.thrift.TServiceClient;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName InvariantTaskGenerator
 * @Description 固参任务生成器
 * @Author pressir
 * @Date 2019-09-03 09:49
 */
public class InvariantTaskGenerator<T extends TServiceClient> implements Generator {

    private final ServiceClientInvocation<T> invocation;
    private final Method method;
    private final Object[] args;

    public InvariantTaskGenerator(ServiceClientInvocation<T> invocation, Method method, Object[] args) {
        this.invocation = invocation;
        this.method = method;
        this.args = args;
    }

    @Override
    public List<Runnable> generate(int num) {
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            tasks.add(this::execute);
        }
        return tasks;
    }

    private void execute() {
        this.invocation.invoke(this.method, this.args);
    }
}
