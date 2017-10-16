import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

public class Value extends ArrayList<String>
{
    WordStream toWordStream()
    {
        return new WordStream(toArray(new String[0]));
    }

    Value()
    {
        super();
    }

    Value(String value)
    {
        this();
        this.add(value);
    }

    Value(String first, WordStream stream)
    {
        if(first.startsWith("["))
        {
            int count = 1;
            first = first.substring(1);
            while(count>0)
            {
                if(first.length() == 0)
                {
                    first = stream.next();
                    continue;
                }
                if(first.startsWith("["))
                {
                    add("[");
                    count++;
                    first = first.substring(1);
                    continue;
                }
                if(first.endsWith("]"))
                {
                    if(count>1)
                    {
                        int end = first.indexOf(']');
                        if(first.length() > 1)
                            add(first.substring(0, end));
                        end = first.length() - end;
                        while(end-- > 0)
                        {
                            if(count > 1)
                                add("]");
                            count--;
                        }
                        if(count >0 )
                            first = stream.next();
                        continue;
                    }
                    if(count == 1)
                    {
                        if(first.length()>1)
                            add(first.substring(0, first.length()-1));
                        count--;
                        continue;
                    }
                }
                add(first);
                first = stream.next();
            }
        }
    }

    Value(String[] values)
    {
        super(Arrays.asList(values));
    }

    public Value clone()
    {
        return (Value) super.clone();
    }

    private void checkSizeForValue(String targetType)
            throws RunningException
    {
        if(size() == 0)
            throw new RunningException("Cannot convert null to "+targetType+".");
        else if(size() > 1)
            throw new RunningException("Cannot convert list to "+targetType+".");
    }

    Boolean toBoolean()
            throws RunningException
    {
        checkSizeForValue("boolean");
        String value = get(0);
        switch(value){
            case "true":
                return true;
            case "false":
                return false;
            default:
                throw new RunningException("Cannot convert " + value + " to boolean");
        }
    }

//    public int toInt()
//            throws RunningException, NumberFormatException
//    {
//        checkSizeForValue(1, "integer");
//        String value = get(0);
//        return Integer.parseInt(value);
//    }
//
//    public double toFloat()
//            throws RunningException, NumberFormatException
//    {
//        checkSizeForValue(1, "float");
//        String value = get(0);
//        return Double.parseDouble(value);
//    }

    BigDecimal toNumeric()
            throws RunningException, NumberFormatException
    {
        checkSizeForValue("numeric");
        String value = get(0);
        return new BigDecimal(value);
    }

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        for (String value: this)
        {
            result.append(value).append(" ");
        }
        return size()>1 ? "[ " + result.toString().trim() + " ]" : result.toString().trim();
    }
}