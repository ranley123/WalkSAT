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
            assignment.add(assignmentMap.get(literals.get(i)));
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

    public void flipAt(int var){
        int index = 0;
        if(literals.contains(var)){
            index = literals.indexOf(var);
            int curAssignment = assignment.get(index);
            if(curAssignment == 1){
                assignment.set(index, 0);
                numSatisfiedLiterals--;
            }
            else{
                assignment.set(index, 1);
                numSatisfiedLiterals++;
            }
        }
        else if(literals.contains(-1 * var)){
            index = literals.indexOf(-1 * var);
            int curAssignment = assignment.get(index);
            if(curAssignment == 1){
                assignment.set(index, 0);
                numSatisfiedLiterals--;
            }
            else{
                assignment.set(index, 1);
                numSatisfiedLiterals++;
            }
        }

//        updateNumSatisfiedLiterals();
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

    public void print(){
        StringBuilder sb = new StringBuilder();
        for(Integer i: this.getLiterals()){
            sb.append(i + ", ");
        }
        System.out.println(sb.toString());
    }

}
