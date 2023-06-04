package search;

import search.parser.VariableReplacerForStringExpression;

import java.util.List;
import java.util.Scanner;


public class Menu {
    private static final String FILE_PATH = "airports.csv";
    private volatile boolean quit = false;
    private static final Scanner scanner = new Scanner(System.in);
    static volatile String filter = "";
    Thread quitCommandWaitThread = new Thread(new Runnable() {
        @Override
        public synchronized void run() {
            while (!quit) {
                if (filter.equals("!quit")) {
                    System.out.println("Выход из программы...");
                    System.exit(0);
                    quit = true;
                }
            }
        }
    });

    public synchronized void drawMenu()  {
        quitCommandWaitThread.setDaemon(true);
        quitCommandWaitThread.start();

        CsvValueFilter csvReader = new CsvValueFilter(FILE_PATH,
                new VariableReplacerForStringExpression(),
                new CacheService());

        while (!quit) {
            System.out.println("(Для завершения программы введите !quit)");
            System.out.println("Введите фильтр вида : \"column[1]>10 & column[5]=’GKA’ || column[<номер колонки с 1>]<операция сравнения>...\" ");
            filter = scanner.nextLine();
            csvReader.setFilter(filter);

            System.out.print("Введите начало имени аэропорта: ");
            filter = scanner.nextLine();

            long startTime = System.currentTimeMillis();
            List<String> result = csvReader.outputFilteredDataOnColumnName(filter);
            long endTime = System.currentTimeMillis();

            for (String s : result) {
                String[] airportName = s.split(",");
                System.out.println(airportName[1] + "[" + s + "]");
            }

            System.out.print("Найдено строк: " + result.size() + " ");
            System.out.println("Время поиска: " + (endTime - startTime) + " мс\n");

        }

    }

}
