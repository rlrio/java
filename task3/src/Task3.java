import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Task3 {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    private static int totalVolume;
    private static int startVolume;

    public static void main(String[] args) {
        try {
            if (args.length == 0) throw new MyExceptionTask3("No arguments found.");
            String path = args[0];
            String from = args[1];
            String to = args[2];
            toCsv(path, from, to);
        } catch (MyExceptionTask3 e) {
            System.err.println(e.getMessage());
        }
    }

    public static void toCsv(String log, String from, String to) {
        try (PrintWriter writer = new PrintWriter(new File("result.csv"))) {

            StringBuilder sb = new StringBuilder();
            sb.append("Начало периода");
            sb.append(',');
            sb.append("Конец периода");
            sb.append(',');
            sb.append("Количество попыток");
            sb.append(',');
            sb.append("Процент ошибок");
            sb.append(',');
            sb.append("Объем налитой воды");
            sb.append(',');
            sb.append("Объем не налитой воды");
            sb.append(',');
            sb.append("Объем вылитой воды");
            sb.append(',');
            sb.append("Объем не вылитой воды");
            sb.append(',');
            sb.append("Объем воды на начало периода");
            sb.append(',');
            sb.append("Объем воды на конец периода");
            sb.append('\n');

            sb.append(from);
            sb.append(",");
            sb.append(to);
            sb.append(",");
            sb.append(numberOfTriesToTopUp(log, from, to));
            sb.append(",");
            sb.append(percentOfFails(log, from, to));
            sb.append(",");
            sb.append(volumeToppedUp(log, from, to));
            sb.append(",");
            sb.append(volumeScooped(log, from, to));
            sb.append(",");
            sb.append(volumeFailedToTopUp(log, from, to));
            sb.append(",");
            sb.append(volumeFailedToScoop(log, from, to));
            sb.append(",");
            sb.append(startVolumeForPeriod(log, from, to));
            sb.append(",");
            sb.append(currentVolumeForPeriod(log, from, to));
            sb.append('\n');

            writer.write(sb.toString());

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private static int volumeToppedUp(String log, String from, String to) {
        long count = filterByDates(log, from, to).count();
        if (count == 0L) {
            throw new MyExceptionTask3("Нет записей для заданного периода: " + from + " - " + to);
        }
        int volume = filterByDates(log, from, to)
                .filter(Record::isToppedUp)
                .filter(Record::isSuccess)
                .mapToInt(Record::getVolume)
                .sum();
        return volume;
    }

    private static int volumeScooped(String log, String from, String to) {
        long count = filterByDates(log, from, to).count();
        if (count == 0L) {
            throw new MyExceptionTask3("Нет записей для заданного периода: " + from + " - " + to);
        }
        int volume = filterByDates(log, from, to)
                .filter(record -> !record.isToppedUp())
                .filter(Record::isSuccess)
                .mapToInt(Record::getVolume)
                .sum();
        return volume;
    }

    private static int volumeFailedToTopUp(String log, String from, String to) {
        long count = filterByDates(log, from, to).count();
        if (count == 0L) {
            throw new MyExceptionTask3("Нет записей для заданного периода: " + from + " - " + to);
        }
        int volume = filterByDates(log, from, to)
                .filter(Record::isToppedUp)
                .filter(record -> !record.isSuccess())
                .mapToInt(Record::getVolume)
                .sum();
        return volume;
    }

    private static int volumeFailedToScoop(String log, String from, String to) {
        long count = filterByDates(log, from, to).count();
        if (count == 0L) {
            throw new MyExceptionTask3("Нет записей для заданного периода: " + from + " - " + to);
        }
        int volume = filterByDates(log, from, to)
                .filter(record -> !record.isToppedUp())
                .filter(record -> !record.isSuccess())
                .mapToInt(Record::getVolume)
                .sum();
        return volume;
    }

    private static long numberOfTriesToTopUp(String log, String from, String to) {
        long count = filterByDates(log, from, to).filter(Record::isToppedUp).count();
        if (count == 0L) {
            throw new MyExceptionTask3("Нет записей для заданного периода: " + from + " - " + to);
        }
        return count;
    }

    private static long percentOfFails(String log, String from, String to) {
        long countAll = filterByDates(log, from, to).count();
        long countOfFails = filterByDates(log, from, to).filter(record -> !record.isSuccess()).count();
        if (countAll == 0L) {
            throw new MyExceptionTask3("Нет записей для заданного периода: " + from + " - " + to);
        }
        long percent = countOfFails * 100 / countAll;
        return percent;
    }

    private static int currentVolumeForPeriod(String log, String from, String to) {
        long count = filterByDates(log, from, to).filter(Record::isSuccess).count();
        if (count == 0L) {
            throw new MyExceptionTask3("Нет записей для заданного периода: " + from + " - " + to);
        }
        int volume = filterByDates(log, from, to).filter(Record::isSuccess).skip(count - 1)
                .findFirst().get().getAfterVolume();
        return volume;
    }

    private static int startVolumeForPeriod(String log, String from, String to) {
        long count = filterByDates(log, from, to).filter(Record::isSuccess).count();
        if (count == 0L) {
            throw new MyExceptionTask3("Нет записей для заданного периода: " + from + " - " + to);
        }
        int volume = filterByDates(log, from, to).filter(Record::isSuccess).findFirst().get().getBeforeVolume();
        return volume;
    }

    private static List<Record> parseLogToList(String log) {
        List<Record> list = new ArrayList<>();
        try (FileInputStream fileInputStream = new FileInputStream(log);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("-")) {
                    String[] split = line.split(" – ");
                    Record record = new Record();
                    String s = split[0].replace('.', ':')
                            .replace("T", " ")
                            .replace("Z", "")
                            .trim();
                    Date date = DATE_FORMAT.parse(s);
                    record.setDate(date);
                    String user = split[1].split(" - ")[0].replace('[', ' ').replace(']', ' ').trim();
                    record.setUser(user);
                    int volume = 0;
                    Matcher digit = Pattern.compile("\\d+").matcher(split[1].split(" - ")[0]);
                    if (digit.find()) {
                        volume = Integer.parseInt(digit.group());
                    }
                    record.setVolume(volume);
                    record.setToppedUp(line.contains("top"));
                    list.add(record);
                } else {
                    Matcher matcher = Pattern.compile("\\d+").matcher(line);
                    if (matcher.find()) {
                        int dataVolume = Integer.parseInt(matcher.group().trim());
                        if (totalVolume == 0) totalVolume = dataVolume;
                        else startVolume = dataVolume;
                    }
                }
            }
            return list;
        } catch (IOException e) {
            throw new MyExceptionTask3("Problems with opening file: " + log);
        } catch (ParseException e) {
            throw new MyExceptionTask3("Problems with parsing date. ");
        }
    }

    private static List<Record> setSuccess(String log) {
        List<Record> list = parseLogToList(log);
        int currentVolume = startVolume;
        for (Record record : list) {
            if (record.isToppedUp()) {
                if (record.getVolume() <= (totalVolume - currentVolume)) {
                    record.setBeforeVolume(currentVolume);
                    record.setSuccess(true);
                    currentVolume += record.getVolume();
                    record.setAfterVolume(currentVolume);
                } else {
                    record.setSuccess(false);
                }
            } else {
                if (record.getVolume() <= currentVolume) {
                    record.setBeforeVolume(currentVolume);
                    record.setSuccess(true);
                    currentVolume -= record.getVolume();
                    record.setAfterVolume(currentVolume);
                } else {
                    record.setSuccess(false);
                }
            }
        }
        return list;
    }

    private static Date getDate(String s) {
        try {
            s = s.replace('.', ':').replace('T', ' ').trim();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return df.parse(s);
        } catch (ParseException e) {
            throw new MyExceptionTask3("Problems with parsing date : " + s);
        }
    }

    private static boolean isBetween(Date value, Date from, Date to) {
        return (from == null || value.compareTo(from) >= 0) && (to == null || value.compareTo(to) < 0);
    }

    private static Stream<Record> filterByDates(String log, String from, String to) {
        List<Record> readLog = setSuccess(log);
        Stream<Record> stream = readLog.stream()
                .filter(record -> isBetween(record.getDate(), getDate(from), getDate(to)));
        return stream;
    }

}


