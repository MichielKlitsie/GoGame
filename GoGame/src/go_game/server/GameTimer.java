package go_game.server;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import go_game.Player;
import go_game.protocol.Constants4;



// TODO: Auto-generated Javadoc
/**
 * The Class GameTimer.
 */
public class GameTimer implements Constants4 {
	
	/** The Timer exceeded. */
	private boolean TimerExceeded;
	
	/** The timer. */
	private Timer timer;
	
	/** The time out in milli seconds. */
	private int timeOutInMilliSeconds;
	
	/** The reminder intervals milli seconds. */
	private int reminderIntervalsMilliSeconds;
	
	/** The reminder ratio. */
	private int reminderRatio = 10;
	
	/** The amount warnings. */
	private int amountWarnings = 3;
	
	/** The player. */
	private Player player;
	

	/**
	 * Instantiates a new game timer.
	 *
	 * @param timeOutInSeconds the time out in seconds
	 * @param player the player
	 */
	public GameTimer(int timeOutInSeconds, Player player) {
		this.player = player;
		// Calculate when the reminders will be set
		this.timeOutInMilliSeconds = timeOutInSeconds * 1000;
		this.reminderIntervalsMilliSeconds = timeOutInMilliSeconds / reminderRatio;
		
		// Set up timer
		TimerExceeded = false;
		System.out.println("Schedule set on" + new Date() + " for " + timeOutInSeconds + " seconds" + "\n");
        timer = new Timer();
        timer.schedule(new RemindTask(),
                       0,        //initial delay
                       reminderIntervalsMilliSeconds);  //subsequent rate
	}
	
	/**
	 * The Class RemindTask.
	 */
	class RemindTask extends TimerTask {
		
		/** The periods left. */
		private int periodsLeft = reminderRatio;

        /* (non-Javadoc)
         * @see java.util.TimerTask#run()
         */
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

	/**
	 * Notification.
	 */
	private void notification() {
		player.sentMessage(CHAT + DELIMITER + "Time left: " + ((reminderIntervalsMilliSeconds * amountWarnings)/1000) + " seconds" + "\n");
//		System.out.println("Notification sent" + new Date() + "\n");
	}

	/**
	 * Complete task.
	 */
	private void completeTask() {

		player.sentMessage(CHAT + DELIMITER + "Your thinking time has passed, you lose the game!" + "\n");
		player.setTimedOut(true);
		timer.cancel();

	}

	
	/**
	 * Sets the stop timer.
	 */
	public void setStopTimer() {
		this.TimerExceeded = false;
		timer.cancel();
	}
	
	/**
	 * Gets the time exceeded.
	 *
	 * @return the time exceeded
	 */
	public boolean getTimeExceeded() {
//		System.out.println("Timer status opgevraagd");
		return this.TimerExceeded;
	}
}

