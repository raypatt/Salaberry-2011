import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class Salaberry {
    private static final String FILE_PATH = "Data.xls";

    public Salaberry() {

    }

    public static void main(String args[]) throws IOException {
        List dataBaseline = getScoresData();
        List baseline = getBaselines(dataBaseline);
        displayBaselines(baseline);

        List dataBaseline3 = getScoresData();
        ArrayList<ArrayList<Double>> simplifiedBaseline3 = (ArrayList<ArrayList<Double>>) simplify2(
                getBaselines(dataBaseline3));
        displayBaselines(simplifiedBaseline3);

        List scores = getScoresData();
        displayMins(scores);

        Map map = getSentenceData();
    }

    /**
     * This is the original simplify method. It creates the 3 groups as SEM1&2,
     * SEM3&4, and NS on it's own. It produced an average distance of the
     * baseline of 0.1025.
     *
     * @param data
     * @return
     */
    private static List simplify2(List data) {
        ArrayList<ArrayList<Double>> simplified = new ArrayList<ArrayList<Double>>();

        ArrayList<Double> tmp1 = (ArrayList<Double>) data.get(0);
        ArrayList<Double> tmp2 = (ArrayList<Double>) data.get(1);
        for (int i = 0; i < tmp1.size(); i++) {
            tmp1.set(i, (tmp1.get(i) + tmp2.get(i)) / 2);
        }
        simplified.add(tmp1);

        tmp1 = (ArrayList<Double>) data.get(2);
        tmp2 = (ArrayList<Double>) data.get(3);
        for (int i = 0; i < tmp1.size(); i++) {
            tmp1.set(i, ((tmp1.get(i) + tmp2.get(i)) / 2));
        }
        simplified.add(tmp1);
        simplified.add((ArrayList<Double>) data.get(4));
        return simplified;
    }

    /**
     * This is the original simplify method. It creates the 3 groups as SEM2&3,
     * SEM4&NS, and SEM1 on it's own. It produced an average distance of the
     * baseline of 0.07825.
     *
     * @param data
     * @return
     */
    private static List simplify(List data) {
        ArrayList<ArrayList<Double>> simplified = new ArrayList<ArrayList<Double>>();
        simplified.add((ArrayList<Double>) data.get(0));
        ArrayList<Double> tmp1 = (ArrayList<Double>) data.get(1);
        ArrayList<Double> tmp2 = (ArrayList<Double>) data.get(2);
        for (int i = 0; i < tmp1.size(); i++) {
            tmp1.set(i, (tmp1.get(i) + tmp2.get(i)) / 2);
        }
        simplified.add(tmp1);

        tmp1 = (ArrayList<Double>) data.get(3);
        tmp2 = (ArrayList<Double>) data.get(4);
        for (int i = 0; i < tmp1.size(); i++) {
            tmp1.set(i, ((tmp1.get(i) + tmp2.get(i)) / 2));
        }
        simplified.add(tmp1);
        return simplified;
    }

    private static void displayMins(List data) {
        System.out.println("--------------------------------------------");
        System.out.println("Minimum # entries");
        System.out.println("--------------------------------------------");
        List mins = getAllMins(data);
        System.out.format("%-5s", "FGP");
        System.out.format("%-5s", "FGN");
        System.out.format("%-5s", "BGP");
        System.out.format("%-5s", "BGN");
        System.out.println();
        for (int i = 0; i < mins.size(); i++) {
            System.out.format("%-5.0f",
                    Double.parseDouble(mins.get(i).toString()) + 1);
        }
        System.out.println();
        System.out.println("--------------------------------------------");
    }

    private static List getAllMins(List data) {
        List mins = new ArrayList<Integer>();
        int FGP = getMin("FGP", data);
        int FGN = getMin("FGN", data);
        int BGP = getMin("BGP", data);
        int BGN = getMin("BGN", data);
        mins.add(FGP);
        mins.add(FGN);
        mins.add(BGP);
        mins.add(BGN);
        return mins;
    }

    private static int getMin(String tag, List data) {
        ArrayList<ArrayList<Double>> tmp = (ArrayList<ArrayList<Double>>) data;
        int sizeMin = Integer.MAX_VALUE;
        for (int i = 0; i < tmp.size() - 1; i++) {
            List scores = getScores(i, tag, data);
            if (scores.size() < sizeMin) {
                sizeMin = scores.size();
            }
        }
        return sizeMin;
    }

    private static Map getSentenceData() throws IOException {
        Map<Integer, String> map = new HashMap<Integer, String>();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(FILE_PATH);

            Workbook workbook = new HSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(5);
            Iterator rowIterator = sheet.iterator();

            while (rowIterator.hasNext()) {

                Row row = (Row) rowIterator.next();
                Iterator cellIterator = row.cellIterator();

                String tmp = cellIterator.next().toString();
                int tmp1 = (int) Double.parseDouble(tmp);
                String tmp2 = cellIterator.next().toString();

                map.put(tmp1, tmp2);
                // map.put(Integer.parseInt(cellIterator.next().toString()),
                // cellIterator.next().toString());

            }

            fis.close();

        } catch (FileNotFoundException e) {
            System.out.println("Not a file");
        }
        return map;
    }

    private static void displayBaselines(List scores) {

        //displayScores(getScoresNoOutliers(4, "BGP", data));
        System.out.println("--------------------------------------------");
        System.out.println("Average Score without Outliers");
        System.out.println("--------------------------------------------");
        System.out.print("   ");
        System.out.format("%-10.3s", "FGP");
        System.out.format("%-10.3s", "FGN");
        System.out.format("%-10.3s", "BGP");
        System.out.format("%-10.3s", "BGN");
        System.out.println();
        for (int i = 0; i < scores.size(); i++) {
            System.out.print(" " + i + " ");
            List year = (List) scores.get(i);
            for (int j = 0; j < year.size(); j++) {
                System.out.format("%-10.3f", year.get(j));
            }
            System.out.println();
        }
        System.out.println("--------------------------------------------");
    }

    private static List getBaselines(List scores) {
        List baselines = new ArrayList<ArrayList<Double>>();

        for (int i = 0; i < scores.size(); i++) {
            List tmp = new ArrayList<Double>();
            tmp.add(average(getScoresNoOutliers(i, "FGP", scores)));
            tmp.add(average(getScoresNoOutliers(i, "FGN", scores)));
            tmp.add(average(getScoresNoOutliers(i, "BGP", scores)));
            tmp.add(average(getScoresNoOutliers(i, "BGN", scores)));
            baselines.add(tmp);
        }

        return baselines;
    }

    private static double average(List scores) {
        double sum = 0;
        int count = 0;
        for (double x : (ArrayList<Double>) scores) {
            sum += x;
            count++;
        }
        return sum / count;
    }

    private static List getScoresNoOutliers(int level, String type, List data) {
        List tmp = getScores(level, type, data);
        //System.out.print("Outliers in level " + level + " " + type + ": ");
        removeOutliers(tmp);
        return tmp;
    }

    private static void removeOutliers(List scores) {
        Collections.sort(scores);

        double q1 = q1(scores);
        double q3 = q3(scores);
        double IQR = q3 - q1;
        double outer = IQR + q3;
        double inner = q1 - IQR;

        Iterator it = scores.iterator();
        while (it.hasNext()) {
            Object tmp = it.next();
            if ((double) tmp < inner) {
                //System.out.print(" | " + tmp);
                it.remove();
            } else if ((double) tmp > outer) {
                //System.out.print(" | " + tmp);
                it.remove();
            }
        }
        //System.out.println();

    }

    private static double q3(List scores) {
        int index = 0;
        index = (int) ((3.0 / 4) * (scores.size()));
        return (double) scores.get(index);
    }

    private static double q1(List scores) {
        int index = 0;
        index = (int) ((1.0 / 4) * (scores.size()));
        return (double) scores.get(index);
    }

    private static void displayScores(List scores) {
        for (int i = 0; i < scores.size(); i++) {
            System.out.println(scores.get(i));
        }
    }

    private static List getScores(int level, String type, List data) {
        List levelGroup = (List) data.get(level);
        int typeIndex = 0;
        switch (type) {
            case "FGP":
                typeIndex = 0;
                break;
            case "FGN":
                typeIndex = 1;
                break;
            case "BGP":
                typeIndex = 2;
                break;
            case "BGN":
                typeIndex = 3;
                break;
        }
        List typeGroup = (List) levelGroup.get(typeIndex);
        return typeGroup;
    }

    private static List getScoresData() throws IOException {
        List data = new ArrayList<ArrayList<Integer>>();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(FILE_PATH);

            // Using XSSF for xlsx format, for xls use HSSF
            Workbook workbook = new HSSFWorkbook(fis);
            //Workbook workbook = new XSSFWorkbook(fis);

            int numberOfSheets = workbook.getNumberOfSheets();

            List years = new ArrayList();

            for (int i = 0; i < 5; i++) {
                List y1 = new ArrayList();
                List FGP = new ArrayList<Double>();
                List FGN = new ArrayList<Double>();
                List BGP = new ArrayList<Double>();
                List BGN = new ArrayList<Double>();
                Sheet sheet = workbook.getSheetAt(i);
                Iterator rowIterator = sheet.iterator();

                while (rowIterator.hasNext()) {

                    Row row = (Row) rowIterator.next();
                    Iterator cellIterator = row.cellIterator();

                    int index = 1;
                    while (cellIterator.hasNext()) {
                        Cell cell = (Cell) cellIterator.next();

                        String strVal = cell.toString();
                        //System.out.println(strVal);
                        //System.out.println(strVal.contains("G"));
                        //System.out.println(strVal);
                        if (strVal.contains("G") == false
                                && !strVal.equals("END")) {
                            double val = Double.parseDouble(strVal);
                            if ((val <= 1.00) && (val >= 0)) {
                                switch (index) {
                                    case 1:
                                        //System.out.println(i + "FGP: " + val);
                                        FGP.add(val);
                                        break;
                                    case 2:
                                        //System.out.println("FGN: " + val);
                                        FGN.add(val);
                                        break;
                                    case 3:
                                        //System.out.println("BGP: " + val);
                                        BGP.add(val);
                                        break;
                                    case 4:
                                        //System.out.println("BGN: " + val);
                                        BGN.add(val);
                                        break;
                                }
                            }
                            index++;
                        }

                    }

                }
                //System.out.println("Finished: " + i);
                y1.add(FGP);
                y1.add(FGN);
                y1.add(BGP);
                y1.add(BGN);
                data.add(y1);

            }

            fis.close();

        } catch (FileNotFoundException e) {
            System.out.println("Not a file");
        }
        return data;
    }
}
