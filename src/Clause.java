import java.util.ArrayList;
import java.util.HashMap;

public class Clause {
    ArrayList<Integer> literals;
    int id;
    int numSatisfiedLiterals;
    ArrayList<Integer> assignment;

    public Clause(int id){
        this.id = id;
        literals = new ArrayList<>();
        assignment = new ArrayList<>();
        numSatisfiedLiterals = 0;
    }

    public void updateAssignment(HashMap<Integer, Integer> assignmentMap){
        assignment = new ArrayList<>();
        for(int i = 0 ; i < literals.size(); i++){
            int var = literals.get(i);
            if(assignmentMap.containsKey(var)){
                assignment.add(assignmentMap.get(var));
            }
            else{
                assignment.add(assignmentMap.get(var * -1) == 1? 0: 1);
            }
        }
        updateNumSatisfiedLiterals();
    }

    public void updateNumSatisfiedLiterals(){
        int counter = 0;
        for(Integer i: assignment){
            if(i == 1)
                counter++;
        }
        numSatisfiedLiterals = counter;
    }

    public int getNumSatisfiedLiterals(){
        return numSatisfiedLiterals;
    }

    public void addLiteral(int literal){
        literals.add(literal);
    }


    public ArrayList<Integer> getLiterals(){
        return literals;
    }

    public boolean hasLiteral(int literal){
        return literals.contains(literal);
    }
}
