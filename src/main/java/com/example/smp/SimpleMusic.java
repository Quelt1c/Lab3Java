package com.example.smp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class SimpleMusic extends Application {

    private ListView<String> playlist;
    private List<String> songs = new ArrayList<>();
    private MediaPlayer mediaPlayer;
    private Slider volumeSlider;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Простий музичний плеєр");

        // Створення елементів інтерфейсу
        Button loadButton = new Button("Додати файл");
        Button removeButton = new Button("Видалити вибране");
        Button savePlaylistButton = new Button("Зберегти плейлист");
        Button loadPlaylistButton = new Button("Завантажити плейлист");
        Button playButton = new Button("Відтворити");
        Button pauseButton = new Button("Пауза");
        Button resumeButton = new Button("Продовжити");
        volumeSlider = new Slider(0, 100, 50);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);
        playlist = new ListView<>();

        // Налаштування обробників подій
        loadButton.setOnAction(e -> loadFile(primaryStage));
        removeButton.setOnAction(e -> removeSelected());
        savePlaylistButton.setOnAction(e -> savePlaylist(primaryStage));
        loadPlaylistButton.setOnAction(e -> loadPlaylist(primaryStage));
        playButton.setOnAction(e -> playMusic());
        pauseButton.setOnAction(e -> pauseMusic());
        resumeButton.setOnAction(e -> resumeMusic());
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(newValue.doubleValue() / 100.0);
            }
        });

        // Створення макету
        HBox controlButtons = new HBox(10, playButton, pauseButton, resumeButton);
        VBox layout = new VBox(10);
        layout.getChildren().addAll(loadButton, playlist, removeButton, controlButtons,
                new Label("Гучність:"), volumeSlider,
                savePlaylistButton, loadPlaylistButton);

        // Налаштування сцени
        Scene scene = new Scene(layout, 300, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Аудіо файли", "*.mp3", "*.wav")
        );
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            songs.add(file.getAbsolutePath());
            updatePlaylist();
        }
    }

    private void removeSelected() {
        int selectedIndex = playlist.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            songs.remove(selectedIndex);
            updatePlaylist();
        }
    }

    private void updatePlaylist() {
        playlist.getItems().clear();
        for (String song : songs) {
            playlist.getItems().add(new File(song).getName());
        }
    }

    private void savePlaylist(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Текстові файли", "*.txt")
        );
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                for (String song : songs) {
                    writer.write(song + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadPlaylist(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Текстові файли", "*.txt")
        );
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                songs = Files.readAllLines(file.toPath());
                updatePlaylist();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void playMusic() {
        String selected = playlist.getSelectionModel().getSelectedItem();
        if (selected != null) {
            int index = playlist.getSelectionModel().getSelectedIndex();
            String path = songs.get(index);
            Media media = new Media(new File(path).toURI().toString());
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(volumeSlider.getValue() / 100.0);
            mediaPlayer.play();
        }
    }

    private void pauseMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    private void resumeMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}