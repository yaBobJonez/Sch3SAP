package yabj.sch3sap;

import java.time.LocalTime;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.stage.Popup;

public class FkTimeChooser extends HBox {
	//There's no TimePicker in default JFX, and I don't wanna use JFoenix (it doesn't fit), so here's my own Fake Time Chooser.
	//ya_Bob_Jonez © 2021
	private TextField editor = new TextField();
	private Button button = new Button("🕓");
	private Popup selector = new Popup();
	private LocalTime time = LocalTime.now();
	private Spinner<Integer> hours = new Spinner<>(00, 23, time.getHour());
	private Spinner<Integer> minutes = new Spinner<>(00, 59, time.getMinute());
	private Spinner<Integer> seconds = new Spinner<>(00, 59, time.getSecond());
	public FkTimeChooser(){
		HBox.setHgrow(editor, Priority.ALWAYS);
		button.setPrefWidth(60);
		hours.setPrefWidth(60);
		minutes.setPrefWidth(60);
		seconds.setPrefWidth(60);
		HBox internal = new HBox();
		internal.setPadding(new Insets(5, 5, 5, 5));
		internal.setBorder(new Border(new BorderStroke(Color.DIMGRAY, BorderStrokeStyle.SOLID, new CornerRadii(3), new BorderWidths(0.5))));
		internal.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, new CornerRadii(3), new Insets(0))));
		internal.getChildren().addAll(hours, minutes, seconds);
		selector.getContent().add(internal);
		selector.setOnShowing(e -> {
			selector.setX(this.localToScreen(0, 0).getX());
			selector.setY(this.localToScreen(0, this.getHeight()).getY());
		}); button.setOnAction(e -> {
			if(selector.isShowing()){
				selector.hide();
				updateTextValue();
			} else {
				selector.show(this.getScene().getWindow());
				hours.getValueFactory().setValue(time.getHour());
				minutes.getValueFactory().setValue(time.getMinute());
				seconds.getValueFactory().setValue(time.getSecond());
			}
		}); this.getChildren().addAll(editor, button);
		hours.valueProperty().addListener((value, old, newV) -> { updateTextValue(); });
		minutes.valueProperty().addListener((value, old, newV) -> { updateTextValue(); });
		seconds.valueProperty().addListener((value, old, newV) -> { updateTextValue(); });
		hours.getValueFactory().setWrapAround(true);
		minutes.getValueFactory().setWrapAround(true);
		seconds.getValueFactory().setWrapAround(true);
	}
	private void updateTextValue(){
		time = LocalTime.of(hours.getValue(), minutes.getValue(), seconds.getValue());
		editor.setText(String.format("%02d:%02d:%02d", time.getHour(), time.getMinute(), time.getSecond()));
	}
	public LocalTime getValue(){
		return this.time;
	} public void setValue(LocalTime time){
		this.time = time;
	}
}
