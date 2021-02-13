package yabj.sch3sap;

import java.time.LocalTime;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class QueueElement { //Why is there no easier way of making tables... :/
	private SimpleObjectProperty<LocalTime> time = new SimpleObjectProperty<>();
	private SimpleStringProperty name = new SimpleStringProperty();
	private SimpleStringProperty path = new SimpleStringProperty();
	public QueueElement(LocalTime time, String name, String path){
		this.time.set(time);
		this.name.set(name);
		this.path.set(path);
	}
	public LocalTime getTime() {
		return time.get();
	}
	public String getName() {
		return name.get();
	}
	public String getPath() {
		return path.get();
	}
	public void setTime(LocalTime time) {
		this.time.set(time);
	}
	public void setName(String name) {
		this.name.set(name);
	}
	public void setPath(String path) {
		this.path.set(path);
	}
}
