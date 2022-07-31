package org.example.service;

import javafx.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CurrencyServiceImpl implements CurrencyService {

    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(11, 11,
            0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(10), new ThreadPoolExecutor.CallerRunsPolicy());
    private static Boolean isEnd = false;

    private static Map<String, AtomicLong> currencyMap = new HashMap<>();
    private static Set<Character> uppercaseLetter = new HashSet<>(
            Arrays.asList('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
                    'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
                    'U', 'V', 'W', 'X', 'Y', 'Z'));

    @Override
    public void handUploadFile(InputStream inputStream, String fileName) {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String readLine = null;
            List<Future<?>> list = new ArrayList<>();
            while ((readLine = bufferedReader.readLine()) != null) {
                String finalReadLine = readLine;
                Future<?> future = executor.submit(() -> {
                    // handle finalReadLine
                    Pair<String, Long> pair = parseInputLine(finalReadLine);
                    if (Objects.isNull(pair)) {
                        System.out.println("filename=" + fileName + " contains wrong line=" + finalReadLine);
                    } else {
                        mergeInput(pair.getKey(), pair.getValue());
                    }
                    return;
                });
                list.add(future);
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            // await task finish
            awaitTaskFinish(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void awaitTaskFinish(List<Future<?>> list) {
        list.forEach(future -> {
            try {
                future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public Map<String, AtomicLong> getCurrencyMap() {
        return currencyMap;
    }

    public static void handInputOfTerminal() {
        Scanner scanner = new Scanner(System.in);
        String scanLine = null;
        while ((scanLine = scanner.nextLine()) != null) {
            scanLine = scanLine.trim();
            if ("quit".equals(scanLine)) {
                shutdownExecutor();
                break;
            }
            Pair<String, Long> pair = parseInputLine(scanLine);
            if (Objects.isNull(pair)) {
                System.out.println("terminal input wrongs line:" + scanLine);
                continue;
            }
            Long payment = mergeInput(pair.getKey(), pair.getValue());
            System.out.println(pair.getKey() + " " + payment);
        }
    }



    private static Pair<String, Long> parseInputLine(String line) {
        // check format
        line = line.trim();
        if (Objects.isNull(line) || line.length() < 4) {
            return null;
        }
        String[] split = line.split(" ");
        if (split.length != 2) {
            return null;
        }
        // check currencyCode
        String currencyCode = split[0];
        if (currencyCode.length() != 3) {
            return null;
        }
        for (int i = 0; i < currencyCode.length(); i++) {
            if (!uppercaseLetter.contains(currencyCode.charAt(i))) {
                return null;
            }
        }
        // check value
        String value = split[1];
        Long parseValue = null;
        try {
            parseValue = Long.valueOf(value);
        } catch (NumberFormatException e) {
        }
        if (Objects.isNull(parseValue)) {
            return null;
        }
        return new Pair<>(currencyCode, parseValue);

    }

    private static Long mergeInput(String currencyCode, Long value) {
        AtomicLong aLong = getAtomicLongValue(currencyCode);
        return aLong.addAndGet(value);
    }

    private static AtomicLong getAtomicLongValue(String currencyCode) {
        AtomicLong aLong = currencyMap.get(currencyCode);
        // double check lock
        if (Objects.isNull(aLong)) {
            synchronized (currencyMap) {
                aLong = currencyMap.get(currencyCode);
                if (Objects.isNull(aLong)) {
                    aLong = new AtomicLong();
                    currencyMap.put(currencyCode, aLong);
                }
            }
        }
        return aLong;
    }

    public static void consoleOutput() {
        executor.execute(() -> {
            while (!isEnd) {
                List<String> consoleList = new ArrayList<>();
                currencyMap.forEach((currency, value) -> {
                    if (value.get() != 0L) {
                        consoleList.add(currency + " " + value);
                    }
                });
                System.out.println("console display once per one minute:");
                consoleList.forEach(str -> System.out.println(str));
                try {
                    Thread.sleep(60_000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private static void shutdownExecutor() {
        isEnd = true;
        executor.shutdown();
    }


}
