package sample;
import javafx.beans.property.StringProperty;

public class Simple {
	private String sample;
	private String sample2;
	private boolean check;
	private int count;
	private StringProperty name;

	public void setSample(String sample) {
		this.sample = sample;
	}

	public String getSample() {
		return sample;
	}

	public void setSample2(String sample2) {
		this.sample2 = sample2;
	}

	public String getSample2() {
		return sample2;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	public boolean getCheck() {
		return check;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getCount() {
		return count;
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public String getName() {
		return name.get();
	}

	public StringProperty nameProperty() {
		return name;
	}
}
