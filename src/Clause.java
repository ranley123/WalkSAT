import java.util.ArrayList;
import java.util.HashMap;

public class Clause {
    ArrayList<Integer> literals;
    int numSatisfiedLiterals;
    ArrayList<Integer> assignment;
    private int id;

    public Clause(int id){
        literals = new ArrayList<>();
        assignment = new ArrayList<>();
        numSatisfiedLiterals = 0;
        this.id = id;
    }

    public int getId(){
        return id;
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

    public int getVarAssignment(int var){
        if(literals.contains(var)){
            int index = literals.indexOf(var);
            return assignment.get(index);
        }
        else{
            return -1;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Clause clause = (Clause) obj;
        return clause.getLiterals().equals(this.getLiterals());
    }

}
