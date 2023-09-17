package controllers.limitdepthfirst;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;
import java.lang.Math;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

/**
 * Created with IntelliJ IDEA.
 * User: ssamot
 * Date: 14/11/13
 * Time: 21:45
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
class Node {
    Node parent =null;
    StateObservation state=null;
}
public class Agent extends AbstractPlayer {

    /**
     * Random generator for the agent.
     */
    protected Random randomGenerator;

    /**
     * Observation grid.
     */
    protected ArrayList<Observation> grid[][];

    /**
     * block size
     */
    protected int block_size;
    public static Types.ACTIONS[] actions;
    public  ArrayList<StateObservation> stateHadExList=new ArrayList<StateObservation>();
    public  ArrayList<Types.ACTIONS> todoact=new ArrayList<Types.ACTIONS>();
    int step=0;
    int steplimits=10000;
    boolean found=false;
    /**
     * Public constructor with state observation and time due.
     * @param so state observation of the current game.
     * @param elapsedTimer Timer for the controller creation.
     */
     public void dfs(StateObservation so,int depth){
        for(int i=0;i<stateHadExList.size();i++){
            if(so.equalPosition(stateHadExList.get(i))){
                return;
            }
        }
        if(depth>=steplimits){
            return;
        }
        stateHadExList.add(so);
        if(so.isGameOver()&&so.getGameWinner()==Types.WINNER.PLAYER_WINS){
            found=true;
            return;
        }
        else if(so.isGameOver()&&so.getGameWinner()==Types.WINNER.PLAYER_LOSES){
            return;
        }
        ArrayList<Types.ACTIONS> act = so.getAvailableActions();
        Types.ACTIONS[] toactions = new Types.ACTIONS[act.size()];
        for(int i = 0; i < toactions.length; ++i)
        {
            if(found)break;
            toactions[i] = act.get(i);
            todoact.add(toactions[i]);
            StateObservation stCopy=so.copy();
            stCopy.advance(toactions[i]);
            dfs(stCopy,depth+1);
            if(!found){
                todoact.remove(todoact.size()-1);
            }
         }
        return;
    }
    public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer)
    {
        randomGenerator = new Random();
        grid = so.getObservationGrid();
        block_size = so.getBlockSize();
        ArrayList<Types.ACTIONS> act = so.getAvailableActions();
        actions = new Types.ACTIONS[act.size()];
        for(int i = 0; i < actions.length; ++i)
        {
            actions[i] = act.get(i);
        }
        dfs(so,0);
    }
   

    /**
     * Picks an action. This function is called every game step to request an
     * action from the player.
     * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
     * @return An action for the current state
     */
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        Types.ACTIONS toact=todoact.get(step);
        step++;
        return toact;
    }

    /**
     * Prints the number of different types of sprites available in the "positions" array.
     * Between brackets, the number of observations of each type.
     * @param positions array with observations.
     * @param str identifier to print
     */
    private void printDebug(ArrayList<Observation>[] positions, String str)
    {
        if(positions != null){
            System.out.print(str + ":" + positions.length + "(");
            for (int i = 0; i < positions.length; i++) {
                System.out.print(positions[i].size() + ",");
            }
            System.out.print("); ");
        }else System.out.print(str + ": 0; ");
    }

    /**
     * Gets the player the control to draw something on the screen.
     * It can be used for debug purposes.
     * @param g Graphics device to draw to.
     */
    public void draw(Graphics2D g)
    {
        int half_block = (int) (block_size*0.5);
        for(int j = 0; j < grid[0].length; ++j)
        {
            for(int i = 0; i < grid.length; ++i)
            {
                if(grid[i][j].size() > 0)
                {
                    Observation firstObs = grid[i][j].get(0); //grid[i][j].size()-1
                    //Three interesting options:
                    int print = firstObs.category; //firstObs.itype; //firstObs.obsID;
                    g.drawString(print + "", i*block_size+half_block,j*block_size+half_block);
                }
            }
        }
    }
}
