package org.example;
import java.util.*;
import java.util.concurrent.*;

public class RobotRouteGenerator {
    private static final String LETTERS = "RLRFR";  //строка команд, которые робот может выполнить.
    private static final int LENGTH = 100; // количество команд в  маршруте.
    private static final int NUM_THREADS = 1000; // количество потоков.
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static final Object LOCK = new Object();

    public static void main(String[] args) {

        ExecutorService service = Executors.newFixedThreadPool(NUM_THREADS); // Создаю пул потоков равным NUM_THREADS.

        for (int i = 0; i < NUM_THREADS; i++) {
            service.submit(() -> {
                String route = generateRoute(LETTERS, LENGTH);
                int freq = calculateFrequency(route, 'R');
                synchronized (LOCK) {
                    sizeToFreq.put(freq, sizeToFreq.getOrDefault(freq, 0) + 1);
                }
            });
        }

        service.shutdown();
        try {
            if (!service.awaitTermination(60, TimeUnit.SECONDS)) {
                service.shutdownNow();
            }
        } catch (InterruptedException ex) {
            service.shutdownNow();
            Thread.currentThread().interrupt();
        }
        // завершаю работу ExecutorService и жду пока все задачи завершатся, иначе в течение 60 секунд остановлю выполнение.

        System.out.println("Наиболее часто встречающееся количество команд поворота направо:");
        List<Map.Entry<Integer, Integer>> list = new ArrayList<>(sizeToFreq.entrySet());
        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        // Вывожу сообщение на экран и сортирую по значениям в обратном порядке.

        for (Map.Entry<Integer, Integer> entry : list) {
            System.out.println(entry.getKey() + " (" + entry.getValue() + " раз)");
        }
    }
    // Вывожу каждую пару ключ-значение на экран.
    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }
    // generateRoute генерирует случайный маршрут робота.

    public static int calculateFrequency(String str, char ch) {
        int count = 0;
        for (char c : str.toCharArray()) {
            if (c == ch) {
                count++;
            }
        }
        return count;
    }
    // calculateFrequency считает количество вхождений символа ch в строку str и возвращает это число.
}
