import java.io.*;
import java.util.*;

public class DataUtil
{
    private static final GregorianCalendar START_DATE = new GregorianCalendar(2020, Calendar.JANUARY, 21);
    private static final int MS_IN_DAY = 86400000;

    private static HashMap<Integer, String> fipsMap;

    public static long getStartDate()
    {
        return START_DATE.getTimeInMillis();
    }

    public static int daysSinceStart(String year, String month, String day)
    {
        GregorianCalendar endDate = new GregorianCalendar(Integer.parseInt(year),
                (Integer.parseInt(month) - 1), Integer.parseInt(day));

        long time = endDate.getTimeInMillis() - START_DATE.getTimeInMillis();

        return (int) (time / MS_IN_DAY);
    }

    public static int daysSinceStart(Date date)
    {
        long time = date.getTime() - START_DATE.getTimeInMillis();

        return (int) (time / MS_IN_DAY);
    }

    public static long daysSinceStartToDate(int days)
    {
        long addedTime = ((long) (days)) * ((long) MS_IN_DAY);

        return START_DATE.getTimeInMillis() + addedTime;
    }

    public static String timeToDate(long time)
    {
        Date date = new Date(time);
        String[] dateString = date.toString().split(" ");

        return dateString[1] + " " + dateString[2] + " " + dateString[5];
    }

    public static boolean checkValidFips(int fips)
    {
        return fipsMap.containsKey(fips);
    }

    public static void readFips(String filename)
    {
        fipsMap = new HashMap<>();

        try
        {
            BufferedReader fipsReader = new BufferedReader(new FileReader(filename));

            fipsReader.readLine();

            String row;
            while((row = fipsReader.readLine()) != null)
            {
                String[] info = row.split(", ");

                int code = Integer.parseInt(info[1]);
                String state = info[0];

                fipsMap.put(code, state);
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public static String getStateByFips(int fips)
    {
        return fipsMap.get(fips);
    }

    public static int getMaxFips()
    {
        int max = 0;

        for(int i : fipsMap.keySet())
        {
            if (i > max)
                max = i;
        }

        return max;
    }

}
