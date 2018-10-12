package org.aion.zero.impl.vm;

import org.aion.base.db.IRepositoryCache;
import org.aion.fastvm.FastVM;
import org.aion.precompiled.ContractFactory;
import org.aion.vm.ExecutionContext;
import org.aion.vm.ExecutorProvider;
import org.aion.vm.IPrecompiledContract;
import org.aion.vm.VirtualMachine;

public class AionExecutorProvider implements ExecutorProvider {

    private AionExecutorProvider() {
    }

    // holder
    public static AionExecutorProvider getInstance() {
        return Holder.INST;
    }

    @Override
    public IPrecompiledContract getPrecompiledContract(ExecutionContext context,
        IRepositoryCache track) {
        return new ContractFactory().getPrecompiledContract(context, track);
    }

    @Override
    public VirtualMachine getVM() {
        return new FastVM();
    }

    private static class Holder {

        static final AionExecutorProvider INST = new AionExecutorProvider();
    }
}
