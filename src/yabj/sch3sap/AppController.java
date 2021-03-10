package yabj.sch3sap;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class AppController {
	public boolean active = false;
	private FileChooser fileChooser = new FileChooser();
	protected Media curr;
	protected MediaPlayer controls;
	private Timer scheduler = new Timer(); //TODO ScheduledExecutorService
	protected LocalTime[] schedulePeriod = {
		LocalTime.of(8, 30, 00),
		LocalTime.of(9, 25, 00),
		LocalTime.of(10, 20, 00),
		LocalTime.of(11, 20, 00),
		LocalTime.of(12, 25, 00),
		LocalTime.of(13, 20, 00),
		LocalTime.of(14, 10, 00),
		LocalTime.of(15, 00, 00)
	}; protected LocalTime[] scheduleRecess = {
		LocalTime.of(9, 15, 00),
		LocalTime.of(10, 10, 00),
		LocalTime.of(11, 05, 00),
		LocalTime.of(12, 05, 00),
		LocalTime.of(13, 10, 00),
		LocalTime.of(14, 05, 00),
		LocalTime.of(14, 55, 00),
		LocalTime.of(15, 45, 00)
	};
	@FXML private TextField editStart;
	@FXML private Button chooseStart;
	@FXML private TextField editEnd;
	@FXML private Button chooseEnd;
	@FXML private Button execute;
	@FXML private Button information;
	protected File saveData = new File("./Preset.s3dn");
	{
		fileChooser.setTitle("Оберіть аудіо-файл.");
		fileChooser.setSelectedExtensionFilter(new ExtensionFilter("Аудіо", "*.mp3", "*.aiff", "*.wav"));
		Platform.runLater(() -> {
		if(saveData.exists() & saveData.length() != 0){ try{
			Scanner reader = new Scanner(saveData);
			this.editStart.setText(reader.nextLine());
			this.editEnd.setText(reader.nextLine());
			reader.close();
		} catch(FileNotFoundException e){ System.err.println("Внутрішня помилка "+e.getClass().getSimpleName()+
		": зверніться до розробника"); }}
		});
	}
	@FXML protected void onExecute() throws ParseException{
		if(!active){
			if(editStart.getText().isBlank() || editEnd.getText().isBlank()) return;
			for(LocalTime time : this.schedulePeriod){
				if(time.isBefore(LocalTime.now())) continue;
				scheduler.schedule(new TimerTask(){
					@Override
					public void run() {
						curr = new Media("file://" + getNormalizedPath(editStart.getText()));
						controls = new MediaPlayer(curr);
						controls.setOnReady(() -> {
							controls.play();
						});
					}
				}, Date.from( time.atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant() ) );
			} for(LocalTime time : this.scheduleRecess){
				if(time.isBefore(LocalTime.now())) continue;
				scheduler.schedule(new TimerTask(){
					@Override
					public void run() {
						curr = new Media("file://" + getNormalizedPath(editEnd.getText()));
						controls = new MediaPlayer(curr);
						controls.setOnReady(() -> {
							controls.play();
						});
					}
				}, Date.from( time.atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant() ) );
			} execute.setTextFill(Color.RED);
			execute.setText("ВИМКНУТИ");
			active = true;
		} else{
			if(controls != null) controls.stop();
			scheduler.cancel();
			scheduler = new Timer();
			execute.setTextFill(Color.GREEN);
			execute.setText("УВІМКНУТИ");
			active = false;
		}
	}
	@FXML protected void onStartSelect(){
		File file = fileChooser.showOpenDialog(execute.getScene().getWindow());
		if(file != null) editStart.setText(file.getAbsolutePath());
	} @FXML protected void onEndSelect(){
		File file = fileChooser.showOpenDialog(execute.getScene().getWindow());
		if(file != null) editEnd.setText(file.getAbsolutePath());
	}
	@FXML protected void onInfo(){
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("");
		alert.setHeaderText("Інформація о ПЗ");
		alert.setContentText("Програвач Запланованого Аудіо (Singleton),\n"
				+ "рідній школі №3 м. Києва ;)\n\n"
				+ "Документація: https://github.com/yaBobJonez/Sch3SAP/\n\n"
				+ "ya_Bob_Jonez © 2021");
		alert.getButtonTypes().set(0, ButtonType.CLOSE);
		alert.show();
	}
	public static String getNormalizedPath(String link){ //Microsoft Windows path to RFC-3986
		if(link.charAt(0) == '.'){
			link = new File(link).getAbsolutePath();
			link = link.replace("\\", "/");
            link = "/" + link;
		} else if(!(link.charAt(0) == '/')){
            link = link.replace("\\", "/");
            link = "/" + link;
        } return link;
	}
}
