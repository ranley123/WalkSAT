import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Helper {
    public static int numVar = 0; // number of variables in the file
    public static int numClause = 0; // number of clauses in the file
    public static ArrayList<Clause> clauses = new ArrayList<>(); // all clauses read from file

    /**
     * reads clauses from the file into global variables
     * @param filename - the file to be read
     */
    public static void readFile(String filename){
        try{
//            InputStream in = Helper.class.getResourceAsStream(filename);
            InputStream in = new FileInputStream(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            int index = 0;

            // read line by line
            while((line = reader.readLine()) != null){
                if(line.length() == 0)
                    continue;
                line = line.strip();
                String[] words = line.split("\\s+");
                if(words[0].equals("c")){ // extra information so don't need to be processed
                    continue;
                }
                else if(words[0].equals("p")){
                    numVar = Integer.parseInt(words[2]);
                    numClause = Integer.parseInt(words[3]);
                }
                else{ // construct a new clause
                    if(words.length != 4)
                        break;
                    Clause curClause = new Clause(index++);
                    for(String word: words){
                        int num = Integer.parseInt(word);
                        if(num != 0)
                            curClause.addLiteral(num);
                    }
                    clauses.add(curClause);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * to verify a solution, iterate over each clause to see if the solution can satisfy it
     * @param solution  - a solution assignment map searched by algorithm
     * @param clauses   - all clauses
     * @return          - true if the solution is verified
     */
    public static boolean verifier(HashMap<Integer, Integer> solution, ArrayList<Clause> clauses){
        // each clause needs to be satisfied by solution
        for(Clause clause: clauses){
            if(is_satisfied(solution, clause) == false){
                return false;
            }
        }
        return true;
    }

    /**
     * given a solution assignment, check if a clause is satisfied
     * @param solution  - a solution assignment map searched by algorithm
     * @param clause    - the current clause to be checked
     * @return          - true if current clause is satisfied
     */
    public static boolean is_satisfied(HashMap<Integer, Integer> solution, Clause clause){
        // check if there is a literal is true according to the assignment map
        for(Integer i: clause.getLiterals()){
            if(solution.get(i) == 1)
                return true;
        }
        return false;
    }

    /**
     * initialise an assignment map by assigning random 1 or 0 to each variable
     * not x should be opposite to x
     * @param assignmentMap
     * @param numVar
     */
    public static void initialiseAssignmentMap(HashMap<Integer, Integer> assignmentMap, int numVar){
        Random random = new Random();
        // assign random values
        for(int i = 1; i < numVar + 1; i++){
            int temp = random.nextInt(2);
            assignmentMap.put(i, temp);
        }
        // assign corresponding values to negative variables
        for(int i = -1 * numVar; i < 0; i++){
            assignmentMap.put(i, assignmentMap.get(-1 * i) == 1? 0 : 1);
        }
    }

    /**
     * initialise clauses' assignment arraylist according to the current assignment map
     * @param clauses       - a list of clauses to be updated
     * @param assignmentMap - the assignment map mapping variables to their truth assignments
     */
    public static void initialiseClauseAssignment(ArrayList<Clause> clauses, HashMap<Integer, Integer>assignmentMap){
        for(Clause clause: clauses){
            clause.updateAssignment(assignmentMap);
        }
    }

    /**
     *
     * @param numVar                        - the number of variables
     * @param clauses                       - all clauses
     * @param clausesContainingLiteralMap   - the clausesContainingLiteralMap mapping literal to a list of clauses
     *                                      which has this literal
     */
    public static void initialiseClausesContainingLiteral(int numVar, ArrayList<Clause> clauses, HashMap<Integer, ArrayList<Clause>> clausesContainingLiteralMap){
        for(int i = -1 * numVar; i < numVar + 1; i++){
            if(i == 0) // no variable 0
                continue;

            ArrayList<Clause> temp = new ArrayList<>();
            for(Clause clause: clauses){
                if (clause.hasLiteral(i)){
                    temp.add(clause);
                }
            }
            clausesContainingLiteralMap.put(i, temp);
        }
    }

    /**
     * update the false clause list
     * @param clauses   - all clauses
     * @return          - a list containing all unsatisfied clauses
     */
    public static ArrayList<Clause> updateFalseClauses(ArrayList<Clause> clauses){
        ArrayList<Clause> falseClauses = new ArrayList<>();
        for(Clause clause: clauses){
            // if there is no satisfied literal
            if(clause.getNumSatisfiedLiterals() == 0){
                falseClauses.add(clause);
            }
        }
        return falseClauses;
    }

    /**
     * randomly choose a false clause
     * @param falseClauses  - a list containing all false clauses
     * @return              - the random picked false clause
     */
    public static Clause getRandomFalseClause(ArrayList<Clause> falseClauses){
        Random random = new Random();
        if(falseClauses.size() == 0)
            return null;
        int index = random.nextInt(falseClauses.size());
        return falseClauses.get(index);
    }
}
