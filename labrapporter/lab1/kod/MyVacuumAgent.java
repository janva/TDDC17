package tddc17;


import java.util.LinkedList;

import aima.core.environment.liuvacuum.*;
import aima.core.agent.Action;
import aima.core.agent.AgentProgram;
import aima.core.agent.Percept;
import aima.core.agent.impl.*;


class MyAgentState
{
	public int[][] world = new int[20][20];
	public int initialized = 0;
	final int UNKNOWN 	= 0;
	final int WALL 		= 1;
	final int CLEAR 	= 2;
	final int DIRT		= 3;
	final int ACTION_NONE 		= 0;
	final int ACTION_MOVE_FORWARD 	= 1;
	final int ACTION_TURN_RIGHT 	= 2;
	final int ACTION_TURN_LEFT 		= 3;
	final int ACTION_SUCK	 		= 4;

	public int agent_x_position = 1;
	public int agent_y_position = 1;
	public int agent_last_action = ACTION_NONE;

	MyAgentState()
	{
		for (int i=0; i < world.length; i++)
			for (int j=0; j < world[i].length ; j++)
				world[i][j] = UNKNOWN;
		world[1][1] = CLEAR;
		agent_last_action = ACTION_NONE;
	}

	public void updateWorld(int x_position, int y_position, int info)
	{
		world[x_position][y_position] = info;
	}

	public void printWorldDebug()
	{
		for (int i=0; i < world.length; i++)
		{
			for (int j=0; j < world[i].length ; j++)
			{
				if (world[j][i]==UNKNOWN)
					System.out.print(" ? ");
				if (world[j][i]==WALL)
					System.out.print(" # ");
				if (world[j][i]==CLEAR)
					System.out.print(" . ");
				if (world[j][i]==DIRT)
					System.out.print(" D ");
			}
			System.out.println("");
		}
	}
}

class MyAgentProgram implements AgentProgram {

	/**
	 * Interface represents Action from MyagentPrograms perspektiv.
	 * This interface is intended to simplify handling of our actionQueue.
	 * @author janva
	 *
	 */
	interface ActionCommand
	{
		public abstract Action execute();

	}

	class TurnLeftCommand implements ActionCommand
	{
		@Override
		public Action execute() {
			return turnLeft();
		}

		/**
		 * 
		 * @return the Action of left turn.
		 */
		private Action turnLeft ()
		{
			setNewDirection (3);
			state.agent_last_action = state.ACTION_TURN_LEFT;
			return LIUVacuumEnvironment.ACTION_TURN_LEFT;
		}	
	}

	class TurnRightCommand implements ActionCommand
	{
		@Override
		public Action execute() {
			return turnRight();
		}

		private Action turnRight()
		{
			setNewDirection (1);
			state.agent_last_action = state.ACTION_TURN_RIGHT;
			return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
		}
	}

	class ForwardCommand implements ActionCommand
	{
		@Override
		public Action execute() {
			return forward();
		}

		private Action forward()
		{
			state.agent_last_action = state.ACTION_MOVE_FORWARD;
			return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
		}	
	}

	public class MyVacuumAgent extends AbstractAgent 
	{
		public MyVacuumAgent() {
			super(new MyAgentProgram());
		}
	}

	// Here you can define your variables!
	enum STAGE {INIT,VACUM,VACUM_COMPLETE, STOP}

	final int NORTH= 0;
	final int EAST= 1;
	final int SOUTH= 2;
	final int WEST= 3;

	public MyAgentState state;

	//The direction currently are facing.
	private int facing;

	//current stage of cleaning process.
	private STAGE stage;

	//queue of actions that we want to preform in fifo order
	private LinkedList<ActionCommand> actionQueue= new LinkedList<ActionCommand>();

	//Home coordinates
	private int home_x_coord;
	private int home_y_coord; 
	

	public MyAgentProgram() {
		state = new MyAgentState();
		facing = EAST;
		stage = STAGE.INIT;
	}


	@Override
	public Action execute(Percept percept) {

		// This example agent program will update the internal agent state while only moving forward.
		// Note! It works under the assumption that the agent starts facing to the right.
		DynamicPercept p = (DynamicPercept) percept;
		Boolean bump = (Boolean)p.getAttribute("bump");
		Boolean dirt = (Boolean)p.getAttribute("dirt");
		Boolean home = (Boolean)p.getAttribute("home");
		System.out.println("percept: " + p);

		// State update based on the percept value and the last action.
		if (state.agent_last_action==state.ACTION_MOVE_FORWARD)
		{
			if (!bump)
			{
				switch (facing) {
				case NORTH:
					state.agent_y_position--;
					break;
				case SOUTH:
					state.agent_y_position++;
					break;
				case WEST:
					state.agent_x_position--;
					break;
				case EAST:
					state.agent_x_position++;
					break;

				default:
					break;
				}
			} else
			{
				switch (facing) {
				case NORTH:
					state.updateWorld(state.agent_x_position,state.agent_y_position-1,state.WALL);
					break;
				case SOUTH:
					state.updateWorld(state.agent_x_position,state.agent_y_position+1,state.WALL);
					break;
				case WEST:
					state.updateWorld(state.agent_x_position-1,state.agent_y_position,state.WALL);
					break;
				case EAST:
					state.updateWorld(state.agent_x_position+1,state.agent_y_position,state.WALL);
					break;

				default:
					break;
				}
			}
		}
		if (dirt)
			state.updateWorld(state.agent_x_position,state.agent_y_position,state.DIRT);
		else
			state.updateWorld(state.agent_x_position,state.agent_y_position,state.CLEAR);

		state.printWorldDebug();


		//VACUM stage is reached after we have reached the north-west (upper left) corner of the square world.
		//This causes the agent clean the from top to bottom one row at the time.
		if(stage == STAGE.VACUM)
		{
			if(home)
			{
				home_x_coord = state.agent_x_position;
				home_y_coord = state.agent_y_position;
			}

			if (dirt)
			{
				System.out.println("DIRT -> choosing SUCK action!");
				state.agent_last_action=state.ACTION_SUCK;
				return LIUVacuumEnvironment.ACTION_SUCK;
			} 
			//If we bump into something abort currently queued actions.
			if(bump)
				actionQueue.clear();
			//if any actions have been queued then perform them first.
			if(actionQueue.isEmpty())
			{
				if(bump)
				{   //if bumped into something to the south then we 
					//must have reached the bottom
					if(facing== SOUTH)
					{
						stage = STAGE.VACUM_COMPLETE;
					}else{ 
						//queue actions to goto next row
						turnTo(SOUTH);
						actionQueue.add(new ForwardCommand());
						if(facing== EAST)
							actionQueue.add(new TurnRightCommand());
						if(facing== WEST)
							actionQueue.add(new TurnLeftCommand());
					}
				}
				else 
					actionQueue.add(new ForwardCommand());	
			}
		}
		//Sucking upp dirt at this stage is a waste we will come every square later.
		//We start up by moving agent to north west corner.
		if(stage == STAGE.INIT )
		{
			//moment 22 do queued actions first.
			if(actionQueue.isEmpty())
			{// 1) turn in northerly direction
				if(facing == EAST)
					turnTo(NORTH);

				if(bump) 
				{
					if(facing == WEST)
					{	
						stage= STAGE.VACUM;
						turnTo(EAST);
					}	
					if(facing == NORTH)
					{
						turnTo(WEST);
					}
				}else
					actionQueue.add(new ForwardCommand());
			}
		}

		//we should be in home square shutdown the agent
		if(actionQueue.isEmpty())
			if (stage == STAGE.STOP)
			{
				state.agent_last_action=state.ACTION_NONE;
				return NoOpAction.NO_OP;
			}

		//All cleaning has been done no more dirt. go home.
		if (stage == STAGE.VACUM_COMPLETE)
		{
			goHome();
			stage = stage.STOP;
		}

		//as long as the actionQueue is not empty just just preform first action
		// in queue
		if(!actionQueue.isEmpty())
		{
			ActionCommand command =actionQueue.getFirst();
			actionQueue.removeFirst();
			return command.execute();
		}

		//shall never reach this statement
		state.agent_last_action=state.ACTION_NONE;
		return NoOpAction.NO_OP;
	}

	/**
	 * Helper method calculates the direction we are facing after having turned some way. 
	 * The directions NORTH..WEST are simply represented by integers 0..3 this makes it 
	 * easy to calculate the new direction we are facing after 
	 * turning.  
	 * @param previousDirection the direction we were facing before turning. 
	 */
	private void setNewDirection (int  previousDirection)
	{
		facing = (facing + previousDirection )%4;
	}

	/**
	 * Helper method turns agent so that it face a certain direction given it's current position. 
	 * 
	 * Note it works under the assumption that the turn is to be taken as next action given 
	 * current position and current facing direction.It's not able to make prediction for future turns
	 *  
	 * @param direction we want to face after turning.
	 */
	private void turnTo(int direction) 
	{
		//TIHI totally not called for :-). not readable not effective....
		int diff= ((Math.abs((facing+1) - direction))%4);

		switch (diff) {
		case 0:
			actionQueue.add(new TurnRightCommand());
			break;
		case 2:
			actionQueue.add(new TurnLeftCommand());
			break;

		default:
			actionQueue.add(new TurnRightCommand());
			actionQueue.add(new TurnRightCommand());
			break;
		}
	}

	/**
	 * Helper method to calculate path home. Simply figures how many
	 * squares in southerly/northerly and  westerly/easterly direction
	 * differs from our current position and queue up needed actions
	 * to be taken to reach home square.
	 */
	private void goHome() 
	{
		int diff_x = state.agent_x_position - home_x_coord ;


		if (diff_x >= 0)
			turnTo(WEST);
		else
			turnTo(EAST);

		diff_x = Math.abs(diff_x);
		while (diff_x-- > 0)
			actionQueue.add(new ForwardCommand());


		int diff_y = state.agent_y_position - home_y_coord;
		if (diff_y > 0)
			if(facing == EAST)
				actionQueue.add(new TurnLeftCommand());
			else
				actionQueue.add(new TurnRightCommand());


		diff_y = Math.abs(diff_y);
		while (diff_y-- > 0)
			actionQueue.add(new ForwardCommand());
	}
}



