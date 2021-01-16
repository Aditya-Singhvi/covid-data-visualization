import javax.print.DocFlavor;
import java.io.*;
import java.util.*;

public class DataConverter
{
    private int[][] caseData;
    private int[][] deathData;

    private int[][] newCaseData;
    private int[][] newDeathData;

    private int[][] avgNewCaseData;
    private int[][] avgNewDeathData;

    private int[][] totalUSCases;
    private int[][] totalCACases;

    private String inputFile;
    private String outputCaseFile;
    private String outputDeathFile;
    private String outputNewCaseFile;
    private String outputNewDeathFile;
    private String outputAvgNewCaseFile;
    private String outputAvgNewDeathFile;
    private String outputUSNewCaseFile;
    private String outputCANewCaseFile;
    private String fipsFile;

    private static final String SEP = ",";

    public DataConverter(String inputFile, String outputCaseFile, String outputDeathFile,
                         String outputNewCaseFile, String outputNewDeathFile,
                         String outputAvgNewCaseFile, String outputAvgNewDeathFile, String outputUSNewCaseFile, String outputCANewCaseFile,
                         String fipsFile)
    {
        this.inputFile = inputFile;

        this.outputCaseFile = outputCaseFile;
        this.outputDeathFile = outputDeathFile;
        this.outputNewCaseFile = outputNewCaseFile;
        this.outputNewDeathFile = outputNewDeathFile;
        this.outputAvgNewCaseFile = outputAvgNewCaseFile;
        this.outputAvgNewDeathFile = outputAvgNewDeathFile;
        this.outputUSNewCaseFile = outputUSNewCaseFile;
        this.outputCANewCaseFile = outputCANewCaseFile;

        this.fipsFile = fipsFile;

        int days = DataUtil.daysSinceStart(new Date()) + 1;

        DataUtil.readFips(fipsFile);

        int maxFips = DataUtil.getMaxFips() + 1;

        caseData = new int[days][maxFips];
        deathData = new int[days][maxFips];
        newCaseData = new int[days][maxFips];
        newDeathData = new int[days][maxFips];
        avgNewCaseData = new int[days][maxFips];
        avgNewDeathData = new int[days][maxFips];
    }

    public void readValues()
    {
        String row = "";

        try
        {
            BufferedReader csvReader = new BufferedReader(new FileReader(inputFile));

            csvReader.readLine();   //Reads Title Line

            while((row = csvReader.readLine()) != null)
            {
                String[] line = row.split(",");

                String[] dateString = line[0].split("-");
                int days= DataUtil.daysSinceStart(dateString[0], dateString[1], dateString[2]);

                int fips = Integer.parseInt(line[2]);

                int cases = Integer.parseInt(line[3]);
                int deaths = Integer.parseInt(line[4]);

                caseData[days][fips] = cases;
                deathData[days][fips] = deaths;

                if(days > 0)
                {
                    newCaseData[days][fips] = cases - caseData[days-1][fips];
                    newDeathData[days][fips] = deaths - deathData[days-1][fips];
                }

                if(days >= 6)
                {
                    int sumCases = 0;
                    int sumDeaths = 0;

                    for(int i = 0; i <= 6; i++)
                    {
                        sumCases += newCaseData[days - i][fips];
                        sumDeaths += newDeathData[days - i][fips];
                    }

                    avgNewCaseData[days][fips] = sumCases/7;
                    avgNewDeathData[days][fips] = sumDeaths/7;
                }
            }
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
        catch(IndexOutOfBoundsException ex)
        {
            ex.printStackTrace();
            System.out.println(row);
        }

        System.out.println(new Date() + ": Values have been read and stored from file " + inputFile + ".");
    }

    public void writeValues()
    {
        makeNewCSV(caseData, outputCaseFile);
        makeNewCSV(deathData, outputDeathFile);
        makeNewCSV(avgNewCaseData, outputAvgNewCaseFile);
        makeNewCSV(avgNewDeathData, outputAvgNewDeathFile);
        allUSAChart();
    }

    public void allUSAChart()
    {
        totalUSCases = new int[newCaseData.length][3];
        totalCACases = new int[newCaseData.length][3];

        for(int day = 0; day < totalUSCases.length; day++)
        {
            totalUSCases[day][0] = day;
            totalCACases[day][0] = day;

            int totalNewCases = 0;

            for(int state = 0; state < newCaseData[0].length; state++)
            {
                totalNewCases += newCaseData[day][state];
            }

            totalUSCases[day][1] = totalNewCases;
            totalCACases[day][1] = newCaseData[day][6]; //6 is CA fips
        }

        for(int day = 3; day < totalUSCases.length - 3; day++)
        {
            int USsum = 0;
            int CAsum = 0;

            for(int i = -3; i <= 3; i++)
            {
                USsum += totalUSCases[day + i][1];
                CAsum += totalCACases[day + i][1];
            }
            totalUSCases[day][2] = USsum/7;
            totalCACases[day][2] = CAsum/7;
        }

        makeSmallerCSV(totalUSCases, outputUSNewCaseFile);
        makeSmallerCSV(totalCACases, outputCANewCaseFile);
    }

    private void makeSmallerCSV(int[][] data, String outputFile)
    {
        try
        {
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));

            String header = "Date, Total New Cases, 7-day average\n";
            bw.append(header);

            for(int i = 0; i < data.length; i++)
            {
                String line = DataUtil.timeToDate(DataUtil.daysSinceStartToDate(data[i][0])) + SEP + data[i][1] + SEP + data[i][2];

                bw.append(line + "\n");
            }

            bw.close();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }

        System.out.println(new Date() + ": Relevant data has been output to file " + outputFile + ".");
    }

    private void makeNewCSV(int[][] data, String outputFile)
    {
        try
        {
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));

            String header = "Name";
            for(int i = 0; i < data.length; i++)
            {
                header += SEP;
                header += DataUtil.timeToDate(DataUtil.daysSinceStartToDate(i));
            }

            bw.append(header + "\n");

            String line;

            DataUtil.readFips(fipsFile);
            for(int i = 0; i < data[0].length; i++)
            {
                if(!DataUtil.checkValidFips(i))
                    continue;

                line = DataUtil.getStateByFips(i);

                for(int j = 0; j < data.length; j++)
                {
                    line += SEP;
                    line += data[j][i];
                }

                bw.append(line + "\n");
            }

            bw.close();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }

        System.out.println(new Date() + ": Relevant data has been output to file " + outputFile + ".");
    }

    public int[][] getCaseData()
    {
        return caseData;
    }

    public int[][] getDeathData()
    {
        return deathData;
    }

}
