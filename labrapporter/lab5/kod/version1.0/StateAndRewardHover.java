/*For example, the angle seems to be the most important sensor variable and as many discrete values as 
 * possible should be assigned to it during the discretization. The horizontal velocity require fewer 
 * values and the vertical velocity will only need a small number of discrete values.*/
public class StateAndRewardHover extends StateAndReward{
	//code duplication
	final int ANGLE_STATES_CONSTANT = 12;//12
	final int HORISONTAL_STATES_CONSTANT = 10;//10
	final int VERTICAL_STATES_CONSTANT = 5;// 11
	@Override
	public String getState(double angle, double vx, double vy) {

		int stateAngleDiscrete = discretize2(angle, ANGLE_STATES_CONSTANT , -3.14159,3.14159 );
		int stateHorizontalVelocityDiscrete = discretize(vx, HORISONTAL_STATES_CONSTANT , -24,24);
		int statevertiVerticalVelocityDiscrete = discretize(vy, VERTICAL_STATES_CONSTANT, -5,5);//-10,10
		
		StringBuilder strBuilder = new StringBuilder();
//		if ((angle >=-0.1) && (angle <= 0.1))
//		{
//			strBuilder.append("bingo_");
//		}else
//		{
			strBuilder.append("angle_");
			strBuilder.append(stateAngleDiscrete);
//		}

//		if ((vx >=-0.1) && (vx <= 0.1))
//		{
//			strBuilder.append("_jackpot_");
//		}else{
			strBuilder.append("_hVelocity_");
			strBuilder.append(stateHorizontalVelocityDiscrete);		
//			}

		if ((vy >=-0.7) && (vy <= 0.7))
		{
			strBuilder.append("_cigar");

		}else
		{
//			strBuilder.append("_vVelocity_SUCKS");
			strBuilder.append("_vVelocity_");
			strBuilder.append(statevertiVerticalVelocityDiscrete );
		}

		return strBuilder.toString();

	}
	/*
	 * A good idea can be to simply come up with separate rewards for each state variable and add them together. 
	 * It is also strongly recommended that you make sure rewards are all positive to make debugging easier
	 *aaa
	 * 
	 * */
	@Override
	public double getReward(double angle, double vx, double vy) {
		/* TODO: IMPLEMENT THIS FUNCTION */
		int stateAngleDiscrete = discretize2(angle,ANGLE_STATES_CONSTANT, -3.14159,3.14159 );
		int stateHorizontalVelocityDiscrete = discretize(vx, HORISONTAL_STATES_CONSTANT, -15,15); //-24,24
		int statevertiVerticalVelocityDiscrete = discretize(vy, VERTICAL_STATES_CONSTANT, -6,6);//-19,29

		double anglePartReward=0.0; 
		double horistontalPartReward=0.0;
		double verticalPartPartReward =0.0;
		double reward = 0;
		//cheeting narrowing it down further :-) moa ha ha ha
//		if ((angle >=-0.1) && (angle <= 0.1))
//		{
//			anglePartReward  =4.5;
//		}else
//		{
			anglePartReward =Math.pow( (1 / Math.abs(stateAngleDiscrete-5.5)), 2.0);//5.5
//		}

// 		if ((vx >=-0.1) && (vx <= 0.1))
//		{
//			horistontalPartReward  =4.0; 
//
//		}else{
			horistontalPartReward=Math.pow( (1 / Math.abs(stateHorizontalVelocityDiscrete-4.5)), 2.0);//4.5
//		}

		if ((vy >=-1.0) && (vy <= 1.0))
		{
			verticalPartPartReward =6;

		}else
		{
			//verticalPartPartReward=0;
		    verticalPartPartReward =Math.pow( (1 / Math.abs(statevertiVerticalVelocityDiscrete-2.5)), 2.0);//5.5
		}
		return verticalPartPartReward + horistontalPartReward +anglePartReward;
		//return 0;
	}

}
