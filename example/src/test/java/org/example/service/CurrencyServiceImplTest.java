package org.example.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CurrencyServiceImplTest {

    private static List<Character> uppercaseList = Arrays.asList('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
                    'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
                    'U', 'V', 'W', 'X', 'Y', 'Z');
    private static Map<String, Long> map = new HashMap<>();
    @Autowired
    private CurrencyService currencyService;

    @Test
    public void handUploadFileTest() {
        String filePath = ".\\test_data";
        int testCount = 100_0000;
        double validRate = 0.8;
        Map<String, Long> expectedMap = generateTestDataFileAndExpectedResult(filePath, testCount, validRate);
        File file = new File(filePath);
        try {
            InputStream inputStream = new FileInputStream(file);
            currencyService.handUploadFile(inputStream, file.getName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Map<String, AtomicLong> actualMap = currencyService.getCurrencyMap();
        // check result
        Assert.assertEquals(expectedMap.size(), actualMap.size());
        expectedMap.forEach((code, payment) -> Assert.assertEquals(payment.longValue(), actualMap.get(code).get()));
    }



    private static Map<String, Long> generateTestDataFileAndExpectedResult(String filePath, int testCount, double validRate) {
        try {
            File file = new File(filePath);
            OutputStream outputStream = new FileOutputStream(file);
            OutputStreamWriter outWriter = new OutputStreamWriter(outputStream, "UTF-8");
            BufferedWriter bufferedWriter = new BufferedWriter(outWriter);
            for (int i = 0; i < testCount; i++) {
                boolean isValid = Math.random() >= (1 - validRate);
                String data;
                if (isValid) {
                    data = generateValidDataAndMerge();
                } else {
                    data = generateInValidData();
                }
                bufferedWriter.write(data + "\r\n");
            }
            bufferedWriter.flush();
            bufferedWriter.close();
            outWriter.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(map.toString());
        return map;
    }

    private static String generateInValidData() {
        int count = (int)(Math.random() * 20);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            char asciiIndex =  (char)(Math.random() * 128);
            // don't contain number
            while (asciiIndex >= 48 && asciiIndex <= 57) {
                asciiIndex =  (char)(Math.random() * 128);
            }
            Character ch = Character.valueOf(asciiIndex);
            stringBuilder.append(ch);
        }
        return stringBuilder.toString();
    }

    private static String generateValidDataAndMerge() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            int index = (int) (Math.random() * 26);
            stringBuilder.append(uppercaseList.get(index));
        }
        String key = stringBuilder.toString();
        Long aLong = map.get(key);
        if (Objects.isNull(aLong)) {
            map.put(key, 0L);
            aLong = map.get(key);
        }

        stringBuilder.append(" ");
        Long value = ((long)(Math.random() * Integer.MAX_VALUE)) - ((long)(Math.random() * Integer.MAX_VALUE));
        aLong += value;
        map.put(key, aLong);
        stringBuilder.append(value.toString());
        return stringBuilder.toString();

    }

}
