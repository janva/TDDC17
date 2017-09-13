public class StateAndRewardStrait extends StateAndReward{
	//NUMBER OF STATES but in this version we never run out of bounds so should 
	// realy use the other version
	final int STATES_CONSTANT = 10;
	final double MIDDLE =4.5;
	@Override
	public String getState(double angle, double vx, double vy) {
		
		// TODO Auto-generated method stub
		int stateDiscrete = discretize2(angle, STATES_CONSTANT, -3.14159,3.14159 );
		String state="ERROR";
		/* TODO: IMPLEMENT THIS FUNCTION  MAYBE ADD HARDCODED VALUE FOR ANGLE 0 with highest utility exact*/
//		if ((angle >=-0.1) && (angle <= 0.1))
//		{
//			state = "angleState_superior";
//		}else
		{
			state = "angleState_"+stateDiscrete;
		}
		return state;
	}

	@Override
	public double getReward(double angle, double vx, double vy) {
		double reward = 0;
		//cheeting narrowing it down further :-) moa ha ha ha
//		if ((angle >=-0.1) && (angle <= 0.1))
//		{
//			reward  =1;
//		}else
		{
		int stateDiscrete = discretize2(angle, STATES_CONSTANT, -3.14159,3.14159 );
		reward =Math.pow((1/Math.abs(stateDiscrete-MIDDLE)), 2.0);
		}
//		switch (stateDiscrete) {
//		case 1:
//			//reward = - 1.0;
//			reward = 0;
//			break;
//		case 2:
//			reward = 0.2;
//			break;
//		case 3:
//			reward = 0.4;
//			break;
//		case 4:
//			//cases 3 and for are eqaualy desirble
//			reward = 0.7;
//			break;
//		case 5:
//			reward = 0.7;
//			break;
//		case 6:
//			reward = 0.4;
//			break;
//		case 7:
//			reward = 0.2;
//			break;
//		case 8:
//			reward = 0;
//			break;
//		}
//		}
		return reward;
	}
}
