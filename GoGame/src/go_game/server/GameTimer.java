package go_game.server;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import go_game.Player;
import go_game.protocol.Constants4;



public class GameTimer implements Constants4 {
	
	private boolean TimerExceeded;
	private Timer timer;
	private int timeOutInMilliSeconds;
	private int reminderIntervalsMilliSeconds;
	private int reminderRatio = 10;
	private int amountWarnings = 3;
	private Player player;
	

	public GameTimer(int timeOutInSeconds, Player player) {
		this.player = player;
		// Calculate when the reminders will be set
		this.timeOutInMilliSeconds = timeOutInSeconds * 1000;
		this.reminderIntervalsMilliSeconds = timeOutInMilliSeconds / reminderRatio;
		
		// Set up timer
		TimerExceeded = false;
		System.out.println("Schedule set on" + new Date() + " for " + timeOutInSeconds + " seconds \n");
        timer = new Timer();
        timer.schedule(new RemindTask(),
                       0,        //initial delay
                       reminderIntervalsMilliSeconds);  //subsequent rate
	}
	
	class RemindTask extends TimerTask {
		private int periodsLeft = reminderRatio;

        public void run() {
//        	System.out.println("Periods left: " + periodsLeft);
            if (periodsLeft <= amountWarnings && periodsLeft > 0) {
                
                notification();
                amountWarnings--;
            } else if (periodsLeft <= 0){
                completeTask();
                TimerExceeded = true;
            }
            
            periodsLeft--;
        }
    }

	private void notification() {
		player.sentMessage(CHAT + DELIMITER + "Time left: " + ((reminderIntervalsMilliSeconds * amountWarnings)/1000) + " seconds\n");
//		System.out.println("Notification sent" + new Date() + "\n");
	}

	private void completeTask() {

		player.sentMessage(CHAT + DELIMITER + "Your thinking time has passed, you lose the game! \n");
		player.setTimedOut(true);
		timer.cancel();

	}

	
	public void setStopTimer() {
		this.TimerExceeded = false;
		timer.cancel();
	}
	
	public boolean getTimeExceeded() {
//		System.out.println("Timer status opgevraagd");
		return this.TimerExceeded;
	}
}

