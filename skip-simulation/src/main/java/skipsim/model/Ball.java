package skipsim.model;

public class Ball {
	public enum Mode {
		NEW, READ, WRITTEN;
	}

	private int no;
	private boolean invalid;
	private Mode mode = Mode.NEW;

	@Override
	public String toString() {
		return "Ball [no=" + no + ", invalid=" + invalid + ", mode=" + mode + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (invalid ? 1231 : 1237);
		result = prime * result + ((mode == null) ? 0 : mode.hashCode());
		result = prime * result + no;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ball other = (Ball) obj;
		if (invalid != other.invalid)
			return false;
		if (mode != other.mode)
			return false;
		if (no != other.no)
			return false;
		return true;
	}

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public boolean isInvalid() {
		return invalid;
	}

	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}
}
