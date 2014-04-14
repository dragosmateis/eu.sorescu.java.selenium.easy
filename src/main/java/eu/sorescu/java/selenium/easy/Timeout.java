package eu.sorescu.java.selenium.easy;

public class Timeout {
	private final long start;
	private final int durationMillis;

	public Timeout(int durationMillis) {
		this.start = System.currentTimeMillis();
		this.durationMillis = durationMillis;
	}

	public boolean done() {
		return System.currentTimeMillis() > this.start + this.durationMillis;
	}
}
