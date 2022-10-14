package it.alessandrobasi.mrturner.config;

public class Config {

	private Integer Delay = 5000;
	private boolean Enable = true;

	public Config() {

	}

	public Integer getDelay() {
		return Delay;
	}

	public void setDelay(Integer delay) {
		Delay = delay;
	}

	public boolean isEnable() {
		return Enable;
	}

	public void setEnable(boolean enable) {
		Enable = enable;
	}
}
