package io.springboard.framework.dubbo;

import org.apache.log4j.Logger;

import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;

public class DubboHystrixCommand extends HystrixCommand<Result> {
	private static Logger logger = Logger.getLogger(DubboHystrixCommand.class);
    private Invoker invoker;
    private Invocation invocation;
    
    public DubboHystrixCommand(Invoker invoker,Invocation invocation){
    	
    	super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(invoker.getInterface().getName()))
                    .andCommandKey(HystrixCommandKey.Factory.asKey(String.format("%s_%d", invocation.getMethodName(),
                                                                                 invocation.getArguments() == null ? 0 : invocation.getArguments().length)))
              .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
//                                            .withCircuitBreakerRequestVolumeThreshold(requestVolumeThreshold)
//                                            .withCircuitBreakerSleepWindowInMilliseconds(sleepWindowInMilliseconds)
//                                            .withCircuitBreakerErrorThresholdPercentage(errorThresholdPercentage)
                                            .withExecutionTimeoutEnabled(false)));//使用dubbo的超时，禁用这里的超时

        this.invoker=invoker;
        this.invocation=invocation;
    }

    @Override
    protected Result run() throws Exception {
        return invoker.invoke(invocation);
    }
}
