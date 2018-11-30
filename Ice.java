import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

public class Ice {
    public static void main(String[] args) {
        int flag = -1;
        try {
            flag = Integer.parseInt(args[0]);
        } catch (Exception e) {
            System.err.println("Invalid Input: Please enter numbers.");
        }
        HashMap<String, Integer> data = new HashMap<>();
        File file = new File("data.txt");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] info = line.split("\t");
                String year = info[0].substring(0, 4);
                Integer days = Integer.parseInt(info[1]);
                if(flag == 100) {
                    System.out.println(year + " " + days);
                }
                data.put(year, days);
            }
        } catch(FileNotFoundException e) {
            System.out.println("Error reading file");
            System.exit(0);
        } catch(IOException e) {
            System.out.println("IOException error");
            System.exit(0);
        }

        Collection<Integer> allDays = data.values();
        Set<String> years = data.keySet();
        double n = allDays.size();
        double sum = 0;
        for(Integer days : allDays) {
            sum += days;
        }
        double mean = sum / n;
        int varianceSum = 0;
        for(Integer days : allDays) {
            varianceSum += Math.pow(days - mean, 2);
        }
        double standardDeviation = Math.sqrt(varianceSum / (n - 1));

        double sumYear = 0;
        for(String year : years) {
            sumYear += Double.parseDouble(year);
        }
        double meanYear = sumYear / n;
        int varianceSumYear = 0;
        for(String year : years) {
            varianceSumYear += Math.pow(Double.parseDouble(year) - meanYear, 2);
        }
        double standardDeviationYear = Math.sqrt(varianceSumYear / (n - 1));

        if(flag == 200) {
            System.out.println(n);
            System.out.println(String.format("%.2f", mean));
            System.out.println(String.format("%.2f", standardDeviation));
        }

        if(flag == 300) {
            double beta0 = Double.parseDouble(args[1]);
            double beta1 = Double.parseDouble(args[2]);
            double mse = 0;
            for(String year : years) {
                mse += Math.pow(beta0 + (beta1 * Integer.parseInt(year)) -  data.get(year), 2);
            }
            mse = mse / n;
            System.out.println(String.format("%.2f", mse));
        }

        if(flag == 400) {
            double beta0 = Double.parseDouble(args[1]);
            double beta1 = Double.parseDouble(args[2]);
            double mse1 = 0;
            double mse2 = 0;
            for(String yearString : years) {
                int year = Integer.parseInt(yearString);
                mse1 += beta0 + (beta1 * year) -  data.get(yearString);
                mse2 += (beta0 + (beta1 * year) -  data.get(yearString)) * year;
            }
            mse1 = 2 * mse1 / n;
            mse2 = 2 * mse2 / n;
            System.out.println(String.format("%.2f", mse1));
            System.out.println(String.format("%.2f", mse2));
        }

        if(flag == 500) {
            double gradient = Double.parseDouble(args[1]);
            int iterations = Integer.parseInt(args[2]);
            double beta0 = 0;
            double beta1 = 0;
            for(int i = 1; i <= iterations; i++) {
                double mse = 0;
                double mse1 = 0;
                double mse2 = 0;
                for (String yearString : years) {
                    int year = Integer.parseInt(yearString);
                    mse1 += beta0 + (beta1 * year) - data.get(yearString);
                    mse2 += (beta0 + (beta1 * year) - data.get(yearString)) * year;
                }
                mse1 = 2 * mse1 / n;
                mse2 = 2 * mse2 / n;
                beta0 = beta0 - gradient * mse1;
                beta1 = beta1 - gradient * mse2;

                for (String yearString : years) {
                    int year = Integer.parseInt(yearString);
                    mse += Math.pow(beta0 + (beta1 * year) -  data.get(yearString), 2);
                }
                mse = mse / n;

                System.out.println(i + " " + String.format("%.2f", beta0) + " " + String.format("%.2f", beta1) +
                        " " + String.format("%.2f", mse));
            }
        }

        int yearSum = 0;
        for(String yearString : years) {
            yearSum += Integer.parseInt(yearString);
        }

        double yearMean = yearSum / n;
        if(flag == 600 || flag == 700) {
            double beta1Numerator = 0;
            double beta1Denominator = 0;
            for(String yearString : years) {
                int year = Integer.parseInt(yearString);
                beta1Numerator += (year - yearMean) * (data.get(yearString) - mean);
                beta1Denominator += Math.pow((year - yearMean), 2);
            }
            double beta1 = beta1Numerator / beta1Denominator;
            double beta0 = mean - (beta1 * yearMean);

            double mse = 0;
            for(String year : years) {
                mse += Math.pow(beta0 + (beta1 * Integer.parseInt(year)) -  data.get(year), 2);
            }
            mse = mse / n;

            if(flag == 600) {
                System.out.println(String.format("%.2f", beta0) + " " + String.format("%.2f", beta1) + " "
                        + String.format("%.2f", mse));
            }
            if(flag == 700) {
                double yearPredict = Double.parseDouble(args[1]);
                double prediction = beta0 + (beta1 * yearPredict);
                System.out.println(String.format("%.2f", prediction));
            }
        }

        if(flag == 800) {
            double gradient = Double.parseDouble(args[1]);
            int iterations = Integer.parseInt(args[2]);
            double beta0 = 0;
            double beta1 = 0;

            for(int i = 1; i <= iterations; i++) {
                double mse = 0;
                double mse1 = 0;
                double mse2 = 0;
                for (String yearString : years) {
                    double year = Integer.parseInt(yearString);
                    year = (year - yearMean) / standardDeviationYear;
                    mse1 += beta0 + (beta1 * year) - data.get(yearString);
                    mse2 += (beta0 + (beta1 * year) - data.get(yearString)) * year;
                }
                mse1 = 2 * mse1 / n;
                mse2 = 2 * mse2 / n;
                beta0 = beta0 - (gradient * mse1);
                beta1 = beta1 - (gradient * mse2);

                for (String yearString : years) {
                    double year = Integer.parseInt(yearString);
                    year = (year - yearMean) / standardDeviationYear;
                    mse += Math.pow(beta0 + (beta1 * year) -  data.get(yearString), 2);
                }
                mse = mse / n;

                System.out.println(i + " " + String.format("%.2f", beta0) + " " + String.format("%.2f", beta1) +
                        " " + String.format("%.2f", mse));
            }
        }

        if(flag == 900) {
            double gradient = Double.parseDouble(args[1]);
            int iterations = Integer.parseInt(args[2]);
            double beta0 = 0;
            double beta1 = 0;
            for(int i = 1; i <= iterations; i++) {
                double mse = 0;
                String randKey = randomValueFromSet(years);
                Double randX = Double.parseDouble(randKey);
                randX = (randX - yearMean) / standardDeviationYear;
                Integer randY = data.get(randKey);
                double mse1 = 2 * (beta0 + (beta1 * randX) - randY);
                double mse2 = 2 * randX * (beta0 + (beta1 * randX) - randY);
                beta0 = beta0 - (gradient * mse1);
                beta1 = beta1 - (gradient * mse2);

                for (String yearString : years) {
                    double year = Integer.parseInt(yearString);
                    year = (year - yearMean) / standardDeviationYear;
                    mse += Math.pow(beta0 + (beta1 * year) -  data.get(yearString), 2);
                }
                mse = mse / n;

                System.out.println(i + " " + String.format("%.2f", beta0) + " " + String.format("%.2f", beta1) +
                        " " + String.format("%.2f", mse));
            }
        }
    }
    private static String randomValueFromSet(Set<String> set) {
        int size = set.size();
        int item = new Random().nextInt(size);
        int i = 0;
        for(String obj : set)
        {
            if (i == item)
                return obj;
            i++;
        }
        return "";
    }
}
