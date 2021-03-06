/* Author: Daniël Geerts
 */
package fileController;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReadFile {

    public ReadFile() {
    }

    /* Do some checks and read the right files from the Database
     * Then return the right records
     *
     * @Parameter: instructions Represents the instruction that the client has send with GET
     *
     * @Author Daniël Geerts
     */
    public List<String> ReadRecordsFromFile(String instructions) throws IOException {
        FileInputStream inputStream = null;
        Scanner sc = null;
        List<String> tosend = new ArrayList<>();

        List<String> filter = Arrays.asList(instructions.split(","));
        int stn_id = Integer.parseInt(filter.get(0).trim());
        Long min_date = convertDateToLong(filter.get(1).trim());
        Long max_date = convertDateToLong(filter.get(2).trim());

        try {
            List<File> list = new ArrayList<File>();
            List<String> dates = getAllDatesBetween(new Date(min_date), new Date(max_date));
            ReadDirectoryAddFiles(FileConfig.FOLDER_NAME, list, dates, stn_id);
            System.out.println("Aantal files added: " + list.size());
            /*for (File f : list) {
                System.out.println("File added: " + f);
            }*/
            for (int i = 0; i < list.size(); i++) {
                inputStream = new FileInputStream(list.get(i));
                sc = new Scanner(inputStream, "UTF-8");

                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    if (!line.contains("STN") && !line.contains("DATETIME")) {
                        if (min_date != -1 && max_date != -1) {
                            Long db_date = convertDateToLong(line.split(",")[Arrays.asList(FileConfig.DB_COLUMNS).indexOf("DATETIME")].trim());
                            if (db_date != -1) {
                                if (min_date.compareTo(db_date) * db_date.compareTo(max_date) > 0) {
                                    tosend.add("(" + line + ")");
                                }
                            }
                        }
                    }
                }

                // note that Scanner suppresses exceptions
                if (sc.ioException() != null) {
                    throw sc.ioException();
                }
                // After reading one file is done, close file Stream
                if (inputStream != null) {
                    inputStream.close();
                }
                if (sc != null) {
                    sc.close();
                }
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (sc != null) {
                sc.close();
            }
        }
        return tosend;
    }

    /* Read from a default folder every folder and file
     * Then add the right files compared with the dates to the (parameter) files
     *
     * @parameter directoryName The default directory to look in
     * @parameter files This List will contain the files that have been found from the right date
     * @parameter dates All dates that the client requested
     *
     * @Author Daniël Geerts
     */
    private void ReadDirectoryAddFiles(String directoryName, List<File> files, List<String> dates, int stn_id) {
        File directory = new File(directoryName);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Get all files from a directory.
        List<File> fList = Arrays.asList(directory.listFiles());

        if (fList != null) {
            for (File file : fList) {
                if (file.isFile()) {
                    if (dates.contains(file.getName().replace(".csv", ""))) {
                        if (file.getAbsoluteFile().toString().contains(Integer.toString(stn_id))) {
                            //System.out.println("Files added: " + file.getAbsoluteFile());
                            if (!files.contains(file)) {
                                files.add(file);
                            }
                        }
                    }
                } else if (file.isDirectory()) {
                    ReadDirectoryAddFiles(file.getAbsolutePath(), files, dates, stn_id);
                }
            }
        }
    }

    /* Convert Date to long with try catch block
     *
     * @parameter data Is the date as String to convert to a long
     *
     * @Author Daniël Geerts
     */
    private Long convertDateToLong(String data) {
        try {
            return Long.parseLong(data);
        } catch (NumberFormatException nfe) {
            System.out.println("ERROR-NumberFormatException: " + nfe.getMessage());
        }

        return -1L;
    }

    /* Get every date and hour between 2 dates
     *
     * @parameter min Minimal Date to check from
     * @parameter max Maximal Date to check till
     *
     * @Author Daniël Geerts
     */
    private List<String> getAllDatesBetween(Date min, Date max){
        List<String> dates = new ArrayList<>();

        long diff = min.getTime() - max.getTime();
        diff = diff / (1000 * 60 * 60);
        if (diff < 0) {
            diff = -diff;
        }

        int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(min));
        int month = Integer.parseInt(new SimpleDateFormat("MM").format(min));
        int day = Integer.parseInt(new SimpleDateFormat("dd").format(min));
        int hour = Integer.parseInt(new SimpleDateFormat("HH").format(min));

        for (int i = 0; i <= diff; i++) {
            int newYear = year;
            int newMonth = month;
            int newDay = day;
            int newHour = hour + i;
            if (newHour >= 24) {
                newDay += newHour / 24;
                newHour = newHour % 24;
            }
            if (newDay >= 31) {
                newMonth += newDay / 31;
                newDay = (newDay % 31) + 1;
            }
            if (newMonth >= 13) {
                newYear += newMonth / 13;
                newMonth = (newMonth % 13) + 1;
            }

            String datebetween = newYear + "-";
            if (newMonth < 10) {
                datebetween += "0";
            }
            datebetween += newMonth + "-";
            if (newDay < 10) {
                datebetween += "0";
            }
            datebetween += newDay + "_h";
            if (newHour < 10) {
                datebetween += "0";
            }
            datebetween += newHour;

            //System.out.println(i + ", Date between: " + datebetween);

            dates.add(datebetween);
        }
        return dates;
    }
}
