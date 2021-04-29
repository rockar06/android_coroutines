package com.rockar.coroutines.data.utils

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

val BACKGROUND: ExecutorService = Executors.newFixedThreadPool(2)
