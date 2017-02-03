package co.colector.utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 *
 * @author ma0
 */
public class Parser {



    String expression;

    public Parser()
    {

    }

    public Parser(String expression)
    {
        this.expression = expression;
    }

    public void setExpression(String expression)
    {
        this.expression = expression;
    }


    public double eval()
    {
        double result = 0.0;

        LinkedList<String> list = new LinkedList(Arrays.asList(expression.split(" ")));
        try{
            result = evalExp(list);
        }catch(Exception e)
        {
            e.printStackTrace();
        }

        return result;
    }

    private boolean isPrimitive(String p)
    {
        switch(p)
        {
            case "+":
                return true;
            case "-":
                return true;
            case "*":
                return true;
            case "/":
                return true;
            default:
                return false;
        }
    }

    private boolean isNumber(String p)
    {
        try{
            Double.parseDouble(p);
            return true;
        }catch(NumberFormatException nfe)
        {
            return false;
        }
    }

    private boolean isVariable(String p)
    {
        return p.startsWith("#");
    }

    private String getVariable(String p)
    {
        return p.replace("#", "");
    }

    private double evalPrimitive(String tk, double x, double y) throws Exception
    {

        if (tk.equals("+"))
        {
            return x + y;
        }
        else if (tk.equals("-"))
        {
            return x - y;
        }
        else if (tk.equals("*"))
        {
            return x * y;
        }
        else if (tk.equals("/"))
        {
            return x / y;
        }
        else
        {
            throw new Exception();
        }
    }

    public double evalExp(LinkedList<String> list) throws Exception
    {

        String first_arg = list.pop();
//        System.out.println(list);
        if (isPrimitive(first_arg))
        {
            double second_arg = evalExp(list);
            double third_arg = evalExp(list);
            return evalPrimitive(first_arg, second_arg, third_arg);
        }
        else if (isNumber(first_arg))
        {
            return Double.parseDouble(first_arg);
        }
        else
        {
            throw new Exception();
        }
    }

    public LinkedList<String> extractVariables(String expression)
    {
        LinkedList<String> variables = new LinkedList<>();

        String[] list = expression.split(" ");
        for (String s: list)
        {
            if (isVariable(s))
            {
                variables.add(getVariable(s));
            }
        }
        return variables;
    }

}
