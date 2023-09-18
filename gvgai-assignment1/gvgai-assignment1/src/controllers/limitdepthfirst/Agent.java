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
public class Agent extends AbstractPlayer {
    protected ArrayList<Observation> grid[][];
    protected int block_size;
    public static Types.ACTIONS[] actions;
    public  ArrayList<StateObservation> stateHadExList=new ArrayList<StateObservation>();
    public  ArrayList<Types.ACTIONS> todoact=new ArrayList<Types.ACTIONS>();
    int step=0;
    int steplimits=100;//深度限制
    boolean found=false;
     public void dfs(StateObservation so,int depth){
        for(int i=0;i<stateHadExList.size();i++){
            if(so.equalPosition(stateHadExList.get(i))){
                return;
            }
        }//对于状态so如果它已经在状态成员列表中存在，那么就直接结束当前状态搜索
        if(depth>steplimits){
            return;
        }//如果这一步已经超过了深度限制则直接返回
        stateHadExList.add(so);
        if(so.isGameOver()&&so.getGameWinner()==Types.WINNER.PLAYER_WINS){
            found=true;
            return;
        }//如果已经能结束游戏将最终路径存在设置为true
        else if(so.isGameOver()&&so.getGameWinner()==Types.WINNER.PLAYER_LOSES){
            return;
        }//如果这一步会导致游戏输掉就结束这一步的搜索
        ArrayList<Types.ACTIONS> act = so.getAvailableActions();//获取当前状态能进行的行动
        Types.ACTIONS[] toactions = new Types.ACTIONS[act.size()];
        for(int i = 0; i < toactions.length; ++i)//对能进行的行动进行遍历
        {
            if(found)break;//上一个行动能找到完成路径则直接停止遍历
            toactions[i] = act.get(i);
            todoact.add(toactions[i]);//将本行动加入最终行动列表
            StateObservation stCopy=so.copy();
            stCopy.advance(toactions[i]);//进行该行动，状态转变
            dfs(stCopy,depth+1);//递归搜索，同时深度增加
            if(!found){//这个行动不能完成即回溯
                todoact.remove(todoact.size()-1);//将本行动从最终行动列表中退出
            }
         }
        return;
    }
    public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer)
    {
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
