import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Solution for the unification algorithm
 * Made by: Manuela Herrera López and Samuel Palacios Bernate
 */

/**
 * The class unify is made for evaluating expressions, it unifies the expression if there is a complete substitution
 * of the constraints from a file, and it fails when none of the recursive cases can be applied to the line that we are
 * working on.
 * The character for substirution is: "°"
 */
public class Unify {
    /**
     * global Patterns, replace and pattern that we use to define our grammar
     */
    static Pattern replace = Pattern.compile("\\s+");
    static Pattern pattern = Pattern.compile("^(?!^Nat$|^Bool$)[a-zA-Z]([a-zA-Z] | [0-9])*");

    /**
     * the following two arrays are the left and the right side of te constraints
     */
    static ArrayList<String> left;
    static ArrayList<String> right;

    static ArrayList<String> substitutions = new ArrayList<>();

    /**
     * The main method where we call the recursive method unify with right and left, which are the right and left side
     * of all equalities.
     * And also it calls the method readFile, which, as it's name says, read the file that we will work in
     *
     * @param args
     */
    public static void main(String[] args) {
        readFile("cs5.txt");
        unify(right, left);
        finalSubstitutions();
    }

    /**
     * The readFile method works by passing to it, the name of the file we will read, and it just add into two arraylist
     * the left (before "=") and right (after "=") side of the equalities
     *
     * @param name
     */
    static void readFile(String name) {
        left = new ArrayList<>();
        right = new ArrayList<>();
        String line;

        try {
            File file = new File("./src/", name);
            try (Scanner in = new Scanner(file)) {
                while (in.hasNext()) {
                    line = processLine(in.nextLine().trim());
                    if (line.equals("<EOF>")) {
                        return;
                    }
                    StringTokenizer token = new StringTokenizer(line.trim(), "=");
                    left.add(token.nextToken().trim());
                    right.add(token.nextToken().trim());
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }

    }

    /**
     * This method take the "<EOL>" (end of line) from the line parameter and removes it
     *
     * @param linea
     * @return
     */
    private static String processLine(String linea) {
        linea = linea.replace("<EOL>", "");
        return linea.trim();
    }

    /**
     * Here we print the file we've already read
     */
    static void printFile() {
        for (int i = 0; i < left.size(); i++) {
            System.out.println(left.get(i) + " = " + right.get(i));
        }
    }

    /**
     * This one is an auxiliar method for returning a boolean variable which says if the line we've
     * passed has some unnecessary parentheses that can be replaced later
     *
     * @param line
     * @return
     */
    private static Boolean unnecessaryParentheses(String line) {
        boolean supervisor = true;
        boolean in = false;
        int cont = 0;
        if (line.charAt(0) == '(' && line.charAt(line.length() - 1) == ')') {
            in = true;
            for (int i = 1; i < line.length() - 1; i++) {
                if (line.charAt(i) == '(')
                    cont++;
                if (line.charAt(i) == ')')
                    cont--;
                if (cont < 0)
                    supervisor = false;
            }
        }
        return supervisor && cont == 0 && in;
    }

    /**
     * This method is another auxiliar which receives as parameters line and side; line is the expression of a side,
     * the one that is indicated in the side parameter. It find the principal "->" for helping the unify function.
     * it separate the right and the left side of the regex we've already introduced and it adds them to the arrays for
     * unifying later
     *
     * @param line
     * @param side
     */
    public static void findPrincipal(String line, String side) {
        String separatedR = "";
        String separatedL = "";
        String aux;
        for (int i = 0; i < line.length(); i++) {
            int cont = 0;
            if (line.charAt(i) == '-') {
                aux = line.substring(0, i).trim();
                for (int j = 0; j < aux.length(); j++) {
                    if (aux.charAt(j) == '(')
                        cont++;
                    else if (aux.charAt(j) == ')')
                        cont--;
                }
                if (cont == 0) {
                    separatedL = line.substring(0, i).trim();
                    separatedR = line.substring(i + 2).trim();
                    break;
                }
            }
        }
        if (side == "right") {
            substitutions.add(right.get(0) + " ° " + separatedL);
            substitutions.add(right.get(0) + " ° " + separatedR);
            right.remove(0);
            right.add(0, separatedR);
            right.add(0, separatedL);
        } else {
            substitutions.add(left.get(0) + " ° " + separatedL);
            substitutions.add(left.get(0) + " ° " + separatedR);
            left.remove(0);
            left.add(0, separatedR);
            left.add(0, separatedL);
        }
    }

    /**
     * The delete method removes the unnecessary parentheses (the most external ones) by the help from the
     * unnecessaryParentheses method, if the last one returns true; it removes the parentheses for adding later the
     * changed line in one of the arrays
     *
     * @param line
     * @param side
     */
    public static void delete(String line, String side) {
        boolean inRight = false, inLeft = false;
        String auxR;
        String auxL;
        if (side.equalsIgnoreCase("right")) {
            inRight = unnecessaryParentheses(right.get(0));

        } else if (side.equalsIgnoreCase("left")) {
            inLeft = unnecessaryParentheses(line);
        }
        if (inRight) {
            auxR = line.substring(1, line.length() - 1);
            right.remove(0);
            right.add(0, auxR);
        }
        if (inLeft) {
            auxL = line.substring(1, line.length() - 1);
            left.remove(0);
            left.add(0, auxL);
        }
    }

    /**
     * This method returns a list of the free variables, which are the ones that match the pattern we've defined in the
     * global Pattern.
     * it founds the matches with the line we've passed to the method.
     *
     * @param line
     * @return
     */
    public static ArrayList<String> freeVariables(String line) {

        ArrayList<String> freeVar = new ArrayList<>();

        if (line.contains("->")) {
            String[] slittedArray = line.split("->");
            for (String s : slittedArray) {
                Matcher matcher = pattern.matcher(s);
                Matcher matcher2 = replace.matcher(s);
                if (matcher.find()) {
                    freeVar.add(matcher2.replaceAll("\t"));
                }
            }
        }

        return freeVar;
    }

    /**
     * This method prints the substitions that we made and delete the ones that are repeated
     */
    public static void finalSubstitutions() {
        for (int i = 0; i < substitutions.size(); i++) {
            for (int j = 0; j < substitutions.size(); j++) {
                if (substitutions.get(i).equals(substitutions.get(j)))
                    substitutions.remove(j);
            }
        }
        System.out.println(Arrays.toString(substitutions.toArray()));
    }

    /**
     * The unify method is a recursive one, where the two sides of the equality are passed for unifying them.
     * It has 5 possible cases, the first of them is our base case, the one that says if the right side and left side
     * are empty.
     * The other case if for unifying expressions that have the same thing on both sides of the equality, if that happen
     * it removes that line from the array.
     * if we have a variable on the left, and a term on the right, it goes until the third case, where all the
     * ocurrences of the variable are replaced by the term.
     * the fourth case is the inverse of the third one
     * We use the last case for unifying functions on both sides,
     * Here we invoke the auxiliary methods for helping the unification
     *
     * @param right
     * @param left
     */
    public static void unify(ArrayList<String> right, ArrayList<String> left) {


        if (right.isEmpty() && left.isEmpty()) {
            System.out.println("Unifica");

        } else {
            if (right.get(0).equals(left.get(0)) && !(right.get(0).contains("->") && left.get(0).contains("->"))) {
                if (unnecessaryParentheses(right.get(0)))
                    delete(right.get(0), "right");

                if (unnecessaryParentheses(left.get(0)))
                    delete(left.get(0), "left");

                substitutions.add(left.get(0) + " ° " + right.get(0));
                right.remove(0);
                left.remove(0);
                unify(right, left);


            } else if (pattern.matcher(left.get(0)).find() &&
                    !pattern.matcher(left.get(0)).replaceAll(left.get(0).substring(0, 1)).contains("->")
                    && pattern.matcher(right.get(0)).find()
                    && !freeVariables(right.get(0)).contains(pattern.matcher(left.get(0))
                    .replaceAll(left.get(0).substring(0, 1)))
                    && !(right.get(0).contains("->") && left.get(0).contains("->"))) {

                String var = pattern.matcher(right.get(0)).replaceAll(right.get(0).substring(0, 1)).trim();
                String var2 = pattern.matcher(left.get(0)).replaceAll(left.get(0).substring(0, 1)).trim();
                for (int i = 0; i < left.size(); i++) {
                    if (left.get(i).equals(var2)) {
                        String aux = left.get(0).replace(var2, var).trim();
                        substitutions.add(left.get(0) + " ° " + aux);
                        left.remove(i);
                        left.add(0, aux);
                    }
                }
                unify(right, left);


            } else if (pattern.matcher(left.get(0)).find() &&
                    !pattern.matcher(right.get(0)).replaceAll(right.get(0).substring(0, 1)).contains("->")
                    && pattern.matcher(right.get(0)).find() &&
                    !freeVariables(left.get(0)).contains(pattern.matcher(right.get(0))
                            .replaceAll(right.get(0).substring(0, 1)))
                    && !(right.get(0).contains("->") && left.get(0).contains("->"))) {

                String var = pattern.matcher(left.get(0)).replaceAll(left.get(0).substring(0, 1)).trim();
                String var2 = pattern.matcher(right.get(0)).replaceAll(right.get(0).substring(0, 1)).trim();

                for (int i = 0; i < right.size(); i++) {
                    if (right.get(i).equals(var2)) {
                        String aux = right.get(0).replace(var2, var).trim();
                        substitutions.add(right.get(0) + " ° " + aux);
                        right.remove(i);
                        right.add(0, aux);
                    }
                }
                unify(right, left);

            } else if (right.get(0).contains("->") && left.get(0).contains("->")) {
                if (unnecessaryParentheses(right.get(0)))
                    delete(right.get(0), "right");

                if (unnecessaryParentheses(left.get(0)))
                    delete(left.get(0), "left");

                findPrincipal(right.get(0), "right");
                findPrincipal(left.get(0), "left");

                if (unnecessaryParentheses(right.get(0)))
                    delete(right.get(0), "right");

                if (unnecessaryParentheses(left.get(0)))
                    delete(left.get(0), "left");

                unify(right, left);
            } else
                System.out.println("fail");
        }
    }

}
