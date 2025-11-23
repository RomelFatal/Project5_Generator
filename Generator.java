/**
 * UNIVERSITY OF FLORIDA
 * ---------------------------------------------------
 * COP 4020 - PROGRAMMING LANGUAGE CONCEPTS
 * FALL 2025
 * November 12, 2025,
 * Project_Part5 - The Generator
 * Written by: Romel Fatal and Yaroslav Voryk
 ********************************************************************* */

package plc.project;

// Packages needed for project to run properly
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class Generator implements Ast.Visitor<Void> {

    private final PrintWriter writer;
    private int indent = 0;

    public Generator(PrintWriter writer) {
        this.writer = writer;
    }

    private void print(Object... objects) {
        for (Object object : objects) {
            if (object instanceof Ast) {
                visit((Ast) object);
            } else {
                writer.write(object.toString());
            }
        }
    }

    private void newline(int indent) {
        writer.println();
        for (int i = 0; i < indent; i++) {
            writer.write("    ");
        }
    }
//---------------------------------------- TEST PASSED: DEFAULT -----------------------------------------------


    // Part 1: Generate source file for Java like program
    @Override
    public Void visit(Ast.Source ast) {
        print("public class Main {");
        indent++;
        newline(0);

        if (!ast.getFields().isEmpty()) {
            for (Ast.Field fields : ast.getFields()) {
                newline(indent);
                print(fields);
            }
            newline(0); // blank line
        }

        // Print the main method and main instance
        newline(indent);
        print("public static void main(String[] args) {");
        indent++;
        newline(indent);
        print("System.exit(new Main().main());");
        indent--;
        newline(indent);
        print("}");
        newline(0);

        // User defined methods
        for (Ast.Method methods : ast.getMethods()) {
            newline(indent);
            print(methods);
        }
        // Add a blank line
        newline(0);

        indent--;
        newline(indent);
        print("}");


        return null;
        //throw new UnsupportedOperationException(); //TODO
    }
//------------------------------------------ TEST PASSED ---------------------------------------------------


    // Part 2: Java primitive types
    @Override
    public Void visit(Ast.Field ast) {
        // Print Java type name
        String javaType;

        switch (ast.getTypeName()) {
            case "Integer": javaType = "int"; break;
            case "Decimal": javaType = "double"; break;
            case "Boolean": javaType = "boolean"; break;
            case "Character": javaType = "char"; break;
            case "String": javaType = "String"; break;
            default:        javaType = ast.getTypeName();
        }

        // Print the types
        print(javaType + " " + ast.getName());

        // Check if the field has an initial value
        if (ast.getValue().isPresent()) {
            print(" = " + ast.getValue().get());
        }

        // End the declaration
        print(";");


        return null;
        //throw new UnsupportedOperationException(); //TODO
    }
//------------------------------------------ TEST PASSED ---------------------------------------------------


    // Part 3: Java methods generator
    @Override
    public Void visit(Ast.Method ast) {
        print(ast.getFunction().getReturnType().getJvmName() + " " + ast.getName());
        //print(" " + ast.getName());
        //print(ast.getName());
        print("(");

        for (int i = 0; i < ast.getParameters().size(); i++) {
            //print(ast.getParameterTypeNames().get(i));
            print(ast.getParameterTypeNames().get(i) + " " + ast.getParameters().get(i));
            //print(ast.getParameters().get(i));
            if (i != ast.getParameters().size() - 1) {
                print(", ");
            }
        }

        print(") {");
        if (!ast.getStatements().isEmpty()) {
            indent++;
            for (Ast.Statement statements : ast.getStatements()) {
                newline(indent);
                print(statements);
            }
            indent--;
            newline(indent);
        }
        print("}");

        //for (Ast.Method methods : ast.getMethods())
        return null;
        //throw new UnsupportedOperationException(); //TODO
    }
//------------------------------------------ TEST PASSED ---------------------------------------------------


    // Part 4: Process expression statements
    @Override
    public Void visit(Ast.Statement.Expression ast) {
        print(ast.getExpression());
        print(";");


        return null;
        //throw new UnsupportedOperationException(); //TODO
    }
//------------------------------------------ TEST PASSED ---------------------------------------------------


    // Part 5: Process variable declaration types
    @Override
    public Void visit(Ast.Statement.Declaration ast) {
        // Print the type of variable name
        print(ast.getVariable().getType().getJvmName() + " " +
                ast.getVariable().getJvmName());

        // If there is a value, print the initialization
        if (ast.getValue().isPresent()) {
            print(" = ");
            print(ast.getValue().get());
        }
        print(";");


        return null;
        //throw new UnsupportedOperationException(); //TODO
    }
//------------------------------------------ TEST PASSED ---------------------------------------------------



    // Part 6: Process assignment statements
    @Override
    public Void visit(Ast.Statement.Assignment ast) {
        print(ast.getReceiver());
        print(" = ");
        print(ast.getValue());
        print(";");


        return null;
        //throw new UnsupportedOperationException(); //TODO
    }
//------------------------------------------ TEST PASSED ---------------------------------------------------


    // Part 7: Process logical statements
    @Override
    public Void visit(Ast.Statement.If ast) {
        // Print the opening part of the "if" statement
        // and display the condition inside parentheses
        print("if (");
        print(ast.getCondition());
        print(") {");
        indent++;

        // Print each statement in the "then" block of the "if" statement
        for (Ast.Statement statement: ast.getThenStatements()) {
            newline(indent);
            print(statement);
        }
        indent--;
        newline(indent);
        print("}");

        if (!ast.getElseStatements().isEmpty()) {
            print(" else {");
            indent++;

            for (Ast.Statement statement : ast.getElseStatements()) {
                newline(indent);
                print(statement);
            }
            indent--;
            newline(indent);
            print("}");
        }


        return null;
        //throw new UnsupportedOperationException(); //TODO
    }
//------------------------------------------ TEST PASSED ---------------------------------------------------


    // Part 8: Java "for" loop style
    @Override
    public Void visit(Ast.Statement.For ast) {
        print("for ( ");
        if (ast.getInitialization() != null) {
            print(ast.getInitialization());
            print(" ");
        } else {
            print("; ");
        }

        print(ast.getCondition());
        print("; ");

        // Trailing semicolon ";" inside the increment section of the
        // "for" loop
        if (ast.getIncrement() != null) {
            print(ast.getIncrement());
            print(" ");
        } else {
            print("");
        }


        print(") {");
        if (ast.getStatements().isEmpty()) {
            print("}");  // Close on the same line immediately if no statements
        } else {
            indent++;
            for (Ast.Statement statements : ast.getStatements()) {
                newline(indent);
                print(statements);
            }
            indent--;
            newline(indent);
            print("}");
        }


        return null;
        //throw new UnsupportedOperationException(); //TODO
    }
//------------------------------------------ TEST NOT PASSED ---------------------------------------------------


    // Part 9:
    @Override
    public Void visit(Ast.Statement.While ast) {
        //print("while (");
        //print(ast.getCondition());
        //print("{");
        print("while (" + ast.getCondition() + "{" + "}");
        if (ast.getStatements().isEmpty()) {
            print("}");
        }
        else {
            indent++;
            for (int i = 0; i < ast.getStatements().size(); i++) {
                newline(indent + 1);
                print(ast.getStatements().get(i));
            }
            indent--;
            newline(indent);
            print("}");
        }


        return null;
        //throw new UnsupportedOperationException(); //TODO
    }
//------------------------------------------ TEST PASSED ---------------------------------------------------


    // Part 10: Process a return statement
    @Override
    public Void visit(Ast.Statement.Return ast) {
        // Process the return values and print "return"
        print("return ");
        print(ast.getValue());
        print(";");


        return null;
        //throw new UnsupportedOperationException(); //TODO
    }
//------------------------------------------ TEST PASSED ---------------------------------------------------


    // Part 11: Process various types of characters
    @Override
    public Void visit(Ast.Expression.Literal ast) {
        // Handles character literals
        if (ast.getType() == Environment.Type.CHARACTER) {
            print("'" + ast.getLiteral() + "'");
        } else if (ast.getType() == Environment.Type.STRING) {
            // Handles string literals
            print("\"" + ast.getLiteral() + "\"");
        } else if (ast.getType() == Environment.Type.DECIMAL) {
            // Handles decimal literals
            BigDecimal decimal = (BigDecimal) ast.getLiteral();
            print(decimal.doubleValue());
        } else if (ast.getType() == Environment.Type.INTEGER) {
            // Handles integer literals
            BigInteger integer = (BigInteger) ast.getLiteral();
            print(integer.intValue());
        } else {
            // Handle any other type of characters
            print(ast.getLiteral());
        }


        return null;
        //throw new UnsupportedOperationException(); //TODO
    }
//------------------------------------------ TEST PASSED ---------------------------------------------------


    // Part 12: Print a group of expression
    @Override
    public Void visit(Ast.Expression.Group ast) {
        // Method to print a group of expression inside parentheses
        print("(");
        print(ast.getExpression());
        print(")");


        return null;
        //throw new UnsupportedOperationException(); //TODO
    }
//------------------------------------------ TEST PASSED ---------------------------------------------------


    // Part 13: Process binary logical expressions
    @Override
    public Void visit(Ast.Expression.Binary ast) {
        // Get the left expression then add a space
        print(ast.getLeft());
        print(" ");

        // Convert to Java logical operators
        String operator = ast.getOperator();
        if ("AND".equals(operator)) {
            print("&&");
        } else if ("OR".equals(operator)) {
            print("||");
        } else {
            print(operator);
        }

        // Add a space then get the right expression
        print(" ");
        print(ast.getRight());


        return null;
        //throw new UnsupportedOperationException(); //TODO
    }
//------------------------------------------ TEST PASSED ---------------------------------------------------


    // Part 14: Checks if the access is qualified
    @Override
    public Void visit(Ast.Expression.Access ast) {
        // Find out if the receiver exist and show it
        if (ast.getReceiver().isPresent()) {
            print(ast.getReceiver().get());
            print(".");
        }

        print(ast.getName());


        return null;
        //throw new UnsupportedOperationException(); //TODO
    }
//------------------------------------------ TEST PASSED ---------------------------------------------------


    // Part 15: Receiver for function calls
    @Override
    public Void visit(Ast.Expression.Function ast) {
        // Find which type of function being evaluated
            // Standalone (function with no receiver object)
            // Method (function with object receiver)
        if (ast.getReceiver().isPresent()) {
            print(ast.getReceiver().get());
            print(".");
        }

        // Print the function's name
        print(ast.getFunction().getJvmName());
        print("(");

        // Print the argument from the function and if there are
        // multiple arguments, show all of them.
        List<Ast.Expression> arguments = ast.getArguments();
        for (int i = 0; i < arguments.size(); i++) {
            print(arguments.get(i));
            if (i < arguments.size() - 1) {
                print(", ");
            }
        }

        // Close the function's call parentheses
        print(")");


        return null;
        //throw new UnsupportedOperationException(); //TODO
    }
//------------------------------------------ TEST PASSED ---------------------------------------------------
}
