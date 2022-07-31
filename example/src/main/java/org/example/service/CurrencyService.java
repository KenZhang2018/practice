package org.example.service;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public interface CurrencyService {

    void handUploadFile(InputStream inputStream, String fileName);

    Map<String, AtomicLong> getCurrencyMap();

}
