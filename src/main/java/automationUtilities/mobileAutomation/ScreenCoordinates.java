package automationUtilities.mobileAutomation;

public class ScreenCoordinates {
	int x,y;
	public ScreenCoordinates(int x, int y){
		
		this.x = x < 0 ? 0 : x;
		this.y = y < 0 ? 0 : y;
	}
	public int getX() {
		return this.x;
	}
	public int getY() {
		return this.y;
	}
}
