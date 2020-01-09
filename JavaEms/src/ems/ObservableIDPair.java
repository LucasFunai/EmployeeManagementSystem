package ems;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;


public class ObservableIDPair {
	SimpleIntegerProperty idProperty;
	SimpleStringProperty nameProperty;

	public ObservableIDPair(String string, int Int) {
		this.nameProperty = new SimpleStringProperty(string);
		this.idProperty = new SimpleIntegerProperty(Int);
	}

	public void setId(int id) {
		idProperty.set(id);
	}

	public void setName(String name) {
		nameProperty.set(name);
	}

	public String getName() {
		return nameProperty.get();

	}

	public int getId() {
		return idProperty.get();

	}
	
	public SimpleStringProperty getNameProperty() {
		return nameProperty;
	}
	
	public SimpleIntegerProperty getIdProperty() {
		return idProperty;
	}
}
