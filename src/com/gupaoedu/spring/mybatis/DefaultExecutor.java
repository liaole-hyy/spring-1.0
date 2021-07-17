package com.gupaoedu.spring.mybatis;

/**
 *  默认的sql执行
 */
public class DefaultExecutor implements Executor {

    @Override
    public void execute(String sql) {
        System.out.println("执行:"+sql);
    }

    public static void main(String[] args) {
        Executor executor = new DefaultExecutor();
        executor.execute("select * from user");
    }
}
