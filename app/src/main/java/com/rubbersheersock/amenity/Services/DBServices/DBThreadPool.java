package com.rubbersheersock.amenity.Services.DBServices;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DBThreadPool {
    public static final Executor DBThreadPoolExecutor = new ThreadPoolExecutor(2,4,30,
            TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(10));
}
