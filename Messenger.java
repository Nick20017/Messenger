package com.minemodsgames.enbl;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import com.mysql.jdbc.*;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class Messenger extends Application {
    // Objects for each page
    private Login login;
    private Register register;
    private Chat chat;

    // UI elements
    private Scene mainScene;
    private Stage mainStage;

    // Params
    private int width = 300;
    private int height = 400;

    // SQL
    private Connection connection = null;
    private Statement statement = null;
    private ResultSet result = null;
    private final String url = "jdbc:mySql://remotemysql.com/HTh6cJiMRG";
    private final String username = "HTh6cJiMRG";
    private final String password = "4jE0XBDGzi";
    private String command = null;

    @Override
    public void start(Stage stage) throws Exception {
        login = new Login();
        register = new Register();
        mainStage = stage;

        KeyFrame update = new KeyFrame(Duration.millis(100), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    if(!connection.isValid(1)) {
                        Connect();
                    }
                    if(statement.isClosed()) {
                        statement = connection.createStatement();
                    }
                } catch (SQLException e) { }
            }
        });

        Timeline timeline = new Timeline(update);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.playFromStart();

        mainStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                if (connection != null) {
                    try {
                        System.out.println("Disconnecting...");
                        connection.close();
                        System.out.println("Disconnected");
                        System.out.println("Closing statement...");
                        statement.close();
                        System.out.println("Statement closed");
                    } catch (Exception e) { }
                }
                timeline.stop();
            }
        });

        mainStage.setOnShowing(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                Connect();
            }
        });

        mainScene = login.getScene();
        mainStage.setScene(mainScene);
        mainStage.setTitle("E.N.B.L.");
        mainStage.show();
    }

    private void Connect() {
        try {
            System.out.println("Connecting...");
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected");
            System.out.println("Creating statement...");
            statement = connection.createStatement();
            System.out.println("Statement is created");
        } catch (Exception e) {
            System.out.println("Connection failed!");
            Connect();
        }
    }

    public void Show(String[] args) {
        launch(args);
    }


    private class Login implements IInitializeUI {
        // UI elements
        private Scene loginScene;
        private GridPane loginPane;
        private Label header;
        private Label loginText;
        private TextField loginField;
        private Label passwordText;
        private PasswordField passwordField;
        private Label switchText;
        private Button loginButton;

        @Override
        public void initializeUI() {
            loginPane = new GridPane();
            loginPane.setMinSize(width, height);
            loginPane.setAlignment(Pos.TOP_CENTER);
            loginPane.setStyle("-fx-background-color: darkblue;");

            header = new Label("Welcome to E.N.B.L.");
            header.setStyle("-fx-font: normal normal 25 'serif'; -fx-text-fill: white;");
            header.setPadding(new Insets(0, 0, 0, 15));
            loginPane.add(header, 0, 0);

            loginText = new Label("Login");
            loginText.setStyle("-fx-text-fill: white; -fx-font: normal normal 18 'serif';");
            loginText.setPadding(new Insets(10, 0, 0, 0));
            loginPane.add(loginText, 0, 1);

            loginField = new TextField();
            loginField.setFont(new Font(12));
            loginField.setPadding(new Insets(2, 0, 0, 0));
            loginField.setPrefHeight(23);
            loginPane.add(loginField, 0, 2);

            passwordText = new Label("Password");
            passwordText.setStyle("-fx-text-fill: white; -fx-font: normal normal 18 'serif';");
            passwordText.setPadding(new Insets(5, 0, 0, 0));
            loginPane.add(passwordText, 0, 3);

            passwordField = new PasswordField();
            passwordField.setFont(new Font(12));
            passwordField.setPadding(new Insets(2, 0, 0, 0));
            passwordField.setPrefHeight(23);
            loginPane.add(passwordField, 0, 4);

            switchText = new Label("Don't have account yet? Click to register");
            switchText.setStyle("-fx-text-fill: white; -fx-font: normal bold 14 'serif';");
            switchText.setPadding(new Insets(3, 0, 0, 0));
            loginPane.add(switchText, 0, 5);

            loginButton = new Button("Log in");
            loginButton.setPrefWidth(250);
            loginButton.setFont(new Font(15));
            loginButton.setPadding(new Insets(3, 0, 0, 0));
            loginButton.setPrefHeight(23);
            loginPane.add(loginButton, 0, 6);

            loginScene = new Scene(loginPane);

            switchText.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    loginScene.setCursor(Cursor.DEFAULT);
                    mainStage.setScene(register.getScene());
                }
            });

            switchText.addEventHandler(MouseEvent.MOUSE_ENTERED_TARGET, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    loginScene.setCursor(Cursor.OPEN_HAND);
                }
            });

            switchText.addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    loginScene.setCursor(Cursor.DEFAULT);
                }
            });

            loginButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    boolean error = false;
                    Alert dialog = new Alert(Alert.AlertType.WARNING);
                    dialog.setTitle("Warning");
                    dialog.setHeaderText(null);

                    String message = "";
                    if(loginField.getText().compareTo("") == 0)
                        message += "Login field must not be empty!\n";
                    if(passwordField.getText().compareTo("") == 0)
                        message += "Password field must not be empty!\n";

                    if(message.compareTo("") != 0)
                        error = true;

                    if(!error) {
                        try {
                            command = "select Login, Password from logindata";
                            result = statement.executeQuery(command);

                            message = "User not found!";
                            while (result.next()) {
                                if (loginField.getText().compareTo(result.getString(1)) == 0 && passwordField.getText().compareTo(result.getString(2)) == 0) {
                                    message = "Welcome " + result.getString(1) + " to E.N.B.L.!";
                                    dialog.setAlertType(Alert.AlertType.INFORMATION);
                                    error = false;
                                    break;
                                } else {
                                    message = "You've entered incorrect login or password!";
                                    error = true;
                                }
                            }
                        } catch (Exception e) {
                            message = e.getMessage();
                            error = true;
                        }
                    }

                    dialog.getDialogPane().setStyle("-fx-font: normal normal 16 'serif'");
                    dialog.setContentText(message);
                    dialog.showAndWait();
                    if(!error) {
                        System.out.println("Creating chat...");
                        chat = new Chat();
                        System.out.println("Chat is created");
                        chat.current = loginField.getText();
                        mainStage.setScene(chat.getScene());
                    }
                }
            });
        }

        public Login() {
            initializeUI();
        }

        public Scene getScene() {
            return loginScene;
        }
    }

    private class Register implements IInitializeUI {
        // UI elements
        private Scene registerScene;
        private GridPane registerPane;
        private Label header;
        private Label loginText;
        private TextField loginField;
        private Label passwordText;
        private PasswordField passwordField;
        private Label emailText;
        private TextField emailField;
        private Label birthdayText;
        private DatePicker birthdayPicker;
        private Label switchText;
        private Button registerButton;

        @Override
        public void initializeUI() {
            registerPane = new GridPane();
            registerPane.setMinSize(width, height);
            registerPane.setStyle("-fx-background-color: darkblue;");
            registerPane.setAlignment(Pos.TOP_CENTER);

            header = new Label("Welcome to E.N.B.L.");
            header.setStyle("-fx-font: normal normal 25 'serif'; -fx-text-fill: white;");
            header.setPadding(new Insets(0, 0, 0, 15));
            registerPane.add(header, 0, 0);

            loginText = new Label("Login");
            loginText.setStyle("-fx-text-fill: white; -fx-font: normal normal 18 'serif';");
            loginText.setPadding(new Insets(10, 0, 0, 0));
            registerPane.add(loginText, 0, 1);

            loginField = new TextField();
            loginField.setFont(new Font(12));
            loginField.setPadding(new Insets(2, 0, 0, 0));
            loginField.setPrefHeight(23);
            registerPane.add(loginField, 0, 2);

            passwordText = new Label("Password");
            passwordText.setStyle("-fx-text-fill: white; -fx-font: normal normal 18 'serif';");
            passwordText.setPadding(new Insets(5, 0, 0, 0));
            registerPane.add(passwordText, 0, 3);

            passwordField = new PasswordField();
            passwordField.setFont(new Font(12));
            passwordField.setPadding(new Insets(2, 0, 0, 0));
            passwordField.setPrefHeight(23);
            registerPane.add(passwordField, 0, 4);

            emailText = new Label("E-mail");
            emailText.setStyle("-fx-text-fill: white; -fx-font: normal normal 18 'serif';");
            emailText.setPadding(new Insets(5, 0, 0, 0));
            registerPane.add(emailText, 0, 5);

            emailField = new TextField();
            emailField.setFont(new Font(12));
            emailField.setPadding(new Insets(2, 0, 0, 0));
            emailField.setPrefHeight(23);
            registerPane.add(emailField, 0, 6);

            birthdayText = new Label("Date of birth");
            birthdayText.setStyle("-fx-text-fill: white; -fx-font: normal normal 18 'serif';");
            birthdayText.setPadding(new Insets(5, 0, 0, 0));
            registerPane.add(birthdayText, 0, 7);

            birthdayPicker = new DatePicker();
            birthdayPicker.setPrefWidth(250);
            birthdayPicker.setPadding(new Insets(2, 0, 0, 0));
            registerPane.add(birthdayPicker, 0, 8);

            switchText = new Label("Already have an account? Click to log in");
            switchText.setStyle("-fx-text-fill: white; -fx-font: normal bold 14 'serif';");
            switchText.setPadding(new Insets(3, 0, 0, 0));
            registerPane.add(switchText, 0, 9);

            registerButton = new Button("Register");
            registerButton.setPrefWidth(250);
            registerButton.setFont(new Font(15));
            registerButton.setPadding(new Insets(3, 0, 0, 0));
            registerButton.setPrefHeight(23);
            registerPane.add(registerButton, 0, 10);

            registerScene = new Scene(registerPane);

            switchText.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    registerScene.setCursor(Cursor.DEFAULT);
                    mainStage.setScene(login.getScene());
                }
            });

            switchText.addEventHandler(MouseEvent.MOUSE_ENTERED_TARGET, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    registerScene.setCursor(Cursor.OPEN_HAND);
                }
            });

            switchText.addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    registerScene.setCursor(Cursor.DEFAULT);
                }
            });

            registerButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    boolean error = false;
                    String message = "";
                    Alert dialog = new Alert(Alert.AlertType.WARNING);
                    dialog.setTitle("Warning");
                    dialog.setHeaderText(null);

                    if(loginField.getText().compareTo("") == 0) {
                        message += "Login must not be empty!\n";
                        error = true;
                    } else {
                        if(loginField.getText().length() < 4) {
                            message += "Login must contain at least 4 characters!\n";
                            error = true;
                        }
                    }

                    if(passwordField.getText().compareTo("") == 0) {
                        message += "Password must not be empty!\n";
                        error = true;
                    } else {
                        if(passwordField.getText().length() < 6) {
                            message += "Password must contain at least 6 characters!\n";
                            error = true;
                        }
                    }

                    if(emailField.getText().compareTo("") == 0) {
                        message += "E-mail must not be empty!\n";
                        error = true;
                    } else {
                        if (emailField.getText().indexOf("@") < 0) {
                            message += "You've entered incorrect e-mail!\nCorrect e-mail form: 'stevejobs@gmail.com'\n";
                            error = true;
                        }
                    }

                    if(birthdayPicker.getValue() == null) {
                        message += "You must pick the birth date!\n";
                        error = true;
                    }

                    if(!error) {
                        try {
                            boolean exists = false;
                            command = "select Login from logindata";
                            result = statement.executeQuery(command);
                            while (result.next()) {
                                if(result.getString(1).compareTo(loginField.getText()) == 0) {
                                    message = "User already exists!";
                                    exists = true;
                                    error = true;
                                    break;
                                }
                            }

                            if(!exists) {
                                command = "insert into logindata values ('"
                                        + loginField.getText() + "', '"
                                        + passwordField.getText() + "', '"
                                        + emailField.getText() + "', '"
                                        + birthdayPicker.getValue().toString() +"')";

                                int lines = statement.executeUpdate(command);
                                System.out.println(lines + " user registered");

                                command = "create table " + loginField.getText() + " (Message varchar(500) not null, Another varchar(50) not null, isSender boolean not null, MessageDate Date not null, MessageTime Time not null, isNew boolean not null)";
                                statement.execute(command);
                                message = "Welcome " + loginField.getText() + " to E.N.B.L.!\nYou successfully registered!";
                                dialog.setAlertType(Alert.AlertType.INFORMATION);
                                error = false;
                            }
                        } catch (Exception e) {
                            message = e.getMessage();
                            error = true;
                        }
                    }

                    dialog.getDialogPane().setStyle("-fx-font: normal normal 16 'serif'");
                    dialog.setContentText(message);
                    dialog.showAndWait();
                    if(!error) {
                        chat = new Chat();
                        chat.current = loginField.getText();
                        mainStage.setScene(chat.getScene());
                    }
                }
            });
        }

        public Register() {
            initializeUI();
        }

        public Scene getScene() {
            return registerScene;
        }
    }

    private class Chat implements IInitializeUI {
        // UI elements
        private Scene chatScene;
        private FlowPane chatPane;
        private Label header;
        private Button backButton;
        private FlowPane headerPane;
        private List<Message> messages;
        private ScrollPane scrollPane;
        private FlowPane messagePane;
        private TextArea messageField;
        private Button sendButton;
        private FlowPane sendingPane;
        private GridPane navBar;

        // Usernames
        private String current; // Current user
        private String another = "Nick"; // Another user

        @Override
        public void initializeUI() {
            chatPane = new FlowPane(Orientation.VERTICAL, 0, 10);
            chatScene = new Scene(chatPane, width, height);

            // Header label
            header = new Label(another);
            header.setFont(new Font(18));

            // Back button
            backButton = new Button("Back");
            backButton.setMaxWidth(50);
            backButton.setFont(new Font(14));

            // Header pane
            headerPane = new FlowPane(Orientation.HORIZONTAL, 10, 0);
            headerPane.setPrefSize(width, 20);
            headerPane.setPadding(new Insets(0, 0, 0, 5));

            // Adding items to header pane
            headerPane.getChildren().add(backButton);
            headerPane.getChildren().add(header);

            // Scroll pane
            scrollPane = new ScrollPane();
            scrollPane.setPadding(new Insets(0, 0, 0, 5));
            scrollPane.setVmax(100);
            scrollPane.setVmin(0);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

            // Sending pane
            sendingPane = new FlowPane(Orientation.HORIZONTAL, 10, 0);
            sendingPane.setPadding(new Insets(0, 0, 0, 5));

            // Send button
            sendButton = new Button("Send");
            sendButton.setMinWidth(50);
            sendButton.setMaxHeight(sendingPane.getHeight());
            sendButton.setFont(new Font(14));

            // Message field
            messageField = new TextArea();
            messageField.setPrefWidth(width - 80);
            messageField.setPrefHeight(30);
            messageField.setMaxHeight(100);

            // Adding items to sending pane
            sendingPane.getChildren().add(messageField);
            sendingPane.getChildren().add(sendButton);

            // Message pane
            messagePane = new FlowPane(Orientation.VERTICAL, 0, 10);
            scrollPane.setPrefHeight(chatScene.getHeight() - headerPane.getHeight() - sendingPane.getHeight() - chatPane.getVgap() * 2 - 75);
            scrollPane.setMaxHeight(scrollPane.getPrefHeight());
            scrollPane.setMaxWidth(chatScene.getWidth() - 20);
            scrollPane.setMinWidth(scrollPane.getMaxWidth());

            messages = new ArrayList<Message>();

            Thread updateThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    KeyFrame update = new KeyFrame(Duration.millis(1000), new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        command = "select * from " + current + " where Another = '" + another + "' and isNew = True";
                                        result = statement.executeQuery(command);
                                        Message message;

                                        while (result.next()) {
                                            if(result.getBoolean(6)) {
                                                message = new Message(result.getString(1), new Date(result.getDate(4).getYear(), result.getDate(4).getMonth(), result.getDate(4).getDay()), new Time(result.getTime(5).getHours(), result.getTime(5).getMinutes(), result.getTime(5).getSeconds()), scrollPane.getWidth());
                                                if(result.getBoolean(3))
                                                    message.sender = current;
                                                else
                                                    message.sender = another;
                                                message.setMessage();
                                                messagePane.getChildren().add(message);
                                            }
                                        }

                                        command = "update " + current + " set isNew = False where Another = '" + another + "' and isNew = True";
                                        int rows = statement.executeUpdate(command);
                                        if(rows > 0)
                                            System.out.println(rows + " rows were affected");
                                    } catch (Exception e) {
                                        System.out.println(e.getMessage());
                                    }
                                }
                            });
                        }
                    });

                    Timeline updateChat = new Timeline(update);
                    updateChat.setCycleCount(-1);

                    KeyFrame load = new KeyFrame(Duration.millis(100), new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            try {
                                command = "select * from " + current + " where Another = '" + another + "'";
                                result = statement.executeQuery(command);
                                while (result.next()) {
                                    messages.add(new Message(result.getString(1), result.getDate(4), result.getTime(5), scrollPane.getMaxWidth()));
                                    if(result.getBoolean(3))
                                        messages.get(messages.size() - 1).sender = current;
                                    else
                                        messages.get(messages.size() - 1).sender = another;
                                }

                                command = "update " + current + " set isNew=False where Another='" + another + "' and isNew = True";
                                int rows = statement.executeUpdate(command);
                                if(rows > 0)
                                    System.out.println(rows + " rows were affected");

                                for(int i = 0; i < messages.toArray().length; i++) {
                                    messages.get(i).setMessage();
                                    messagePane.getChildren().add(messages.get(i));
                                }

                                updateChat.play();
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }
                        }
                    });

                    Timeline loadChat = new Timeline(load);
                    loadChat.setCycleCount(1);
                    loadChat.playFromStart();
                }
            });

            updateThread.start();

            sendButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if(messageField.getText() != null) {
                        LocalDate date = LocalDate.now();
                        LocalTime time = LocalTime.now();
                        Message message = new Message(messageField.getText(), new Date(date.getYear(), date.getMonthValue(), date.getDayOfMonth()), new Time(time.getHour(), time.getMinute(), time.getSecond()), scrollPane.getWidth());

                        try {
                            command = "insert into " + current + " values ('" + message.getText() + "', '" + another + "', True, '" + message.msgDate.toString() + "', '" + message.msgTime.toString() + "', False); ";
                            int rows = statement.executeUpdate(command);
                            System.out.println(rows + " rows affected");

                            command = "insert into " + another + " values ('" + message.getText() + "', '" + current + "', False, '" + message.msgDate.toString() + "', '" + message.msgTime.toString() + "', True)";
                            rows = statement.executeUpdate(command);
                            System.out.println(rows + " rows affected");
                        } catch (Exception e) {
                            System.out.println(e.getMessage() + "\n602");
                        }

                        message.sender = current;
                        message.setMessage();
                        messagePane.getChildren().add(message);
                        messageField.setText("");
                    }
                }
            });

            messageField.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    if(messageField.getText().length() > 200) {
                        int offset = messageField.getText().length() - 200;
                        String msg = "";

                        messageField.setText(messageField.getText().substring(0, messageField.getText().length() - offset));
                    }
                }
            });

            scrollPane.setVvalue(messages.toArray().length);

            messagePane.setStyle("-fx-font: normal normal 16 'serif'");

            messagePane.setMaxWidth(scrollPane.getWidth());

            messagePane.setPrefWrapLength(29 * 48);

            scrollPane.setContent(messagePane);

            // Adding items to chat pane
            chatPane.getChildren().add(headerPane);
            chatPane.getChildren().add(scrollPane);
            chatPane.getChildren().add(sendingPane);
        }

        public Chat() { initializeUI(); }

        public Scene getScene() {
            return chatScene;
        }
    }
}
